package com.github.cloudyrock.springboot.base.builder.migration;

import com.github.cloudyrock.mongock.config.MongockConfiguration;
import com.github.cloudyrock.mongock.driver.api.driver.ChangeSetDependency;
import com.github.cloudyrock.mongock.driver.api.entry.ChangeEntry;
import com.github.cloudyrock.mongock.runner.core.builder.MigrationBuilderBase;
import com.github.cloudyrock.springboot.base.builder.SpringApplicationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;

//TODO javadoc
public interface MigrationSpringbootBuilderBase<
    SELF extends MigrationSpringbootBuilderBase<SELF, CHANGE_ENTRY, CONFIG>,
    CHANGE_ENTRY extends ChangeEntry,
    CONFIG extends MongockConfiguration>
    extends
    SpringApplicationBean,
    MigrationBuilderBase<SELF, CHANGE_ENTRY, Boolean, CONFIG> {

  //TODO javadoc
  SELF setSpringContext(ApplicationContext springContext);

  //TODO javadoc
  SELF setEventPublisher(ApplicationEventPublisher applicationEventPublisher);

  @Override
  default SELF addDependency(String name, Class<?> type, Object instance) {
    getDependencyManager().addDriverDependency(new ChangeSetDependency(name, type, instance));
    return getInstance();
  }
}
