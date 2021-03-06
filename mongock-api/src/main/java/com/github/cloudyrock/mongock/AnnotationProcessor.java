package com.github.cloudyrock.mongock;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;

public interface AnnotationProcessor<CHANGESET extends ChangeSetItem> {

  default Collection<Class<? extends Annotation>> getChangeLogAnnotationClass() {
    return Collections.singletonList(ChangeLog.class);
  }

  default boolean isMethodAnnotatedAsChange(Method method) {
    return isChangeSet(method) || isBeforeChangeSets(method) || isAfterChangeSets(method);
  }
  
  default boolean isChangeSet(Method method) {
      return method.isAnnotationPresent(ChangeSet.class);
  }

  default boolean isBeforeChangeSets(Method method) {
      return method.isAnnotationPresent(BeforeChangeSets.class);
  }

  default boolean isAfterChangeSets(Method method) {
      return method.isAnnotationPresent(AfterChangeSets.class);
  }

  default boolean isRollback(Method method) {
    return method.isAnnotationPresent(Rollback.class);
  }

  default String getChangeLogOrder(Class<?> type) {
    return type.getAnnotation(ChangeLog.class).order();
  }

  default boolean isFailFast(Class<?> changeLogClass) {
    return changeLogClass.getAnnotation(ChangeLog.class).failFast();
  }

  /**
   * Returns the metatada associated to a method via a mongock change annotation, which includes
   * : ChangetSet, validation, undo, etc.
   * @param changeSetMethod
   * @return The metadata associated to a change method
   */
  default CHANGESET getChangePerformerItem(Method changeSetMethod) {
    return getChangePerformerItem(changeSetMethod, null);
  }


  CHANGESET getChangePerformerItem(Method changeSetMethod, Method rollbackMethod);

  default String getId(Method method) {
    if (isRollback(method)) {
      Rollback ann = method.getAnnotation(Rollback.class);
      return ann.value();
    } else {
      return getChangePerformerItem(method).getId();
    }
  }
  
  void validateChangeLogClass(Class<?> type);
}
