package com.github.daggerok;

import lombok.extern.slf4j.Slf4j;

import javax.enterprise.inject.se.SeContainerInitializer;
import javax.enterprise.inject.spi.BeanManager;

@Slf4j
public class App {
  public static void main(String[] args) {
    log.info("Ignore this! There are no apps at all!");
    log.info("Just run tests with Maven or Gradle an check output directory for video recordings!");
    SeContainerInitializer.newInstance()
                          .setClassLoader(App.class.getClassLoader())
                          // require <exclude name="org.jboss.weld.**"/> into beans.xml
                          .addPackages(true, App.class, BeanManager.class)
                          .initialize();
  }
}
