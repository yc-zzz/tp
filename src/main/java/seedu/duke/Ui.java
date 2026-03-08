package seedu.duke;

//can be customised more, problems for later
import java.util.Scanner;
public class Ui {
    private final Scanner scanner = new Scanner(System.in);

    public String readInput() {
        System.out.print("Enter a command: ");
        return scanner.nextLine().trim();
    }

    public void showHelp(){
        System.out.println("Listing all current transactions: `list`");
        System.out.println("Adding an expense: `add [category]/PRICE`\n" +
                " - Category can be any user-initiated category for now.");
        System.out.println("Adding an expense: `add income/PRICE`\n");
        System.out.println("Deleting an expense or income:`delete [ENTRY INDEX]`");
        System.out.println("Summary of all expenses or incomes: `summary [category]\n`" +
                " - Category include: `all`, `expense`, `outflow`");
        System.out.println("Exiting the program: `exit`");
    }

    public void showWelcomeMessage(){
        System.out.println("Welcome to MoneyBagProMax, give us your money.");
        System.out.println("Enter `help` to check the list of available commands.");
    }

    public void showMessage(String message) {
        System.out.println(message);
    }
}
