package com.github.cloudyrock.mongock.driver.api.lock.guard.proxy;


import com.github.cloudyrock.mongock.driver.api.lock.LockManager;
import com.github.cloudyrock.mongock.driver.api.lock.guard.proxy.util.ContentHandlerFactoryImpl;
import com.github.cloudyrock.mongock.driver.api.lock.guard.proxy.util.FinalClass;
import com.github.cloudyrock.mongock.driver.api.lock.guard.proxy.util.InterfaceType;
import com.github.cloudyrock.mongock.driver.api.lock.guard.proxy.util.InterfaceTypeImpl;
import com.github.cloudyrock.mongock.driver.api.lock.guard.proxy.util.InterfaceTypeImplNonLockGuarded;
import com.github.cloudyrock.mongock.driver.api.lock.guard.proxy.util.SomeClass;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.Serializable;
import java.net.ContentHandlerFactory;
import java.util.ArrayList;
import java.util.List;

import static com.github.cloudyrock.mongock.util.test.ReflectionUtils.getImplementationFromLockGuardProxy;
import static com.github.cloudyrock.mongock.util.test.ReflectionUtils.isProxy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class LockGuardProxyFactoryTest {

  private LockManager lockManager;
  private LockGuardProxyFactory lockGuardProxyFactory;

  @Before
  public void before() {
    lockManager = Mockito.mock(LockManager.class);
    lockGuardProxyFactory = new LockGuardProxyFactory(lockManager);
  }

  private Object getRawProxy(Object o, Class<?> interfaceType) {
    return lockGuardProxyFactory.getRawProxy(o, interfaceType);
  }

  @Test
  public void shouldNotReturnProxy_IfInterfaceTypePackageIsJava() {
    assertFalse(isProxy(getRawProxy(new ArrayList<>(), List.class)));
    assertFalse(isProxy(getRawProxy(new ContentHandlerFactoryImpl(), ContentHandlerFactory.class)));
  }

  @Test
  public void shouldNotReturnProxy_IfInterfaceTypeisJavaNet() {
    lockGuardProxyFactory = new LockGuardProxyFactory(lockManager, InterfaceType.class.getPackage().getName().substring(0, 12));
    assertFalse(isProxy(getRawProxy(new InterfaceTypeImpl(), InterfaceType.class)));
  }


  @Test
  public void shouldNotReturnProxyForBasicCollection() {
    assertFalse(isProxy(getRawProxy(new ArrayList<>(), List.class)));
  }

  @Test
  public void shouldReturnProxy() {
    assertTrue(isProxy(getRawProxy(new InterfaceTypeImpl(), InterfaceType.class)));
  }

  @Test
  public void shouldNotReturnProxy_ifImplClassNonLockGuarded() {
    assertFalse(isProxy(getRawProxy(new InterfaceTypeImplNonLockGuarded(), InterfaceType.class)));
  }

  //failing in local but not in CI
  @Test
  public void shouldReturnProxyWithRightImplementation() {
    assertEquals(SomeClass.class, getImplementationFromLockGuardProxy(getRawProxy(new SomeClass(), SomeClass.class)).getClass());
  }

  @Test
  public void ShouldReturnNull_ifTargetIsNull() {
    assertNull(getRawProxy(null, InterfaceTypeImpl.class));
  }

  @Test
  public void ShouldReturnProxy_ifTargetNotInterface() {
    assertTrue(isProxy(getRawProxy(new FinalClass(), InterfaceTypeImpl.class)));
  }

  @Test
  public void ShouldNotReturnProxy_ifTargetIsFinal() {
    assertFalse(isProxy(getRawProxy(new FinalClass(), FinalClass.class)));
  }

  @Test
  public void ShouldNotReturnProxy_ifPrimitive() {
    assertFalse(isProxy(getRawProxy(1, Comparable.class)));
  }

  @Test
  public void ShouldNotReturnProxy_ifPrimitiveWrapper() {
    assertFalse(isProxy(getRawProxy(new Integer(1), Comparable.class)));
  }

  @Test
  public void ShouldNotReturnProxy_ifString() {
    assertFalse(isProxy(getRawProxy("anyString", Serializable.class)));
  }

  @Test
  public void ShouldNotReturnProxy_ifClass() {
    assertFalse(isProxy(getRawProxy(InterfaceTypeImpl.class, Serializable.class)));
  }

}
