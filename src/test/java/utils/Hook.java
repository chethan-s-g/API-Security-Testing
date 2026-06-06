
package utils;
import org.testng.annotations.AfterSuite;
public class Hook {
 @AfterSuite public void run() throws Exception{ Dashboard.gen(); }
}
