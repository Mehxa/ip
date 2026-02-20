package purpleguy;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import purpleguy.exception.AftonException;
import purpleguy.tasklist.TaskList;

/**
 * Validates the commands inputted by the user
 */
public class CommandValidator {
    private static final String ERROR_MISSING_BY_VALUE = "A tag with no data? You're stalling."
                + " Tell me *when* the clock stops."
                + "\n[HINT]: Provide the timing details immediately after the /by tag";
    private static final String ERROR_MISSING_FROM_VALUE = "The stage is set, but the actors have no cues for /from."
                + " Provide a time, or the curtains stay closed."
                + "\n[HINT]: Provide the timing details immediately after the /from tag";
    private static final String ERROR_MISSING_TO_VALUE = "You've started the performance,"
                + " but left the ending in a void."
                + " Tell me when /to finish it"
                + "\n[HINT]: Provide the timing details immediately after the /to tag";
    private static final String ERROR_TIME_WRONG_FORMAT = "Your grasp of time is... messy."
                + " I don't operate on 'soon' or 'later'."
                + " Give me a format that holds weight in my ledger, or the record will be lost to the void."
                + "\n[HINT]: Time Format: yyyy-MM-dd HH:mm (e.g., 2026-10-31 23:59)";

    /**
     * Validates input given by the user
     * @param command Command derived from the user input
     * @param details String array of details derived from the user input
     * @param tL ArrayList of Tasks, the current taskList
     * @throws AftonException If input is invalid: Missing tag/task name, improper tag/command usage
     */
    public void validate(String command, String[] details, TaskList tL) throws AftonException {
        if (!isKnownCommand(command)) {
            throw new AftonException("'" + command + "'? I don't recognize that. Don't waste my time with nonsense. "
                + "\n[HINT]: Use 'help' to view the full list of valid commands.");
        }
        // Resolving list first as it is the only command
        // which does not require extra parameters
        if (command.equals("list")) {
            validateList(details, tL.isEmpty());
            return;
        }

        // This is to let allHelp pass the default case
        // Validation for the normal help is done later
        if (command.equals("help") && details.length == 0) {
            return;
        }

        // Command missing essential information
        validateMissingInfo(details, command);

        if (command.equals("find")) {
            validateFind(tL.isEmpty());
            return;
        }

        switch (command) {
        case "todo":
            // Todo command inputted with /by, /from or /to
            if (details.length > 1) {
                throw new AftonException("A simple task shouldn't have extra baggage."
                    + " Keep it clean... like a well-wiped crime scene."
                    + "\n[HINT]: Todo tasks do not use /by, /from, or /to tags.");
            }
            break;

        case "deadline":
            validateDeadline(details);
            break;

        case "event":
            validateEvent(details);
            break;

        case "mark":
            // Fallthrough
            // Mark unmark and delete go through the same validation process
        case "unmark":
            // Fallthrough
        case "delete":
            validateIndex(details, command, tL);
            break;
        case "help":
            validateHelp(details[0]);
            break;
        // Any other unrecognised command
        default:
            throw new AftonException("'" + command + "'? I don't recognize that. Don't waste my time with nonsense. "
                + "\n[HINT]: I only respond to: todo, deadline, event, list, find, mark, unmark, or delete.");
        }

    }

    private static boolean isKnownCommand(String command) {
        return List.of("todo", "deadline", "event", "list", "mark", "unmark", "find", "delete", "help", "bye")
        .contains(command);
    }

    private static void validateList(String[] details, boolean isEmpty) throws AftonException {
        if (details.length > 0) {
            throw new AftonException("Do you think I'm blind? "
                                    + "I don't need your 'extra information' to view my own ledger. "
                                    + "Just say the word and be quiet."
                                    + "\n[HINT]: The 'list' command requires no additional words.");
        }
        if (isEmpty) {
            throw new AftonException("There is nothing here to silence. You haven't even started your work. "
                                    + "\n[HINT]: Add a task before trying to list");
        }
    }

    private static void validateMissingInfo(String[] details, String command) throws AftonException {
        if (details.length < 1 || details[0].trim().isEmpty() || details[0].startsWith("/")) {
            // More specific error message for missing index rather than missing name
            if (command.equals("mark") || command.equals("unmark") || command.equals("delete")) {
                throw new AftonException("You're pointing at thin air. Give me a number, or step away. "
                                    + "\n[HINT]: Usage: " + command + " [task index number]");
            }
            throw new AftonException("A hollow entry? Much like those empty suits, "
                                + "it's useless without...something inside. Give it a name."
                                + "\n[HINT]: Try: " + command + " [description] ...");
        }
    }

    private static void validateFind(boolean isEmpty) throws AftonException {
        if (isEmpty) {
            throw new AftonException("You're hunting for shadows in an empty room. "
                    + "There is nothing here to find... yet."
                    + "\n[HINT]: Add a task before you attempt to find it");
        }
    }

    private static int countTags(String[] details, String tag) {
        int count = 0;
        for (int i = 0; i < details.length; i++) {
            String detail = details[i].trim();
            if (detail.startsWith(tag)) {
                count++;
            }
        }
        return count;
    }

    private static int getTagIndex(String[] details, String tag) {
        int index = -1;
        for (int i = 0; i < details.length; i++) {
            String detail = details[i].trim();
            if (detail.startsWith(tag)) {
                index = i;
                break;
            }
        }
        return index;
    }

    private static void validateTagContent(String[] details, int index,
        String tag, String errorMessage) throws AftonException {
        if (details[index].trim().equals(tag)) {
            throw new AftonException(errorMessage);
        }
    }

    private static LocalDateTime validateDate(String dateString, String tag) throws AftonException {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            return LocalDateTime.parse(dateString.replace(tag, "").trim(), formatter);
        } catch (Exception e) {
            throw new AftonException(ERROR_TIME_WRONG_FORMAT);
        }

    }

    private static void validateDeadline(String[] details) throws AftonException {
        int noOfBy = countTags(details, "/by");
        int noOfFrom = countTags(details, "/from");
        int noOfTo = countTags(details, "/to");

        if (noOfFrom > 0 || noOfTo > 0) {
            throw new AftonException("You're confusing a deadline with an event."
                + " Only one end matters here."
                + "\n[HINT]: Remove /from and /to. Only use /by.");
        }

        if (noOfBy == 0) {
            throw new AftonException("How disappointing. You forgot the '/by' tag."
                + " Precision is the difference between a masterpiece and a... mess."
                + "\n[HINT]: A deadline requires a '/by [time]' marker.");
        }
        if (noOfBy > 1) {
            throw new AftonException("Too many endings? Even I only needed one."
                + " One /by is enough."
                + "\n[HINT]: Ensure you only have one '/by' tag.");
        }

        int byIndex = 1;
        validateTagContent(details, byIndex, "/by", ERROR_MISSING_BY_VALUE);

        validateDate(details[1], "/by");
    }

    private static void validateEvent(String[] details) throws AftonException {
        int noOfBy = countTags(details, "/by");

        if (noOfBy > 0) {
            throw new AftonException("Events don't have deadlines; they have schedules. Lose the /by tag."
                + "\n[HINT]: Replace '/by' with '/from' and '/to'.");
        }

        int noOfFrom = countTags(details, "/from");
        int noOfTo = countTags(details, "/to");

        if (noOfFrom != 1 || noOfTo != 1) {
            throw new AftonException("The stage is set, but the timing is incomplete."
                + " I require exactly one start and one end."
                + "\n[HINT]: Ensure you have exactly one '/from' and one '/to' tag.");
        }

        int fromIdx = getTagIndex(details, "/from");
        int toIdx = getTagIndex(details, "/to");

        if (toIdx < fromIdx) {
            throw new AftonException("You're trying to end the show before the curtains even rise?"
                + " Order is everything."
                + "\n[HINT]: Place the '/from' tag before the '/to' tag.");
        }

        validateTagContent(details, fromIdx, "/from", ERROR_MISSING_FROM_VALUE);
        validateTagContent(details, toIdx, "/to", ERROR_MISSING_TO_VALUE);

        LocalDateTime fromTime = validateDate(details[1], "/from");
        LocalDateTime toTime = validateDate(details[2], "/to");
        if (fromTime.isAfter(toTime)) {
            throw new AftonException("Time is a linear path, not a circle for you to wander. "
                + "You're trying to end an event before it even begins... "
                + "such a sloppy paradox. Fix the record, or I'll leave it to rot."
                + "\n[HINT]: The /from time must be before the /to time");
        }
        if (fromTime.isEqual(toTime)) {
            throw new AftonException("An event with no duration? How pointless. "
                + "Even a moment of agony has a beginning and an end."
                + "\n[HINT]: The /from time and /to time cannot be the same");
        }
    }

    private static void validateIndex(String[] details, String command, TaskList tL) throws AftonException {
        if (tL.isEmpty()) {
            throw new AftonException("There is nothing here to silence. You haven't even started your work. "
                + "\n[HINT]: Add a task before trying to " + command);
        }
        try {
            int index = Integer.parseInt(details[0]);
            if (index < 1 || index > tL.size()) {
                throw new AftonException("Are you seeing ghosts? That record doesn't exist. Stay within the count."
                    + "\n[HINT]: Enter a number between 1 and " + tL.size() + ".");
            }
        } catch (NumberFormatException e) {
            throw new AftonException("'" + details[0] + "' is not a number."
                + "I require mathematical precision, not guesswork."
                + "\n[HINT]: Provide a valid integer index.");
        }
    }

    private static void validateHelp(String command) throws AftonException {
        if (!isKnownCommand(command)) {
            throw new AftonException("Are you trying to find a secret door? There is no such command in my ledger."
                + " Stick to the protocols I gave you, or don't speak at all."
                + "\n[HINT]: For a list of all valid commands try inputting 'help' only");
        }
    }
}
