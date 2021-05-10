import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.security.Key;
import java.util.ArrayList;
import java.util.Scanner;

import static java.lang.Thread.sleep;

public class Test {
    public static void main(String[] args) throws AWTException, InterruptedException {
       //(KeyEvent.VK_F13)  (KeyEvent.VK_R);}});
        Robot robot = new Robot();
        Toolkit toolkit = Toolkit.getDefaultToolkit();
//        toolkit.setLockingKeyState(KeyEvent.VK_NUM_LOCK , false);
        System.out.println(toolkit.getLockingKeyState(KeyEvent.VK_NUM_LOCK));

        robot.keyPress(KeyEvent.VK_NUM_LOCK);
        robot.keyRelease(KeyEvent.VK_NUM_LOCK);

        sleep(3000);

        toolkit.sync();
        System.out.println(toolkit.getLockingKeyState(KeyEvent.VK_NUM_LOCK));
    }
}
