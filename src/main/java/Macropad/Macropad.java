package Macropad;

import com.fazecast.jSerialComm.SerialPort;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

import static java.lang.Thread.sleep;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

public class Macropad {
    private final boolean presetSwitchDialog;
    @Getter
    @Setter
    private int port;
    private volatile Config config;
    private Thread main;
    private Thread fileWatcher;
    private SerialPort comPort;

    public Macropad() {
        this.presetSwitchDialog = true;
        this.port = portSearch();
        try {
            this.config = Config.getConfig();
        } catch (Throwable e) {
            stop();
            debug("Config faulty", 1);
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
        if (getPort() == -1) {
            portSearchDialog();
        }

        comPort = SerialPort.getCommPorts()[getPort()];
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
    public void macropad() {
        ArrayList<Integer> oldInput = new ArrayList<>();
        execCMD:
        while (true) {
            Scanner s = new Scanner(comPort.getInputStream());
            while (comPort.bytesAvailable() == 0) {
//                the shorter you wait the more cpu usage u have
                try {
                    //noinspection BusyWait
                    sleep(20);
                } catch (InterruptedException e) {
                    break execCMD;
                }
            }
            var input = nextNumber(s);
            if (!(config.getCommands().get(getPreset()).size() - 1 >= input - 1 + config.getOffset())) {
                debug("command not found", 1);
//                remove the next number from query
                continue execCMD;
            }
            Command command = new Command(config.getCommands().get(getPreset()).get(input - 1 + config.getOffset()));
//              Allows to release a command e.g. a Keypress
            for (int i = 0; i < oldInput.size(); i++) {
                if (input == oldInput.get(i)) {
                    oldInput.remove(i);
                    new Thread(() -> command.release(this)).start();
                    continue execCMD;
                }
            }

            debug(String.valueOf(input-1), 3);
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
                try {
                    key = watchService.take();
                }catch (InterruptedException e){
                    break;
                }

                for (WatchEvent<?> event : key.pollEvents()) {
                    // The filename is the
                    // context of the event.
                    @SuppressWarnings("unchecked") WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    Path filename = ev.context();
//                  the first event name ends with ~
//                  IDK why though
                    if (filename.toString().equals("macropad.conf~")) {
                        reload();
                    }
                    boolean valid = key.reset();
                    if (!valid) {
                        break;
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void stop() {
        fileWatcher.interrupt();
        main.interrupt();
        main = null;
        fileWatcher = null;
        comPort.closePort();
    }

    private void reload() {
        debug("Trying reload", 1);
        try {
            config = Config.getConfig();
            debug("Reloaded", 1);
        } catch (Throwable e) {
            debug("Config faulty", 1);
        }
    }

    void debug(String message, int debugLevel) {
        debug(message, true, debugLevel);
    }

    public void debug(String message, boolean formatting, int debugLevel) {
        String errorFormated = formatting ? String.format("%s\n", message) : message;
        if (config.getDebugLevel() >= debugLevel) config.log(errorFormated);
    }

    //Dialoge
    void presetSwitchDialog() {
        if (isPresetswitchdialog()) {
            @SuppressWarnings("unchecked") ArrayList<String> possibilities = (ArrayList<String>) config.getPresetNames().clone();
            possibilities.add("exit");
            String gewaehltesPreset = (String) JOptionPane.showInputDialog(null, String.format("Preset wählen (aktuell = %s )", config.getPresetNames().get(getPreset())), "Preset", JOptionPane.QUESTION_MESSAGE, null, possibilities.toArray(), "1");

            debug("Preset Switch Dialog: %s".formatted(gewaehltesPreset != null ? gewaehltesPreset : "presetswichdialog abgebrochen, " + getPreset()), 2);

            if (Objects.equals(gewaehltesPreset, "exit"))
                stop();
            else if (possibilities.contains(gewaehltesPreset))
                setPreset(possibilities.indexOf(gewaehltesPreset));
            possibilities.clear();
        } else {
            if (getPreset() >= config.getCommands().size()) setPreset(1);
            else setPreset(getPreset() + 1);
        }
    }

    /**
     * don't no why but we need error correction
     *
     * @param scanner reads the next line and
     * @return a number
     */
    private static int nextNumber(Scanner scanner) throws NoSuchElementException {
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

    public void portSearchDialog() {
        SerialPort comPort;
        String input;
        do {
            input = JOptionPane.showInputDialog(null, Arrays.toString(SerialPort.getCommPorts()));
            try {
                setPort(Integer.parseInt(input));
                comPort = SerialPort.getCommPorts()[getPort()];
                comPort.openPort();
                debug(String.format("Started with port %d and preset %d", getPort(), getPreset()), 2);
                comPort.closePort();
                return;
            } catch (NumberFormatException e) {
                debug("Bitte Nummer eingeben", 1);
            } catch (Exception e) {
                debug("Fehler" + e.getMessage(), 1);
            }
            //solte tmp null sein wurde warscheinlich abgebrochen
        } while (input != null);
        stop();
    }

    public int getPreset() {
        return config.getPreset() - 1;
    }

    public void setPreset(int presetNr) {
        config.setPreset(presetNr + 1);
        debug(String.format("preset = %d\n", getPreset()), false, 2);
    }

    public boolean isPresetswitchdialog() {
        return presetSwitchDialog;
    }
}