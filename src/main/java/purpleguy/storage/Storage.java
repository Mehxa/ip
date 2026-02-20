package purpleguy.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import purpleguy.Deadline;
import purpleguy.Event;
import purpleguy.Task;
import purpleguy.ToDo;
import purpleguy.exception.AftonException;
import purpleguy.tasklist.TaskList;

/**
 * Handles the reading and writing of tasks to and from the storage file (PurpleGuy.txt)
 */
public class Storage {
    public static final String STORAGE_FILEPATH = "./src/main/java/purpleguy/data/PurpleGuy.txt";

    public Storage() {}

    /**
     * Stores the current state of the taskList to the PurpleGuy.txt file
     * @param tL The current taskList
     */
    public void storeTL(TaskList tL) {
        Path fileName = Paths.get(STORAGE_FILEPATH);
        try {
            Files.createDirectories(fileName.getParent());
            Files.write(fileName, tL.toData(),
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("An error has occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Retrieves task data from the PurpleGuy.txt file to update the taskList
     * @param tL The current taskList
     */
    public void readTL(TaskList tL) throws AftonException {
        Path fileName = Paths.get(STORAGE_FILEPATH);
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
                    t = new Deadline(taskName, formatDate(taskVars[3].trim()));
                    break;
                case "E":
                    String[] time = taskVars[3].trim().split("-");
                    t = new Event(taskName, formatDate(time[0].trim()), formatDate(time[1].trim()));
                    break;
                default:
                    break;
                }
                if (taskVars[1].trim().equals("X")) {
                    t.mark();
                }
                tL.addTask(t);
            }

        } catch (IOException e) {
            throw new AftonException("An error occured while attempting to read PurpleGuy.txt");
        } catch (IndexOutOfBoundsException e) {
            throw new AftonException("Someone messed with my files...");
        }
    }

    private LocalDateTime formatDate(String dateString) {
        return LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern("MMM dd yyyy HH:mm"));
    }
}
