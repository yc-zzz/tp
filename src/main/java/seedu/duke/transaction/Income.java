package seedu.duke.transaction;

import java.time.LocalDate;

public class Income extends Transaction {
    public Income(String category, double amount, String description, LocalDate date) {
        super(category, amount, description, date);
    }

    public Income(String category, double amount, String description) {
        super(category, amount, description);
    }

    public Income(String category, double amount) {
        super(category, amount);
    }

    @Override
    public String getType() {
        return "income";
    }

    @Override
    public String toString() {
        String descriptionSuffix = description.isEmpty() ? "" : " \"" + description + "\"";
        return String.format("[Income] %s%s $%.2f (%s)", category, descriptionSuffix, amount, date);
    }
}
