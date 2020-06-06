package io.changock.runner.core;

import io.changock.driver.api.common.ForbiddenParameterException;
import io.changock.driver.api.driver.ChangeSetDependency;
import io.changock.driver.api.driver.ForbiddenParametersMap;
import io.changock.driver.api.lock.guard.proxy.LockGuardProxyFactory;
import io.changock.migration.api.exception.ChangockException;
import io.changock.utils.annotation.NotThreadSafe;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Optional;

@NotThreadSafe
public class DependencyManager {

  private final LinkedHashSet<ChangeSetDependency> connectorDependencies;
  private final LinkedHashSet<ChangeSetDependency> standardDependencies;
  private final ForbiddenParametersMap forbiddenParametersMap;
  protected LockGuardProxyFactory lockGuardProxyFactory;

  public DependencyManager() {
    standardDependencies = new LinkedHashSet<>();
    connectorDependencies = new LinkedHashSet<>();
    forbiddenParametersMap = new ForbiddenParametersMap();
  }

  public Optional<Object> getDependencyByClass(Class type) throws ForbiddenParameterException {
    return getDependencyByClass(type, true);
  }


  public Optional<Object> getDependencyByClass(Class type, boolean lockGuarded) throws ForbiddenParameterException {
    Optional<Object> dependencyOpt = forbiddenParametersMap.throwExceptionIfPresent(type)
        .or(() -> getDependencyFromStoreByClass(connectorDependencies, type));
    return dependencyOpt.isPresent() ? dependencyOpt : getStandardDependencyByClass(type, lockGuarded);
  }


  private Optional<Object> getStandardDependencyByClass(Class type, boolean lockProxy) {
    Optional<Object> dependencyOpt = getDependencyFromStoreByClass(standardDependencies, type);
    if (dependencyOpt.isPresent() && lockProxy) {
      if (!type.isInterface()) {
        throw new ChangockException(String.format("Parameter of type [%s] must be an interface", type.getSimpleName()));
      }
      return dependencyOpt.map(instance -> lockGuardProxyFactory.getRawProxy(instance, type));
    } else {
      return dependencyOpt;
    }
  }

  @SuppressWarnings("unchecked")
  private Optional<Object> getDependencyFromStoreByClass(Collection<ChangeSetDependency> dependencyStore, Class type) {
    return dependencyStore
        .stream()
        .filter(dependency -> type.isAssignableFrom(dependency.getType()))
        // following step is to ensure that it will return a default dependency if there is any. Otherwise will return first appearance
        .reduce((dependency1, dependency2) -> !dependency1.isDefaultNamed() && dependency2.isDefaultNamed() ? dependency2 : dependency1)
        .map(ChangeSetDependency::getInstance);
  }

  ///////////
  /////////// By name

  public Optional<Object> getDependencyByName(Class type, String name) throws ForbiddenParameterException {
    return getDependencyByName(type, name, true);
  }

  public Optional<Object> getDependencyByName(Class type, String name, boolean lockGuarded) throws ForbiddenParameterException {
    Optional<Object> dependencyOpt = forbiddenParametersMap
        .throwExceptionIfPresent(type)
        .or(() -> getDependencyFromStoreByName(connectorDependencies, name));
    return dependencyOpt.isPresent() ? dependencyOpt : getStandardDependencyByName(type, name, lockGuarded);
  }

  private Optional<Object> getStandardDependencyByName(Class type, String name, boolean lockProxy) {
    Optional<Object> dependencyOpt = getDependencyFromStoreByName(standardDependencies, name);
    if (dependencyOpt.isPresent() && lockProxy) {
      if (!type.isInterface()) {
        throw new ChangockException(String.format("Parameter of type [%s] must be an interface", type.getSimpleName()));
      }
      return dependencyOpt.map(instance -> lockGuardProxyFactory.getRawProxy(instance, type));
    } else {
      return dependencyOpt;
    }
  }

  private Optional<Object> getDependencyFromStoreByName(Collection<ChangeSetDependency> dependencyStore, String name) {
    return dependencyStore
        .stream()
        .filter(dependency -> name.equals(dependency.getName()))
        .map(ChangeSetDependency::getInstance)
        .findFirst();
  }

  // setters

  public DependencyManager setLockGuardProxyFactory(LockGuardProxyFactory lockGuardProxyFactory) {
    this.lockGuardProxyFactory = lockGuardProxyFactory;
    return this;
  }

  public DependencyManager addDriverDependencies(Collection<? extends ChangeSetDependency> dependencies) {
    dependencies.forEach(this::addDriverDependency);
    return this;
  }

  public DependencyManager addDriverDependency(ChangeSetDependency dependency) {
    return addDependency(connectorDependencies, dependency);
  }

  public DependencyManager addStandardDependency(Collection<? extends ChangeSetDependency> dependencies) {
    dependencies.forEach(this::addStandardDependency);
    return this;
  }

  public DependencyManager addStandardDependency(ChangeSetDependency dependency) {
    return addDependency(standardDependencies, dependency);
  }

  public DependencyManager addForbiddenParameters(ForbiddenParametersMap forbiddenParametersMap) {
    this.forbiddenParametersMap.putAll(forbiddenParametersMap);
    return this;
  }

  private <T extends ChangeSetDependency> DependencyManager addDependency(Collection<T> dependencyStore, T dependency) {
    //add returns false if it's already there. In that case, it needs to be removed and then inserted
    if (!dependencyStore.add(dependency)) {
      dependencyStore.remove(dependency);
      dependencyStore.add(dependency);
    }
    return this;
  }


}
