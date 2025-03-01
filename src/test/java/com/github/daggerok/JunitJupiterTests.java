package com.github.daggerok;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testcontainers.containers.BrowserWebDriverContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byValue;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.testcontainers.containers.BrowserWebDriverContainer.VncRecordingMode.RECORD_ALL;

// 1: Add required dependency: org.testcontainers:junit-jupiter
// 2: Add class level annotation:
@Slf4j
@Testcontainers
class JunitJupiterTests {

  static final String classPath = System.getProperty("java.class.path");
  static final int gradle = classPath.split(".gradle").length;
  static final int maven = classPath.split(".m2").length;
  static final String outputDir = gradle > maven ? "build" : "target";
  static final Path path = Paths.get(".", outputDir);
  static final Capabilities capabilities = System.currentTimeMillis() % 2 == 0
      ? new ChromeOptions() : new FirefoxOptions();
  // static final Capabilities capabilities = System.currentTimeMillis() % 2 == 0
  //         ? DesiredCapabilities.chrome() : DesiredCapabilities.firefox();

  @Container // 3: Use @Container instead of @Rule
  static final BrowserWebDriverContainer browser = new BrowserWebDriverContainer()
      .withRecordingMode(RECORD_ALL, path.toFile())
      .withCapabilities(capabilities);

  @BeforeEach
  void before() { // this one is not needed in case of build-tool output dir...
    if (Files.notExists(path)) path.toFile().mkdirs();
  }

  // @Test // Google Captcha is blocking a test...
  void main() {
    should_google_search_and_wait_for("DonaldTrump");
    // WebDriverRunner.closeWebDriver(); // should I do that?
  }

  private void should_google_search_and_wait_for(String query) {
    // // WebDriverRunner.setWebDriver(new org.openqa.selenium.firefox.FirefoxDriver());
    // create selenide driver from existing one - remote Chrome WebDriver, pointing of docker test container:
    log.info("RemoteWebDriver remoteWebDriver = browser.getWebDriver();");
    RemoteWebDriver remoteWebDriver = browser.getWebDriver();
    log.info("WebDriverRunner.setWebDriver(remoteWebDriver);");
    WebDriverRunner.setWebDriver(remoteWebDriver);

    // base project dir
    String baseDir = System.getProperty("user.dir");
    log.info("baseDir: {}", baseDir);

    // regular Selenide test:
    log.info("Selenide.open('https://google.com?q={}')", query);
    Selenide.open("https://google.com?q=" + query);

    // Selenide.screenshot(String.format("%s/target/google-search-and-wait-for-ololo-screenshot-1.png", baseDir));
    Selenide.screenshot("../../../target/google-search-and-wait-for-ololo-screenshot-1.png");

    log.info("$('form[action='/search']').shouldBe(exist).shouldBe(visible).submit();");
    $("form[action='/search']").shouldBe(exist).shouldBe(visible).submit();
    // Selenide.screenshot(String.format("%s/target/google-search-and-wait-for-ololo-screenshot-2.png", baseDir));
    Selenide.screenshot("../../../target/google-search-and-wait-for-ololo-screenshot-2.png");

    // log.info("$(byValue(query)).shouldBe(exist).shouldBe(visible).submit();");
    // $(byValue(query)).shouldBe(exist).shouldBe(visible).submit();
    // // Selenide.screenshot(String.format("%s/target/google-search-and-wait-for-ololo-screenshot-3.png", baseDir));
    // Selenide.screenshot("../../../target/google-search-and-wait-for-ololo-screenshot-3.png");
  }

  @Test
  void should_test_html() {
    // WebDriverRunner.setWebDriver(new org.openqa.selenium.firefox.FirefoxDriver());
    // create selenide driver from existing one - remote Chrome WebDriver, pointing of docker test container:
    log.info("RemoteWebDriver remoteWebDriver = browser.getWebDriver();");
    RemoteWebDriver remoteWebDriver = browser.getWebDriver();
    log.info("WebDriverRunner.setWebDriver(remoteWebDriver);");
    WebDriverRunner.setWebDriver(remoteWebDriver);

    // base project dir
    String baseDir = System.getProperty("user.dir");
    log.info("baseDir: {}", baseDir);

    // Selenide
    log.info("Selenide.open('{}/src/test/resources/test.html')", baseDir);
    Selenide.open(String.format("file://%s/src/test/resources/test.html", baseDir));
    Selenide.screenshot("../../../target/should-test-html-1.png");
  }

  @AfterAll
  static void afterAll() {
    // Warning: at this point if time Selenide will also stops Chrome remote WebDriver too...
    log.info("Selenide.closeWindow();");
    Selenide.closeWindow();
    log.info("Selenide.closeWebDriver();");
    Selenide.closeWebDriver();
  }
}
