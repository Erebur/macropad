package Macropad;

import javax.swing.*;
import java.io.IOException;

public class Pad {
    public static void main(String[] args) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
        //Linux GTK support
        for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
            if ("com.sun.java.swing.plaf.gtk.GTKLookAndFeel".equals(info.getClassName())) {
                javax.swing.UIManager.setLookAndFeel(info.getClassName());
                break;
            }
        }

        new Macropad().start();
    }
}
