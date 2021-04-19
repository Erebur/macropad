import com.fazecast.jSerialComm.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class Macropadmain {
    public static void main(String[] args) throws InterruptedException, IOException {
        //noinspection InfiniteLoopStatement
        while (true){
            FileWriter fw = new FileWriter("log",true);
            PrintWriter pw = new PrintWriter(fw);
            SerialPort comPort = null;
            //serial port reader
            try {
                pw.println("Started");
                pw.flush();
                comPort = SerialPort.getCommPorts()[ 1 ];
                comPort.openPort();

                //finds and presses keys
                keypress(comPort);


            } catch (Exception e) {
                pw.println("a error occurred restarting with 10sec delay ");
                if (comPort != null && comPort.isOpen()) {
                    comPort.closePort();
                }
                //noinspection BusyWait
                Thread.sleep(10000);
            }

            pw.close();
            fw.close();
        }


    }

    @SuppressWarnings("InfiniteLoopStatement")
    private static void keypress(SerialPort comPort) throws AWTException, InterruptedException, IOException {

        while (true) {
            FileWriter fw = new FileWriter("input.log" , true);
            PrintWriter pw = new PrintWriter(fw);
            Scanner s     = new Scanner(comPort.getInputStream());
            Robot   robot = new Robot();

            //waits for input
            waiting(comPort);

            //saves input in int

            int input = s.nextInt();
            pw.println(input);
            fw.close();
            pw.close();
            int key = getKey(input);

            //System.out.println(key);

            robot.keyPress(key);
            robot.keyRelease(key);


        }
    }

    private static int getKey(int input) {
        //https://docs.microsoft.com/en-us/windows/win32/inputdev/virtual-key-codes
        return  switch (input) {
            case 2 -> KeyEvent.VK_F13;
            case 3 -> KeyEvent.VK_F14;
            case 4 -> KeyEvent.VK_F15;
            case 5 -> KeyEvent.VK_F16;
            case 6 -> KeyEvent.VK_F17;
            case 7 -> KeyEvent.VK_F18;
            case 8 -> KeyEvent.VK_F19;
            case 9 -> KeyEvent.VK_F20;
            case 10 -> KeyEvent.VK_F21;
            case 11 -> KeyEvent.VK_F22;
            case 12 -> KeyEvent.VK_F23;
            //13 and 14 dont work
            case 15 -> KeyEvent.VK_F24;
            case 16 -> 0xAF; //Volume up
            case 17 -> 0xAE; //Volume down
            default -> 0x00;
        };

    }

    private static void waiting(SerialPort comPort) throws InterruptedException {
        while (comPort.bytesAvailable() == 0)
            //noinspection BusyWait
            Thread.sleep(20);
    }

}
