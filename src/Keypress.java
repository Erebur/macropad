import com.fazecast.jSerialComm.SerialPort;

import javax.swing.*;
import java.awt.*;
import java.util.Scanner;

import static java.lang.Thread.sleep;

public class Keypress {
    //im prinzip main
    public static void keypress(SerialPort comPort) throws AWTException, InterruptedException {
        int oldinput = 0;

        label:
        while (Macropadmain.getPreset() != 0) {
            Scanner s     = new Scanner(comPort.getInputStream());
            Robot   robot = new Robot();

            //waits for input
            Macropadmain.waiting(comPort);

            //saves input in int
            int input = s.nextInt();

            //input in console ausgeben

            if (oldinput != input) System.out.println();
            oldinput = input;
            System.out.print(input);

            //sucht sich den Hexcode aus den presets
            int[] key = Presets.getKey(input);

            if (key.length == 1 ){
                switch (key[0]) {
                    //einfach das preset ändern
                    case 0 -> Macropadmain.presetswichdialog();
                    //error ist aufgetreten und preset wird geändert
                    case 1 -> {
                        Macropadmain.showerrordialog(input);
                        Macropadmain.presetswichdialog();
                    }
                    //programm wird beendet
                    case 3 -> {
                        break label;
                    }
                    // music dialog fals preset music ausgewählt wurde TODO Dialog
                    case 2 -> {
                        JOptionPane.showMessageDialog(null,"wip");
                    }
                    default -> {
                        robot.keyPress(key[0]);
                        robot.keyRelease(key[0]);
                    }

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
