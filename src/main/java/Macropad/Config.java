package Macropad;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

@Getter
@Setter
public class Config {
    private int preset;
    private int debugLevel;
    private int offset;
    private ArrayList<ArrayList<String>> commands;
    private String output_command;
    private ArrayList<String> presetNames;

    public Config(int preset, int debugLevel) {
        this.commands = new ArrayList<>() {{
            add(new ArrayList<>());
        }};
        this.presetNames = new ArrayList<>();
        this.preset = preset;
        this.debugLevel = debugLevel;
    }

    public Config() {
        this(1, 1);
    }

    @SneakyThrows
    public static Config getConfig() {
        String delimiter = System.getProperty("file.separator");
        File confDir = new File(String.join(delimiter, System.getProperty("user.home"), ".config", "macropad"));
        if (!confDir.exists()) log(String.format("folder was created %s", confDir.mkdirs()), System.out::println);

        File conf = new File(String.join(delimiter, confDir.getAbsolutePath(), "macropad.conf"));
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Config config;
        // create the config file
        if (!conf.exists()) {
            try (FileWriter fr = new FileWriter(conf)) {
                config = new Config();
                fr.write(gson.toJson(config));
                return config;
            } catch (IOException e) {
                log("could not create conf file", System.out::println);
                System.exit(400);
            }
        }
        // reading existing config file
        Scanner myReader = new Scanner(conf);
        StringBuilder data = new StringBuilder();
        while (myReader.hasNextLine()) {
            var d = myReader.nextLine();
            if (!d.replaceAll(" ", "").startsWith("#")) data.append(d);
        }
        return gson.fromJson(data.toString(), Config.class);
    }

    private static void log(String s, Consumer<String> function) {
        function.accept(s);
    }

    public void log(String s) {
        if (Objects.equals(output_command, null)) log(s, System.out::print);
        else log(s, this::cmdLog);
    }

    private void cmdLog(String s) {
//        TODO exception handling
        try {
            var title = "Macropad";
            var commandList = new ArrayList<>(Collections.singletonList(output_command.substring(0, output_command.indexOf(' '))));
            for (String s2 : output_command.substring(output_command.indexOf(' ') + 1).split(" ")) {
                if (s2.equals("{msg}")) s2 = s;
                if (s2.equals("{title}")) s2 = title;
                commandList.add(s2);
            }
            Runtime.getRuntime().exec(Arrays.copyOf(commandList.toArray(), commandList.size(), String[].class));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
