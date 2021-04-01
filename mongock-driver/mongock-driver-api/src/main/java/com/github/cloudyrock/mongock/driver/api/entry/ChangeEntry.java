package com.github.cloudyrock.mongock.driver.api.entry;

import com.github.cloudyrock.mongock.ChangeSetItem;
import com.github.cloudyrock.mongock.utils.StringUtils;
import com.github.cloudyrock.mongock.utils.field.Field;

import java.util.Date;

/**
 * Entry in the changes collection log
 * Type: entity class.
 *
 * @since 27/07/2014
 */
public class ChangeEntry {

  @Field("executionId")
  private final String executionId;

  @Field("changeId")
  private final String changeId;

  @Field("author")
  private final String author;

  @Field("timestamp")
  private final Date timestamp;

  @Field("state")
  private final ChangeState state;

  @Field("changeLogClass")
  private final String changeLogClass;

  @Field("changeSetMethod")
  private final String changeSetMethod;

  @Field("metadata")
  private final Object metadata;

  @Field("executionMillis")
  private final long executionMillis;

  @Field("executionHostname")
  private final String executionHostname;

  public static ChangeEntry createInstance(String executionId, ChangeState state, ChangeSetItem changeSet, long executionMillis, String executionHostname, Object metadata) {
    return new ChangeEntry(
        executionId,
        changeSet.getId(),
        changeSet.getAuthor(),
        new Date(),
        state,
        changeSet.getMethod().getDeclaringClass().getName(),
        changeSet.getMethod().getName(),
        executionMillis,
        executionHostname,
        metadata);
  }

  public ChangeEntry(String executionId,
                     String changeId,
                     String author,
                     Date timestamp,
                     ChangeState state,
                     String changeLogClass,
                     String changeSetMethod,
                     long executionMillis,
                     String executionHostname,
                     Object metadata) {
    this.executionId = executionId;
    this.changeId = changeId;
    this.author = author;
    this.timestamp = new Date(timestamp.getTime());
    this.state = state;
    this.changeLogClass = changeLogClass;
    this.changeSetMethod = changeSetMethod;
    this.executionMillis = executionMillis;
    this.executionHostname = executionHostname;
    this.metadata = metadata;
  }


  public String getExecutionId() {
    return executionId;
  }

  public String getChangeId() {
    return this.changeId;
  }

  public String getAuthor() {
    return this.author;
  }

  public Date getTimestamp() {
    return this.timestamp;
  }

  public ChangeState getState() {
    return state;
  }

  public String getChangeLogClass() {
    return this.changeLogClass;
  }

  public String getChangeSetMethod() {
    return this.changeSetMethod;
  }

  public long getExecutionMillis() {
    return executionMillis;
  }

  public String getExecutionHostname() {
    return executionHostname;
  }

  public Object getMetadata() {
    return metadata;
  }

  @Override
  public String toString() {
    return "ChangeEntry{" +
        "executionId='" + executionId + '\'' +
        ", changeId='" + changeId + '\'' +
        ", author='" + author + '\'' +
        ", timestamp=" + timestamp +
        ", state=" + state +
        ", changeLogClass='" + changeLogClass + '\'' +
        ", changeSetMethod='" + changeSetMethod + '\'' +
        ", metadata=" + metadata +
        ", executionMillis=" + executionMillis +
        '}';
  }

  public String toPrettyString() {
    return "ChangeEntry{" +
        ", \"id\"=\"" + changeId + "\"" +
        ", \"author\"=\"" + author + "\"" +
        ", \"class\"=\"" + StringUtils.getSimpleClassName(changeLogClass) + "\"" +
        ", \"method\"=\"" + changeSetMethod + "\"" +
        '}';
  }
}
