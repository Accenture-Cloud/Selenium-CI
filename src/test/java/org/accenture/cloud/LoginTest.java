package org.accenture.cloud;

import org.openqa.selenium.By;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test(groups="login")
public class LoginTest extends TestBase {

  @BeforeMethod
  public void setUp() {
	
  }

  @Test
  public void testLogin() {
	driver.get(baseUrl + "/?ec=302&startURL=%2Fhome%2Fhome.jsp");
    driver.findElement(By.id("username")).clear();
    driver.findElement(By.id("username")).sendKeys(user);
    driver.findElement(By.id("password")).clear();
    driver.findElement(By.id("password")).sendKeys(password);
    driver.findElement(By.id("Login")).click();
  }
}
