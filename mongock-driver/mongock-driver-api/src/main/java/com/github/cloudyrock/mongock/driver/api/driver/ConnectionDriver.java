package com.github.cloudyrock.mongock.driver.api.driver;

import com.github.cloudyrock.mongock.driver.api.common.Validable;
import com.github.cloudyrock.mongock.driver.api.entry.ChangeEntry;
import com.github.cloudyrock.mongock.driver.api.entry.ChangeEntryService;
import com.github.cloudyrock.mongock.driver.api.lock.LockManager;

import java.util.Set;

public interface ConnectionDriver<CHANGE_ENTRY extends ChangeEntry> extends Transactionable, Validable {
  void initialize();

  LockManager getLockManager();

  LockManager getManagerAndAcquireLock();

  ChangeEntryService<CHANGE_ENTRY> getChangeEntryService();

  Set<ChangeSetDependency> getDependencies();

  Class getLegacyMigrationChangeLogClass(boolean runAlways);

  void setChangeLogRepositoryName(String changeLogRepositoryName);

  void setLockRepositoryName(String lockRepositoryName);

  void setIndexCreation(boolean indexCreation);

  boolean isInitialized();

  long getLockAcquiredForMillis();

  long getLockQuitTryingAfterMillis();

  long getLockTryFrequencyMillis();

  String getChangeLogRepositoryName();

  String getLockRepositoryName();

  boolean isIndexCreation();
}
