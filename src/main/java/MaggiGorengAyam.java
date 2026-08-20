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

        String[] tasks = new String[100];
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
                for (int i = 0; i < taskCount; i++) {
                    System.out.println(" " + (i + 1) + ". " + tasks[i]);
                }
                System.out.println(LINE);
                continue;
            }
            tasks[taskCount] = command;
            taskCount++;
            System.out.println(LINE);
            System.out.println(" added: " + command);
            System.out.println(LINE);
        }
        scanner.close();
    }
}
