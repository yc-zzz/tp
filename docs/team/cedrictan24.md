# Cedric Tan's Project Portfolio Page

## Project: MoneyBagProMax

MoneyBagProMax is a command-line personal finance management application that helps users track income and expenses, manage budgets, and gain insights into spending habits through financial statistics. The user interacts with it using a CLI, and the application is written in Java.

Given below are my contributions to the project.

---

## Summary of Contributions

### New Feature: Budget Feature

What it does:  
Allows users to set a monthly budget and view budget status, including total expenses, remaining budget, and percentage used.

Justification:  
Improves usability by helping users monitor spending and avoid exceeding their budget.

Highlights:  
Required filtering transactions by month and integrating budget calculations with transaction data.

---

### New Feature: Statistics Feature

What it does:  
Displays financial statistics such as total income/expenses, highest/lowest transactions, most frequent category, average spending per category, spending trend, and budget usage.

Justification:  
Provides users with meaningful insights into their financial behaviour.

Highlights:  
Used data structures such as HashMap for aggregation and refactored logic to reduce duplication. Implemented a month-based spending trend feature to ensure meaningful time-based analysis.

---

### Enhancements implemented

- Budget Feature
  - Implemented budget set and budget status
  - Added budget tracking (remaining amount, percentage used)
  - Implemented persistence using [BUDGET] format
  - Added validation for zero, negative, and excessively large values

- Statistics Feature
  - Implemented stats command for financial analysis
  - Designed spending trend using month-based aggregation
  - Ensured correctness by returning "Not enough data" when insufficient data is present

- Core Command Improvements
  - Enhanced delete command to handle empty lists and invalid indices
  - Improved error handling to prevent runtime exceptions

---

### Code Contributed

[RepoSense link](https://nus-cs2113-ay2526-s2.github.io/tp-dashboard/?search=cedrictan24&breakdown=true&sort=groupTitle%20dsc&sortWithin=title&since=2026-02-20T00%3A00%3A00&timeframe=commit&mergegroup=&groupSelect=groupByRepos&checkedFileTypes=docs~functional-code~test-code~other&filteredFileName=)

---

### Contributions to the User Guide (UG)

- Documented:
  - list, delete, budget, and stats commands
- Included command formats, examples, and expected outputs
- Ensured consistency between documentation and implementation

<div style="page-break-before: always;"></div>

### Contributions to the Developer Guide (DG)

- Added implementation details for Budget and Statistics features
- Documented spending trend logic and design considerations
- Updated documentation for core commands:
  - delete command (index validation and empty list handling)
  - list command (interaction with `TransactionList`)
- Created and refined UML diagrams:
  - sequence diagrams for command execution flow
  - class diagrams for Budget and Statistics features

---

### Contributions to team-based tasks

- Integrated features into the main codebase
- Maintained consistency in validation and error handling

---

### Review / mentoring contributions

- Reviewed teammates’ PRs and provided feedback on correctness and code quality

---

### Contributions beyond the project team

- Reported multiple bugs during PE-D with clear reproduction steps and severity classification

---

### Tools Used

- Gradle, JUnit, Checkstyle, GitHub
