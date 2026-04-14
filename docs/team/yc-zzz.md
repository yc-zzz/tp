# Zhu Yicheng - Project Portfolio Page

## Project: MoneyBagProMax
MoneyBagProMax is a CLI personal finance manager for tracking income, expenses, budgets,
and spending habits, written in Java.

---

## Summary of Contributions

### New Feature: Transaction and Expense Classes
Provides the abstract `Transaction` base class defining the four shared fields (`category`,
`amount`, `description`, `date`) and the abstract `getType()` method. `Expense` extends it with
seven built-in categories and enforces category validity at construction time. All fields are
`protected final`, so editing a transaction requires constructing a new object rather than mutating
in place — keeping the list consistent and undo/redo straightforward.

### New Feature: Edit Command
Allows users to replace an existing transaction using
`edit INDEX [category]/PRICE [desc/DESCRIPTION] [d/YYYY-MM-DD]`.
The command performs an atomic remove-then-insert so all other indices remain stable, integrates
with `UndoRedoManager` via `recordEdit()` for full undo/redo support, and validates both index
bounds and category before any list modification — the list is never left in a partially-edited
state.

### New Feature: Category Management System
Lets users define, persist, and remove custom expense categories via `category add/NAME`,
`category remove/NAME`, and `category list`. Custom categories are saved to `data/categories.txt`
and reloaded on startup, integrating with `add`, `edit`, and `summary` without changes to those
commands. `CategoryManager` is a singleton, giving all commands a single source of truth. The
remove operation includes an in-use guard: categories referenced by existing transactions cannot
be deleted.

### Enhancements and Bug Fixes
- Fixed `EditCommand` rejecting custom categories by replacing the hardcoded
  `Expense.VALID_CATEGORIES` check with `CategoryManager.isValidExpenseCategory()`.
- Fixed `misc` always creating an Income transaction by removing it from
  `Income.VALID_CATEGORIES`.
- Fixed case-sensitivity inconsistency: category is normalised to lowercase before
  being passed to `Income`/`Expense` constructors in both `AddCommand` and `EditCommand`.
- Fixed wrong validation order: category is now validated before amount and
  frequency parsing in `parseAddCommand`.
- Strengthened amount validation to reject `Infinity`, sub-cent values, and amounts above
  $10,000,000 in both `parseAddCommand` and `parseEditCommand`.
- Fixed misleading "Using today's date" error message and silent empty `desc/`
  acceptance in the parser.
- Fixed "Invalid price" error caused by `d/` appearing before `desc/` by changing
  `else if` to `if` in `parseAmount`.
- Added parameter-order detection in `parseAddCommand` and `parseEditCommand`.
- Added assertions and Javadoc to `Expense`, `EditCommand`, and `CategoryManager`.
- Wrote JUnit tests for `EditCommand`, `CategoryCommand`, `CategoryManager`, `Expense`,
  `Transaction`, and `Parser.parseEditCommand()`.

### Code Contributed
[RepoSense link](https://nus-cs2113-ay2526-s2.github.io/tp-dashboard/?search=yc-zzz&breakdown=true&sort=groupTitle%20dsc&sortWithin=title&since=2026-02-20T00%3A00%3A00&timeframe=commit&mergegroup=&groupSelect=groupByRepos&checkedFileTypes=docs~functional-code~test-code~other&filteredFileName=)

### Contributions to the User Guide
- Edit command section (format, optional parameters, examples).
- Category management section (`category add/NAME`, `category remove/NAME`, `category list`).

### Contributions to the Developer Guide
- Transaction and Expense class sections (field table, immutability rationale, category table,
  design considerations, alternatives considered).
- Edit Transaction Feature section (architecture, flow, design considerations, alternatives,
  future improvements, `EditSequenceDiagram.puml` covering three execution paths).
- Category Management section (singleton design, persistence strategy, in-use guard,
  integration with existing commands).

### Contributions to Team-Based Tasks
- Reviewed pull requests and provided feedback on correctness and code style.
- Added assertions and logging across multiple classes as part of a defensive programming pass.
- Managed releases v1.0–v2.0 (2 releases) on GitHub.
- Performed manual testing of the edit and category commands across valid and invalid inputs.

### Beyond-Team Contributions
- Reported 10 bugs during PE-D for another team's product.

### Tools Used
- Gradle for build automation
- JUnit for unit testing
- Checkstyle for code quality
- GitHub for version control and pull request management