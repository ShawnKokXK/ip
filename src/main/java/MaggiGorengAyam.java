import java.util.Scanner;

public class MaggiGorengAyam {
    private static final String LINE = "____________________________________________________________";

    public static void main(String[] args) {
        String banner = LINE + "\n" +
                "  __  __  _____    _    \n" +
                " |  \\/  |/ ____|  / \\   \n" +
                " | \\  / ||   __  / _ \\  \n" +
                " | |\\/| ||  |_ |/ ___ \\ \n" +
                " |_|  |_|\\_____/_/   \\_\\\n" +
                "Hello! I'm Maggi Goreng Ayam.\n" +
                "What can I do for you?\n" +
                LINE;
        System.out.println(banner);

        Task[] tasks = new Task[100];
        int taskCount = 0;

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            if (command.equals("bye")) {
                System.out.println(LINE);
                System.out.println(" Bye. Hope to see you again soon!");
                System.out.println(LINE);
                break;
            }
            if (command.equals("list")) {
                System.out.println(LINE);
                System.out.println(" Here are the tasks in your list:");
                for (int i = 0; i < taskCount; i++) {
                    System.out.println(" " + (i + 1) + "." + tasks[i]);
                }
                System.out.println(LINE);
                continue;
            }
            if (command.startsWith("mark ")) {
                int index = Integer.parseInt(command.substring(5).trim()) - 1;
                tasks[index].markAsDone();
                System.out.println(LINE);
                System.out.println(" Nice! I've marked this task as done:");
                System.out.println("   " + tasks[index]);
                System.out.println(LINE);
                continue;
            }
            if (command.startsWith("unmark ")) {
                int index = Integer.parseInt(command.substring(7).trim()) - 1;
                tasks[index].markAsNotDone();
                System.out.println(LINE);
                System.out.println(" OK, I've marked this task as not done yet:");
                System.out.println("   " + tasks[index]);
                System.out.println(LINE);
                continue;
            }
            if (command.startsWith("todo ")) {
                String description = command.substring(5).trim();
                tasks[taskCount] = new ToDo(description);
                taskCount++;
                printAdded(tasks[taskCount - 1], taskCount);
                continue;
            }
            if (command.startsWith("deadline ")) {
                String rest = command.substring(9).trim();
                String[] parts = rest.split(" /by ", 2);
                String description = parts[0];
                String by = parts.length > 1 ? parts[1] : "";
                tasks[taskCount] = new Deadline(description, by);
                taskCount++;
                printAdded(tasks[taskCount - 1], taskCount);
                continue;
            }
            if (command.startsWith("event ")) {
                String rest = command.substring(6).trim();
                String[] fromParts = rest.split(" /from ", 2);
                String description = fromParts[0];
                String[] toParts = (fromParts.length > 1 ? fromParts[1] : "").split(" /to ", 2);
                String from = toParts[0];
                String to = toParts.length > 1 ? toParts[1] : "";
                tasks[taskCount] = new Event(description, from, to);
                taskCount++;
                printAdded(tasks[taskCount - 1], taskCount);
                continue;
            }
        }
        scanner.close();
    }

    private static void printAdded(Task task, int taskCount) {
        System.out.println(LINE);
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + taskCount + " tasks in the list.");
        System.out.println(LINE);
    }
}
