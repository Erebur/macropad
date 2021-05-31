import com.fazecast.jSerialComm.SerialPort;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import static java.lang.Thread.sleep;

@SuppressWarnings("BusyWait")
public class Macropadmain {

    //0 aus // 1 console // 2 pop up // 3 (1 + 2)
    private static final int debugLevel = 1;
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
                error("Started");
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
                error(String.format("Started with port %d and preset %d", port, preset) );
                comPort.closePort();
            } catch (Exception ignored) {
                port = 1 ;
//                error = true ;
            }
        } while (!error);
    }

    private static void error(SerialPort comPort, Throwable e) throws InterruptedException {
        error(String.format("%S \n %S", "a error occurred restarting with 10sec delay", e.toString()));

        if (comPort != null && comPort.isOpen()) {
            comPort.closePort();
        }
        //10 sec
        sleep(10000);
    }

    static void error(String errorMessage){
        error(errorMessage , true);
    }

    @SuppressWarnings("ConstantConditions")
    public static void error(String errorMessage , boolean formating){
        String errorFormated  = errorMessage;
        if (formating) {
            errorFormated = String.format("\n %S", errorMessage);
        }
        //0 aus // 1 console // 2 pop up // 3 (1 + 2)
        switch (debugLevel){
            case 1 -> System.out.print(errorFormated);
            case 2 -> showerrordialog(errorFormated);
            case 3 -> {
                System.out.print(errorFormated);
                showerrordialog(errorFormated);
            }
        }
    }



    //key = reihenfolge // String = die bezeichnung // boolean special (oder setPreset) // nummer der aktion
    public static ArrayList<ArrayList<Object>> presets = new ArrayList<>(){{

        add(new ArrayList<>(){{add("Function keys"); add(false);add(1);}} );
        add(new ArrayList<>(){{add("Function keys but music"); add(false);add(6);}} );
        add(new ArrayList<>(){{add("Wasd etc"); add(false);add(2);}} );
        add(new ArrayList<>(){{add("numpad"); add(false);/*numpad kriegt 3,4*/add(3);}});
        add(new ArrayList<>(){{add("music"); add(false);add(5);}} );

        add(new ArrayList<>(){{add("exit");add(true);add(0);}} );
        add(new ArrayList<>(){{add("PortSwitch"); add(true);add(1);}} );
    }};



    //Dialoge
    public static void presetswichdialog() {
        ArrayList<String> possibilities = new ArrayList<>();
        for (ArrayList<Object> objects : presets) {
            possibilities.add((String) objects.get(0));
        }

        if (isPresetswitchdialog()) {
            //Aktuelles preset suchen
            int tmp = 0 ;
            for (int i = 0; i < presets.size() - 1 ; i++) {
                if ((int) presets.get(i).get(2) == getPreset()){
                    tmp = i ;
                }
            }

            String gewaehltesPreset = (String) JOptionPane.showInputDialog(null, String.format("Preset wählen (aktuell = %s )", presets.get(tmp).get(0)), "Preset", JOptionPane.QUESTION_MESSAGE, null, possibilities.toArray(), "1");


            error(gewaehltesPreset);

            System.out.println(possibilities.indexOf(gewaehltesPreset));
            if ((boolean) presets.get(possibilities.indexOf(gewaehltesPreset)).get(1)) {
                special((int) presets.get(possibilities.indexOf(gewaehltesPreset)).get(2));
            }
            else {
                setPreset((int) presets.get(possibilities.indexOf(gewaehltesPreset)).get(2));
            }
        }else {
            if (getPreset() == Presets.getgesamtpresets()) {
                setPreset(1);
            }
            else {
                setPreset(getPreset() + 1);
            }
        }








//old
/*
        if (isPresetswitchdialog()) {
            String   fk            = "Function keys", wasd = "Wasd etc", numpad = "numpad", exit = "exit", music = "music", fkm = "Function keys but music", portSwitch = "PortSwitch";
            String[] possibilities = {fk, fkm, wasd, numpad, music, portSwitch, exit};

            try {
                String presetInString = (String) JOptionPane.showInputDialog(null, String.format("Preset wählen (aktuell = %d )" , getPreset()), "Preset", JOptionPane.QUESTION_MESSAGE, null, possibilities, "1");

                error(presetInString);

                if (presetInString.equals(exit)){
                    special(0);
                }else if (presetInString.equals(portSwitch)){
                    special(1);
                }else if (presetInString.equals(fk)){
                    setPreset(1);
                }else if (presetInString.equals(fkm)){
                    setPreset(6);
                }else if (presetInString.equals(wasd)){
                    setPreset(2);
                }else if (presetInString.equals(numpad)){
                    setPreset(3);
                    //numpad kriegt 3,4
                }else if (presetInString.equals(music)){
                    setPreset(5);
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
*/
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
            case 0 -> setExit(true);
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
            error(String.format("\npreset=%d\n", getPreset()) ,false );
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

    //TODO
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

    public static void setExit(boolean exit) {
        Macropadmain.exit = exit;
    }
}
