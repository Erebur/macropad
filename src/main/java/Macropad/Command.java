package Macropad;

import lombok.Getter;
import lombok.Setter;

import javax.swing.*;

@Getter
@Setter
public class Command {

    private String command;

    public Command(String command) {
        this.command = command;
    }

    public Command() {
    }

    public void release(Macropad macropad) {
    }

    public void execute(Macropad macropad) {
        try {
            switch (command.substring(0,2)) {
                case "MA" -> {
                    //todo implement check for this
                    macropad.presetswichdialog();
                }
                case "KS" ->{
                    //todo implement Keystrokes
                }
                default -> {
                    try {
                        Process process = Runtime.getRuntime().exec(command);
                    } catch (Throwable ignored) {}
                }
            }
            {
                JOptionPane pane = new JOptionPane("test");
                pane.setVisible(true);
                Timer timer = new Timer(90000, e -> {
                    pane.setVisible(false);
                });
                timer.setRepeats(false);
                timer.start();
            }
        }catch (Throwable ignored){}

    }
}
