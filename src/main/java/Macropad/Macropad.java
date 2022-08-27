package Macropad;

import com.fazecast.jSerialComm.SerialPort;

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
    private final boolean presetSwitchDialog;
    private volatile Config config;
    private Thread buttonListener;
    private Thread fileWatcher;
    private SerialPort comPort;

    public Macropad() {
        this.presetSwitchDialog = true;
        try {
            this.config = Config.getConfig();
        } catch (Throwable e) {
            debug("Config faulty", 1);
        }
        start();
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
        System.out.println(Arrays.toString(args));
//        todo flags
        new Macropad();
    }

    /**
     * don't no why but we need error correction
     *
     * @param scanner reads the next line and
     * @return a number
     */
    private static int nextNumber(Scanner scanner) {
        String eingabe;
        if (scanner.hasNext()) {
            eingabe = scanner.nextLine();
        }else {
            throw new RuntimeException("no input available");
        }
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

    public void start() {
//      port konnte nicht gefunden werden
        if (!openPort()) debug("Could not open Port", 1);

        debug("Started", 1);
        buttonListener = new Thread(this::buttonListener);
        fileWatcher = new Thread(this::fileWatcher);

        buttonListener.start();
        fileWatcher.start();
    }

    public void stop() {
        for (Thread thread : Arrays.asList(fileWatcher, buttonListener)) {
            try {
                thread.interrupt();
            } catch (Throwable ignored) {
            }
        }
        buttonListener = null;
        fileWatcher = null;
        comPort.closePort();
    }

    public void restartBL(){
        buttonListener.interrupt();
        buttonListener = null;
        openPort();
        buttonListener = new Thread(this::buttonListener);
        buttonListener.start();
    }

    /**
     * Main method that handels Button-presses
     */
    public void buttonListener() {
        try {
            ArrayList<Integer> oldInput = new ArrayList<>();
            execCMD:
            while (true) {
                Scanner s = new Scanner(comPort.getInputStream());
                while (comPort.bytesAvailable() <= 0) {
                    try {
//                the shorter you wait the more cpu usage u have
                        if (comPort.bytesAvailable() == -1) {
                            String description = comPort.getPortDescription();
                            System.out.println(Arrays.toString(SerialPort.getCommPorts()));
                            comPort.closePort();
//                            waits till the macropad gets reconected
//                            todo should timeout after some time
                            while (Arrays.stream(SerialPort.getCommPorts()).
                                    noneMatch(serialPort -> Objects.equals(serialPort.getPortDescription(), description))) {
                                System.out.println(Arrays.toString(SerialPort.getCommPorts()));
                                //noinspection BusyWait
                                sleep(200);
                            }
//                            just restart wtf
                            restartBL();
                        }

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
                    continue;
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

                debug(String.valueOf(input - 1), 3);
                oldInput.add(input);
                new Thread(() -> command.execute(this)).start();
            }
            debug("exited", 1);
        } catch (Throwable e) {
            System.out.println(e.getMessage());
            stop();
        }
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
                } catch (InterruptedException e) {
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

            if (Objects.equals(gewaehltesPreset, "exit")) stop();
            else if (possibilities.contains(gewaehltesPreset)) setPreset(possibilities.indexOf(gewaehltesPreset));
            possibilities.clear();
        } else {
            if (getPreset() >= config.getCommands().size()) setPreset(1);
            else setPreset(getPreset() + 1);
        }
    }

    /**
     * Search for the port to be used. Tries name from the config, then fallback to user input <br>
     * Then opens the port
     *
     * @return false if the port could not be opened
     */
    public boolean openPort() {
        ArrayList<String> ports = new ArrayList<>(Arrays.stream(SerialPort.getCommPorts()).map(SerialPort::getPortDescription).toList());
//        search ports automatically w/name from config

        if (Objects.nonNull(config.getPort()))
            for (int i = 0; i < ports.size(); i++) {
                String port = ports.get(i);
                if (port.contains(config.getPort()) && testPort(i)) {
                    return true;
                }
            }

//        search port wia user input
        String input;
        do {
            input = JOptionPane.showInputDialog(null, Arrays.toString(ports.toArray()));
//          null => user canceled
            if (Objects.equals(input, null)) break;
//          can't use something not a number
            if (!input.matches("\\d+")) continue;
            if (testPort(Integer.parseInt(input) - 1)) {
                // TODO: 8/23/22 method to save config back
                //  config.setPort(ports.get(Integer.parseInt(input)));
                debug(String.format("Started with preset %d", getPreset()), 2);
                return true;
            }
        } while (true);

        return false;
    }

    private boolean testPort(int port) {
        comPort = SerialPort.getCommPorts()[port];
        boolean openPort = comPort.openPort();
        if (!openPort) comPort.closePort();
        return openPort;
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