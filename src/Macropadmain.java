import com.fazecast.jSerialComm.SerialPort;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

import static java.lang.Thread.sleep;

@SuppressWarnings("BusyWait")
public class Macropadmain {

    //0 aus // 1 console // 2 pop up // 3 (1 + 2)
    private static final int errormessage = 0;
    //der Pfad der config datei die zum speichern des presets genutzt wird
    private static final File config = new File("C:\\Users\\simon\\OneDrive\\Dokumente\\Programmieren\\eigengebrauch\\macropad\\config");
    //das preset wird aus dieser datei gesucht
    private static int preset = initializePreset();
    private static boolean presetSwitchDialog = true;
    //speichert den status des numlocks --> nur am anfang des Programms aktuell --> fängt keine änderungen ab
    private static boolean numLock = Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_NUM_LOCK);
    private static int port;
    private static boolean exit = false;

    public static void main(String[] args) throws InterruptedException {
        //usereingabe des Ports
        // bei falscher eingabe wartet das programm ewig auf eingabe durch serial Port bekommt aber nie etwas -> das programm macht nicht und man kann nicht beenden (was ungünstig ist lol)
        //TODO mit arduino den Port übergeben und automatisch richtig wählen
        portsuchen();

        while (!exit) {

            SerialPort comPort = null;
            //serial port reader
            try {
                System.out.println("Started");
                //öffnet den ausgewählten port
                comPort = SerialPort.getCommPorts()[ port ];
                comPort.openPort();

                //drückt tasten lol
                Keypress.keypress(comPort);


            } catch (Throwable e) {
                error(comPort, e);
                //wär doch ungünstig wenn man errors(mit pop ups) ohne ende bekommen würde und man nicht abbrechen kann lol
                presetswichdialog();
            }
        }
        //exit dialog
        JOptionPane.showMessageDialog(null, "exited");
    }

    private static void portsuchen() {
        boolean error = false;
        do {
            try {
                port = Integer.parseInt(JOptionPane.showInputDialog(null, Arrays.toString(SerialPort.getCommPorts())));
                SerialPort comPort = SerialPort.getCommPorts()[ port ];
                comPort.openPort();
                error = true;
                System.out.printf("Started with port %d and preset %d \n", port, preset);
                comPort.closePort();
            } catch (Exception ignored) {
            }
        } while (!error);
    }

    @SuppressWarnings("ConstantConditions")
    private static void error(SerialPort comPort, Throwable e) throws InterruptedException {
        //0 aus // 1 console // 2 pop up // 3 (1 + 2)
        switch (errormessage){
            case 1 -> System.out.printf("%S \n %S%n \n", "a error occurred restarting with 10sec delay", e.getCause());
            case 2 -> showerrordialog(String.format("%S \n %S", "a error occurred restarting with 10sec delay", e.toString()));
            case 3 -> {
                System.out.printf("%S \n %S%n \n", "a error occurred restarting with 10sec delay", e.getCause());
                showerrordialog(String.format("%S \n %S", "a error occurred restarting with 10sec delay", e.toString()));
            }
        }
        if (comPort != null && comPort.isOpen()) {
            comPort.closePort();
        }
        //10 sec
        sleep(10000);
    }


    //Dialoge
    public static void presetswichdialog() {
        if (isPresetswitchdialog()) {
            String   fk            = "Function keys", wasd = "Wasd etc", numpad = "numpad", exit = "exit", music = "music", fkm = "Function keys but music", portSwitch = "PortSwitch";
            Object[] possibilities = {fk, fkm, wasd, numpad, music, portSwitch, exit};
            try {
                String presetInString = (String) JOptionPane.showInputDialog(null, "choose preset", "Preset", JOptionPane.QUESTION_MESSAGE, null, possibilities, "1");

                switch (presetInString){
                    case "exit"         -> special(0);
                    case "portSwitch"   -> special(1);
                    case "fk"           -> setPreset(1);
                    case "fkm"          -> setPreset(6);
                    case "wasd"         -> setPreset(2);
                    //numpad kriegt 3,4
                    case "numpad"       -> setPreset(3);
                    case "music"        -> setPreset(5);

                }


            } catch (NullPointerException ignored) {
                //Falls der dialog abgebrochen wird einfach ignorieren
            }
        }
        else {
            if (getPreset() == Presets.getgesamtpresets()) {
                setPreset(1);
            }
            else {
                setPreset(getPreset() + 1);
            }
        }
    }

    public static void showerrordialog(String message) {
        JOptionPane.showMessageDialog(null, message);
    }

    public static void waiting(SerialPort comPort) throws InterruptedException {
        while (comPort.bytesAvailable() == 0) {
            //noinspection BusyWait
            sleep(20);
        }
    }

    public static void testnumlock(boolean Konsolenausgabe) {
        //wenn numlock an dann normal nummern sonst anderes zeug

        if (getNumlockOn()) {
            Macropadmain.setPreset(3, Konsolenausgabe);
        }
        else {
            Macropadmain.setPreset(4, Konsolenausgabe);
        }
    }

    public static boolean getNumlockOn() {
        return numLock;
    }

    public static void switchnumlock() {
        numLock = !getNumlockOn();


    }

    public static void special(int type) {
        switch (type){
            case 0 -> exit = true;
            case 1 -> portsuchen();
        }
    }

    public static void setPreset(int preset, boolean Konsolenausgabe) {
        if (preset == 0) {
            Macropadmain.preset = preset;
        }
        else {
            Macropadmain.preset = preset;
            writePreset(preset);
        }

        if (Konsolenausgabe) {
            System.out.printf("\npreset=%d", getPreset());
        }
    }

    public static int getPreset() {
        return preset;
    }

    public static void setPreset(int preset) {
        setPreset(preset, true);
    }

    public static boolean isPresetswitchdialog() {
        return presetSwitchDialog;
    }

    public static void setPresetwitchdialog(boolean presetswitchdialog) {
        Macropadmain.presetSwitchDialog = presetswitchdialog;
    }

    //zwei methoden zum speichern des presets in einer config
    public static int initializePreset() {
        var        trennzeichen = ":";
        FileReader fr;
        int        result       = 1;
        try {
            fr = new FileReader(config);
            Scanner configScanner = new Scanner(fr);
            var     tmp           = configScanner.nextLine();
            fr.close();
            configScanner.close();
            result = Integer.parseInt(tmp.substring(tmp.indexOf(trennzeichen) + 1));
        } catch (Exception ignored) {
        }

        if (result == 0) {
            result = 1;
        }
        return result;
    }

    public static void writePreset(int preset) {
        var        trennzeichen = ":";
        FileReader fr;
        try {
            fr = new FileReader(config);
            Scanner configScanner = new Scanner(fr);
            var     tmp           = configScanner.nextLine();

            fr.close();
            configScanner.close();
            tmp = tmp.substring(0, tmp.indexOf(trennzeichen) + 1) + preset;

            PrintWriter pw = new PrintWriter(new FileWriter(config));
            pw.write(tmp);
            pw.close();

        } catch (IOException ignored) {
        }
    }

}
