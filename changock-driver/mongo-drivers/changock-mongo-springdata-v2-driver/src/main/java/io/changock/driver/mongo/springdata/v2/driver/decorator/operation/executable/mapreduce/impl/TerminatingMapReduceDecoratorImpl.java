package io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.mapreduce.impl;

import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.mapreduce.TerminatingMapReduceDecorator;
import io.changock.driver.api.lock.guard.invoker.LockGuardInvoker;
import org.springframework.data.mongodb.core.ExecutableMapReduceOperation;

public class TerminatingMapReduceDecoratorImpl<T> implements TerminatingMapReduceDecorator<T> {

    private final ExecutableMapReduceOperation.TerminatingMapReduce<T> impl;
    private final LockGuardInvoker invoker;

    public TerminatingMapReduceDecoratorImpl(ExecutableMapReduceOperation.TerminatingMapReduce<T> implementation, LockGuardInvoker lockGuardInvoker) {
        this.impl = implementation;
        this.invoker = lockGuardInvoker;
    }
    @Override
    public ExecutableMapReduceOperation.TerminatingMapReduce<T> getImpl() {
        return impl;
    }

    @Override
    public LockGuardInvoker getInvoker() {
        return invoker;
    }
}
