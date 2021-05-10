package com.github.cloudyrock.springboot.v2_4.config;

import com.github.cloudyrock.mongock.driver.api.driver.ConnectionDriver;
import com.github.cloudyrock.springboot.v2_4.base.config.MongockContextBase;
import com.github.cloudyrock.springboot.v2_4.base.config.MongockDriverContextSelector;
import com.github.cloudyrock.springboot.v2_4.MongockSpringbootV2_4;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Import;

@Import(MongockDriverContextSelector.class)
@ConditionalOnExpression("${mongock.enabled:true}")
public class MongockContext extends MongockContextBase<MongockSpringConfiguration> {

  protected MongockSpringbootV2_4.Builder getBuilder(ConnectionDriver connectionDriver,
                                                     MongockSpringConfiguration springConfiguration,
                                                     ApplicationContext springContext,
                                                     ApplicationEventPublisher applicationEventPublisher) {
    return MongockSpringbootV2_4.builder()
        .setDriver(connectionDriver)
        .setConfig(springConfiguration)
        .setSpringContext(springContext)
        .setEventPublisher(applicationEventPublisher);
  }
}


