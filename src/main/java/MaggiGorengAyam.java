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
            tasks[taskCount] = new Task(command);
            taskCount++;
            System.out.println(LINE);
            System.out.println(" added: " + command);
            System.out.println(LINE);
        }
        scanner.close();
    }
}
