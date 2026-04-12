# Sean Chng's Project Portfolio Page

## Project: MoneyBagProMax

MoneyBagProMax is a command-line personal finance management application designed to help users track
income and expenses, manage budgets, and gain insights into spending habits through financial statistics.
The user interacts with it using a CLI, and the application is written in Java.

Given below are my contributions to the project.

---

## Summary of Contributions

### New Feature: Income Class
**What it does:**
Represents an income transaction. Extends `Transaction` and defines valid income categories (e.g. salary, allowance, freelance, investment, and misc). The formatted display (e.g. `[Income] salary "monthly pay" $3000.00 (2026-03-20)`) omits the description if it is empty, keeping output clean.

**Justification:**
A dedicated `Income` subclass keeps the category list and formatting logic together in one place. Exposing `VALID_CATEGORIES` as a public static final field allows `AddCommand` and `EditCommand` to reference it directly when deciding which transaction type to instantiate, without coupling those commands to the Parser.

**Highlights:**
Category validity is enforced at construction time, matching how the rest of the codebase handles invalid input. It mirrors the structure of `Expense` so adding a new transaction type in future only requires a new subclass.

---

### New Feature: Storage (Save and Load)
**What it does:**
Handles the persistent saving and loading of all transactions to and from a local data file. Transactions are saved after every mutating command, and the full transaction list is restored from the file on application startup. The component also gracefully handles missing or corrupted files, ensuring the application can always launch cleanly.

**Justification:**
Without persistent storage, all data would be lost every time the application exits. This feature ensures that users never lose their transaction history and can pick up exactly where they left off across sessions.

**Highlights:**
The storage component is designed to be robust — if the data file is missing, it is created fresh; if it is corrupted or contains malformed entries, those entries are skipped with a warning rather than crashing the application. A final save is also triggered on application exit as a safety net to ensure no data is lost regardless of the last command run.

---

### Enhancements Implemented:
- Added defensive programming via assertions in `Income` constructor and relevant command `execute()` methods.
- Added Javadoc to `Income` and `Storage` classes.
- Wrote JUnit tests for `Income` (valid categories, invalid categories, formatted display) and `Storage` (save/load round-trip, missing file creation, corrupted entry handling).
- Added cross-platform stress tests for `CsvExporter` covering embedded newlines, carriage returns, combined quote+comma escaping, and large decimal amounts.
- Added edge case tests for `Income` covering empty description display and case-insensitive category input.
---

### Bug Fixes:
- **`AddCommand` and `EditCommand` not persisting data:**
  Identified that `AddCommand` and `EditCommand` were missing the `isMutating()` override, causing transactions to not be saved to disk after execution. Fixed by adding `isMutating()` returning `true` to both commands.
- **Data loss on exit:**
  Added a final `storage.save()` and `storage.saveRecurring()` call in the main loop when `isExit` is true, ensuring all data is persisted before the application closes regardless of what the last command was.
- **Misleading error message for invalid dates:**
  Identified and documented that the error message `"Invalid date format — expected yyyy-MM-dd. Using today's date."` is inaccurate — the command is rejected entirely rather than falling back to today's date. Flagged for correction before the next release.
- **Extra whitespace in commands:**
  Fixed `Parser.parse()` to normalise multiple consecutive spaces into one before tokenization, making commands robust to accidental extra spaces. Added 4 JUnit tests to verify the fix.
- **Help menu missing commands:**
  Added `category`, `export-csv`, and `export-data` entries to the help menu in `Ui.showHelp()`. Corrected the `undo` description to include `edit` as an undoable action. Updated `EXPECTED.TXT` to match.
- **`CsvExporter` returning null:**
  Fixed `export()` to return the output `Path` instead of null, allowing callers to verify the file location.
- **CsvExporter malformed rows on embedded newlines/carriage returns:**
  Identified that descriptions containing `\n` or `\r` characters were written as literal line breaks in the CSV output, producing malformed rows. Fixed by sanitising newlines and carriage returns to spaces in `CsvExporter.escape()`. Verified cross-platform via CI on Windows, macOS, and Linux.
- **Case-insensitive category input not normalised before storage:**
  Identified that `AddCommand` was passing the raw category string (potentially uppercase) to `Income` and `Expense` constructors after checking against lowercased valid categories, causing inconsistent display (e.g. `[Income] Salary` instead of `[Income] salary`). Fixed by lowercasing the category in `AddCommand` before construction, and updating the `Income` assertion to use `toLowerCase()`.

---

### Code Contributed
[SeanChng's RepoSense](https://nus-cs2113-ay2526-s2.github.io/tp-dashboard/?search=SeanChng&breakdown=true)

---

### Contributions to the User Guide
I contributed the following sections to the User Guide:
- Add income command
- Storage and data persistence section

Each section includes the command format, examples, and explanations.

---

### Contributions to the Developer Guide
I contributed the following sections to the Developer Guide:
- Implementation of the Income class, including the category table, key methods, design considerations, and alternatives considered.
- Implementation of the Storage component, including architecture and flow, save/load logic, error handling strategy, and sequence diagrams for both load and save execution paths.
- Implementation of the Export feature, covering both `CsvExporter` and `TextFileExporter`, including design considerations and alternatives considered.
- Parser, Command, and Ui component descriptions, written from the actual source code.
- Non-Functional Requirements, Glossary, and Instructions for Manual Testing sections.
- Acknowledgements section, crediting Duke, AB3, Java SE 17 documentation, PlantUML, and the CS2113 Teaching Team.
- Error handling documentation for invalid date input in the Parser section, including the known misleading error message bug.
- Contributor warning in the Command section documenting the `isMutating()` contract and the v2.0 persistence bug.

---

### Contributions to Team-Based Tasks
- Reviewed pull requests and provided feedback on code quality and documentation.
- Actively managed the team's GitHub Issues board and assigned issues to relevant team members.
- Fixed checkstyle issues and improved code quality.
- Performed manual testing and text-ui testing.
- Responded to PE-D bug reports and triaged documentation and functionality issues.

---

### Review / Mentoring Contributions
- Reviewed teammates' pull requests and suggested improvements to code correctness and documentation.

---

### Tools Used
- Gradle for build automation
- JUnit for unit testing
- Checkstyle for code quality
- GitHub for version control and pull request management