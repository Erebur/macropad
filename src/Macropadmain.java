import com.fazecast.jSerialComm.SerialPort;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.Scanner;

import static java.lang.Thread.sleep;

@SuppressWarnings("BusyWait")
public class Macropadmain {

    private static final File config = new File("config");
    private static int preset = initializePreset();
    private static boolean presetswitchdialog  = true ;
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
        System.out.printf("%S \n %S%n \n","a error occurred restarting with 10sec delay" , e.getCause());
        if (comPort != null && comPort.isOpen()) {
            comPort.closePort();
        }
        sleep(10000);
    }


    //Dialoge
    public static void presetswichdialog() {
        if (isPresetswitchdialog()){
            String   fk            = "Function keys", wasd = "Wasd etc", numpad = "numpad", exit = "exit", music = "music", fkm = "Function keys but music";
            Object[] possibilities = {fk,fkm,wasd,numpad,music,exit};
            try {
                String presetInString = (String) JOptionPane.showInputDialog(null,"choose preset","Preset",JOptionPane.QUESTION_MESSAGE,null,possibilities,"1");
                if (presetInString.equals(exit))    setPreset(0);
                if (presetInString.equals(fk))      setPreset(1);
                if (presetInString.equals(wasd))    setPreset(2);
                if (presetInString.equals(numpad))  setPreset(3);
                //numpad kriegt 3,4
                if (presetInString.equals(music))   setPreset(5);
                if (presetInString.equals(fkm))     setPreset(6);
            } catch (NullPointerException ignored) {
                //Falls der dialog abgebrochen wird einfach ignorieren
            }
        }else {
            if (getPreset() == Presets.getgesamtpresets()){
                setPreset(1);
            }else{
                setPreset(getPreset() + 1 );
            }
        }





    }

    public static void showerrordialog(String message) {
        JOptionPane.showMessageDialog(null,message);
    }

    public static void waiting(SerialPort comPort) throws InterruptedException {
        while (comPort.bytesAvailable() == 0){
            //noinspection BusyWait
            sleep(20);
        }
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
        if (preset == 0) {
            Macropadmain.preset = preset;
        }else {
            Macropadmain.preset = preset;
            writePreset(preset);
        }

        if (Konsolenausgabe){
            System.out.printf("\npreset=%d", getPreset());
        }
    }

    public static int getPreset() {
        return preset;
    }

    public static boolean isPresetswitchdialog() {
        return presetswitchdialog;
    }

    public static void setPresetwitchdialog(boolean presetswitchdialog) {
        Macropadmain.presetswitchdialog = presetswitchdialog;
    }

    //zwei methoden zum speichern des presets in einer config
    public static int initializePreset() {
        var trennzeichen = ":";
        FileReader fr;
        int result = 1 ;
        try {
            fr = new FileReader(config);
            Scanner configScanner = new Scanner(fr);
            var tmp = configScanner.nextLine();
            fr.close();
            configScanner.close();
            result =  Integer.parseInt(tmp.substring(tmp.indexOf(trennzeichen) + 1 ));
        } catch (Exception ignored) {
        }

        if (result == 0){
            result = 1 ;
        }
        return result ;
    }

    public static void writePreset(int preset){
        var trennzeichen = ":";
        FileReader fr;
        try {
            fr = new FileReader(config);
            Scanner configScanner = new Scanner(fr);
            var tmp = configScanner.nextLine();

            fr.close();
            configScanner.close();
            tmp = tmp.substring(0 ,tmp.indexOf(trennzeichen) + 1 ) + preset;

            PrintWriter pw = new PrintWriter(new FileWriter(config));
            pw.write(tmp);
            pw.close();

        } catch (IOException ignored) {
        }
    }

}
