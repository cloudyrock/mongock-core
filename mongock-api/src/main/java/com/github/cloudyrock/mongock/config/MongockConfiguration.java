package com.github.cloudyrock.mongock.config;

import com.github.cloudyrock.mongock.TransactionStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class MongockConfiguration {

  private static final Logger logger = LoggerFactory.getLogger(MongockConfiguration.class);

  private final static String LEGACY_DEFAULT_CHANGELOG_REPOSITORY_NAME = "mongockChangeLog";
  private final static String LEGACY_DEFAULT_LOCK_REPOSITORY_NAME = "mongockLock";
  private static final String DEPRECATED_PROPERTY_TEMPLATE =
      "\n\n\n*****************************************************************" +
          "\nPROPERTY [{}] DEPRECATED. IT WILL BE REMOVED IN NEXT VERSIONS" +
          "\nPlease use the following properties instead: [{}]" +
          "\n\n\n*****************************************************************";
  public static final long DEFAULT_QUIT_TRYING_AFTER_MILLIS = 3 * 60 * 1000L;

  /**
   * Repository name for changeLogs history
   */
  private String changeLogRepositoryName;

  /**
   * If false, Mongock won't create the necessary index. However it will check that they are already
   * created, failing otherwise. Default true
   */
  private boolean indexCreation = true;

  /**
   * Repository name for locking mechanism
   */
  private String lockRepositoryName;

  /**
   * The period the lock will be reserved once acquired.
   * If it finishes before, it will release it earlier.
   * If the process takes longer thant this period, it will automatically extended.
   * Default 1 minute.
   * Minimum 3 seconds.
   */
  private long lockAcquiredForMillis = 60 * 1000L;

  /**
   * The time after what Mongock will quit trying to acquire the lock, in case it's acquired
   * by another process.
   * Default 3 minutes.
   * Minimum 0, which means won't wait whatsoever.
   */
  private Long lockQuitTryingAfterMillis;

  /**
   * In case the lock is held by another process, it indicates the frequency to try to acquire it.
   * Regardless of this value, the longest Mongock will wait if until the current lock's expiration.
   * Default 1 second.
   * Minimum 500 millis.
   */
  private long lockTryFrequencyMillis = 1000L;

  /**
   * Mongock will throw MongockException if lock can not be obtained. Default true
   */
  private boolean throwExceptionIfCannotObtainLock = true;

  /**
   * If true, will track ignored changeSets in history. Default false
   */
  private boolean trackIgnored = false;

  /**
   * If false, will disable Mongock. Default true
   */
  private boolean enabled = true;

  /**
   * Package paths where the changeLogs are located. mandatory
   */
  private List<String> changeLogsScanPackage;

  /**
   * System version to start with. Default '0'
   */
  private String startSystemVersion = "0";

  /**
   * System version to end with. Default Integer.MAX_VALUE
   */
  private String endSystemVersion = String.valueOf(Integer.MAX_VALUE);

  /**
   * Service identifier.
   */
  private String serviceIdentifier = null;

  /**
   * Map for custom data you want to attach to your migration
   */
  private Map<String, Object> metadata;


  private LegacyMigration legacyMigration = null;

  private TransactionStrategy transactionStrategy;

  @Deprecated
  private Integer maxTries;

  @Deprecated
  private Long maxWaitingForLockMillis;


  public MongockConfiguration() {
    this(TransactionStrategy.MIGRATION);
  }

  public MongockConfiguration(TransactionStrategy transactionStrategy) {
    setChangeLogRepositoryName(getChangeLogRepositoryNameDefault());
    setLockRepositoryName(getLockRepositoryNameDefault());
    this.transactionStrategy = transactionStrategy;
  }


  public long getLockAcquiredForMillis() {
    return lockAcquiredForMillis;
  }

  public void setLockAcquiredForMillis(long lockAcquiredForMillis) {
    this.lockAcquiredForMillis = lockAcquiredForMillis;
  }

  /**
   * temporal due to legacy Lock configuration deprecated.
   * TODO It should be removed as soon as the legacy properties, maxWaitingForLockMillis and maxTries, are removed
   * @return
   */
  public long getLockQuitTryingAfterMillis() {
    if (lockQuitTryingAfterMillis == null) {
      if(maxWaitingForLockMillis != null) {
        return maxWaitingForLockMillis * (this.maxTries != null ? this.maxTries : 3);
      } else {
        return DEFAULT_QUIT_TRYING_AFTER_MILLIS;
      }
    } else {
      return lockQuitTryingAfterMillis;

    }
  }

  public void setLockQuitTryingAfterMillis(long lockQuitTryingAfterMillis) {
    this.lockQuitTryingAfterMillis = lockQuitTryingAfterMillis;
  }

  public long getLockTryFrequencyMillis() {
    return lockTryFrequencyMillis;
  }

  public void setLockTryFrequencyMillis(long lockTryFrequencyMillis) {
    this.lockTryFrequencyMillis = lockTryFrequencyMillis;
  }

  public String getChangeLogRepositoryName() {
    return changeLogRepositoryName;
  }

  public void setChangeLogRepositoryName(String changeLogRepositoryName) {
    this.changeLogRepositoryName = changeLogRepositoryName;
  }

  public String getLockRepositoryName() {
    return lockRepositoryName;
  }

  public void setLockRepositoryName(String lockRepositoryName) {
    this.lockRepositoryName = lockRepositoryName;
  }

  public boolean isIndexCreation() {
    return indexCreation;
  }

  public void setIndexCreation(boolean indexCreation) {
    this.indexCreation = indexCreation;
  }


  public boolean isTrackIgnored() {
    return trackIgnored;
  }

  public void setTrackIgnored(boolean trackIgnored) {
    this.trackIgnored = trackIgnored;
  }

  public boolean isThrowExceptionIfCannotObtainLock() {
    return throwExceptionIfCannotObtainLock;
  }

  public void setThrowExceptionIfCannotObtainLock(boolean throwExceptionIfCannotObtainLock) {
    this.throwExceptionIfCannotObtainLock = throwExceptionIfCannotObtainLock;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public List<String> getChangeLogsScanPackage() {
    return changeLogsScanPackage;
  }

  public void setChangeLogsScanPackage(List<String> changeLogsScanPackage) {
    this.changeLogsScanPackage = changeLogsScanPackage;
  }

  public String getStartSystemVersion() {
    return startSystemVersion;
  }

  public void setStartSystemVersion(String startSystemVersion) {
    this.startSystemVersion = startSystemVersion;
  }

  public String getEndSystemVersion() {
    return endSystemVersion;
  }

  public void setEndSystemVersion(String endSystemVersion) {
    this.endSystemVersion = endSystemVersion;
  }

  public String getServiceIdentifier() {
    return this.serviceIdentifier;
  }

  public void setServiceIdentifier(String serviceIdentifier) {
    this.serviceIdentifier = serviceIdentifier;
  }

  public Map<String, Object> getMetadata() {
    return metadata;
  }

  public void setMetadata(Map<String, Object> metadata) {
    this.metadata = metadata;
  }

  @Deprecated
  public boolean isTransactionEnabled() {
    return transactionStrategy != TransactionStrategy.NONE;
  }


  /**
   * When transaction mechanism is possible, ff false, disable transactions.
   * Default true.
   *
   * Deprecated: Use transaction-strategy instead.
   * This will set transaction-strategy to MIGRATION, in case of community version, or CHANGE_LOG, if professional
   *
   */
  @Deprecated
  public void setTransactionEnabled(boolean transactionEnabled) {
    setTransactionStrategy(TransactionStrategy.MIGRATION);
  }


  public TransactionStrategy getTransactionStrategy() {
    return transactionStrategy;
  }

  public void setTransactionStrategy(TransactionStrategy transactionStrategy) {
    this.transactionStrategy = transactionStrategy;
  }

  public LegacyMigration getLegacyMigration() {
    return legacyMigration;
  }

  public void setLegacyMigration(LegacyMigration legacyMigration) {
    this.legacyMigration = legacyMigration;
  }

  protected String getChangeLogRepositoryNameDefault() {
    return LEGACY_DEFAULT_CHANGELOG_REPOSITORY_NAME;
  }

  protected String getLockRepositoryNameDefault() {
    return LEGACY_DEFAULT_LOCK_REPOSITORY_NAME;
  }

  @Deprecated
  public void setLockAcquiredForMinutes(int lockAcquiredForMinutes) {
    logger.warn(DEPRECATED_PROPERTY_TEMPLATE, "lockAcquiredForMinutes", "lockQuitTryingAfterMillis and lockTryFrequencyMillis");
    this.lockAcquiredForMillis = minutesToMillis(lockAcquiredForMinutes);
  }

  @Deprecated
  public void setMaxWaitingForLockMinutes(int maxWaitingForLockMinutes) {
    logger.warn(DEPRECATED_PROPERTY_TEMPLATE, "maxWaitingForLockMinutes", "lockQuitTryingAfterMillis and lockTryFrequencyMillis");
    this.maxWaitingForLockMillis = minutesToMillis(maxWaitingForLockMinutes);
  }


  @Deprecated
  public void setMaxTries(int maxTries) {
    logger.warn(DEPRECATED_PROPERTY_TEMPLATE, "maxTries", "lockQuitTryingAfterMillis and lockTryFrequencyMillis");
    this.maxTries = maxTries;
  }

  private static long minutesToMillis(int minutes) {
    return minutes * 60 * 1000L;
  }


}
