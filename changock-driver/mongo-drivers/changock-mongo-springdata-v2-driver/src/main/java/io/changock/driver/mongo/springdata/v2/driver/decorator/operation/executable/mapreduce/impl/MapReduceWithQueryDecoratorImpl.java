package io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.mapreduce.impl;

import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.mapreduce.MapReduceWithQueryDecorator;
import io.changock.driver.core.lock.guard.invoker.LockGuardInvoker;
import org.springframework.data.mongodb.core.ExecutableMapReduceOperation;

public class MapReduceWithQueryDecoratorImpl<T> implements MapReduceWithQueryDecorator<T> {

    private final LockGuardInvoker invoker;
    private final ExecutableMapReduceOperation.MapReduceWithQuery<T> impl;

    public MapReduceWithQueryDecoratorImpl(ExecutableMapReduceOperation.MapReduceWithQuery<T> impl, LockGuardInvoker lockGuardInvoker) {
        this.impl = impl;
        this.invoker = lockGuardInvoker;
    }

    @Override
    public ExecutableMapReduceOperation.MapReduceWithQuery<T> getImpl() {
        return impl;
    }

    @Override
    public LockGuardInvoker getInvoker() {
        return invoker;
    }
}
