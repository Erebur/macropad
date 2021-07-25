package Testing;

import javax.swing.*;

import Macropad.Macropadmain;
import Macropad.Preset;

import java.util.ArrayList;
import java.util.HashMap;


public class presetswich extends JFrame {
    Preset pr = Macropadmain.preset;

    public presetswich(String title ) {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle(title);
        setSize(500, 500);

        //JTable
        HashMap<Integer , ArrayList<Integer>> preset = pr.getActivePreset();
        String [][]                           inhalt = new String[2][preset.size()] ;
        for (int i = 2; i < inhalt[0].length; i++) {
            inhalt[0][i] = String.valueOf(i);
            inhalt[1][i] = String.valueOf(preset.get(i));
        }

        for (int i = 0; i < inhalt[0].length; i++) {
            System.out.printf("%s , %s \n" , inhalt[0][i] , inhalt[1][i]);
        }
        String[] titel = {"Interpret", "Titel"};
        JTable table = new JTable(inhalt, titel);


        add(new JScrollPane(table)); // ohne JScrollPane keine Titel!

        setVisible(true);
    }

}
