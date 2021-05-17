package com.github.cloudyrock.springboot;


import com.github.cloudyrock.mongock.runner.core.executor.ExecutorFactory;
import com.github.cloudyrock.mongock.runner.core.executor.migration.ExecutorConfiguration;
import com.github.cloudyrock.springboot.base.SpringbootBuilderBase;
import com.github.cloudyrock.springboot.config.MongockSpringConfiguration;

public final class MongockSpringboot {


  //TODO javadoc
  public static Builder builder() {
    return new Builder(new ExecutorFactory<>());
  }

  public static class Builder extends SpringbootBuilderBase<Builder, MongockSpringConfiguration, ExecutorConfiguration> {


    protected Builder(ExecutorFactory<ExecutorConfiguration> executorFactory) {
      super(executorFactory);
    }

    //TODO javadoc
    @SuppressWarnings("unchecked")
    public MongockApplicationRunner buildApplicationRunner() {
      this.runner = getRunner();
      return args -> runner.execute();
    }


    //TODO javadoc
    @SuppressWarnings("unchecked")
    public MongockInitializingBeanRunner buildInitializingBeanRunner() {
      this.runner = getRunner();
      return () -> runner.execute();
    }

    @Override
    protected ExecutorConfiguration getExecutorConfiguration() {
      return new ExecutorConfiguration(trackIgnored, serviceIdentifier);
    }

    @Override
    protected Builder getInstance() {
      return this;
    }
  }


  @FunctionalInterface
  public interface MongockApplicationRunner extends SpringbootBuilderBase.MongockApplicationRunnerBase {
  }

  @FunctionalInterface
  public interface MongockInitializingBeanRunner extends SpringbootBuilderBase.MongockInitializingBeanRunnerBase {
  }

}
