package com.github.cloudyrock.mongock.runner.core.executor;

import com.github.cloudyrock.mongock.exception.MongockException;

public interface MongockRunner<T> {

  boolean isExecutionInProgress();

  boolean isEnabled();

  void execute() throws MongockException;
}
