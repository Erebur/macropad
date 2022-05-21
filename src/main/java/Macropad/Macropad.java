package Macropad;

import com.fazecast.jSerialComm.SerialPort;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import static java.lang.Thread.sleep;

@SuppressWarnings("BusyWait")
public class Macropad {

    //0 aus // 1 console // 2 pop up // 3 (1 + 2)
    private static final int DEBUG_LEVEL = 1;
    private static final String OS = System.getProperty("os.name").toLowerCase();
    private static final Config CONFIG = Config.getConfig();
    private int presetNr;
    //wird versucht automatisch dem Port zu suchen (error = -1 )
    private int port;
    private boolean presetSwitchDialog;
    //speichert den status des numlocks → nur am anfang des Programms aktuell → fängt keine änderungen ab
    private boolean numLock;
    private boolean exit;

    public Macropad() {
        this.exit = false;
        this.numLock = Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_NUM_LOCK);
        this.presetSwitchDialog = true;
        this.port = autoPortSuchen();
        this.presetNr = CONFIG.getPreset() - 1;
    }

    public static void main(String[] args) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("com.sun.java.swing.plaf.gtk.GTKLookAndFeel".equals(info.getClassName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {
        }
        new Macropad().start();
    }

    /**
     * don't no why but we need error correction
     * @param scanner reads the next line from the scanner and
     * @return a number
     */
    private static int testForNumber(Scanner scanner) {
        String eingabe = scanner.nextLine();

        int input = 0;

        for (int i = 0; i < eingabe.length(); i++) {
            try {
                eingabe = eingabe.substring(0, eingabe.length() - i);
                input = Integer.parseInt(eingabe);
                break;
            } catch (Exception ignored) {
            }

        }
        return input;
    }

    static void debug(String errorMessage) {
        debug(errorMessage, true);
    }

    @SuppressWarnings("ConstantConditions")
    public static void debug(String errorMessage, boolean formating) {
        String errorFormated = formating ? String.format("%s\n", errorMessage) : errorMessage;
        //0 aus // 1 console // 2 pop up // 3 (1 + 2)
        switch (DEBUG_LEVEL) {
            case 1 -> System.out.print(errorFormated);
            case 2 -> showerrordialog(errorFormated);
            case 3 -> {
                System.out.print(errorFormated);
                showerrordialog(errorFormated);
            }
        }
    }

    private static void showerrordialog(String message) {
        JOptionPane.showMessageDialog(null, message);
    }

    public static int autoPortSuchen() {
        Object[] tmp = SerialPort.getCommPorts();
        String[] ports = new String[tmp.length];
        for (int i = 0; i < ports.length; i++) {
            ports[i] = tmp[i].toString();
        }

        for (int i = 0; i < ports.length; i++) {
            String port = ports[i];
            if (port.equals("USB2.0-Serial") | port.contains("ch341")) {
                return i;
            }
        }
        return -1;
    }

    public void start() {

        // bei falscher eingabe wartet das programm ewig auf eingabe durch serial Port bekommt aber nie etwas -> das programm macht nicht und man kann nicht beenden (was ungünstig ist lol)
        if (port == -1) {
            portSuchenDialog();
        }

        //testing the device

        while (!exit) {
            SerialPort comPort = null;
            //serial port reader
            try {
                debug("Started");
                //öffnet den ausgewählten port
                comPort = SerialPort.getCommPorts()[port];
                comPort.openPort();

                ArrayList<Integer> oldInput = new ArrayList<>();
                while (!exit) {


                    Scanner s = new Scanner(comPort.getInputStream());
//                  Waiting for input
                    while (comPort.bytesAvailable() == 0) {
                        //noinspection BusyWait
                        sleep(20);
                    }
                    var input = testForNumber(s);
                    Command command = new Command(CONFIG.getCommands().get(presetNr).get(input - 2));

                    boolean matched = false;
//                  Allows to release a command e.g. a Keypress
                    for (int i = 0; i < oldInput.size(); i++) {
                        if (input == oldInput.get(i)) {
                            oldInput.remove(i);
                            i--;
                            matched = true;
                            command.release(this);
                        }
                    }

                    if (!matched) {
                        debug(String.valueOf(input));
                        oldInput.add(input);
                        command.execute(this);
                    }
                }

//                //drückt tasten lol
//                Keypress keypress = new Keypress(comPort , this);
//                keypress.start();

            } catch (Exception e) {
                e.printStackTrace();
                error(comPort, e);
                //wär doch ungünstig, wenn man errors(mit Pop-ups) ohne Ende bekommen würde und man nicht abbrechen kann lol
                presetswichdialog();
            }
        }

        if (!portTest()) {
            debug("can't get output from " + SerialPort.getCommPorts()[port].getSystemPortName());
        } else {
            //exit dialog
            JOptionPane.showMessageDialog(null, "exited");
        }
    }

    private boolean portTest() {

        SerialPort comPort = null;
        comPort = SerialPort.getCommPorts()[port];
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
                CONFIG.setPort(port);
                debug(String.format("Started with port %d and preset %d", port, presetNr));
                comPort.closePort();
                return;
            } catch (NumberFormatException e) {
                debug("Bitte Nummer eingeben");
            } catch (Exception e) {
                debug("Fehler" + e.getMessage());
            }
            //solte tmp null sein wurde warscheinlich abgebrochen
        } while (input != null);
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

    //Dialoge
    void presetswichdialog() {
        ArrayList<String> possibilities = CONFIG.getPresetNames();
        possibilities.add("Exit");

        if (isPresetswitchdialog()) {
            String gewaehltesPreset = (String) JOptionPane
                    .showInputDialog(null, String.format("Preset wählen (aktuell = %s )", CONFIG.getPresetNames().get(presetNr)), "Preset", JOptionPane.QUESTION_MESSAGE, null, possibilities.toArray(), "1");


            debug(gewaehltesPreset +(gewaehltesPreset != null ? String.valueOf(possibilities.indexOf(gewaehltesPreset)) : "presetswichdialog abgebrochen, " + getPreset()));


        } else {
            if (getPreset() >= CONFIG.getCommands().size())
                setPreset(1);
            else
                setPreset(getPreset() + 1);
        }

    }

    public boolean isExit() {
        return exit;
    }

    public void setExit(boolean exit) {
        this.exit = exit;
    }

    public void testnumlock(boolean Konsolenausgabe) {
        //wenn numlock an dann normal nummern sonst anderes zeug

        if (getNumlockOn()) {
            this.setPreset(3, Konsolenausgabe);
        } else {
            this.setPreset(4, Konsolenausgabe);
        }
    }

    public boolean getNumlockOn() {
        return numLock;
    }


    //methoden zum Speichern in einer config

    public void switchnumlock() {
        numLock = !getNumlockOn();
    }

    public void special(int type) {
        switch (type) {
            case 0 -> setExit(true);
            case 1 -> portSuchenDialog();
        }
    }

    public void setPreset(int preset, boolean Konsolenausgabe) {
        this.presetNr = preset;

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
}
