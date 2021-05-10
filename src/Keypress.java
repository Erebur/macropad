import com.fazecast.jSerialComm.SerialPort;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Scanner;


public class Keypress {
    //im prinzip main
    public static void keypress(SerialPort comPort) throws AWTException, InterruptedException {
        int oldInput = 0;

        while (Macropadmain.getPreset() != 0) {
            Scanner s     = new Scanner(comPort.getInputStream());
            Robot   robot = new Robot();

            //waits for input
            Macropadmain.waiting(comPort);

            //saves input in var
            var input = s.nextInt();

            //input in console ausgeben

            if (oldInput != input) System.out.println();
            oldInput = input;
            System.out.print(input);

            //sucht sich den Hexcode aus den presets
            int[] key = Presets.getKey(input);

            if (key.length == 1 ){
                //einfach das preset ändern
                switch (key[ 0 ]) {
                    case 0:
                        Macropadmain.presetswichdialog();
                        break;
                    //cases with a key pressed
                    case KeyEvent.VK_NUM_LOCK:
                        Macropadmain.switchnumlock();
                    default:
                        robot.keyPress(key[ 0 ]);
                        robot.keyRelease(key[ 0 ]);
                        break;
                }
            }else {
                //drückt alle tasten
                for (int j : key) {
                    robot.keyPress(j);
                }
                //lässt alle tasten los
                for (int j : key) {
                    robot.keyRelease(j);
                }
            }


        }
    }

}
