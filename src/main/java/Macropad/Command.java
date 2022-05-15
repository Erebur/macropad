package Macropad;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Command {

    private String command;

    public Command(String command) {
        this.command = command;
    }

    public Command() {
    }

}
