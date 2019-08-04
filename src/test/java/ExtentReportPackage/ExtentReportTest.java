package ExtentReportPackage;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

public class ExtentReportTest 
{
	public WebDriver driver;
	public ExtentReports extent;
	public ExtentTest extentTest;
	
	//Before all the Test Cases.
	@BeforeTest
	public void setExtent()
	{
		//Telling System Where Exactly Extent Report has to be Generated under Project.
		//Giving Boolean value true >> If Previous ExtentReport.html is there Replace it with New.
		//If we make False, It will not Replace.
		extent = new ExtentReports(System.getProperty("user.dir")+"/ExtentReportsResults/ExtentReport.html", true);
		extent.addSystemInfo("Host Name", "Pavan's Windows System");
		extent.addSystemInfo("User Name", "Pavan KrishnanReddy");
		extent.addSystemInfo("Environment", "Automation Testing");
	}
	
	//Once all the Test Cases are Executed what we want to do.
	//To Close the Connection with Extent Report.
	@AfterTest
	public void endReport()
	{
		extent.flush();
		extent.close();
	}
	
	//To take Screenshot
	//Generating Screenshot as Date Wise.
	public static String getScreenshot(WebDriver driver, String screenshotName) throws IOException
	{
		//We have generated Date now.
		String dateName = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
		TakesScreenshot ts = (TakesScreenshot) driver;
		File source = ts.getScreenshotAs(OutputType.FILE);
		//After execution, you could see a folder "FailedTestsScreenshots"
		//Under Source folder
		String destination = System.getProperty("user.dir") + "/FailedTestsScreenshots/" + screenshotName + dateName+ ".png";
		File finalDestination = new File(destination);
		FileUtils.copyFile(source, finalDestination);
		return destination;
	}
	
	@BeforeMethod
	public void setUp()
	{
		System.setProperty("webdriver.chrome.driver", "./Drivers/chromedriver.exe");
		driver = new ChromeDriver();
		
		driver.manage().window().maximize();
		driver.manage().deleteAllCookies();
			
		driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
		driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
			
		driver.get("https://classic.crmpro.com/index.html");
	}
	
	@Test(priority=1)
	public void freeCRMTitleTest()
	{
		extentTest = extent.startTest("freeCrmTitleTest");
		String title = driver.getTitle();
		System.out.println(title);
		Assert.assertEquals(title,"CRMPRO - CRM software for customer relationship management, sales, and support.123");
	}
	
	@Test(priority=2)
	public void freemCRMLogoTest()
	{
		extentTest = extent.startTest("freemCRMLogoTest");
		boolean b = driver.findElement(By.xpath("//img[@class='img-responsive111']")).isDisplayed();
		Assert.assertTrue(b);
	}
	
	//Only for Failure Test Cases, we need to attach Screenshot in Extent Report.
	@AfterMethod
	public void tearDown(ITestResult result) throws IOException
	{
		if(result.getStatus()==ITestResult.FAILURE)
		{
			extentTest.log(LogStatus.FAIL, "Test Case Failed is "+result.getName()); //To Add Name in Extent Report.
			extentTest.log(LogStatus.FAIL, "Test Case Failed is "+result.getThrowable()); //To Add Errors and Exceptions in Extent Report.
		
			String screenshotPath = ExtentReportTest.getScreenshot(driver, result.getName());
			extentTest.log(LogStatus.FAIL, extentTest.addScreenCapture(screenshotPath)); //To Add Screenshot in Extent Report.
		}
		else if(result.getStatus()==ITestResult.SKIP)
		{
			extentTest.log(LogStatus.SKIP, "Test Case Skipped is " + result.getName());
		}
		else if(result.getStatus()==ITestResult.SUCCESS)
		{
			extentTest.log(LogStatus.PASS, "Test Case Passed is " + result.getName());
		}
		extent.endTest(extentTest); //Ending Test and Ends the Current Test and Prepare to Create HTML Report.
		driver.quit();
	}
}
