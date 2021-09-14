package Macropad;

import com.fazecast.jSerialComm.SerialPort;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.*;

import static java.lang.Thread.sleep;

@SuppressWarnings("BusyWait")
public class Macropad {

    //0 aus // 1 console // 2 pop up // 3 (1 + 2)
    private static final int DEBUG_LEVEL = 1;
    //der Pfad der config datei die zum Speichern des presets genutzt wird
    private static final File CONFIG = Macropad.getConfig();
    private static final char TRENNZEICHEN = ':';
    private static  final String OS = System.getProperty("os.name").toLowerCase();


    public Preset preset;
    private int presetNr;
    //wird versucht automatisch dem Port zu suchen (error = -1 )
    private int port;
    private boolean presetSwitchDialog ;
    //speichert den status des numlocks → nur am anfang des Programms aktuell → fängt keine änderungen ab
    private boolean numLock ;
    private boolean exit;

    public Macropad(Preset preset) {
        this.exit = false;
        this.numLock = Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_NUM_LOCK);
        this.port = autoPortSuchen();
        this.presetSwitchDialog = true;
        this.presetNr = initializePreset();
        this.preset = preset;
    }

    public Macropad() {
        this(new Preset(Macropad.OS));
    }

    public void start() {

        //usereingabe des Ports
        // bei falscher eingabe wartet das programm ewig auf eingabe durch serial Port bekommt aber nie etwas -> das programm macht nicht und man kann nicht beenden (was ungünstig ist lol)
        if (port == -1){
            portSuchenDialog();
        }

        //testing the device



        while (!exit) {
            SerialPort comPort = null;
            //serial port reader
            try {
                debug("Started");
                //öffnet den ausgewählten port
                comPort = SerialPort.getCommPorts()[ port ];
                comPort.openPort();

                //drückt tasten lol
                Keypress keypress = new Keypress(comPort , this);
                keypress.start();


            } catch (Exception e) {
                e.printStackTrace();
                error(comPort, e);
                //wär doch ungünstig, wenn man errors(mit Pop-ups) ohne Ende bekommen würde und man nicht abbrechen kann lol
                presetswichdialog();
            }
        }

        if (!testing()) {
            System.out.println("can't get output from " + SerialPort.getCommPorts()[ port ].getSystemPortName() );
        }else{
            //exit dialog
            JOptionPane.showMessageDialog(null, "exited");
        }


    }

    private boolean testing() {

        SerialPort comPort = null;
        comPort = SerialPort.getCommPorts()[ port ];
        comPort.openPort();

        return comPort.bytesAvailable() != -1;
    }

    public void portSuchenDialog() {
        SerialPort comPort;
        String input;

        do {
            input = JOptionPane.showInputDialog(null, Arrays.toString(SerialPort.getCommPorts()));


            try {
                port = Integer.parseInt(input);
                comPort = SerialPort.getCommPorts()[port];
                comPort.openPort();
                writePort(port);
                debug(String.format("Started with port %d and preset %d", port, presetNr));
                comPort.closePort();
                return;
            }catch (NumberFormatException e){
                debug("Bitte Nummer eingeben");
            }catch (Exception e) {
                debug("Fehler" + e.getMessage());
            }
        //solte tmp null sein wurde warscheinlich abgebrochen
        }while (input != null);
        setExit(true);
    }


    private void error(SerialPort comPort, Throwable e) {
        debug(String.format("\t%s \n%s", "a error occurred restarting with 10sec delay", e.toString()));

        if (comPort != null && comPort.isOpen()) {
            comPort.closePort();
        }
        //10 sec
        try {
            sleep(10000);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    private static void debug(String errorMessage) {
        debug(errorMessage, true);
    }

    @SuppressWarnings("ConstantConditions")
    public static void debug(String errorMessage, boolean formating) {
        String errorFormated = errorMessage;
        if (formating) {
            errorFormated = String.format("\n%s", errorMessage);
        }
        //0 aus // 1 console // 2 pop up // 3 (1 + 2)
        switch (DEBUG_LEVEL){
            case 1 -> System.out.print(errorFormated);
            case 2 -> showerrordialog(errorFormated);
            case 3 -> {
                System.out.print(errorFormated);
                showerrordialog(errorFormated);
            }
        }
    }

    //Dialoge
    void presetswichdialog() {
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


            debug(gewaehltesPreset);

            if (gewaehltesPreset != null){
                System.out.println(possibilities.indexOf(gewaehltesPreset));
                if ((boolean) presets.get(possibilities.indexOf(gewaehltesPreset)).get(1)) {
                    special((int) presets.get(possibilities.indexOf(gewaehltesPreset)).get(2));
                }
                else {
                    setPreset((int) presets.get(possibilities.indexOf(gewaehltesPreset)).get(2));
                }
            }else{
                debug("presetswichdialog abgebrochen, " + getPreset());
            }

        }
        else {
            if (getPreset() >= preset.getgesamtpresets()) {
                setPreset(1);
            }
            else {
                setPreset(getPreset() + 1);
            }
        }

    }

    private static void showerrordialog(String message) {
        JOptionPane.showMessageDialog(null, message);
    }

    static void waiting(SerialPort comPort) throws InterruptedException {
        while (comPort.bytesAvailable() == 0) {
            //noinspection BusyWait
            sleep(20);
        }
    }

    public  boolean isExit() {
        return exit;
    }

    public void testnumlock(boolean Konsolenausgabe) {
        //wenn numlock an dann normal nummern sonst anderes zeug

        if (getNumlockOn()) {
            this.setPreset(3, Konsolenausgabe);
        }
        else {
           this. setPreset(4, Konsolenausgabe);
        }
    }

    public boolean getNumlockOn() {
        return numLock;
    }

    public void switchnumlock() {
        numLock = !getNumlockOn();
    }

    public void special(int type) {
        switch (type){
            case 0 -> setExit(true);
            case 1 -> portSuchenDialog();
        }
    }

    public void setPreset(int preset, boolean Konsolenausgabe) {
        if (preset == 0) {
            this.presetNr = preset;
        } else {
            this.presetNr = preset;
            try {
                writePreset(preset);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (Konsolenausgabe) {
            debug(String.format("preset=%d\n", getPreset()), false);
        }
    }

    public int getPreset() {
        return this.presetNr;
    }

    public void setPreset(int preset) {
        setPreset(preset, true);
    }

    public boolean isPresetswitchdialog() {
        return presetSwitchDialog;
    }

    //TODO
    public void setPresetwitchdialog(boolean presetswitchdialog) {
        this.presetSwitchDialog = presetswitchdialog;
    }


    public void setExit(boolean exit) {
        this.exit = exit;
    }



    public static int autoPortSuchen(){
        Object[] tmp =  SerialPort.getCommPorts();
        String[] ports = new String[ tmp.length];
        for (int i = 0; i < ports.length; i++) {
            ports[i] = tmp[i].toString();
        }

        for (int i = 0, portsLength = ports.length; i < portsLength; i++) {
            String port = ports[ i ];
            if (port.equals("USB2.0-Serial") | port.contains("ch341")) {
                return i;
            }
        }
        return -1;
    }


    //methoden zum Speichern in einer config
    
    //really useless
    protected static void writePort(int port) throws IOException {
//        write("portsAlt" , Integer.valueOf(Arrays.toString(SerialPort.getCommPorts())));
        write("port" , port);
    }

    private static void writePreset(int preset) throws IOException {
        write("preset" , preset);
    }

    private static int initializePreset() {
        int port = 1 ;

        try {
            port = Objects.requireNonNull(read("preset"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return port;
    }

    private static File getConfig() {
        String os = System.getProperty("os.name").toLowerCase();
        //TODO relative Pfade

        if (os.contains("linux")){
            return  new File("/home/erebur/Documents/Programmieren/eigengebrauch/macropad/config");
        }else if(os.contains("win")){
            return  new File("C:\\Users\\simon\\OneDrive\\Dokumente\\Programmieren\\eigengebrauch\\macropad\\config");
        }
        return new File("./config");
    }

    private static Integer read(String suchen) throws IOException {

        //entweder lesen oder schreiben
        if (read().containsKey(suchen)){
            return read().get(suchen);
        }else {
            throw new RuntimeException("not Found" + suchen);
        }
    }

    private static LinkedHashMap<String, Integer> read() throws IOException {

        FileReader fr;
        fr = new FileReader(CONFIG);
        Scanner configScanner = new Scanner(fr);
        LinkedHashMap<String , Integer> daten = new LinkedHashMap<>();

        while (configScanner.hasNextLine()){
            var tmp = configScanner.nextLine();
            if(!tmp.isEmpty()) {
                daten.put(tmp.substring(0 , tmp.indexOf(TRENNZEICHEN)), Integer.parseInt(String.valueOf(tmp.charAt(tmp.length() - 1 ))));
            }
        }

        fr.close();
        configScanner.close();
        return daten;
    }


    private static void write(String suchen , Integer schreiben ) throws IOException {
        LinkedHashMap<String, Integer> daten = read();

        if (daten.containsKey(suchen)){
            daten.replace(suchen , schreiben);
        }else{
            daten.put(suchen , schreiben);
        }

        StringBuilder neu = new StringBuilder();
        String[] list = daten.keySet().toArray(new String[0]);

        for (String s : list) {
            neu.append(String.format("%s:%s\n", s, daten.get(s)));
        }

        PrintWriter pw = new PrintWriter(new FileWriter(CONFIG));
        pw.write(String.valueOf(neu));
        pw.close();
    }

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
}
