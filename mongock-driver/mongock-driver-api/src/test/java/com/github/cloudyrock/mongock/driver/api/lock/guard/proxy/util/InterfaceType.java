package com.github.cloudyrock.mongock.driver.api.lock.guard.proxy.util;

import com.github.cloudyrock.mongock.NonLockGuarded;
import com.github.cloudyrock.mongock.NonLockGuardedType;

public interface InterfaceType {

  default String getString() {
    return "simpleValue";
  }

  default long getPrimitive() {
    return 1L;
  }

  default Integer getPrimitiveWrapper() {
    return 1;
  }

  default Class<?> getClassType() {
    return Long.class;
  }

  default void voidMethod() {
  }

  default NontInterfacedClass getNontInterfacedClass() {
    return new NontInterfacedClass();
  }

  @NonLockGuarded
  default void callMethodNoLockGuarded() {
  }

  default InterfaceType getGuardedImpl() {
    return new InterfaceTypeImpl();
  }

  default InterfaceType getNonGuardedImpl() {
    return new InterfaceTypeImplNonLockGuarded();
  }

  @NonLockGuarded
  default InterfaceType getGuardedImplWithAnnotationDefault() {
    return new InterfaceTypeImpl();
  }

  @NonLockGuarded(NonLockGuardedType.METHOD)
  default InterfaceType getGuardedImplWithAnnotationMethod() {
    return new InterfaceTypeImpl();
  }

  @NonLockGuarded(NonLockGuardedType.METHOD)
  default InterfaceType getNonGuardedImplWithAnnotationMethod() {
    return new InterfaceTypeImplNonLockGuarded();
  }

  @NonLockGuarded(NonLockGuardedType.NONE)
  default InterfaceType getGuardedImplWithAnnotationNone() {
    return new InterfaceTypeImpl();
  }


}
