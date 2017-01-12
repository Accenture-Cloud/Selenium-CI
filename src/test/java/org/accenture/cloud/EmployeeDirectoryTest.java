package org.accenture.cloud;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.startsWith;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test(groups = "employee")
public class EmployeeDirectoryTest extends TestBase {

  @BeforeMethod
  public void setUp() {

  }

  @Test
  public void testEmployeeCanBeDisplayedWithDirects() throws Exception {
    //driver.get(baseUrl + ".directory.EmployeeDirectory");
	driver.get("and-activity://io.selendroid.directory.EmployeeDirectory");

    // Switch to the web view context
    driver.switchTo().window("WEBVIEW");

    String vpOfEngineering = "John Williams";
    driver.findElement(By.tagName("input")).sendKeys(vpOfEngineering);
    driver.findElement(By.partialLinkText(vpOfEngineering)).click();
    Assert.assertEquals(driver.getCurrentUrl(), "file:///android_asset/www/index.html#employees/4");

    // Verify Manager
    Assert.assertThat(driver.findElements(By.tagName("li")).get(0).getText(), endsWith("James King"));

    // Verify number of direct reports
    WebElement directs = driver.findElements(By.tagName("li")).get(1);
    Assert.assertThat(directs.getText(), endsWith("3"));
    directs.click();
    Assert.assertEquals(driver.getCurrentUrl(), "file:///android_asset/www/index.html#employees/4/reports");

    // Verify directs by name
    Assert.assertThat(driver.findElements(By.tagName("li")).get(0).getText(), startsWith("Paul Jones"));
    Assert.assertThat(driver.findElements(By.tagName("li")).get(1).getText(), startsWith("Paula Gates"));
    Assert.assertThat(driver.findElements(By.tagName("li")).get(2).getText(), startsWith("Steven Wells"));

    driver.navigate().back();

    Assert.assertEquals(driver.getCurrentUrl(), "file:///android_asset/www/index.html#employees/4");
  }
}
