package pages;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utilities.DriverFactory;

import java.time.Duration;
import java.util.List;

public class BasePageObject {

    protected WebDriver driver;
    protected Logger log;

    public BasePageObject(WebDriver driver, Logger log) {
        this.driver = DriverFactory.getDriver();
        this.log = log;
    }

    /** Open page with given URL */
    protected void openUrl(String url) {
        driver.get(url);
    }

    /** Safe find */
    protected WebElement find(By locator) {
        return driver.findElement(locator);
    }

    /** Safe click with retry */
    protected void click(By locator) {
        int attempts = 0;
        while (attempts < 2) {
            try {
                WebElement element = waitForVisibility(locator);
                element.click();
                return;
            } catch (StaleElementReferenceException e) {
                log.warn("Stale element on click(), retrying...");
            }
            attempts++;
        }
        throw new StaleElementReferenceException("Element remained stale after retries: " + locator);
    }

    /** Safe type with retry */
    protected void type(String text, By locator) {
        int attempts = 0;
        while (attempts < 2) {
            try {
                WebElement element = waitForVisibility(locator);
                element.clear();
                element.sendKeys(text);
                return;
            } catch (StaleElementReferenceException e) {
                log.warn("Stale element on type(), retrying...");
            }
            attempts++;
        }
        throw new StaleElementReferenceException("Element remained stale after retries: " + locator);
    }

    /** Wait for visibility */
    protected WebElement waitForVisibility(By locator) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /** Find all elements */
    protected List<WebElement> findAll(By locator) {
        return driver.findElements(locator);
    }

    /** Switch to alert */
    protected Alert switchToAlert() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.alertIsPresent());
        return driver.switchTo().alert();
    }

    /** Switch to frame */
    protected void switchToFrame(By frameLocator) {
        WebElement frame = waitForVisibility(frameLocator);
        driver.switchTo().frame(frame);
    }

    /** Press key */
    protected void pressKey(By locator, Keys key) {
        WebElement element = waitForVisibility(locator);
        element.sendKeys(key);
    }

    /** Press key with Actions */
    public void pressKeyWithActions(Keys key) {
        Actions action = new Actions(driver);
        action.sendKeys(key).perform();
    }

    /** Scroll */
    public void scrollToBottom() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
    }

    /** Drag and drop */
    protected void performDragAndDrop(By from, By to) {
        WebElement source = waitForVisibility(from);
        WebElement target = waitForVisibility(to);
        Actions action = new Actions(driver);
        action.dragAndDrop(source, target).perform();
    }

    /** Hover */
    protected void hoverOverElement(By locator) {
        WebElement element = waitForVisibility(locator);
        Actions action = new Actions(driver);
        action.moveToElement(element).perform();
    }

    /** Cookies */
    public void setCookie(Cookie ck) {
        driver.manage().addCookie(ck);
    }

    public String getCookie(String name) {
        return driver.manage().getCookieNamed(name).getValue();
    }
}
