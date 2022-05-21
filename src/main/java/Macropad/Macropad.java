package Macropad;

import com.fazecast.jSerialComm.SerialPort;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

import static java.lang.Thread.sleep;

@SuppressWarnings("BusyWait")
public class Macropad {

    //0 aus // 1 console // 2 pop up // 3 (1 + 2)
    private static final String OS = System.getProperty("os.name").toLowerCase();
    private static int debugLevel;
    private final Config config;
    private int presetNr;
    //wird versucht automatisch dem Port zu suchen (error = -1 )
    private int port;
    private final boolean presetSwitchDialog;
    private boolean exit;

    public Macropad() {
//        todo use parameters
        this.config = Config.getConfig();
        this.exit = false;
        this.presetSwitchDialog = true;
        this.port = portSearch();
        this.presetNr = config.getPreset() - 1;
        debugLevel = config.getDebugLevel();
    }

    public static void main(String[] args) throws InterruptedException {
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
     *
     * @param scanner reads the next line from the scanner and
     * @return a number
     */
    private static int nextNumber(Scanner scanner) {
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

    static void debug(String message) {
        debug(message, true);
    }

    public static void debug(String message, boolean formatting) {
        String errorFormated = formatting ? String.format("%s\n", message) : message;
        //0 aus // 1 console // 2 pop up // 3 (1 + 2)
        switch (debugLevel) {
            case 1 -> System.out.print(errorFormated);
            case 2 -> alertdialog(errorFormated);
            case 3 -> {
                System.out.print(errorFormated);
                alertdialog(errorFormated);
            }
        }
    }

    private static void alertdialog(String message) {
        JOptionPane.showMessageDialog(null, message);
    }

    /**
     * @return the port with the name the device could have <br> or -1
     */
    public static int portSearch() {
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

    public void start() throws InterruptedException {

        // bei falscher eingabe wartet das programm ewig auf eingabe durch serial Port bekommt aber nie etwas -> das programm macht nicht und man kann nicht beenden (was ungünstig ist lol)
        if (port == -1) {
            portSuchenDialog();
        }

        //testing the device

        while (!exit) {
            SerialPort comPort = null;
            //serial port reader

            debug("Started");
            //öffnet den ausgewählten port
            comPort = SerialPort.getCommPorts()[port];
            comPort.openPort();

            ArrayList<Integer> oldInput = new ArrayList<>();
            execCMD:
            while (!exit) {
                Scanner s = new Scanner(comPort.getInputStream());
//                  Waiting for input
                while (true) {
                    if (comPort.bytesAvailable() != 0) break;
                }
                var input = nextNumber(s);
                Command command = new Command(config.getCommands().get(presetNr).get(input - 2));


//                  Allows to release a command e.g. a Keypress
                for (int i = 0; i < oldInput.size(); i++) {
                    if (input == oldInput.get(i)) {
                        oldInput.remove(i);
                        command.release(this);
                        continue execCMD;
                    }
                }

                debug(String.valueOf(input));
                oldInput.add(input);
                command.execute(this);
            }
        }

        debug("exited");
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
                config.setPort(port);
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

        if (isPresetswitchdialog()) {
            ArrayList<String> possibilities = config.getPresetNames();
            possibilities.add("exit");


            String gewaehltesPreset = (String) JOptionPane
                    .showInputDialog(null, String.format("Preset wählen (aktuell = %s )", config.getPresetNames().get(presetNr)), "Preset", JOptionPane.QUESTION_MESSAGE, null, possibilities.toArray(), "1");


            debug("%s %s".formatted(gewaehltesPreset, gewaehltesPreset != null ? String.valueOf(possibilities.indexOf(gewaehltesPreset)) : "presetswichdialog abgebrochen, " + getPreset()));

            exit = Objects.equals(gewaehltesPreset, "exit");

            possibilities.clear();

        } else {
            if (getPreset() >= config.getCommands().size())
                setPreset(1);
            else
                setPreset(getPreset() + 1);
        }
    }

    public void setExit(boolean exit) {
        this.exit = exit;
    }

    public int getPreset() {
        return this.presetNr;
    }

    public void setPreset(int presetNr) {
        this.presetNr = presetNr;
        debug(String.format("preset=%d\n", getPreset()), false);
    }

    public boolean isPresetswitchdialog() {
        return presetSwitchDialog;
    }
}