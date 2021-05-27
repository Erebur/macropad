import java.awt.*;

public class Test {
    public static void main(String[] args) throws InterruptedException, AWTException {
        String test = "ja";
        switch (test) {
            case "ja" -> System.out.println("nö");
            case "test" -> System.out.println("ja");
            default -> throw new IllegalStateException("Unexpected value: " + test);
        }
    }
}
