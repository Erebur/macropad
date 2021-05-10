import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.security.Key;
import java.util.ArrayList;
import java.util.Scanner;

public class Test {
    public static final int VK_RCONTROL                 = 0xA3;

    public static void main(String[] args) throws AWTException {
       //(KeyEvent.VK_F13)  (KeyEvent.VK_R);}});
        Robot robot = new Robot();
        var k1 = KeyEvent.VK_CONTROL ;
        var k2 = KeyEvent.VK_PAGE_DOWN ;
        robot.keyPress(k1);
        robot.keyPress(k2);

        robot.keyRelease(k1);
        robot.keyRelease(k2);
    }
}
