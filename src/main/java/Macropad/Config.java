package Macropad;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import static Macropad.Macropad.debug;

@Getter
@Setter
public class Config {
    private int preset;
    private int port;
    private int debugLevel;
    private int offset;
    private ArrayList<ArrayList<String>> commands;
    private ArrayList<String> presetNames;

    public Config(int preset, int port, int debugLevel) {
        this.commands = new ArrayList<>() {{
            add(new ArrayList<>());
        }};
        this.presetNames = new ArrayList<>();
        this.preset = preset;
        this.port = port;
        this.debugLevel = debugLevel;
    }

    public Config() {
        this(1, 1,1);
    }

    @SneakyThrows
    public static Config getConfig() {
        String delimiter = System.getProperty("file.separator");
        File confDir = new File(String.join(delimiter, System.getProperty("user.home"), ".config", "macropad"));
        if (!confDir.exists())
            debug(String.format("folder was created %s", confDir.mkdirs()));

        File conf = new File(String.join(delimiter, confDir.getAbsolutePath(), "macropad.conf"));
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Config config;
        // erstellen einer Config Datei
        if (!conf.exists()) {
            try (FileWriter fr = new FileWriter(conf)) {
                config = new Config();
                fr.write(gson.toJson(config));
                return config;
            } catch (IOException e) {
                debug("could not create conf file");
                System.exit(400);
            }
        }
        // Auslesen Einer Config Datei

        Scanner myReader = new Scanner(conf);
        StringBuilder data = new StringBuilder();
        while (myReader.hasNextLine()) {
            var d = myReader.nextLine();
            if (!d.replaceAll(" ", "").startsWith("#"))
                data.append(d);
        }
        return gson.fromJson(data.toString(), Config.class);
    }
}
