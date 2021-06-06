package com.github.cloudyrock.test.runner;

import com.github.cloudyrock.mongock.config.MongockConfiguration;
import com.github.cloudyrock.mongock.driver.api.driver.ConnectionDriver;
import com.github.cloudyrock.mongock.driver.api.entry.ChangeEntry;
import com.github.cloudyrock.mongock.runner.core.executor.dependency.DependencyManager;
import com.github.cloudyrock.mongock.runner.core.executor.operation.change.MigrationExecutor;

import javax.annotation.concurrent.NotThreadSafe;
import java.lang.reflect.Parameter;
import java.util.function.Function;

@NotThreadSafe
public class TestMigrationExecutor extends MigrationExecutor {

  private final String executionId;

  public TestMigrationExecutor(String executionId,
                               ConnectionDriver<ChangeEntry> driver,
                               DependencyManager dependencyManager,
                               Function<Parameter, String> paramNameExtractor,
                               MongockConfiguration config) {
    //todo remove null
    super(null, driver, dependencyManager, paramNameExtractor, config);
    this.executionId = executionId;
  }

  @Override
  protected String generateExecutionId() {
    return executionId != null ? executionId : super.generateExecutionId();
  }


}
