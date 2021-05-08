package com.github.cloudyrock.mongock.driver.core.driver;

import com.github.cloudyrock.mongock.TransactionStrategy;
import com.github.cloudyrock.mongock.driver.api.driver.ConnectionDriver;
import com.github.cloudyrock.mongock.driver.api.entry.ChangeEntry;
import com.github.cloudyrock.mongock.driver.api.lock.LockManager;
import com.github.cloudyrock.mongock.driver.core.lock.DefaultLockManager;
import com.github.cloudyrock.mongock.driver.core.lock.LockRepository;
import com.github.cloudyrock.mongock.exception.MongockException;
import com.github.cloudyrock.mongock.utils.TimeService;
import com.github.cloudyrock.mongock.utils.annotation.NotThreadSafe;

@NotThreadSafe
public abstract class ConnectionDriverBase<CHANGE_ENTRY extends ChangeEntry> implements ConnectionDriver<CHANGE_ENTRY> {

  private static final TimeService TIME_SERVICE = new TimeService();

  //Lock
  private final long lockAcquiredForMillis;
  private final long lockQuitTryingAfterMillis;
  private final long lockTryFrequencyMillis;

  private boolean initialized = false;
  private LockManager lockManager = null;
  private String changeLogRepositoryName;
  private String lockRepositoryName;
  private boolean indexCreation = true;
  private TransactionStrategy transactionStrategy;


  protected ConnectionDriverBase(long lockAcquiredForMillis, long lockQuitTryingAfterMillis, long lockTryFrequencyMillis) {
    this.lockAcquiredForMillis = lockAcquiredForMillis;
    this.lockQuitTryingAfterMillis = lockQuitTryingAfterMillis;
    this.lockTryFrequencyMillis = lockTryFrequencyMillis;
  }

  @Override
  public final void initialize() {
    if (!initialized) {
      initialized = true;
      LockRepository lockRepository = this.getLockRepository();
      lockRepository.initialize();
      lockManager = new DefaultLockManager(lockRepository, TIME_SERVICE)
          .setLockAcquiredForMillis(lockAcquiredForMillis)
          .setLockQuitTryingAfterMillis(lockQuitTryingAfterMillis)
          .setLockTryFrequencyMillis(lockTryFrequencyMillis);
      getChangeEntryService().initialize();
      specificInitialization();
    }
  }

  protected void setTransactionStrategy(TransactionStrategy transactionStrategy) {
    this.transactionStrategy = transactionStrategy;
  }

  public TransactionStrategy getTransactionStrategy() {
    return transactionStrategy;
  }

  @Override
  public LockManager getLockManager() {
    if (lockManager == null) {
      throw new MongockException("Internal error: Driver needs to be initialized by the runner");
    }
    return lockManager;
  }

  @Override
  public LockManager getManagerAndAcquireLock() {
    LockManager lockManager = getLockManager();
    lockManager.acquireLockDefault();
    return lockManager;
  }

  @Override
  public boolean isInitialized() {
    return initialized;
  }

  @Override
  public long getLockAcquiredForMillis() {
    return lockAcquiredForMillis;
  }

  @Override
  public long getLockQuitTryingAfterMillis() {
    return lockQuitTryingAfterMillis;
  }

  @Override
  public long getLockTryFrequencyMillis() {
    return lockTryFrequencyMillis;
  }

  @Override
  public String getChangeLogRepositoryName() {
    return changeLogRepositoryName;
  }

  @Override
  public String getLockRepositoryName() {
    return lockRepositoryName;
  }

  @Override
  public boolean isIndexCreation() {
    return indexCreation;
  }

  @Override
  public void setChangeLogRepositoryName(String changeLogRepositoryName) {
    this.changeLogRepositoryName = changeLogRepositoryName;
  }

  @Override
  public void setLockRepositoryName(String lockRepositoryName) {
    this.lockRepositoryName = lockRepositoryName;
  }

  @Override
  public void setIndexCreation(boolean indexCreation) {
    this.indexCreation = indexCreation;
  }

  protected abstract LockRepository getLockRepository();

  protected abstract void specificInitialization();

  @Override
  public void runValidation() throws MongockException {

  }
}
