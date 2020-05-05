package io.changock.driver.mongo.springdata.v3.driver.decorator.operation.executable.mapreduce.impl;

import io.changock.driver.mongo.springdata.v3.driver.decorator.operation.executable.mapreduce.MapReduceWithReduceFunctionDecorator;
import io.changock.driver.api.lock.guard.invoker.LockGuardInvoker;
import org.springframework.data.mongodb.core.ExecutableMapReduceOperation;

public class MapReduceWithReduceFunctionDecoratorImpl<T> implements MapReduceWithReduceFunctionDecorator<T> {

    private final ExecutableMapReduceOperation.MapReduceWithReduceFunction<T> impl;
    private final LockGuardInvoker invoker;

    public MapReduceWithReduceFunctionDecoratorImpl(ExecutableMapReduceOperation.MapReduceWithReduceFunction<T> impl,
                                                    LockGuardInvoker invoker) {
        this.impl = impl;
        this.invoker = invoker;
    }
    @Override
    public ExecutableMapReduceOperation.MapReduceWithReduceFunction<T> getImpl() {
        return impl;
    }

    @Override
    public LockGuardInvoker getInvoker() {
        return invoker;
    }
}
