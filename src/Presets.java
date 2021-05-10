import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;

public class Presets {
    public static final int VK_RCONTROL                 = 0xA3;
    public static final int VK_VOLUME_UP                = 0xAF;
    public static final int VK_VOLUME_DOWN              = 0xAE;
    public static final int VK_MEDIA_NEXT_TRACK         = 0xB0;
    public static final int VK_MEDIA_PREV_TRACK         = 0xB1;


    //https://docs.microsoft.com/en-us/windows/win32/inputdev/virtual-key-codes
    private static final ArrayList<HashMap<Integer, ArrayList<Integer>>> presets = new ArrayList<>();

    static {
        //In array form auch mit  Array als rückgabe wert
        //bruh initialisierung 100

        //preset 1 functions
    presets.add(new HashMap<>() {{
        //win keys
        put(2 , new ArrayList<>(){{add(KeyEvent.VK_WINDOWS);add(KeyEvent.VK_G);}});
        put(3 , new ArrayList<>(){{add(KeyEvent.VK_WINDOWS);add(KeyEvent.VK_TAB);}});
        //discord
        put(4 , new ArrayList<>(){{add(KeyEvent.VK_F14);}});
        put(5 , new ArrayList<>(){{add(KeyEvent.VK_F15);}});
        //geforce Overlay
        put(6 , new ArrayList<>(){{add(KeyEvent.VK_CONTROL);add(KeyEvent.VK_R);}});
        put(7 , new ArrayList<>(){{add(KeyEvent.VK_CONTROL);add(KeyEvent.VK_T);}});
        //music
        put(8 , new ArrayList<>(){{add(KeyEvent.VK_CONTROL);add(KeyEvent.VK_NUMPAD5);}});
        put(9 , new ArrayList<>(){{add(KeyEvent.VK_CONTROL);add(KeyEvent.VK_PAGE_DOWN);}});
        put(10 , new ArrayList<>(){{add(0);}});
        put(11 , new ArrayList<>(){{add(KeyEvent.VK_CONTROL);add(KeyEvent.VK_NUMPAD4);}});
        put(12 , new ArrayList<>(){{add(KeyEvent.VK_CONTROL);add(KeyEvent.VK_NUMPAD6);}});
        put(13 , new ArrayList<>(){{add(KeyEvent.VK_CONTROL);add(KeyEvent.VK_PAGE_UP);}});}});
    //preset 2 wasd
        presets.add(new HashMap<>() {{
            //win keys
            put(2 , new ArrayList<>(){{add(KeyEvent.VK_A);}});
            put(3 , new ArrayList<>(){{add(KeyEvent.VK_S);}});
            put(4 , new ArrayList<>(){{add(KeyEvent.VK_D);}});
            put(5 , new ArrayList<>(){{add(KeyEvent.VK_F);}});
            put(6 , new ArrayList<>(){{add(KeyEvent.VK_Q);}});
            put(7 , new ArrayList<>(){{add(KeyEvent.VK_W);}});
            put(8 , new ArrayList<>(){{add(KeyEvent.VK_E);}});
            put(9 , new ArrayList<>(){{add(KeyEvent.VK_R);}});
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
                put(9 , new ArrayList<>(){{add(KeyEvent.VK_ENTER);}});
                //reihe 3
                put(10 , new ArrayList<>(){{add(KeyEvent.VK_NUMPAD7);}});
                put(11 , new ArrayList<>(){{add(KeyEvent.VK_NUMPAD8);}});
                put(12 , new ArrayList<>(){{add(KeyEvent.VK_NUMPAD9);}});
                put(13 , new ArrayList<>(){{add(KeyEvent.VK_NUM_LOCK);}});}});
        //preset 4 wenn numlock aus
        presets.add(new HashMap<>() {{
                put(2 , new ArrayList<>(){{add(KeyEvent.VK_LEFT);}});
                put(3 , new ArrayList<>(){{add(KeyEvent.VK_DOWN);}});
                put(4 , new ArrayList<>(){{add(KeyEvent.VK_RIGHT);}});
                put(5 , new ArrayList<>(){{add(KeyEvent.VK_NUMPAD7);}});
                //reihe 2
                put(6 , new ArrayList<>(){{add(KeyEvent.VK_END);}});
                put(7 , new ArrayList<>(){{add(KeyEvent.VK_UP);}});
                put(8 , new ArrayList<>(){{add(KeyEvent.VK_HOME);}});
                put(9 , new ArrayList<>(){{add(KeyEvent.VK_NUMPAD8);}});
                //reihe 3
                put(10 , new ArrayList<>(){{add(0);}});
                put(11 , new ArrayList<>(){{add(KeyEvent.VK_DELETE);}});
                put(12 , new ArrayList<>(){{add(KeyEvent.VK_ENTER);}});
                put(13 , new ArrayList<>(){{add(KeyEvent.VK_NUM_LOCK);}});}});
    }

    public static int[] getKey(int input){
        if (Macropadmain.getPreset() == 3 | Macropadmain.getPreset() == 4  ){
            Macropadmain.testnumlock(false);
        }

        int [] list = new int[ presets.get(Macropadmain.getPreset() - 1).get(input).size()];
        for (int i = 0; i < list.length; i++) {
           list[i] =  presets.get(Macropadmain.getPreset() - 1).get(input).get(i);
        }
        return list ;
    }



    public static HashMap<Integer, ArrayList<Integer>> getActivePreset(){
        return presets.get(Macropadmain.getPreset());
    }

}

