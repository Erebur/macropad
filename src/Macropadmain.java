import com.fazecast.jSerialComm.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Scanner;

import static java.lang.Thread.sleep;

@SuppressWarnings("BusyWait")
public class Macropadmain {

    private static int preset = 1;

    public static void main(String[] args) throws InterruptedException {

        while (preset != 0) {
            //FileReader fr = new FileReader("config");

            SerialPort comPort = null;
            //serial port reader
            try {
                System.out.println("Started");
                //reads the second (first real) open port
                comPort = SerialPort.getCommPorts()[ 1 ];
                comPort.openPort();

                //finds and presses keys
                keypress(comPort);


            } catch (Throwable e) {
                System.out.println(e);
                System.out.println("a error occurred restarting with 10sec delay ");
                if (comPort != null && comPort.isOpen()) {
                    comPort.closePort();
                }

                //noinspection BusyWait
                sleep(10000);
            }
        }
        //exit dialog
        JOptionPane.showMessageDialog(null,"exited");
    }

    //im prinzip main
    private static void keypress(SerialPort comPort) throws AWTException, InterruptedException {
        int oldinput = 0;

        label:
        while (preset != 0) {
            Scanner s     = new Scanner(comPort.getInputStream());
            Robot   robot = new Robot();

            //waits for input
            waiting(comPort);

            //saves input in int

            int input = s.nextInt();

            System.out.print(input);
            if (oldinput != input) System.out.println();

            oldinput = input;

            int key = getKey(input);


            switch (key) {
                //einfach das preset ändern
                case 0 -> presetswichdialog();
                //error ist aufgetreten und preset wird geändert
                case 1 -> {
                    showerrordialog(input);
                    presetswichdialog();
                }
                //programm wird beendet
                case 3 -> {
                    break label;
                }
                // music dialog fals praset music ausgewählt wurde TODO Dialog
                case 2 -> {
                    JOptionPane.showMessageDialog(null,"wip");
                }
                default -> {
                    robot.keyPress(key);
                    sleep(100);
                    robot.keyRelease(key);
                }

            }


        }
    }


    //belegung
    private static int getKey(int input) {
        //https://docs.microsoft.com/en-us/windows/win32/inputdev/virtual-key-codes
        if (preset == 1) {
            return switch (input) {
                case 2 -> KeyEvent.VK_F13;
                case 3 -> KeyEvent.VK_F14;
                case 4 -> KeyEvent.VK_F15;
                case 5 -> KeyEvent.VK_F16;
                case 6 -> KeyEvent.VK_F17;
                case 7 -> KeyEvent.VK_F18;
                case 8 -> KeyEvent.VK_F19;
                case 9 -> KeyEvent.VK_F20;
                case 10 -> 0;
                case 11 -> KeyEvent.VK_F22;
                case 12 -> KeyEvent.VK_F23;
                case 13 -> KeyEvent.VK_F24;
                default -> 1;
            };
        }
        else if (preset == 2) {
            return switch (input) {
                case 2 -> KeyEvent.VK_A;
                case 3 -> KeyEvent.VK_S;
                case 4 -> KeyEvent.VK_D;
                case 5 -> KeyEvent.VK_F;
                case 6 -> KeyEvent.VK_Q;
                case 7 -> KeyEvent.VK_W;
                case 8 -> KeyEvent.VK_E;
                case 9 -> KeyEvent.VK_R;
                case 10 -> 0;
                case 11 -> KeyEvent.VK_1;
                case 12 -> KeyEvent.VK_2;
                case 13 -> KeyEvent.VK_DELETE;
                default -> 1;
            };
        }
        else if (preset == 3) {
            return 5;
        }
        return 1;
    }

    //Dialogue
    private static void presetswichdialog() {
        String   fk            = "Function keys", wasd = "Wasd etc", numpad = "numpad(wip)", exit = "exit", music = "music";
        Object[] possibilities = {fk,wasd,numpad,exit,};
        try {
            String presetInString = (String) JOptionPane.showInputDialog(null,"choose preset","Preset",JOptionPane.QUESTION_MESSAGE,null,possibilities,"1");
            if (presetInString.equals(exit)) setPreset(0);
            if (presetInString.equals(fk)) setPreset(1);
            if (presetInString.equals(wasd)) setPreset(2);
            if (presetInString.equals(music)) setPreset(3);
            if (presetInString.equals(numpad)) setPreset(4);//TODO no preset 4

        } catch (NullPointerException ignored) {
        } //Fals der dialog abgebrochen wird einfach ignorieren


    }

    private static void showerrordialog(int input) {
        JOptionPane.showMessageDialog(null,"bad input " + input);
    }

    //
    private static void waiting(SerialPort comPort) throws InterruptedException {
        while (comPort.bytesAvailable() == 0)
            //noinspection BusyWait
            sleep(20);
    }

//    public static int getPreset() {
//        return preset;
//    }

    public static void setPreset(int preset) {
        Macropadmain.preset = preset;
    }

}
