import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

class Parser {
    String commandName;
    String[] args;
    ArrayList<String> history = new ArrayList<>();

    // This method will divide the input into commandName and args
    // where "input" is the string command entered by the user
    public boolean parse(String input) {
        history.add(input);
        String[] splitInput = input.split(" ");
        if (splitInput.length == 0) {
            return false; // Invalid input
        }
        // Setting command name
        commandName = splitInput[0];
        // Checks if ther's a flag in the command to concatinate it to command name
        if (splitInput.length > 1 && splitInput[1].charAt(0) == '-')
            commandName += ' ' + splitInput[1];
        // Setting args
        args = new String[splitInput.length - 1];
        for (int i = 1; i < splitInput.length; i++) {
            args[i - 1] = splitInput[i];
        }
        return true;
    }

    public String getCommandName() {
        return commandName;
    }

    public String[] getArgs() {
        return args;
    }
}

class Terminal {
    Parser parser;
    Path path;

    Terminal() {
        parser = new Parser();
        path = Paths.get(System.getProperty("user.dir"));
    }

    // changes the directory
    public void cd(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: cd [directory]");
        } else if (args[0].equals("..")) {
            // Handle moving up to the parent directory
            path = path.getParent();
        } else if (args[0].startsWith("/")) {
            // Handle absolute path
            path = Paths.get(args[0]);
        } else {
            Path newDirectory = path.resolve(args[0]);
            if (Files.exists(newDirectory) && Files.isDirectory(newDirectory)) {
                path = newDirectory;
            } else {
                System.out.println("Directory not found: " + args[0]);
            }
        }
        System.setProperty("user.dir", path.toString());
    }


    // prints the current path
    public void pwd() {
        System.out.println(path.toString());
    }

    // prints the text entered
    public void echo(String[] input) {
        for (int i = 0; i < input.length; i++) {
            System.out.print(input[i] + " ");
        }
        System.out.println(" ");
    }

    // Sorts the files and folders and displays them
    public void ls() {
        File currentFolder = new File(String.valueOf(path));
        String[] allFilesandFolders = currentFolder.list();
        Arrays.sort(allFilesandFolders);
        for (int i = 0; i < allFilesandFolders.length; i++) {
            System.out.println(allFilesandFolders[i]);
        }
    }

    // Sorts the files and folders descendingly and displays them
    public void ls_r() {
        File currentFolder = new File(String.valueOf(path));
        String[] allFilesandFolders = currentFolder.list();
        Arrays.sort(allFilesandFolders);
        for (int i = allFilesandFolders.length - 1; i >= 0; i--) {
            System.out.println(allFilesandFolders[i]);
        }
    }

    // makes a new folder in the current directory
    public void mkdir(String[] args) {
        for (int i = 0; i < args.length; i++) {
            try {
                Files.createDirectory(Paths.get(String.valueOf(path) + "\\" + args[i]));
            } catch (IOException e) {
                System.err.println("Failed to create directory: " + args[i]);
            }
        }
    }

    // removes a folder in the current directory
    public void rmdir(String[] args) {
        // if the user entered * it will delete all the empty folders and files in the
        // current directory
        if (args[0].equals("*")) {
            File currentFolder = new File(String.valueOf(path));
            File[] allFilesandFolders = currentFolder.listFiles();
            for (int i = 0; i < allFilesandFolders.length; i++) {
                allFilesandFolders[i].delete();
            }
        } else {
            File deleteFile = new File(String.valueOf(path) + "\\" + args[0]);
            if (deleteFile.isDirectory() && deleteFile.listFiles().length != 0) {
                System.out.println("Cannot remove non-empty folder");
                return;
            }
            deleteFile.delete();
        }
    }

    // shows commands history
    public void history() {
        for (int i = 0; i < parser.history.size(); i++) {
            System.out.println((i + 1) + "- " + parser.history.get(i));
        }
    }

    // creates a file
    public void touch(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: touch [file]");
        } else {
            Path newFile = path.resolve(args[0]);
            try {
                if (Files.notExists(newFile)) {
                    Files.createFile(newFile);
                    System.out.println("File created: " + args[0]);
                } else {
                    System.out.println("File already exists: " + args[0]);
                }
            } catch (IOException e) {
                System.out.println("Error creating file: " + e.getMessage());
            }
        }
    }

    // removes a file
    public void rm(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: rm [file]");
        } else {
            Path filePath = path.resolve(args[0]);
            try {
                if (Files.exists(filePath)) {
                    if (Files.isDirectory(filePath)) {
                        System.out.println("Error: The specified path is a directory. Use 'rm -r' to remove directories.");
                    } else {
                        Files.delete(filePath);
                        System.out.println("File removed: " + args[0]);
                    }
                } else {
                    System.out.println("File not found: " + args[0]);
                }
            } catch (IOException e) {
                System.out.println("Error removing file: " + e.getMessage());
            }
        }
    }

    public void chooseCommandAction() {
        switch (parser.getCommandName()) {
            // 1
            case "cd" -> cd(parser.getArgs());
            // 2
            case "pwd" -> pwd();
            // 3
            case "echo" -> echo(parser.getArgs());
            // 4
            case "ls" -> ls();
            // 5
            case "ls -r" -> ls_r();
            // 6
            case "mkdir" -> mkdir(parser.getArgs());
            // 7
            case "rmdir" -> rmdir(parser.getArgs());
            // 8
            case "history" -> history();
            // 9
            case "touch" -> touch(parser.getArgs());
            // 10
            case "rm" -> rm(parser.getArgs());

            case "exit" -> System.exit(0);

            default -> System.out.println("Command not found");
        }
    }

    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);
        Terminal ter = new Terminal();
        String userInput;

        for (; ; ) {
            System.out.print('>');
            userInput = input.nextLine();
            if (ter.parser.parse(userInput)) {
                ter.chooseCommandAction();
            } else {
                System.out.println("Invalid Command");
            }

        }
    }
}



