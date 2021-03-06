package com.github.cloudyrock.mongock.driver.api.driver;

import com.github.cloudyrock.mongock.driver.api.common.Validable;
import com.github.cloudyrock.mongock.driver.api.entry.ChangeEntry;
import com.github.cloudyrock.mongock.driver.api.entry.ChangeEntryService;
import com.github.cloudyrock.mongock.driver.api.lock.LockManager;

import java.util.Optional;
import java.util.Set;

public interface ConnectionDriver<CHANGE_ENTRY extends ChangeEntry> extends Validable, DriverLegaciable {
  void initialize();

  LockManager getLockManager();

  ChangeEntryService<CHANGE_ENTRY> getChangeEntryService();

  Set<ChangeSetDependency> getDependencies();

  boolean isInitialized();

  void setChangeLogRepositoryName(String changeLogRepositoryName);

  void setLockRepositoryName(String lockRepositoryName);

  boolean isIndexCreation();

  void setIndexCreation(boolean indexCreation);

  /**
   * Mechanism to disabled transactions in case they are available.
   */
  void disableTransaction();

  /**
   * If transaction available, returns the Transactioner
   *
   * @return the Transactioner
   */
  Optional<Transactioner> getTransactioner();

  default boolean isTransactionable() {
    return getTransactioner().isPresent();
  }

}
