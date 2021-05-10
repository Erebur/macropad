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
        JOptionPane.showMessageDialog(null,"Started");

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
                Keypress.keypress(comPort);


            } catch (Throwable e) {
                System.out.println(e.getMessage());
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

    //belegung alt
//    private static int getKey(int input) {
//        //https://docs.microsoft.com/en-us/windows/win32/inputdev/virtual-key-codes
//        if (preset == 1) {
//            return switch (input) {
//                case 2 -> KeyEvent.VK_F13;
//                case 3 -> KeyEvent.VK_F14;
//                case 4 -> KeyEvent.VK_F15;
//                case 5 -> KeyEvent.VK_F16;
//                case 6 -> KeyEvent.VK_F17;
//                case 7 -> KeyEvent.VK_F18;
//                case 8 -> KeyEvent.VK_F19;
//                case 9 -> KeyEvent.VK_F20;
//                case 10 -> 0;
//                case 11 -> KeyEvent.VK_F22;
//                case 12 -> KeyEvent.VK_F23;
//                case 13 -> KeyEvent.VK_F24;
//                default -> 1;
//            };
//        }
//        else if (preset == 2) {
//            return switch (input) {
//                case 2 -> KeyEvent.VK_A;
//                case 3 -> KeyEvent.VK_S;
//                case 4 -> KeyEvent.VK_D;
//                case 5 -> KeyEvent.VK_F;
//                case 6 -> KeyEvent.VK_Q;
//                case 7 -> KeyEvent.VK_W;
//                case 8 -> KeyEvent.VK_E;
//                case 9 -> KeyEvent.VK_R;
//                case 10 -> 0;
//                case 11 -> KeyEvent.VK_1;
//                case 12 -> KeyEvent.VK_2;
//                case 13 -> KeyEvent.VK_DELETE;
//                default -> 1;
//            };
//        }
//        else if (preset == 3) {
//            return 5;
//        }
//        return 1;
//    }

    //Dialogue
    public static void presetswichdialog() {
        String   fk            = "Function keys", wasd = "Wasd etc", numpad = "numpad(wip)", exit = "exit", music = "music";
        Object[] possibilities = {fk,wasd,numpad,music,exit};
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

    public static void showerrordialog(int input) {
        JOptionPane.showMessageDialog(null,"bad input " + input);
    }

    //
    public static void waiting(SerialPort comPort) throws InterruptedException {
        while (comPort.bytesAvailable() == 0)
            //noinspection BusyWait
            sleep(20);
    }

//    public static int getPreset() {
//        return preset;
//    }

    public static void setPreset(int preset) {
        Macropadmain.preset = preset;
        System.out.printf("preset = %d%n", getPreset());
    }

    public static int getPreset() {
        return preset;
    }
}
