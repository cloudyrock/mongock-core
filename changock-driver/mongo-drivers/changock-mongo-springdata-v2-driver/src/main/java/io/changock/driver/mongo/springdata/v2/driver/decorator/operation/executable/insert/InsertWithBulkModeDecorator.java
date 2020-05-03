package io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.insert;

import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.insert.impl.TerminatingBulkInsertDecoratorImpl;
import io.changock.driver.api.lock.guard.decorator.Invokable;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.ExecutableInsertOperation;

public interface InsertWithBulkModeDecorator<T> extends Invokable, ExecutableInsertOperation.InsertWithBulkMode<T>, TerminatingInsertDecorator<T> {

  ExecutableInsertOperation.InsertWithBulkMode<T> getImpl();

  @Override
  default ExecutableInsertOperation.TerminatingBulkInsert<T> withBulkMode(BulkOperations.BulkMode bulkMode) {
    return new TerminatingBulkInsertDecoratorImpl<>(getInvoker().invoke(()-> getImpl().withBulkMode(bulkMode)), getInvoker());
  }
}
