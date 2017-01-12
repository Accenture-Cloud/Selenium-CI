package org.accenture.cloud;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test(groups = "promotion")
public class PromotionCalendarTest extends TestBase {

  @BeforeMethod
  public void setUp() {

  }

  @Test(dependsOnGroups = "login")
  public void testNewPromotionCalendar() {
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Promotions")));
    driver.findElement(By.linkText("Promotions")).click();
    driver.findElement(By.name("new")).click();
    driver.findElement(By.cssSelector("#bottomButtonRow > input[name=\"save\"]")).click();
    driver.findElement(By.id("j_id0:theForm:theBlock:theSection:j_id28:PromotionTemplate")).click();
    new Select(driver.findElement(By.id("j_id0:theForm:theBlock:theSection:j_id28:PromotionTemplate")))
        .selectByVisibleText("**Spider*PrmTemplate*New");
    driver.findElement(By.id("j_id0:theForm:theBlock:theSection:j_id35:Date_From")).click();
    driver.findElement(By.xpath("(//td[@onclick='DatePicker.datePicker.selectDate(this);'])[12]")).click();
    driver.findElement(By.id("j_id0:theForm:theBlock:theSection:j_id35:Date_From")).clear();
    driver.findElement(By.id("j_id0:theForm:theBlock:theSection:j_id35:Date_From")).sendKeys("11/10/2016");
    driver.findElement(By.id("j_id0:theForm:theBlock:theSection:j_id39")).click();
    driver.findElement(By.xpath("(//td[@onclick='DatePicker.datePicker.selectDate(this);'])[12]")).click();
    driver.findElement(By.id("j_id0:theForm:theBlock:theSection:j_id39")).clear();
    driver.findElement(By.id("j_id0:theForm:theBlock:theSection:j_id39")).sendKeys("11/10/2016");
    new Select(driver.findElement(By.id("j_id0:theForm:theBlock:theSection:j_id40:Account")))
        .selectByVisibleText("*Kroger Atlanta");
    driver.findElement(By.id("j_id0:theForm:theBlock:theSection:j_id49:0:j_id50")).clear();
    driver.findElement(By.id("j_id0:theForm:theBlock:theSection:j_id49:0:j_id50")).sendKeys("test sel3");
    driver.findElement(By.id("j_id0:theForm:theBlock:theButtons:bottom:btnSave")).click();
  }
}
