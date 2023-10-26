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
        File currentFolder = new File(".");
        String[] allFilesandFolders = currentFolder.list();
        Arrays.sort(allFilesandFolders);
        for (int i = 0; i < allFilesandFolders.length; i++) {
            System.out.println(allFilesandFolders[i]);
        }
    }

    // Sorts the files and folders descendingly and displays them
    public void ls_r() {
        File currentFolder = new File(".");
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
                Files.createDirectory(Paths.get(System.getProperty("user.dir") + "\\" + args[i]));
                System.out.println(System.getProperty("user.dir"));
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
            File currentFolder = new File(System.getProperty("user.dir"));
            File[] allFilesandFolders = currentFolder.listFiles();
            for (int i = 0; i < allFilesandFolders.length; i++) {
                allFilesandFolders[i].delete();
            }
        } else {
            File deleteFile = new File(System.getProperty("user.dir") + "\\" + args[0]);
            if (deleteFile.isDirectory() && deleteFile.listFiles().length != 0) {
                System.out.println("Cannot remove non-empty folder");
                return;
            }
            deleteFile.delete();
        }
    }

    public void chooseCommandAction() {
        switch (parser.getCommandName()) {
            case "cd" -> cd(parser.getArgs());
            case "pwd" -> pwd();
            case "echo" -> echo(parser.getArgs());
            case "ls" -> ls();
            case "ls -r" -> ls_r();
            case "mkdir" -> mkdir(parser.getArgs());
            case "rmdir" -> rmdir(parser.getArgs());
            case "exit" -> System.exit(0);
        }
    }


}

public class Main {

    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);
        Terminal ter = new Terminal();
        String userInput;

        for (; ; ) {
            System.out.print('>');
            userInput = input.nextLine();
            ter.parser.parse(userInput);
            ter.chooseCommandAction();
        }
    }

}

