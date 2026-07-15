        package utilities;

        import org.openqa.selenium.WebDriver;
        import org.openqa.selenium.chrome.ChromeDriver;
        import org.openqa.selenium.chrome.ChromeOptions;
        import org.openqa.selenium.edge.EdgeDriver;
        import org.openqa.selenium.edge.EdgeOptions;
        import org.openqa.selenium.firefox.FirefoxDriver;
        import org.openqa.selenium.firefox.FirefoxOptions;
        import org.testng.annotations.Optional;

        import java.util.HashMap;
        import java.util.Map;

        public final class DriverFactory {
            //private static WebDriver driver;
            private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();

            private DriverFactory() {
                //prevent instantiation
            }

            public static WebDriver getDriver() {
                return driver.get();
            }

            public static void setDriver(WebDriver driverInstance) {
                driver.set(driverInstance);
            }

            public static WebDriver createDriver(@Optional String browserParam, @Optional String chromeProfile, @Optional String deviceName) {
                WebDriver d;

                if (chromeProfile != null) {
                    d = createChromeWithProfile(chromeProfile);
                } else if (deviceName != null) {
                    d = createChromeWithMobileEmulation(deviceName);
                } else {
                    //String browser = ConfigReader.get("browser");

                    String browser = (browserParam != null) ? browserParam : ConfigReader.get("browser");

                    switch (browser.toLowerCase()) {
                        case "chrome":
                            ChromeOptions chromeOptions = new ChromeOptions();
                            chromeOptions.addArguments("start-maximized");
                            d = new ChromeDriver(chromeOptions);
                            break;

                        case "edge":
                            EdgeOptions edgeOptions = new EdgeOptions();
                            edgeOptions.addArguments("start-maximized");
                            d = new EdgeDriver(edgeOptions);
                            break;

                        case "firefox":
                            FirefoxOptions firefoxOptions = new FirefoxOptions();
                            firefoxOptions.addArguments("--width=1920");
                            firefoxOptions.addArguments("--height=1080");
                            d = new FirefoxDriver(firefoxOptions);
                            break;

                        case "chrome-headless":
                            ChromeOptions options = new ChromeOptions();
                            options.addArguments("--headless=new");
                            d = new ChromeDriver(options);
                            break;

                        default:
                            throw new RuntimeException("Invalid browser in config.properties: " + browser);

                        /*default:
                            ChromeOptions chromeOptionsDefault = new ChromeOptions();
                            chromeOptionsDefault.addArguments("start-maximized");
                            d = new ChromeDriver(chromeOptionsDefault);*/
                    }
                }
                driver.set(d);
                return driver.get();
            }

            public static WebDriver createChromeWithProfile(String profile) {
                ChromeOptions chromeOptions = new ChromeOptions();
                // Build absolute path
                String absolutePath = new java.io.File("src/main/resources/Profiles/" + profile).getAbsolutePath();
                chromeOptions.addArguments("user-data-dir=" + absolutePath);
                chromeOptions.addArguments("--profile-directory=" + profile);
                System.out.println("Using Chrome profile at: " + absolutePath);
                return new ChromeDriver(chromeOptions);
            }

            public static WebDriver createChromeWithMobileEmulation(String deviceName) {
                Map<String, String> mobileEmulation = new HashMap<>();
                mobileEmulation.put("deviceName", deviceName);
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.setExperimentalOption("mobileEmulation", mobileEmulation);
                return new ChromeDriver(chromeOptions);
            }

            public static void quitDriver() {
                if (driver != null && driver.get() != null) {
                    driver.get().quit();
                    driver.remove();
                }
            }
        }
