package com.github.cloudyrock.mongock.runner.core.executor;

import com.github.cloudyrock.mongock.ChangeLogItem;

import java.util.SortedSet;

public interface MigrationExecutor<T> {
  T executeMigration(SortedSet<ChangeLogItem> changeLogs);
  boolean isExecutionInProgress();
}
