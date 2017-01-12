package org.accenture.cloud;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.accenture.cloud.util.PropertyLoader;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.edge.EdgeDriverService;
import org.openqa.selenium.firefox.GeckoDriverService;
import org.openqa.selenium.ie.InternetExplorerDriverService;
import org.openqa.selenium.remote.service.DriverService;
import org.openqa.selenium.safari.SafariDriverService;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.winium.StoreAppsOptions;
import org.openqa.selenium.winium.WiniumDriver;
import org.openqa.selenium.winium.WiniumDriverService;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.client.uiamodels.impl.RemoteIOSDriver;
import org.uiautomation.ios.server.IOSServer;
import org.uiautomation.ios.server.IOSServerConfiguration;

import io.selendroid.client.SelendroidDriver;
import io.selendroid.standalone.SelendroidConfiguration;
import io.selendroid.standalone.SelendroidLauncher;
import ru.stqa.selenium.factory.WebDriverPool;

/**
 * Base class for TestNG-based test classes
 */
public class TestBase {

  protected static String gridHubUrl;
  protected static String baseUrl;
  protected static String user;
  protected static String password;
  protected static Capabilities capabilities;

  protected static DriverService service;
  protected static WebDriver driver;
  protected static WebDriverWait wait;
  protected static SelendroidLauncher androidService;
  protected static IOSServer iOSService;
  protected StringBuffer verificationErrors = new StringBuffer();

  @BeforeSuite
  public void initTestSuite() throws IOException {
    baseUrl = PropertyLoader.loadProperty("site.url");
    user = PropertyLoader.loadProperty("site.user");
    password = PropertyLoader.loadProperty("site.password");
    gridHubUrl = PropertyLoader.loadProperty("grid.url");
    if ("".equals(gridHubUrl)) {
      gridHubUrl = null;
    }
    capabilities = PropertyLoader.loadCapabilities();
  }

  @BeforeTest
  public static void createAndStartService() throws Exception {
    if (capabilities.getCapability("browserName").toString().equalsIgnoreCase("android")) {
      createAndStartAndroidDriver();

    } else if (capabilities.getCapability("browserName").toString().equalsIgnoreCase("ios")) {
      createAndStartIOSDriver();

    } else if (capabilities.getCapability("browserName").toString().equalsIgnoreCase("windows")) {
      createAndStartWiniumDriver();

    } else {
      createAndStartWebDriver();
    }

    driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
    wait = new WebDriverWait(driver, 30);
  }

  public static void createAndStartWebDriver() throws IOException {
    if (capabilities.getCapability("browserName").toString().equalsIgnoreCase("firefox")) {
      service = new GeckoDriverService.Builder()
          .usingDriverExecutable(new File(capabilities.getCapability("browserBin").toString()))
          .usingAnyFreePort().build();
      System.setProperty("webdriver.gecko.driver", capabilities.getCapability("browserBin").toString());

    } else if (capabilities.getCapability("browserName").toString().equalsIgnoreCase("chrome")) {
      service = new ChromeDriverService.Builder()
          .usingDriverExecutable(new File(capabilities.getCapability("browserBin").toString()))
          .usingAnyFreePort().build();
      System.setProperty("webdriver.chrome.driver", capabilities.getCapability("browserBin").toString());

    } else if (capabilities.getCapability("browserName").toString().equalsIgnoreCase("MicrosoftEdge")) {
      service = new EdgeDriverService.Builder()
          .usingDriverExecutable(new File(capabilities.getCapability("browserBin").toString()))
          .usingAnyFreePort().build();
      System.setProperty("webdriver.edge.driver", capabilities.getCapability("browserBin").toString());

    } else if (capabilities.getCapability("browserName").toString().equalsIgnoreCase("internet explorer")) {
      service = new InternetExplorerDriverService.Builder()
          .usingDriverExecutable(new File(capabilities.getCapability("browserBin").toString()))
          .usingAnyFreePort().build();
      System.setProperty("webdriver.ie.driver", capabilities.getCapability("browserBin").toString());

    } else if (capabilities.getCapability("browserName").toString().equalsIgnoreCase("safari")) {
      service = new SafariDriverService.Builder()
          .usingDriverExecutable(new File(capabilities.getCapability("browserBin").toString()))
          .usingAnyFreePort().build();
    }

    service.start();

    // InitWebDriver
    driver = WebDriverPool.DEFAULT.getDriver(gridHubUrl, capabilities);
  }

  public static void createAndStartAndroidDriver() throws Exception {
    SelendroidConfiguration config = new SelendroidConfiguration();
    config.addSupportedApp(capabilities.getCapability("supportedApp").toString());
    config.setSelendroidServerPort(
        Integer.valueOf(capabilities.getCapability("androidServerPort").toString()).intValue());

    androidService = new SelendroidLauncher(config);
    androidService.launchSelendroid();

    // InitAndroidDriver
    driver = new SelendroidDriver(capabilities);
  }

  public static void createAndStartIOSDriver() throws Exception {
    IOSServerConfiguration config = new IOSServerConfiguration();
    config.addSupportedApp(new File(capabilities.getCapability("supportedApp").toString()).getAbsolutePath());
    config.setPort(Integer.valueOf(capabilities.getCapability("iosServerPort").toString()).intValue());

    iOSService = new IOSServer(config);
    iOSService.start();

    // InitIOSDriver
    driver = new RemoteIOSDriver(new URL(gridHubUrl), (IOSCapabilities) capabilities);
  }

  public static void createAndStartWiniumDriver() {
    StoreAppsOptions options = new StoreAppsOptions();
    options.setApplicationPath(capabilities.getCapability("supportedApp").toString());

    service = new WiniumDriverService.Builder()
        .usingDriverExecutable(new File(capabilities.getCapability("browserBin").toString())).usingAnyFreePort()
        .buildStoreAppsService();
    // System.setProperty("webdriver.winium.driver.desktop",

    // InitWindowsDriver
    driver = new WiniumDriver((WiniumDriverService) service, options);
  }

  @AfterMethod
  public void tearDown() {
    String verificationErrorString = verificationErrors.toString();
    if (!"".equals(verificationErrorString)) {
      Assert.fail(verificationErrorString);
    }
  }

  @AfterTest
  public static void createAndStopService() throws Exception {
    if (capabilities.getCapability("browserName").toString().equalsIgnoreCase("android")) {
      if (androidService != null) {
        androidService.stopSelendroid();
        androidService = null;
      }

    } else if (capabilities.getCapability("browserName").toString().equalsIgnoreCase("ios")) {
      if (iOSService != null) {
        iOSService.stop();
        iOSService = null;
      }

    } else {
      if (service != null) {
        service.stop();
        service = null;
      }
    }

    // QuitDriver
    if (driver != null) {
      driver.quit();
      driver = null;
    }

    if (capabilities.getCapability("browserName").toString().equalsIgnoreCase("internet explorer")) {
      Runtime.getRuntime().exec("taskkill /F /IM iexplore.exe");
    }
  }

  @AfterSuite(alwaysRun = true)
  public void shutDown() {
    WebDriverPool.DEFAULT.dismissAll();
  }

  public static void getScreenshot(ITestResult testResult) {
    File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
    String screenshotName = capabilities.getCapability("browserName").toString().toUpperCase() + "-"
        + testResult.getTestClass().getRealClass().getSimpleName() + ".png";
    String screenshotPath = "target/surefire-reports/Tests/" + screenshotName;
    File screenshotFile = new File(screenshotPath);

    try {
      FileUtils.copyFile(scrFile, screenshotFile);
      Reporter.log("<a href=" + screenshotFile.getAbsolutePath() + " target='_blank'>" + screenshotName + "</a>");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
