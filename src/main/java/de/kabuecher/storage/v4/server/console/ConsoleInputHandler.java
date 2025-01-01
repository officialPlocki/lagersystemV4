package de.kabuecher.storage.v4.server.console;

import de.kabuecher.storage.v4.server.console.command.Command;
import de.kabuecher.storage.v4.server.console.command.UserCommand;

import java.util.HashMap;
import java.util.List;

public class ConsoleInputHandler {

    HashMap<String, UserCommand> commands = new HashMap<>();

    private Thread thread;

    public void addCommand(String commandName, UserCommand command) {
        commands.put(commandName, command);
    }

    public void start() {
        thread = new Thread(() -> {
            while (true) {
                System.out.print("> ");
                String input = System.console().readLine();
                if (input == null) {
                    break;
                }
                String[] args = input.split(" ");
                if (args.length == 0) {
                    continue;
                }
                String command = args[0];
                switch (command) {
                    case "exit":
                        System.exit(0);
                        break;
                    case "help":
                        System.out.println("Available commands: exit, help" + commands.keySet());
                        break;
                    default:
                        if(commands.containsKey(command)) {
                            commands.get(command).run(List.of(args).subList(1, args.length).toArray(new String[0]));
                        } else {
                            Command defaultCommand = new Command() {};
                            defaultCommand.run(args);
                        }
                }
            }
        });
        thread.start();
    }

    public void stop() {
        thread.interrupt();
    }

}
