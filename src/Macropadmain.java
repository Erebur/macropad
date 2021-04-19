import com.fazecast.jSerialComm.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

public class Macropadmain {
    public static void main(String[] args) throws InterruptedException, FileNotFoundException {
        //noinspection InfiniteLoopStatement
        while (true){
            FileReader fr = new FileReader("config");

            SerialPort comPort = null;
            //serial port reader
            try {
                System.out.println("Started");

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
                Thread.sleep(10000);
            }

        }


    }

    @SuppressWarnings("InfiniteLoopStatement")
    private static void keypress(SerialPort comPort) throws AWTException, InterruptedException {

        while (true) {
            Scanner s     = new Scanner(comPort.getInputStream());
            Robot   robot = new Robot();

            //waits for input
            waiting(comPort);

            //saves input in int

            int input = s.nextInt();
            System.out.println(input);


            int key = getKey(input);
            //int key = belegung[ input ];


            //System.out.println(key);

            robot.keyPress(key);
            robot.keyRelease(key);


        }
    }

    private static int preset = 0 ;

    private static int getKey(int input) {
        int fehler = 0 ;
        //https://docs.microsoft.com/en-us/windows/win32/inputdev/virtual-key-codes
        if (preset == 0 ) {
            return switch (input) {
                case 2 -> KeyEvent.VK_F13;
                case 3 -> KeyEvent.VK_F14;
                case 4 -> KeyEvent.VK_F15;
                case 5 -> KeyEvent.VK_F16;
                case 6 -> KeyEvent.VK_F17;
                case 7 -> KeyEvent.VK_F18;
                case 8 -> KeyEvent.VK_F19;
                case 9 -> KeyEvent.VK_F20;
                case 10 -> preset = 1 ;
                case 11 -> KeyEvent.VK_F22;
                case 12 -> KeyEvent.VK_F23;
                case 13 -> KeyEvent.VK_F24;
                default -> fehler = 1 ;
            };
        }else if (preset == 1 ){
            return switch (input) {
                case 2 -> KeyEvent.VK_A;
                case 3 -> KeyEvent.VK_S;
                case 4 -> KeyEvent.VK_D;
                case 5 -> KeyEvent.VK_F;
                case 6 -> KeyEvent.VK_Q;
                case 7 -> KeyEvent.VK_W;
                case 8 -> KeyEvent.VK_E;
                case 9 -> KeyEvent.VK_R;
                case 10 -> preset = 0 ;
                case 11 -> KeyEvent.VK_1;
                case 12 -> KeyEvent.VK_2;
                case 13 -> KeyEvent.VK_3;
                default -> fehler = 1 ;
            };
        }

        if (fehler == 1 ){
            JOptionPane.showMessageDialog(null,"bad input " + input);
        }
        return KeyEvent.VK_DELETE;
    }

    private static void waiting(SerialPort comPort) throws InterruptedException {
        while (comPort.bytesAvailable() == 0)
            //noinspection BusyWait
            Thread.sleep(20);
    }

}
