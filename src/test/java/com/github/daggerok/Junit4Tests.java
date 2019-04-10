package com.github.daggerok;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.testcontainers.containers.BrowserWebDriverContainer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byValue;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.testcontainers.containers.BrowserWebDriverContainer.VncRecordingMode.RECORD_ALL;

public class Junit4Tests {

  static final String classPath = System.getProperty("java.class.path");
  static final int gradle = classPath.split(".gradle").length;
  static final int maven = classPath.split(".m2").length;
  static final String outputDir = gradle > maven ? "build" : "target";
  static final Path path = Paths.get(".", outputDir);
  static final Capabilities capabilities = System.currentTimeMillis() % 2 == 0
      ? new ChromeOptions() : new FirefoxOptions();

  @Rule // Important 1
  public BrowserWebDriverContainer browser = new BrowserWebDriverContainer()
      .withRecordingMode(RECORD_ALL, path.toFile())
      .withCapabilities(capabilities);

  @Before
  public void before() { // this one is not needed in case of build-tool output dir...
    if (Files.notExists(path)) path.toFile().mkdirs();
  }

  @Test
  public void main() {
    for (int i = 0; i < 10; i++) {
      should_google_search_and_wait_for("Порошенко педофил?"); // ru
      should_google_search_and_wait_for("is Poroshenko pedofile?"); // en
    }
  }

  private void should_google_search_and_wait_for(String query) {
    // Important 2: create selenide driver from existing one - remote Chrome WebDriver, pointing of docker container:
    WebDriverRunner.setWebDriver(browser.getWebDriver());
    // regular Selenide test:
    Selenide.open("https://google.com?q=" + query);
    $$("form").filterBy(exist).first().shouldBe(visible).submit();
    $(byValue(query)).shouldBe(exist).shouldBe(visible).submit();
    // Warning: at this point if time Selenide will also stops Chrome remote WebDriver too...
    Selenide.close();
  }
}
