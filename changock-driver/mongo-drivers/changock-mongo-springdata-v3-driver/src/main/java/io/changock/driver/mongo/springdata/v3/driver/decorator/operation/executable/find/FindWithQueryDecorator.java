package io.changock.driver.mongo.springdata.v3.driver.decorator.operation.executable.find;

import io.changock.driver.mongo.springdata.v3.driver.decorator.operation.executable.find.impl.TerminatingFindDecoratorImpl;
import io.changock.driver.mongo.springdata.v3.driver.decorator.operation.executable.find.impl.TerminatingFindNearDecoratorImpl;
import io.changock.driver.api.lock.guard.decorator.Invokable;
import org.springframework.data.mongodb.core.ExecutableFindOperation;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;

public interface FindWithQueryDecorator<T> extends Invokable, ExecutableFindOperation.FindWithQuery<T>, TerminatingFindDecorator<T> {

  ExecutableFindOperation.FindWithQuery<T> getImpl();


  @Override
  default ExecutableFindOperation.TerminatingFind<T> matching(Query query) {
    return new TerminatingFindDecoratorImpl<>(getInvoker().invoke(()-> getImpl().matching(query)), getInvoker());
  }

  @Override
  default ExecutableFindOperation.TerminatingFind<T> matching(CriteriaDefinition criteria) {
    return new TerminatingFindDecoratorImpl<>(getInvoker().invoke(()-> getImpl().matching(criteria)), getInvoker());
  }

  @Override
  default ExecutableFindOperation.TerminatingFindNear<T> near(NearQuery nearQuery) {
    return new TerminatingFindNearDecoratorImpl<>(getInvoker().invoke(()-> getImpl().near(nearQuery)), getInvoker());
  }
}
