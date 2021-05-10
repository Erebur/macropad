import com.fazecast.jSerialComm.SerialPort;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Scanner;


public class Keypress {
    //im prinzip main
    public static void keypress(SerialPort comPort) throws AWTException, InterruptedException {
        int oldInput = 0;
        int oldInputausgabe = 0;

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

            //sucht sich den Hexcode aus den presets
            int[] key = Presets.getKey(input);

            //TODO its a mess pls fix --> mit button halten pls
            if (key.length == 1 ){
                //einfach das preset ändern
                switch (key[ 0 ]) {
                    case 0:
                        if (oldInput != input){
                            Macropadmain.presetswichdialog();
                            oldInput = input;
                        }else{
                            oldInput = 0 ;
                        }
                        break;
                    //cases with a key pressed
                    case KeyEvent.VK_NUM_LOCK:
                        if (oldInput != input){
                            Macropadmain.switchnumlock();
                        }
                    default:
                        if (oldInput != input){
                            robot.keyPress(key[ 0 ]);
                            oldInput = input;
                        }else{
                            robot.keyRelease(key[ 0 ]);
                            oldInput = 0 ;
                        }
                        break;
                }
            }else {
                if (oldInput != input){
                    //drückt alle tasten
                    for (int j : key) {
                        robot.keyPress(j);
                        oldInput = input;
                    }
                }else {
                    //lässt alle tasten los
                    for (int j : key) {
                        robot.keyRelease(j);
                        oldInput = 0 ;
                    }
                }


            }



        }
    }

}
