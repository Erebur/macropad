import com.fazecast.jSerialComm.SerialPort;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Scanner;


public class Keypress {
    //im prinzip main
    public static void keypress(SerialPort comPort) throws AWTException, InterruptedException {
        ArrayList<Integer> oldInput        = new ArrayList<>();
        int                oldInputAusgabe = 0;

        while (Macropadmain.getPreset() != 0) {
            Scanner s     = new Scanner(comPort.getInputStream());

            //waits for input
            Macropadmain.waiting(comPort);

            //saves input in var
            var input = aufzahltesten(s);

            //input in console ausgeben

            //noinspection ConstantConditions
            if (true) {
                if (oldInputAusgabe != input) System.out.println();
                oldInputAusgabe = input;
                System.out.print(input);
            }

            //schaut ob die taste gedrückt oder losgelassen wird

            boolean matched = false;
            if (!oldInput.isEmpty()){
                for (int i = 0; i < oldInput.size(); i++) {
                    if (input == oldInput.get(i)) {
//                        int[] key = Presets.getKey(oldInput.get(i));
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

    private static int aufzahltesten(Scanner s) {
        String eingabe = s.nextLine() ;
        int    input = 0 ;
        //da es manchmal(Random lol) falsche eingaben gibt braucht man  fehlerkorrektur

        for (int i = 0; i < eingabe.length(); i++) {
            try {
                eingabe = eingabe.substring(0, eingabe.length() - i);
                input   = Integer.parseInt(eingabe);
                break;
            }catch (Exception ignored){
            }

        }
        return input;
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
