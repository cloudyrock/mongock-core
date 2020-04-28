package io.changock.driver.mongo.v3.core.decorator.impl;

import com.mongodb.client.MongoCursor;

import io.changock.driver.core.lock.guard.invoker.LockGuardInvoker;
import io.changock.driver.mongo.v3.core.decorator.MongoCursorDecorator;

public class MongoCursorDecoratorImpl<T> implements MongoCursorDecorator<T> {

  private final MongoCursor<T> impl;
  private final LockGuardInvoker checker;

  public MongoCursorDecoratorImpl(MongoCursor<T> implementation, LockGuardInvoker lockerCheckInvoker) {
    this.impl = implementation;
    this.checker = lockerCheckInvoker;
  }

  @Override
  public MongoCursor<T> getImpl() {
    return impl;
  }

  @Override
  public LockGuardInvoker getInvoker() {
    return checker;
  }
}
