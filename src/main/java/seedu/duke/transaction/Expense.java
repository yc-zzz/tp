package seedu.duke;

import java.time.LocalDate;

public class Expense extends Transaction {

    public static final java.util.List<String> VALID_CATEGORIES = java.util.List.of(
            "food", "transport", "utilities", "education", "rent", "medical", "misc"
    );

    public Expense(String category, double amount, String description, LocalDate date) {
        super(category, amount, description, date);
    }

    public Expense(String category, double amount, String description) {
        super(category, amount, description);
    }

    public Expense(String category, double amount) {
        super(category, amount);
    }

    @Override
    public String getType() {
        return "expense";
    }

    @Override
    public String toString() {
        String descriptionSuffix = description.isEmpty() ? "" : " \"" + description + "\"";
        return String.format("[Expense] %s%s $%.2f (%s)", category, descriptionSuffix, amount, date);
    }
}
