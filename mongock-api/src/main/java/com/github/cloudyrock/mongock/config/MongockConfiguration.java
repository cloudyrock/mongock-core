package com.github.cloudyrock.mongock.config;

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
   * How long the lock will be hold once acquired in minutes. Default 3
   */
  private long lockAcquiredForMillis = 60 * 1000L;

  /**
   * Max time in minutes to wait for the lock in each try. Default 4
   */
  private Long lockQuitTryingAfterMillis;

  /**
   * Max number of times Mongock will try to acquire the lock. Default 3
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

  /**
   * When transaction mechanism s possible, ff false, disable transactions. Default true.
   */
  private boolean transactionEnabled = true;

  private LegacyMigration legacyMigration = null;

  @Deprecated
  private Integer maxTries;

  @Deprecated
  private Long maxWaitingForLockMillis;


  public MongockConfiguration() {
    setChangeLogRepositoryName(getChangeLogRepositoryNameDefault());
    setLockRepositoryName(getLockRepositoryNameDefault());
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

  public boolean isTransactionEnabled() {
    return transactionEnabled;
  }

  public void setTransactionEnabled(boolean transactionEnabled) {
    this.transactionEnabled = transactionEnabled;
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
