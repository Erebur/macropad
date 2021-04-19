import com.fazecast.jSerialComm.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

public class Macropadmain {

    private static  int preset = 1 ;

    public static void main(String[] args) throws InterruptedException, FileNotFoundException {
        //noinspection InfiniteLoopStatement
        while (preset != 0){
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
                Thread.sleep(10000);
            }
        }
        //exit dialog
        JOptionPane.showMessageDialog(null , "exited");
    }

        //im prinzip main
    private static void keypress(SerialPort comPort) throws AWTException, InterruptedException {
        int oldinput = 0 ;

        while (preset != 0) {
            Scanner s     = new Scanner(comPort.getInputStream());
            Robot   robot = new Robot();

            //waits for input
            waiting(comPort);

            //saves input in int

            int input = s.nextInt();

            System.out.print(input);
            if (oldinput != input) System.out.println();

            oldinput = input ;

            int key = getKey(input);

            if (key == 0 ){
                presetswichdialog();
            }else if (key ==  1){
                showerrordialog(input);
                presetswichdialog();
            }else if (key == 3 ){
                break;
            }else{
               robot.keyPress(key);
                robot.keyRelease(key);
            }

            //System.out.println(key);

        }
    }



        //belegung
    private static int getKey(int input) {
        int fehler = 0  , presetswich = 0 ;
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
                default -> fehler = 1 ;
            };
        }else if (preset == 2 ){
            return switch (input) {
                case 2 -> KeyEvent.VK_A;
                case 3 -> KeyEvent.VK_S;
                case 4 -> KeyEvent.VK_D;
                case 5 -> KeyEvent.VK_F;
                case 6 -> KeyEvent.VK_Q;
                case 7 -> KeyEvent.VK_W;
                case 8 -> KeyEvent.VK_E;
                case 9 -> KeyEvent.VK_R;
                case 10 ->  0  ;
                case 11 -> KeyEvent.VK_1;
                case 12 -> KeyEvent.VK_2;
                case 13 -> KeyEvent.VK_DELETE;
                default -> 1 ;
            };
        }
        return 1;
    }
        //Dialogue
    private static void presetswichdialog(){
        String fk ="Function keys" , wasd = "Wasd etc" , numpad = "numpad(wip)" , exit = "exit";
        Object[] possibilities = {fk , wasd , numpad , exit};
        Icon     icon          = null;

        String presetInString = (String)JOptionPane.showInputDialog(null,"choose preset","Preset",JOptionPane.QUESTION_MESSAGE,null,possibilities,"1");
        if (presetInString.equals(exit))    setPreset(0);
        if (presetInString.equals(fk))      setPreset(1);;
        if (presetInString.equals(wasd))    setPreset(2);
        if (presetInString.equals(numpad))  setPreset(3);//TODO no preset 3
    }

    private static void showerrordialog(int input) {
        JOptionPane.showMessageDialog(null,"bad input " + input);
    }
        //
    private static void waiting(SerialPort comPort) throws InterruptedException {
        while (comPort.bytesAvailable() == 0)
            //noinspection BusyWait
            Thread.sleep(20);
    }

    public static int getPreset() {
        return preset;
    }

    public static void setPreset(int preset) {
        Macropadmain.preset = preset;
    }

}
