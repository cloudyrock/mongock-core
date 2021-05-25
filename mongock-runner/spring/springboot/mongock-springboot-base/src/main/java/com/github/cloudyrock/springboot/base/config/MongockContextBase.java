package com.github.cloudyrock.springboot.base.config;

import com.github.cloudyrock.mongock.config.MongockConfiguration;
import com.github.cloudyrock.mongock.driver.api.driver.ConnectionDriver;
import com.github.cloudyrock.springboot.base.MongockApplicationRunner;
import com.github.cloudyrock.springboot.base.MongockInitializingBeanRunner;
import com.github.cloudyrock.springboot.base.builder.migration.MigrationSpringbootBuilderBase;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;


public abstract class MongockContextBase<CONFIG extends MongockConfiguration> {

  @Bean
  @ConditionalOnExpression("'${mongock.runner-type:ApplicationRunner}'.equals('ApplicationRunner')")
  public MongockApplicationRunner<?> applicationRunner(ConnectionDriver connectionDriver,
                                                       CONFIG springConfiguration,
                                                       ApplicationContext springContext,
                                                       ApplicationEventPublisher applicationEventPublisher) {
    return getBuilder(connectionDriver, springConfiguration, springContext, applicationEventPublisher)
        .buildApplicationRunner();
  }

  @Bean
  @ConditionalOnExpression("'${mongock.runner-type:null}'.equals('InitializingBean')")
  public MongockInitializingBeanRunner<?> initializingBeanRunner(ConnectionDriver connectionDriver,
                                                                 CONFIG springConfiguration,
                                                                 ApplicationContext springContext,
                                                                 ApplicationEventPublisher applicationEventPublisher) {
    return getBuilder(connectionDriver, springConfiguration, springContext, applicationEventPublisher)
        .buildInitializingBeanRunner();
  }

  @SuppressWarnings("all")
  public abstract <T extends MigrationSpringbootBuilderBase> T getBuilder(ConnectionDriver connectionDriver,
                                                                          CONFIG springConfiguration,
                                                                          ApplicationContext springContext,
                                                                          ApplicationEventPublisher applicationEventPublisher);
}


