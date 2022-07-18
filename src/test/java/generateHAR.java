import io.github.bonigarcia.wdm.WebDriverManager;
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.proxy.CaptureType;
import org.openqa.selenium.By;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.EnumSet;

public class generateHAR {

    @Test
    public void generateHARFile() throws InterruptedException, IOException {

        //1. Start the proxy on some port
        BrowserMobProxy myProxy=new BrowserMobProxyServer();

        myProxy.start(0);

        //2. Set SSL and HTTP proxy in SeleniumProxy
        Proxy seleniumProxy=new Proxy();
        seleniumProxy.setHttpProxy("localhost:" +myProxy.getPort());
        seleniumProxy.setSslProxy("localhost:" +myProxy.getPort());


        //3. Set captureTypes
        EnumSet<CaptureType> captureTypes=CaptureType.getAllContentCaptureTypes();
        captureTypes.addAll(CaptureType.getCookieCaptureTypes());
        captureTypes.addAll(CaptureType.getHeaderCaptureTypes());
        captureTypes.addAll(CaptureType.getRequestCaptureTypes());
        captureTypes.addAll(CaptureType.getResponseCaptureTypes());

        //4. setHarCaptureTypes with above captureTypes
        myProxy.setHarCaptureTypes(captureTypes);

        //5. HAR name
        myProxy.newHar("MyHAR");

        //6. Start browser and open URL
        WebDriverManager.chromedriver().setup();
        ChromeOptions options=new ChromeOptions();
        options.addArguments("--headless");
        DesiredCapabilities capability =new DesiredCapabilities();
        capability.setCapability(CapabilityType.PROXY, seleniumProxy);
        capability.acceptInsecureCerts();
        capability.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
        capability.setCapability(ChromeOptions.CAPABILITY, options);
        options.merge(capability);
        WebDriver driver=new ChromeDriver(options);



        //Print Driver Capabilities
        System.out.println("Driver Capabilities===> \n" +((RemoteWebDriver)driver).getCapabilities().asMap().toString());

//        driver.get("https://glencore.uat.resource.kryha.dev/user/login/");
//        driver.findElement(By.xpath("//input[@id='login-email']")).sendKeys("device.kcc.glencore.resource@kryha.io");
//        driver.findElement(By.xpath("//input[@id='password']")).sendKeys("KXqwc4gLyV");
//        WebElement clickButton = driver.findElement(By.xpath("//button[@id='login-button']"));
//        clickButton.click();

        driver.get("https://opensource-demo.orangehrmlive.com/index.php/dashboard");
        driver.findElement(By.xpath("//input[@id='txtUsername']")).sendKeys("Admin");
        driver.findElement(By.xpath("//input[@id='txtPassword']")).sendKeys("admin123");
        driver.findElement(By.xpath("//input[@id='btnLogin']")).click();


        Thread.sleep(2000);

        Har har=myProxy.getHar();

        File myHARFile=new File("testHAR1.har");
        har.writeTo(myHARFile);

        System.out.println("==> HAR details has been successfully written in the file.....");

        driver.close();
    }
}
