import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class LoadHtmlContent {

	public static int numOfTries = 5;
	public static WebDriver driver;

	public static void initDriver() {
		System.setProperty("webdriver.chrome.driver", "//Users/badhwarv/SBC/chromedriver");
		// System.setProperty("webdriver.gecko.driver",
		// "//Users/badhwarv/SBC/geckodriver");
		driver = new ChromeDriver();
		// driver = new HtmlUnitDriver(true);
		// driver = new FirefoxDriver();
	}

	public static WebDriver getWebDriver(String input) throws Exception {
		String key = input;
		if (input.contains("&")) {
			key = key.replaceAll("&", "\\%26");
		}
		if (key.contains("+")) {
			key = key.replaceAll("\\+", "\\%2B");
		}

		driver.get("http://www.google.com/search?q=" + key);
		Thread.sleep(2000);
		driver.findElement(By.linkText("Use Google.com")).click();

		int num = numOfTries;
		while (!driver.getCurrentUrl().contains("google.com") && num > 0) {
			Thread.sleep(2000);
			System.out.println("Sleeping for 2 secs");
			driver.findElement(By.linkText("Use Google.com")).click();
			num--;
		}

		Utils.storeLocalDisk(Utils.fileStorePath + key, driver.getPageSource());
		return driver;
	}

	public static String getHtmlContent(String key) throws Exception {

		driver = getWebDriver(key);
		System.out.println("Application title for key " + key + " is " + driver.getCurrentUrl());
		// String pageSource =
		// ((JavaScriptExecutor)driver).executeScript("return
		// document.documentElement.outerHTML;").toString();
		WebElement element = driver.findElement(By.tagName("html"));
		System.out.println(element.getAttribute("outerHTML"));
		// String contents =
		// (String)((JavascriptExecutor)driver).executeScript("return
		// arguments[0].innerHTML;", element);
		// return driver.getPageSource();

		WebElement ele = driver.findElement(By.xpath("//*"));
		// String javascript = "return arguments[0].innerHTML";
		// String pageSource = (String) ((JavascriptExecutor)
		// driver).executeScript(javascript, element);
		// pageSource = "<html>" + pageSource + "</html>";
		String pageSource = ele.getAttribute("outerHTML");
		System.out.println(pageSource);
		return pageSource;
	}

	public static void closeDriver() {
		driver.close();
	}
}
