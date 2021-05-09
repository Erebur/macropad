import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.security.Key;
import java.util.Scanner;

public class Test {
    public static void main(String[] args) throws AWTException {
        Robot robot = new Robot();

        robot.keyPress(KeyEvent.VK_WINDOWS);
        robot.keyPress(KeyEvent.VK_TAB);
        robot.keyRelease(KeyEvent.VK_WINDOWS);
        robot.keyRelease(KeyEvent.VK_TAB);
    }
}
