package Macropad;

import com.fazecast.jSerialComm.SerialPort;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

import static java.lang.Thread.sleep;

@SuppressWarnings("BusyWait")
public class Macropadmain {

    //0 aus // 1 console // 2 pop up // 3 (1 + 2)
    private static final int debugLevel = 1;
    private static String os = "unix";
    public static Preset preset = new Preset(os);
    //der Pfad der config datei die zum speichern des presets genutzt wird
    private static final File config = new File("C:\\Users\\simon\\OneDrive\\Dokumente\\Programmieren\\eigengebrauch\\macropad\\config");
    private static final char trennzeichen = ':';
    //key = reihenfolge // String = die bezeichnung // boolean special (oder setPreset) // nummer der aktion
    public static ArrayList<ArrayList<Object>> presets = new ArrayList<>() {{
        add(new ArrayList<>() {{
            add("Function keys");
            add(false);
            add(1);
        }});
        add(new ArrayList<>() {{
            add("Function keys but music");
            add(false);
            add(6);
        }});
        add(new ArrayList<>() {{
            add("Wasd etc");
            add(false);
            add(2);
        }});
        add(new ArrayList<>() {{
            add("numpad");
            add(false);/*numpad kriegt 3,4*/
            add(3);
        }});
        add(new ArrayList<>() {{
            add("music");
            add(false);
            add(5);
        }});

        add(new ArrayList<>() {{
            add("exit");
            add(true);
            add(0);
        }});
        add(new ArrayList<>() {{
            add("PortSwitch");
            add(true);
            add(1);
        }});
    }};
    //das preset wird aus dieser datei gesucht
    private static int presetNr = initializePreset();
    //wird versucht automatisch dem Port zu suchen (error = -1 )
    private static int port = autoPortSuchen();
    private static boolean presetSwitchDialog = true;
    //speichert den status des numlocks --> nur am anfang des Programms aktuell --> fängt keine änderungen ab
    private static boolean numLock = Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_NUM_LOCK);
    private static boolean exit = false;
    
    public static void main(String[] args) throws InterruptedException {
        //usereingabe des Ports
        // bei falscher eingabe wartet das programm ewig auf eingabe durch serial Port bekommt aber nie etwas -> das programm macht nicht und man kann nicht beenden (was ungünstig ist lol)
        //TODO mit arduino den Port übergeben und automatisch richtig wählen lol 
        if (port == -1){
            portSuchenDialog();
        }


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

    public static void portSuchenDialog() {
        boolean error = false;
        do {
            try {
                port = Integer.parseInt(JOptionPane.showInputDialog(null, Arrays.toString(SerialPort.getCommPorts())));
                SerialPort comPort = SerialPort.getCommPorts()[ port ];
                comPort.openPort();
                error = true;
                error(String.format("Started with port %d and preset %d", port, presetNr));
                comPort.closePort();
            } catch (Exception ignored) {
                port = 1;
//                error = true ;
            }
        } while (!error);
        writePort(port);
    }


    private static void error(SerialPort comPort, Throwable e) throws InterruptedException {
        error(String.format("%S \n %S", "a error occurred restarting with 10sec delay", e.toString()));

        if (comPort != null && comPort.isOpen()) {
            comPort.closePort();
        }
        //10 sec
        sleep(10000);
    }

    static void error(String errorMessage) {
        error(errorMessage, true);
    }

    @SuppressWarnings("ConstantConditions")
    public static void error(String errorMessage, boolean formating) {
        String errorFormated = errorMessage;
        if (formating) {
            errorFormated = String.format("\n %s", errorMessage);
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

    //Dialoge
    public static void presetswichdialog() {
        ArrayList<String> possibilities = new ArrayList<>();
        for (ArrayList<Object> objects : presets) {
            possibilities.add((String) objects.get(0));
        }

        if (isPresetswitchdialog()) {
            //Aktuelles preset suchen bruh
            int tmp = 0;
            for (int i = 0; i < presets.size() - 1; i++) {
                if ((int) presets.get(i).get(2) == getPreset()) {
                    tmp = i;
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
        }
        else {
            if (getPreset() == preset.getgesamtpresets()) {
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
            case 0 -> setExit(true);
            case 1 -> portSuchenDialog();
        }
    }

    public static void setPreset(int preset, boolean Konsolenausgabe) {
        if (preset == 0) {
            Macropadmain.presetNr = preset;
        }
        else {
            Macropadmain.presetNr = preset;
            writePreset(preset);
        }

        if (Konsolenausgabe) {
            error(String.format("\npreset=%d\n", getPreset()), false);
        }
    }

    public static int getPreset() {
        return presetNr;
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



    //methoden zum speichern in einer config

    public static int autoPortSuchen(){
        Object[] tmp =  SerialPort.getCommPorts();
        String[] ports = new String[ tmp.length];
        for (int i = 0; i < ports.length; i++) {
            ports[i] = tmp[i].toString();
        }

        for (int i = 0, portsLength = ports.length; i < portsLength; i++) {
            String port = ports[ i ];
            if (port.equals("USB2.0-Serial")) {
                return i;
            }
        }
        return -1;
    }

    public static void writePort(int port){
        write("portsAlt" , Arrays.toString(SerialPort.getCommPorts()));
        write("port" , String.valueOf(port));
    }


    public static int initializePreset() {
        int result = 1 ;
        if (read("preset") != null){
            result = Integer.parseInt(Objects.requireNonNull(read("preset")));
        }
        return result;
    }

    public static void writePreset(int preset) {
        write("preset" , String.valueOf(preset));
    }

    public static String read(String suchen){

        //entweder lesen oder schreiben
        FileReader fr;

        try {
            fr = new FileReader(config);
            Scanner configScanner = new Scanner(fr);
            var tmp        = configScanner.nextLine();
            while (!tmp.substring(0 , tmp.indexOf(":")).equals(suchen)){
                tmp = configScanner.nextLine();
            }
            fr.close();
            configScanner.close();
            return tmp.substring(tmp.indexOf(trennzeichen) + 1);
        } catch (Exception ignored) {
            return null ;
        }
    }

    public static void write(String suchen , String schreiben ){
        FileReader fr;
        try {
            fr = new FileReader(config);
            Scanner configScanner = new Scanner(fr);
            var           tmp  = configScanner.nextLine();
            StringBuilder zuschreibendes = new StringBuilder();

            while (!tmp.substring(0 , tmp.indexOf(":")).equals(suchen)){
                zuschreibendes.append(tmp).append("\n");
                tmp = configScanner.nextLine();
            }
            //einstellung überschreiben
            zuschreibendes.append(tmp.substring(0, tmp.indexOf(trennzeichen) + 1)).append(schreiben).append("\n");

            while (configScanner.hasNextLine()){
                zuschreibendes.append(configScanner.nextLine()).append("\n");
            }

            fr.close();
            configScanner.close();

            PrintWriter pw = new PrintWriter(new FileWriter(config));
            pw.write(String.valueOf(zuschreibendes));
            pw.close();

        } catch (IOException ignored) {
        }

    }

    public static void setExit(boolean exit) {
        Macropadmain.exit = exit;
    }
}
