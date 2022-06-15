package Macropad;

import com.fazecast.jSerialComm.SerialPort;
import lombok.SneakyThrows;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

import static java.lang.Thread.sleep;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

public class Macropad {
    private static int debugLevel;
    private volatile Config config;
    private final boolean presetSwitchDialog;
    //Offset because Arduino wiring is slightly off
    public int offset;
    private int presetNr;
    private int port;
    private boolean exit;
    private Thread main;
    private Thread fileWatcher;
    private SerialPort comPort;

    public Macropad() {
        this.presetSwitchDialog = true;
        try {
            this.config = Config.getConfig();
            this.port = portSearch();
            this.presetNr = config.getPreset() - 1;
            debugLevel = config.getDebugLevel();
            offset = config.getOffset();
        }catch (Throwable e){
            stop();
            debug("Config faulty" , 1);
        }
    }

    public static void main(String[] args) {
//        todo: toggle for GTK
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("com.sun.java.swing.plaf.gtk.GTKLookAndFeel".equals(info.getClassName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {
        }
        new Macropad().init();
    }

    public void init() {
        // bei falscher eingabe wartet das programm ewig auf eingabe durch serial Port bekommt aber nie etwas -> das programm macht nicht und man kann nicht beenden (was ungünstig ist lol)
        if (port == -1) {
            portSuchenDialog();
        }

        comPort = SerialPort.getCommPorts()[port];
        comPort.openPort();
        debug("Started", 1);
        main = new Thread(this::macropad);

        fileWatcher = new Thread(this::fileWatcher);

        main.start();
        fileWatcher.start();
    }

    /**
     * Main method that handels Button-presses
     */
    @SneakyThrows
    public void macropad() {
        ArrayList<Integer> oldInput = new ArrayList<>();
        Thread thisThread = Thread.currentThread();
        execCMD:
        while (main == thisThread) {
            Scanner s = new Scanner(comPort.getInputStream());
            while (comPort.bytesAvailable() == 0 ){
//
//                the shorter you wait the more cpu usage u have
                //noinspection BusyWait
                sleep(20);
            }
            var input = nextNumber(s);
            Command command = new Command(config.getCommands().get(presetNr).get(input - 1 + offset));

//              Allows to release a command e.g. a Keypress
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
        debug("exited", 1);
    }

    private void fileWatcher() {
        WatchService watchService;
        try {
            watchService = FileSystems.getDefault().newWatchService();
            Path path = Path.of(String.join(System.getProperty("file.separator"), System.getProperty("user.home"), ".config", "macropad"));
            path.register(watchService, ENTRY_MODIFY);
            
            while (fileWatcher == Thread.currentThread()) {
                WatchKey key;
                key = watchService.take();

                for (WatchEvent<?> event : key.pollEvents()) {
                    // The filename is the
                    // context of the event.
                    @SuppressWarnings("unchecked") WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    Path filename = ev.context();
//                the first event name ends with ~
//                IDK why though
                    if (filename.toString().equals("macropad.conf~")) {
                        reload();
                    }
                    boolean valid = key.reset();
                    if (!valid) {
                        break;
                    }
                }
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void stop() {
        main = null;
        fileWatcher = null;
    }

    private void reload() {
        debug("Trying reload" , 1);
        try {
            config = Config.getConfig();
            debug("Reloaded" , 1);
        }catch (Throwable e){
            debug("Config faulty" , 1);
        }
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

    void debug(String message, int debugLevel) {
        debug(message, true, debugLevel);
    }

    public void debug(String message, boolean formatting, int debugLevel) {
        String errorFormated = formatting ? String.format("%s\n", message) : message;
        if (Macropad.debugLevel >= debugLevel) config.log(errorFormated);
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

            if (Objects.equals(gewaehltesPreset, "exit"))
                stop();

            possibilities.clear();

        } else {
            if (getPreset() >= config.getCommands().size()) setPreset(1);
            else setPreset(getPreset() + 1);
        }
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