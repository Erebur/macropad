package Macropad;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;

public class Preset {

    //https://docs.microsoft.com/en-us/windows/win32/inputdev/virtual-key-codes
    //https://docs.oracle.com/javase/7/docs/api/java/awt/event/KeyEvent.html
    private final ArrayList<HashMap<Integer, ArrayList<Integer>>> presets = new ArrayList<>();


    public Preset() {
        this(System.getProperty("os.name").toLowerCase());
    }

    public Preset(String os) {

        //In array form auch mit  Array als rückgabe wert
        //bruh initialisierung 100
            if (os.equals("win")){

            //preset 1 functions
            presets.add(new HashMap<>() {{
                //reihe 1
                put(2 , new ArrayList<>(){{add(KeyEvent.VK_WINDOWS);}});
                put(3 , new ArrayList<>(){{add(KeyEvent.VK_WINDOWS);}});
                put(4 , new ArrayList<>(){{add(KeyEvent.VK_F14);}});
                put(5 , new ArrayList<>(){{add(KeyEvent.VK_F15);}});
                //reihe 2
                put(6 , new ArrayList<>(){{add(KeyEvent.VK_CONTROL);}});
                put(7 , new ArrayList<>(){{add(KeyEvent.VK_CONTROL);}});
                //music
                put(8 , new ArrayList<>(){{add(KeyEvent.VK_CONTROL);}});
                put(9 , new ArrayList<>(){{add(KeyEvent.VK_CONTROL);}});
                //reihe 4
                put(10 , new ArrayList<>(){{add(0);}});
                put(11 , new ArrayList<>(){{add(KeyEvent.VK_CONTROL);}});
                put(12 , new ArrayList<>(){{add(KeyEvent.VK_CONTROL);}});
                put(13 , new ArrayList<>(){{add(KeyEvent.VK_CONTROL);}});
            }});
            //preset 2 wasd
            presets.add(new HashMap<>() {{
                //reihe 1
                put(2 , new ArrayList<>(){{add(KeyEvent.VK_A);}});
                put(3 , new ArrayList<>(){{add(KeyEvent.VK_S);}});
                put(4 , new ArrayList<>(){{add(KeyEvent.VK_D);}});
                put(5 , new ArrayList<>(){{add(KeyEvent.VK_F);}});
                //reihe 2
                put(6 , new ArrayList<>(){{add(KeyEvent.VK_Q);}});
                put(7 , new ArrayList<>(){{add(KeyEvent.VK_W);}});
                put(8 , new ArrayList<>(){{add(KeyEvent.VK_E);}});
                put(9 , new ArrayList<>(){{add(KeyEvent.VK_R);}});
                //reihe 3
                put(10 , new ArrayList<>(){{add(0);}});
                put(11 , new ArrayList<>(){{add(KeyEvent.VK_1);}});
                put(12 , new ArrayList<>(){{add(KeyEvent.VK_2);}});
                put(13 , new ArrayList<>(){{add(KeyEvent.VK_DELETE);}});}});
            //preset 3 numpad
            presets.add(new HashMap<>() {{
                //reihe 1
                put(2 , new ArrayList<>(){{add(KeyEvent.VK_NUMPAD1);}});
                put(3 , new ArrayList<>(){{add(KeyEvent.VK_NUMPAD2);}});
                put(4 , new ArrayList<>(){{add(KeyEvent.VK_NUMPAD3);}});
                put(5 , new ArrayList<>(){{add(KeyEvent.VK_NUMPAD0);}});
                //reihe 2
                put(6 , new ArrayList<>(){{add(KeyEvent.VK_NUMPAD4);}});
                put(7 , new ArrayList<>(){{add(KeyEvent.VK_NUMPAD5);}});
                put(8 , new ArrayList<>(){{add(KeyEvent.VK_NUMPAD6);}});
                put(9 , new ArrayList<>(){{add(0);}});
                //reihe 3
                put(10 , new ArrayList<>(){{add(KeyEvent.VK_NUMPAD7);}});
                put(11 , new ArrayList<>(){{add(KeyEvent.VK_NUMPAD8);}});
                put(12 , new ArrayList<>(){{add(KeyEvent.VK_NUMPAD9);}});
                put(13 , new ArrayList<>(){{add(KeyEvent.VK_NUM_LOCK);}});}});
            //preset 4 wenn numlock aus
            presets.add(new HashMap<>() {{
                put(2 , new ArrayList<>(){{add(KeyEvent.VK_END);}});
                put(3 , new ArrayList<>(){{add(KeyEvent.VK_DOWN);}});
                put(4 , new ArrayList<>(){{add(KeyEvent.VK_PAGE_DOWN);}});
                put(5 , new ArrayList<>(){{add(KeyEvent.VK_ENTER);}});
                //reihe 2
                put(6 , new ArrayList<>(){{add(KeyEvent.VK_LEFT);}});
                put(7 , new ArrayList<>(){{add(KeyEvent.VK_NUMPAD5);}});//Kein plan was das machen soll
                put(8 , new ArrayList<>(){{add(KeyEvent.VK_RIGHT);}});
                put(9 , new ArrayList<>(){{add(0);}});
                //reihe 3
                put(10 , new ArrayList<>(){{add(KeyEvent.VK_HOME);}});
                put(11 , new ArrayList<>(){{add(KeyEvent.VK_UP);}});
                put(12 , new ArrayList<>(){{add(KeyEvent.VK_PAGE_UP);}});
                put(13 , new ArrayList<>(){{add(KeyEvent.VK_NUM_LOCK);}});}});
            //preset 5 music
            presets.add(new HashMap<>() {{
                //win keys
                put(2 , new ArrayList<>(){{add(KeyEvent.VK_WINDOWS);add(KeyEvent.VK_G);}});
                put(3 , new ArrayList<>(){{add(KeyEvent.VK_WINDOWS);add(KeyEvent.VK_TAB);}});
                //discord
                put(4 , new ArrayList<>(){{add(KeyEvent.VK_F14);}});
                put(5 , new ArrayList<>(){{add(KeyEvent.VK_CONTROL);add(KeyEvent.VK_ALT);add(KeyEvent.VK_NUMPAD2);}});
                //geforce Overlay
                put(6 , new ArrayList<>(){{add(KeyEvent.VK_CONTROL);add(KeyEvent.VK_R);}});
                put(7 , new ArrayList<>(){{add(KeyEvent.VK_CONTROL);add(KeyEvent.VK_T);}});
                //music
                put(8 , new ArrayList<>(){{add(KeyEvent.VK_CONTROL);add(KeyEvent.VK_ALT);add(KeyEvent.VK_NUMPAD5);}});
                put(9 , new ArrayList<>(){{add(KeyEvent.VK_CONTROL);add(KeyEvent.VK_ALT);add(KeyEvent.VK_PAGE_DOWN);}});
                put(10 , new ArrayList<>(){{add(0);}});
                put(11 , new ArrayList<>(){{add(KeyEvent.VK_CONTROL);add(KeyEvent.VK_ALT);add(KeyEvent.VK_NUMPAD4);}});
                put(12 , new ArrayList<>(){{add(KeyEvent.VK_CONTROL);add(KeyEvent.VK_ALT);add(KeyEvent.VK_NUMPAD6);}});
                put(13 , new ArrayList<>(){{add(KeyEvent.VK_CONTROL);add(KeyEvent.VK_ALT);add(KeyEvent.VK_PAGE_UP);}});}});
            //preset 6 funktion keys with music
            presets.add(new HashMap<>() {{
                putAll(presets.get(0));
                replace(7 , new ArrayList<>(){{add(KeyEvent.VK_CONTROL);add(KeyEvent.VK_ALT);add(KeyEvent.VK_NUMPAD2);}});


                replace(11 , new ArrayList<>(){{add(KeyEvent.VK_CONTROL);add(KeyEvent.VK_ALT);add(KeyEvent.VK_NUMPAD4);}});
                replace(12 , new ArrayList<>(){{add(KeyEvent.VK_CONTROL);add(KeyEvent.VK_ALT);add(KeyEvent.VK_NUMPAD6);}});

            }});


        }else if (os.equals("linux")){
                //KeyEvent.VK_WINDOWS macht nichts

            //preset 1 functions
            presets.add(new HashMap<>() {{
                //reihe 1
                    //win keys
                put(2 , new ArrayList<>(){{add(KeyEvent.VK_CONTROL);add(KeyEvent.VK_ALT);add(KeyEvent.VK_SHIFT);add(KeyEvent.VK_F1);}});
                put(3 , new ArrayList<>(){{add(KeyEvent.VK_CONTROL);add(KeyEvent.VK_ALT);add(KeyEvent.VK_SHIFT);add(KeyEvent.VK_F2);}});
                put(4 , new ArrayList<>(){{add(KeyEvent.VK_CONTROL);add(KeyEvent.VK_ALT);add(KeyEvent.VK_SHIFT);add(KeyEvent.VK_F3);}});
                put(5 , new ArrayList<>(){{add(KeyEvent.VK_CONTROL);add(KeyEvent.VK_ALT);add(KeyEvent.VK_SHIFT);add(KeyEvent.VK_0);}});
                //reihe 2

                put(6 , new ArrayList<>(){{add(KeyEvent.VK_CONTROL);add(KeyEvent.VK_ALT);add(KeyEvent.VK_SHIFT);add(KeyEvent.VK_F4);}});
                put(7 , new ArrayList<>(){{add(KeyEvent.VK_CONTROL);add(KeyEvent.VK_ALT);add(KeyEvent.VK_SHIFT);add(KeyEvent.VK_F5);}});
                put(8 , new ArrayList<>(){{add(KeyEvent.VK_CONTROL);add(KeyEvent.VK_ALT);add(KeyEvent.VK_SHIFT);add(KeyEvent.VK_F6);}});
                put(9 , new ArrayList<>(){{add(KeyEvent.VK_CONTROL);add(KeyEvent.VK_ALT);add(KeyEvent.VK_SHIFT);add(KeyEvent.VK_MINUS);}});
                //reihe 4
                put(10 , new ArrayList<>(){{add(0);}});
                put(11 , new ArrayList<>(){{add(KeyEvent.VK_CONTROL);add(KeyEvent.VK_ALT);add(KeyEvent.VK_LEFT);}});
                put(12 , new ArrayList<>(){{add(KeyEvent.VK_CONTROL);add(KeyEvent.VK_ALT);add(KeyEvent.VK_RIGHT);}});
                put(13 , new ArrayList<>(){{add(KeyEvent.VK_CONTROL);add(KeyEvent.VK_ALT);add(KeyEvent.VK_SHIFT);add(KeyEvent.VK_PLUS);}});
            }});
            //preset 2 wasd
            presets.add(new HashMap<>() {{
                //reihe 1
                put(2 , new ArrayList<>(){{add(KeyEvent.VK_A);}});
                put(3 , new ArrayList<>(){{add(KeyEvent.VK_S);}});
                put(4 , new ArrayList<>(){{add(KeyEvent.VK_D);}});
                put(5 , new ArrayList<>(){{add(KeyEvent.VK_F);}});
                //reihe 2
                put(6 , new ArrayList<>(){{add(KeyEvent.VK_Q);}});
                put(7 , new ArrayList<>(){{add(KeyEvent.VK_W);}});
                put(8 , new ArrayList<>(){{add(KeyEvent.VK_E);}});
                put(9 , new ArrayList<>(){{add(KeyEvent.VK_R);}});
                //reihe 3
                put(10 , new ArrayList<>(){{add(0);}});
                put(11 , new ArrayList<>(){{add(KeyEvent.VK_1);}});
                put(12 , new ArrayList<>(){{add(KeyEvent.VK_2);}});
                put(13 , new ArrayList<>(){{add(KeyEvent.VK_DELETE);}});}});
            //preset 3 numpad
            presets.add(new HashMap<>() {{
                //reihe 1
                put(2 , new ArrayList<>(){{add(KeyEvent.VK_NUMPAD1);}});
                put(3 , new ArrayList<>(){{add(KeyEvent.VK_NUMPAD2);}});
                put(4 , new ArrayList<>(){{add(KeyEvent.VK_NUMPAD3);}});
                put(5 , new ArrayList<>(){{add(KeyEvent.VK_NUMPAD0);}});
                //reihe 2
                put(6 , new ArrayList<>(){{add(KeyEvent.VK_NUMPAD4);}});
                put(7 , new ArrayList<>(){{add(KeyEvent.VK_NUMPAD5);}});
                put(8 , new ArrayList<>(){{add(KeyEvent.VK_NUMPAD6);}});
                put(9 , new ArrayList<>(){{add(0);}});
                //reihe 3
                put(10 , new ArrayList<>(){{add(KeyEvent.VK_NUMPAD7);}});
                put(11 , new ArrayList<>(){{add(KeyEvent.VK_NUMPAD8);}});
                put(12 , new ArrayList<>(){{add(KeyEvent.VK_NUMPAD9);}});
                put(13 , new ArrayList<>(){{add(KeyEvent.VK_NUM_LOCK);}});}});
            //preset 4 wenn numlock aus
            presets.add(new HashMap<>() {{
                put(2 , new ArrayList<>(){{add(KeyEvent.VK_END);}});
                put(3 , new ArrayList<>(){{add(KeyEvent.VK_DOWN);}});
                put(4 , new ArrayList<>(){{add(KeyEvent.VK_PAGE_DOWN);}});
                put(5 , new ArrayList<>(){{add(KeyEvent.VK_ENTER);}});
                //reihe 2
                put(6 , new ArrayList<>(){{add(KeyEvent.VK_LEFT);}});
                put(7 , new ArrayList<>(){{add(KeyEvent.VK_NUMPAD5);}});//Kein plan was das machen soll
                put(8 , new ArrayList<>(){{add(KeyEvent.VK_RIGHT);}});
                put(9 , new ArrayList<>(){{add(0);}});
                //reihe 3
                put(10 , new ArrayList<>(){{add(KeyEvent.VK_HOME);}});
                put(11 , new ArrayList<>(){{add(KeyEvent.VK_UP);}});
                put(12 , new ArrayList<>(){{add(KeyEvent.VK_PAGE_UP);}});
                put(13 , new ArrayList<>(){{add(KeyEvent.VK_NUM_LOCK);}});}});
            //preset 5 music
            presets.add(new HashMap<>() {{
                //win keys
                put(2 , new ArrayList<>(){{add(KeyEvent.VK_WINDOWS);add(KeyEvent.VK_G);}});
                put(3 , new ArrayList<>(){{add(KeyEvent.VK_WINDOWS);add(KeyEvent.VK_TAB);}});
                //discord
                put(4 , new ArrayList<>(){{add(KeyEvent.VK_F14);}});
                put(5 , new ArrayList<>(){{add(KeyEvent.VK_CONTROL);add(KeyEvent.VK_ALT);add(KeyEvent.VK_NUMPAD2);}});
                //geforce Overlay
                put(6 , new ArrayList<>(){{add(KeyEvent.VK_CONTROL);add(KeyEvent.VK_R);}});
                put(7 , new ArrayList<>(){{add(KeyEvent.VK_CONTROL);add(KeyEvent.VK_T);}});
                //music
                put(8 , new ArrayList<>(){{add(KeyEvent.VK_CONTROL);add(KeyEvent.VK_ALT);add(KeyEvent.VK_NUMPAD5);}});
                put(9 , new ArrayList<>(){{add(KeyEvent.VK_CONTROL);add(KeyEvent.VK_ALT);add(KeyEvent.VK_PAGE_DOWN);}});
                put(10 , new ArrayList<>(){{add(0);}});
                put(11 , new ArrayList<>(){{add(KeyEvent.VK_CONTROL);add(KeyEvent.VK_ALT);add(KeyEvent.VK_NUMPAD4);}});
                put(12 , new ArrayList<>(){{add(KeyEvent.VK_CONTROL);add(KeyEvent.VK_ALT);add(KeyEvent.VK_NUMPAD6);}});
                put(13 , new ArrayList<>(){{add(KeyEvent.VK_CONTROL);add(KeyEvent.VK_ALT);add(KeyEvent.VK_PAGE_UP);}});}});
            //preset 6 funktion keys with music
            presets.add(new HashMap<>() {{
                putAll(presets.get(0));
                //replace(7 , new ArrayList<>(){{add(KeyEvent.VK_CONTROL);add(KeyEvent.VK_ALT);add(KeyEvent.VK_NUMPAD2);}});


                replace(11 , new ArrayList<>(){{add(KeyEvent.VK_CONTROL);add(KeyEvent.VK_ALT);add(KeyEvent.VK_SHIFT);add(KeyEvent.VK_F11);}});
                replace(12 , new ArrayList<>(){{add(KeyEvent.VK_CONTROL);add(KeyEvent.VK_ALT);add(KeyEvent.VK_SHIFT);add(KeyEvent.VK_F12);}});

            }});
        }
    

    }

    public int[] getKey(int input, Macropad macropad) {
        if (macropad.getPreset() == 3 | macropad.getPreset() == 4  ){
            macropad.testnumlock(false);
        }

        int [] list = new int[ presets.get(macropad.getPreset() - 1).get(input).size()];
        for (int i = 0; i < list.length; i++) {
           list[i] =  presets.get(macropad.getPreset() - 1).get(input).get(i);
        }
        return list ;
    }



    public HashMap<Integer, ArrayList<Integer>> getActivePreset(Macropad macropad){
        return presets.get(macropad.getPreset());
    }
    public int  getgesamtpresets(){
        return presets.size();
    }

}

