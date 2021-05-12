import com.fazecast.jSerialComm.SerialPort;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Scanner;

import static java.lang.Thread.sleep;


public class Keypress {
    //im prinzip main
    public static void keypress(SerialPort comPort) throws AWTException, InterruptedException {
        ArrayList<Integer> oldInput        = new ArrayList<>();
        int                oldInputausgabe = 0;

        while (Macropadmain.getPreset() != 0) {
            Scanner s     = new Scanner(comPort.getInputStream());
            Robot   robot = new Robot();

            //waits for input
            Macropadmain.waiting(comPort);

            //saves input in var
            var input = Integer.parseInt(s.nextLine());

            //input in console ausgeben

            if (oldInputausgabe != input) System.out.println();
            oldInputausgabe = input;
            System.out.print(input);


            //TODO its a mess pls fix --> mit button halten pls
            //schaut ob die taste gedrückt oder losgelassen wird

            boolean matched = false;
            if (!oldInput.isEmpty()){
                for (int i = 0; i < oldInput.size(); i++) {
                    if (input == oldInput.get(i)) {
                        int[] key = Presets.getKey(oldInput.get(i));
                        if (key[0] != 0 ) {
//                            sleep(200);
//                            drückt alle tasten
//                            press(key);
                        }
                        oldInput.remove(i);
                        matched = true;
                    }
                }
            }


            if (!matched){
                oldInput.add(input);
                int[] key = Presets.getKey(input);
                //sucht sich den Hexcode aus den presets

                switch (key[ 0 ]) {
                    case 0:
                        Macropadmain.presetswichdialog();
                        break;
                    //cases with a key pressed
                    case KeyEvent.VK_NUM_LOCK:
                        Macropadmain.switchnumlock();
                    default:
                        //drückt alle tasten
                        press(key);
                        break;
                }
            }
        }
    }

    private static void press(int[] key) throws AWTException {
        Robot robot = new Robot();
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
