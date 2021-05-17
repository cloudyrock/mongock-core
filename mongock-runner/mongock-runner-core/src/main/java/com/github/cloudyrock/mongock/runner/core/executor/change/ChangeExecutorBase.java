package com.github.cloudyrock.mongock.runner.core.executor.change;

import com.github.cloudyrock.mongock.ChangeLogItem;
import com.github.cloudyrock.mongock.ChangeSetItem;
import com.github.cloudyrock.mongock.NonLockGuarded;
import com.github.cloudyrock.mongock.config.MongockConfiguration;
import com.github.cloudyrock.mongock.driver.api.common.DependencyInjectionException;
import com.github.cloudyrock.mongock.driver.api.driver.ConnectionDriver;
import com.github.cloudyrock.mongock.driver.api.driver.Transactioner;
import com.github.cloudyrock.mongock.driver.api.entry.ChangeEntry;
import com.github.cloudyrock.mongock.driver.api.entry.ChangeState;
import com.github.cloudyrock.mongock.driver.api.lock.LockManager;
import com.github.cloudyrock.mongock.driver.api.lock.guard.proxy.LockGuardProxyFactory;
import com.github.cloudyrock.mongock.exception.MongockException;
import com.github.cloudyrock.mongock.runner.core.executor.Executor;
import com.github.cloudyrock.mongock.runner.core.executor.dependency.DependencyManager;
import com.github.cloudyrock.mongock.utils.LogUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.concurrent.NotThreadSafe;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.SortedSet;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.github.cloudyrock.mongock.driver.api.entry.ChangeState.EXECUTED;
import static com.github.cloudyrock.mongock.driver.api.entry.ChangeState.FAILED;
import static com.github.cloudyrock.mongock.driver.api.entry.ChangeState.IGNORED;

@NotThreadSafe
public class ChangeExecutorBase<CONFIG extends MongockConfiguration> implements Executor {

  private static final Logger logger = LoggerFactory.getLogger(ChangeExecutorBase.class);

  private final Map<String, Object> metadata;
  private final DependencyManager dependencyManager;
  private final Function<Parameter, String> parameterNameProvider;
  private boolean executionInProgress = false;
  protected final ConnectionDriver driver;
  protected final String serviceIdentifier;
  protected final boolean trackIgnored;


  protected ChangeExecutorBase(ConnectionDriver driver,
                               DependencyManager dependencyManager,
                               Function<Parameter, String> parameterNameProvider,
                               CONFIG config) {
    this.driver = driver;
    this.dependencyManager = dependencyManager;
    this.parameterNameProvider = parameterNameProvider;
    this.metadata = config.getMetadata();
    this.serviceIdentifier = config.getServiceIdentifier();
    this.trackIgnored = config.isTrackIgnored();
  }

  public boolean isExecutionInProgress() {
    return this.executionInProgress;
  }

  public void executeMigration(SortedSet<ChangeLogItem> changeLogs) {
    initializationAndValidation();
    try (LockManager lockManager = driver.getLockManager()) {
      if (!this.isThereAnyChangeSetItemToBeExecuted(changeLogs)) {
        logger.info("Mongock skipping the data migration. All change set items are already executed or there is no change set item.");
        return;
      }
      lockManager.acquireLockDefault();
      String executionId = generateExecutionId();
      String executionHostname = generateExecutionHostname(executionId);
      logger.info("Mongock starting the data migration sequence id[{}]...", executionId);
      processPreMigration(changeLogs, executionId, executionHostname);
      processMigration(changeLogs, executionId, executionHostname);
      processPostMigration(changeLogs, executionId, executionHostname);
    } finally {
      this.executionInProgress = false;
      logger.info("Mongock has finished");
    }
  }

  protected void processMigration(SortedSet<ChangeLogItem> changeLogs, String executionId, String executionHostname) {
    List<ChangeLogItem> changeLogsMigration = changeLogs.stream().filter(ChangeLogItem::isMigration).collect(Collectors.toList());
    getTransactioner()
        .orElse(Runnable::run)
        .executeInTransaction(() -> processChangeLogs(executionId, executionHostname, changeLogsMigration));
  }

  protected void processPreMigration(SortedSet<ChangeLogItem> changeLogs, String executionId, String executionHostname) {
    List<ChangeLogItem> changeLogPreMigration = changeLogs.stream().filter(ChangeLogItem::isPreMigration).collect(Collectors.toList());
    processChangeLogs(executionId, executionHostname, changeLogPreMigration);
  }

  protected void processPostMigration(SortedSet<ChangeLogItem> changeLogs, String executionId, String executionHostname) {
    List<ChangeLogItem> changeLogPostMigration = changeLogs.stream().filter(ChangeLogItem::isPostMigration).collect(Collectors.toList());
    processChangeLogs(executionId, executionHostname, changeLogPostMigration);
  }

  protected void processChangeLogs(String executionId, String executionHostname, Collection<ChangeLogItem> changeLogs) {
    for (ChangeLogItem changeLog : changeLogs) {
      processSingleChangeLog(executionId, executionHostname, changeLog);
    }
  }

  protected void processSingleChangeLog(String executionId, String executionHostname, ChangeLogItem changeLog) {
    try {
      for (ChangeSetItem changeSet : changeLog.getChangeSetElements()) {
        processSingleChangeSet(executionId, executionHostname, changeLog, changeSet);
      }
    } catch (Exception e) {
      if (changeLog.isFailFast()) {
        throw e;
      }
    }
  }

  private void processSingleChangeSet(String executionId, String executionHostname, ChangeLogItem changeLog, ChangeSetItem changeSet) {
    try {
      executeAndLogChangeSet(executionId, executionHostname, changeLog.getInstance(), changeSet);
    } catch (Exception e) {
      processExceptionOnChangeSetExecution(e, changeSet.getMethod(), changeSet.isFailFast());
    }
  }

  protected String generateExecutionId() {
    return String.format("%s-%d", LocalDateTime.now().toString(), new Random().nextInt(999));
  }

  private String generateExecutionHostname(String executionId) {
    String hostname;
    try {
      hostname = InetAddress.getLocalHost().getHostName();
    } catch (Exception e) {
      hostname = "unknown-host." + executionId;
    }

    if (StringUtils.isNotEmpty(serviceIdentifier)) {
      hostname += "-";
      hostname += serviceIdentifier;
    }
    return hostname;
  }

  private boolean isThereAnyChangeSetItemToBeExecuted(SortedSet<ChangeLogItem> changeLogs) {
    return changeLogs.stream()
        .map(ChangeLogItem::getChangeSetElements)
        .flatMap(List::stream)
        .anyMatch(changeSetItem -> changeSetItem.isRunAlways() || !this.isAlreadyExecuted(changeSetItem));
  }

  private boolean isAlreadyExecuted(ChangeSetItem changeSetItem) {
    return driver.getChangeEntryService().isAlreadyExecuted(changeSetItem.getId(), changeSetItem.getAuthor());
  }

  protected void executeAndLogChangeSet(String executionId, String executionHostname, Object changelogInstance, ChangeSetItem changeSetItem) throws IllegalAccessException, InvocationTargetException {
    ChangeEntry changeEntry = null;
    boolean alreadyExecuted = false;
    try {
      if (!(alreadyExecuted = isAlreadyExecuted(changeSetItem)) || changeSetItem.isRunAlways()) {
        final long executionTimeMillis = executeChangeSetMethod(changeSetItem.getMethod(), changelogInstance);
        changeEntry = createChangeEntryInstance(executionId, executionHostname, changeSetItem, executionTimeMillis, EXECUTED);

      } else {
        changeEntry = createChangeEntryInstance(executionId, executionHostname, changeSetItem, -1L, IGNORED);

      }
    } catch (Exception ex) {
      changeEntry = createChangeEntryInstance(executionId, executionHostname, changeSetItem, -1L, FAILED);
      throw ex;
    } finally {
      if (changeEntry != null) {
        logChangeEntry(changeEntry, changeSetItem, alreadyExecuted);
        // if not runAlways or, being runAlways, it hasn't been executed before
        if (!changeSetItem.isRunAlways() || !alreadyExecuted) {
          //if not ignored or, being ignored, should be tracked anyway
          if (changeEntry.getState() != IGNORED || trackIgnored) {
            driver.getChangeEntryService().save(changeEntry);
          }
        }
      }
    }
  }

  private void logChangeEntry(ChangeEntry changeEntry, ChangeSetItem changeSetItem, boolean alreadyExecuted) {
    switch (changeEntry.getState()) {
      case EXECUTED:
        logger.info("{}APPLIED - {}", alreadyExecuted ? "RE-" : "", changeEntry.toPrettyString());
        break;
      case IGNORED:
        logger.info("PASSED OVER - {}", changeSetItem.toPrettyString());
        break;
      case FAILED:
        logger.info("FAILED OVER - {}", changeSetItem.toPrettyString());
        break;
    }
  }

  protected ChangeEntry createChangeEntryInstance(String executionId, String executionHostname, ChangeSetItem changeSetItem, long executionTimeMillis, ChangeState state) {
    return ChangeEntry.createInstance(executionId, state, changeSetItem, executionTimeMillis, executionHostname, metadata);
  }

  protected long executeChangeSetMethod(Method changeSetMethod, Object changeLogInstance) throws IllegalAccessException, InvocationTargetException {
    final long startingTime = System.currentTimeMillis();
    Class<?>[] parameterTypes = changeSetMethod.getParameterTypes();
    Parameter[] parameters = changeSetMethod.getParameters();
    List<Object> changelogInvocationParameters = new ArrayList<>(parameterTypes.length);
    for (int paramIndex = 0; paramIndex < parameterTypes.length; paramIndex++) {
      changelogInvocationParameters.add(getParameter(parameterTypes[paramIndex], parameters[paramIndex]));
    }
    LogUtils.logMethodWithArguments(logger, changeSetMethod.getName(), changelogInvocationParameters);
    changeSetMethod.invoke(changeLogInstance, changelogInvocationParameters.toArray());
    return System.currentTimeMillis() - startingTime;
  }

  protected Object getParameter(Class<?> parameterType, Parameter parameter) {
    String name = getParameterName(parameter);
    return dependencyManager
        .getDependency(parameterType, name, !parameterType.isAnnotationPresent(NonLockGuarded.class) && !parameter.isAnnotationPresent(NonLockGuarded.class))
        .orElseThrow(() -> new DependencyInjectionException(parameterType, name));
  }


  protected String getParameterName(Parameter parameter) {
    return parameterNameProvider.apply(parameter);
  }

  protected void processExceptionOnChangeSetExecution(Exception exception, Method method, boolean throwException) {
    String exceptionMsg = exception instanceof InvocationTargetException
        ? ((InvocationTargetException) exception).getTargetException().getMessage()
        : exception.getMessage();
    String finalMessage = String.format("Error in method[%s.%s] : %s", method.getDeclaringClass().getSimpleName(), method.getName(), exceptionMsg);
    if (throwException) {
      throw new MongockException(finalMessage, exception);

    } else {
      logger.warn(finalMessage, exception);
    }
  }

  @SuppressWarnings("unchecked")
  protected void initializationAndValidation() throws MongockException {
    this.executionInProgress = true;
    driver.initialize();
    driver.runValidation();
    this.dependencyManager
        .setLockGuardProxyFactory(new LockGuardProxyFactory(driver.getLockManager()))
        .addDriverDependencies(driver.getDependencies());
    this.dependencyManager.runValidation();
  }


  protected Optional<Transactioner> getTransactioner() {
    //this casting is required because Java Generic is broken. As the connectionDriver is generic, it cannot deduce the
    //type of the Optional
    return (Optional<Transactioner>) driver.getTransactioner();
  }

}