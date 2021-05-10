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
    private static ArrayList<HashMap<Integer, Integer>> presets = new ArrayList<>();
    private static ArrayList<HashMap<Integer, ArrayList<Integer>>> presetsnew = new ArrayList<>();

    static {
        //OLD
      presets.add(new HashMap<>() {{
            put(2 , KeyEvent.VK_F13);
            put(3 , KeyEvent.VK_F14);
            put(4 , KeyEvent.VK_F15);
            put(5 , KeyEvent.VK_F16);
            put(6 , KeyEvent.VK_F17);
            put(7 , KeyEvent.VK_F18);
            put(8 , KeyEvent.VK_F19);
            put(9 , KeyEvent.VK_F20);
            put(10 , 0);
            put(11 , KeyEvent.VK_F21);
            put(12 , KeyEvent.VK_F22);
            put(13, KeyEvent.VK_F23);
        }});
      presets.add(new HashMap<>() {{
            put(2 , KeyEvent.VK_A);
            put(3 , KeyEvent.VK_S);
            put(4 , KeyEvent.VK_D);
            put(5 , KeyEvent.VK_F);
            put(6 , KeyEvent.VK_Q);
            put(7 , KeyEvent.VK_W);
            put(8 , KeyEvent.VK_E);
            put(9 , KeyEvent.VK_R);
            put(10 , 0);
            put(11 , KeyEvent.VK_1);
            put(12 , KeyEvent.VK_2);
            put(13, KeyEvent.VK_DELETE);
      }});



        //new In array form auch mit  Array als rückgabe wert
        //bruh initialisierung 100

        //preset 1
    presetsnew.add(new HashMap<>() {{
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
        put(13 , new ArrayList<>(){{add(KeyEvent.VK_CONTROL);add(KeyEvent.VK_PAGE_UP);}});


    }});
    //preset 2
        presetsnew.add(new HashMap<>() {{
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
            put(13 , new ArrayList<>(){{add(KeyEvent.VK_DELETE);}});


        }});
    }



    public static int[] getKey(int input){
        int [] list = new int[presetsnew.get(Macropadmain.getPreset() - 1).get(input).size()];
        for (int i = 0; i < list.length; i++) {
           list[i] =  presetsnew.get(Macropadmain.getPreset() - 1).get(input).get(i);
        }
        return list ;
    }

    public static HashMap<Integer, Integer> getActivePreset() {
        return presets.get(Macropadmain.getPreset());
    }

}
