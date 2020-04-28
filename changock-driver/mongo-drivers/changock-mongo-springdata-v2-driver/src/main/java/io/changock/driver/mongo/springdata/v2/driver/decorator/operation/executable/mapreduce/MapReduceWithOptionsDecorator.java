package io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.mapreduce;

import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.mapreduce.impl.ExecutableMapReduceDecoratorImpl;
import io.changock.driver.core.lock.guard.invoker.LockGuardInvoker;
import org.springframework.data.mongodb.core.ExecutableMapReduceOperation;
import org.springframework.data.mongodb.core.mapreduce.MapReduceOptions;

public interface MapReduceWithOptionsDecorator<T> extends ExecutableMapReduceOperation.MapReduceWithOptions<T> {

    ExecutableMapReduceOperation.MapReduceWithOptions<T> getImpl();

    LockGuardInvoker getInvoker();

    //TODO implement
    @Override
    default ExecutableMapReduceOperation.ExecutableMapReduce<T> with(MapReduceOptions options) {
        return getInvoker().invoke(() -> new ExecutableMapReduceDecoratorImpl<>(getImpl().with(options), getInvoker()));
    }
}
