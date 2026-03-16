import java.nio.file.*;
public class RealPathCheck {
  public static void main(String[] args) throws Exception {
    Path p = Path.of(args[0]);
    System.out.println(p.toRealPath());
  }
}