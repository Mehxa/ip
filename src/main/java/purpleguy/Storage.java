package purpleguy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles the reading and writing of tasks to and from the storage file (PurpleGuy.txt)
 */
public class Storage {
    public static final String STORAGE_FILEPATH = "./src/main/java/purpleguy/data/PurpleGuy.txt";

    public Storage() {}

    /**
     * Stores the current state of the taskList to the PurpleGuy.txt file
     * @param tL ArrayList of Tasks, the current taskList
     */
    public void storeTL(ArrayList<Task> tL) {
        Path fileName = Paths.get("./src/main/java/purpleguy/data/PurpleGuy.txt");
        try {
            Files.createDirectories(fileName.getParent());
            Files.write(fileName, tL.stream().map(x->x.toData()).toList(),
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("An error has occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Retrieves task data from the PurpleGuy.txt file to update the taskList
     * @param tL ArrayList of Tasks, the current taskList
     */
    public void readTL(ArrayList<Task> tL) {
        Path fileName = Paths.get("./src/main/java/purpleguy/data/PurpleGuy.txt");
        System.err.println(Paths.get("").toAbsolutePath().toString());
        try {
            List<String> taskData = Files.readAllLines(fileName);
            for (String string : taskData) {
                String[] taskVars = string.split("\\|");
                Task t = null;
                String taskName = taskVars[2].trim();
                switch (taskVars[0].trim()) {
                case "T":
                    t = new ToDo(taskName);
                    break;
                case "D":
                    t = new Deadline(taskName, LocalDateTime.parse(
                        taskVars[3].trim(),
                        DateTimeFormatter.ofPattern("MMM dd yyyy HH:mm")));
                    break;
                case "E":
                    String[] time = taskVars[3].trim().split("-");
                    t = new Event(taskName, time[0].trim(), time[1].trim());
                    break;
                default:
                    break;
                }
                if (taskVars[1].trim().equals("X")) {
                    t.mark();
                }
                tL.add(t);
            }

        } catch (IOException e) {
            System.err.println("An error occured while attempting to read PurpleGuy.txt");
            System.err.println(e.getMessage());
        }
    }
}
