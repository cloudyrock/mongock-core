package com.github.cloudyrock.springboot;

import com.github.cloudyrock.mongock.ChangeLogItem;
import com.github.cloudyrock.mongock.ChangeSetItem;
import com.github.cloudyrock.mongock.config.MongockConfiguration;
import com.github.cloudyrock.mongock.driver.api.entry.ChangeEntry;
import com.github.cloudyrock.mongock.runner.core.executor.ExecutorFactory;
import com.github.cloudyrock.mongock.runner.core.executor.ExecutorFactoryDefault;
import com.github.cloudyrock.mongock.runner.core.executor.changelog.ChangeLogService;
import com.github.cloudyrock.mongock.runner.core.executor.operation.change.MigrationOp;
import com.github.cloudyrock.springboot.base.builder.SpringbootBuilderBase;

public final class MongockSpringboot {

  //TODO javadoc
  public static MigrationSpringbootBuilder builder() {
    return new MigrationBuilderImpl(new ExecutorFactoryDefault<>(), new MongockConfiguration());
  }

  public static class MigrationBuilderImpl extends SpringbootBuilderBase<MigrationBuilderImpl, Boolean, ChangeLogItem<ChangeSetItem>, ChangeSetItem, ChangeEntry, MongockConfiguration>
      implements MigrationSpringbootBuilder {

    private MigrationBuilderImpl(ExecutorFactory<ChangeLogItem<ChangeSetItem>, ChangeSetItem, ChangeEntry, MongockConfiguration, Boolean> executorFactory, MongockConfiguration config) {
      super(new MigrationOp(), executorFactory, new ChangeLogService(), config);
    }

    @Override
    public MigrationBuilderImpl getInstance() {
      return this;
    }
  }
}
