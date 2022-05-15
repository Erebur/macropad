

import Macropad.Commands;
import Macropad.Config;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.awt.*;
import java.util.ArrayList;

public class Test{
    public static void main(String[] args) throws AWTException {

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Config config = new Config();
        config.getCommands().get(0).add(Commands.presetswichdialog.string);
        config.getCommands().get(0).add("playerctl play-pause -p spotify");
        config.getCommands().add(new ArrayList<>());
        config.getCommands().get(1).add("playerctl play-pause -p spotify");
        System.out.println(gson.toJson(config));
    }


}
