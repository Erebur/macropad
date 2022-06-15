package Macropad;

import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;

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
            switch (command.substring(0, 2)) {
                case "MA" -> {
                    //todo implement check for this / multiple options
                    macropad.presetSwitchDialog();
                }
                case "KS" -> {
                    String[] strokes = command.substring(3).split("\\+");
                    Robot robot = new Robot();
                    for (String stroke : strokes)
                        robot.keyPress(KeyStroke.getKeyStroke(stroke).getKeyCode());
                    for (String stroke : strokes)
                        robot.keyRelease(KeyStroke.getKeyStroke(stroke).getKeyCode());
                }
                case "SE" -> {
                    String[] strokes = command.substring(3).split("\\+");
                    Robot robot = new Robot();
                    for (String stroke : strokes){
                        robot.keyPress(KeyStroke.getKeyStroke(stroke).getKeyCode());
                        robot.keyRelease(KeyStroke.getKeyStroke(stroke).getKeyCode());
                    }
                }
                default -> Runtime.getRuntime().exec(command.startsWith("CL") ? command.substring(3) : command);
            }
//            {
//                JOptionPane pane = new JOptionPane("test");
//                pane.setVisible(true);
//                Timer timer = new Timer(90000, e -> pane.setVisible(false));
//                timer.setRepeats(false);
//                timer.start();
//            }
        } catch (Throwable ignored) {}

    }
}