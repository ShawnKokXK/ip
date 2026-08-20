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

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            if (command.equals("bye")) {
                System.out.println(LINE);
                System.out.println(" Bye. Hope to see you again soon!");
                System.out.println(LINE);
                break;
            }
            System.out.println(LINE);
            System.out.println(" " + command);
            System.out.println(LINE);
        }
        scanner.close();
    }
}
