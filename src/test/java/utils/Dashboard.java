
package utils;
import java.nio.file.*;
public class Dashboard {
 public static void gen() throws Exception{
  String h="<h1>Run Completed</h1>";
  Files.write(Paths.get("dashboard.html"),h.getBytes());
 }
}
