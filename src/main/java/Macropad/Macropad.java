package Macropad;

import com.fazecast.jSerialComm.SerialPort;
import lombok.SneakyThrows;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

import static java.lang.Thread.sleep;

public class Macropad {

    //0 aus // 1 console // 2 pop up // 3 (1 + 2)
    private static final String OS = System.getProperty("os.name").toLowerCase();
    private static int debugLevel;
    private final Config config;
    private int presetNr;
    private int port;
    private final boolean presetSwitchDialog;
    private boolean exit;
    //Offset because Arduino wiring is slightly off
    public int offset;

    public Macropad() {
        this.config = Config.getConfig();
        this.exit = false;
        this.presetSwitchDialog = true;
        this.port = portSearch();
        this.presetNr = config.getPreset() - 1;
        debugLevel = config.getDebugLevel();
        offset = config.getOffset();
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
     *
     * @param scanner reads the next line and
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

    @SneakyThrows
    public void start() {

        // bei falscher eingabe wartet das programm ewig auf eingabe durch serial Port bekommt aber nie etwas -> das programm macht nicht und man kann nicht beenden (was ungünstig ist lol)
        if (port == -1) {
            portSuchenDialog();
        }

        //testing the device

        while (!exit) {
            SerialPort comPort;
            //serial port reader

            debug("Started", 1);
            //öffnet den ausgewählten port
            comPort = SerialPort.getCommPorts()[port];
            comPort.openPort();

            ArrayList<Integer> oldInput = new ArrayList<>();
            execCMD:
            while (!exit) {
                Scanner s = new Scanner(comPort.getInputStream());
//                  Waiting for input
                while (comPort.bytesAvailable() == 0)
//                  the shorter you wait the more cpu usage u have
                    //noinspection BusyWait
                    sleep(20);
                var input = nextNumber(s);
                Command command = new Command(config.getCommands().get(presetNr).get(input - 1 + offset));


//                  Allows to release a command e.g. a Keypress
                for (int i = 0; i < oldInput.size(); i++) {
                    if (input == oldInput.get(i)) {
                        oldInput.remove(i);
                        new Thread(() -> command.release(this)).start();
                        continue execCMD;
                    }
                }

                debug(String.valueOf(input), 3);
                oldInput.add(input);
                new Thread(() -> command.execute(this)).start();
            }
        }

        debug("exited");
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
                debug(String.format("Started with port %d and preset %d", port, presetNr), 2);
                comPort.closePort();
                return;
            } catch (NumberFormatException e) {
                debug("Bitte Nummer eingeben", 1);
            } catch (Exception e) {
                debug("Fehler" + e.getMessage(), 1);
            }
            //solte tmp null sein wurde warscheinlich abgebrochen
        } while (input != null);
        setExit(true);
    }

    private void error(SerialPort comPort, Throwable e) {
        debug(String.format("\t%s \n%s", "a error occurred restarting with 10sec delay", e.toString()), 1);

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


            String gewaehltesPreset = (String) JOptionPane.showInputDialog(null, String.format("Preset wählen (aktuell = %s )", config.getPresetNames().get(presetNr)), "Preset", JOptionPane.QUESTION_MESSAGE, null, possibilities.toArray(), "1");


            debug("%s %s".formatted(gewaehltesPreset, gewaehltesPreset != null ? String.valueOf(possibilities.indexOf(gewaehltesPreset)) : "presetswichdialog abgebrochen, " + getPreset()), 2);

            exit = Objects.equals(gewaehltesPreset, "exit");

            possibilities.clear();

        } else {
            if (getPreset() >= config.getCommands().size()) setPreset(1);
            else setPreset(getPreset() + 1);
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
        debug(String.format("preset = %d\n", getPreset()), false, 2);
    }

    public boolean isPresetswitchdialog() {
        return presetSwitchDialog;
    }
}