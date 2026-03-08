package seedu.duke;
//todo: list, delete, income

public class Parser {
    /**
     * Checks user command and calls functions according to the command
     *
     * @param input user input string
     * @param list  the current list of transactions
     * @param ui    ui class
     */
    public void parse(String input, TransactionList list, Ui ui) {
        if (input.startsWith("add ")) {//can replace with "add income" for priority
            String args = input.substring(4).trim();
            String[] parts = args.split("/", 2);

            if (parts.length < 2) {
                ui.showMessage("Invalid, try: add [category]/PRICE");
                return;
            }

            try {
                String category = parts[0].trim();
                double amount = Double.parseDouble(parts[1].trim());
                Expense expense = new Expense(category, amount);
                list.add(expense);
                ui.showMessage("Added: " + expense);
            } catch (NumberFormatException e) {
                ui.showMessage("Invalid price.");
            }
        } else if (input.startsWith("help")) {
            ui.showHelp();
        } else {
            ui.showMessage("Unknown command.");
        }
    }
}
