import com.fazecast.jSerialComm.SerialPort;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

import static java.lang.Thread.sleep;

@SuppressWarnings("BusyWait")
public class Macropadmain {

    private static int preset = 1;
    private static boolean numlock = Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_NUM_LOCK);
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
                error(comPort, e);
            }
        }
        //exit dialog
        JOptionPane.showMessageDialog(null,"exited");
    }

    private static void error(SerialPort comPort, Throwable e) throws InterruptedException {
        showerrordialog(String.format("%S \n %S" ,"a error occurred restarting with 10sec delay" , e.toString() ));
        System.out.printf("%S \n %S%n \n","a error occurred restarting with 10sec delay" , e);
        if (comPort != null && comPort.isOpen()) {
            comPort.closePort();
        }
        sleep(10000);
    }


    //Dialoge
    public static void presetswichdialog() {
        String   fk            = "Function keys", wasd = "Wasd etc", numpad = "numpad", exit = "exit", music = "music";
        Object[] possibilities = {fk,wasd,numpad,music,exit};
        try {
            String presetInString = (String) JOptionPane.showInputDialog(null,"choose preset","Preset",JOptionPane.QUESTION_MESSAGE,null,possibilities,"1");
            if (presetInString.equals(exit))    setPreset(0);
            if (presetInString.equals(fk))      setPreset(1);
            if (presetInString.equals(wasd))    setPreset(2);
            if (presetInString.equals(numpad))  setPreset(3);
            //numpad kriegt 3,4
            if (presetInString.equals(music))   setPreset(5);

        } catch (NullPointerException ignored) {
        } //Falls der dialog abgebrochen wird einfach ignorieren


    }

    public static void showerrordialog(String message) {
        JOptionPane.showMessageDialog(null,message);
    }

    public static void waiting(SerialPort comPort) throws InterruptedException {
        while (comPort.bytesAvailable() == 0)
            //noinspection BusyWait
            sleep(20);
    }

    public static void testnumlock(boolean Konsolenausgabe) {
        //wenn numlock an dann normal nummern sonst anderes zeug

        if (getNumlockOn()){
            Macropadmain.setPreset(3, Konsolenausgabe);
        }else {
            Macropadmain.setPreset(4 , Konsolenausgabe);
        }
    }

    public static boolean getNumlockOn() {
        return numlock;
    }
    public static void switchnumlock() {
        numlock = !getNumlockOn();


    }

    public static void setPreset(int preset) {
        setPreset(preset , true);
    }
    public static void setPreset(int preset , boolean Konsolenausgabe) {
        Macropadmain.preset = preset;
        if (Konsolenausgabe){
            System.out.printf("\npreset=%d", getPreset());
        }
    }

    public static int getPreset() {
        return preset;
    }
}
