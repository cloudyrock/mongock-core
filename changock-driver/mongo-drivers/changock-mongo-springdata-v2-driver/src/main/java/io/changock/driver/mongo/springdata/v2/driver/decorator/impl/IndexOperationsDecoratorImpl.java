package io.changock.driver.mongo.springdata.v2.driver.decorator.impl;

import io.changock.driver.mongo.springdata.v2.driver.decorator.IndexOperationsDecorator;
import io.changock.driver.core.lock.guard.invoker.LockGuardInvoker;
import org.springframework.data.mongodb.core.index.IndexOperations;

public class IndexOperationsDecoratorImpl implements IndexOperationsDecorator {

    private final IndexOperations impl;
    private final LockGuardInvoker invoker;

    public IndexOperationsDecoratorImpl(IndexOperations impl, LockGuardInvoker invoker) {
        this.impl = impl;
        this.invoker = invoker;
    }
    @Override
    public IndexOperations getImpl() {
        return impl;
    }

    @Override
    public LockGuardInvoker getInvoker() {
        return invoker;
    }
}