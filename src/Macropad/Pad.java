package Macropad;

public class Pad {
    public static void main(String[] args) {
        Macropad m = new Macropad();
        try {
            m.start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
