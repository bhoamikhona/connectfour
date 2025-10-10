<img width="2182" height="2023" alt="image" src="https://github.com/user-attachments/assets/29c6a069-4cf7-4bf2-91de-6cd653cc19f0" />


# Connect Four (Java, console)

A simple, fully playable **two-player Connect Four** for the terminal, written in Java. It includes a help menu, **undo**, **restart**, **board reprint**, and an on-demand **hint** system. The project is organized as an IntelliJ IDEA project but can also be compiled and run from the command line. 
## Features

* **Classic rules**: drop discs into a 7×6 grid; first to connect four (horizontal, vertical, or diagonal) wins.
* **Console UI**: prints the board with column headers; prompts players by name/tokens.
* **Commands**:

  * `0–6` — drop into a column
  * `board` — reprint the current board
  * `undo` — revert the last move
  * `restart` — clear the board and start over
  * `hint` — show *safe* / *recommended* moves
  * `help` — show the help menu
  * `quit` — exit the game
    (The help text includes `hint`, and hints were switched from auto-display to on-demand.)
* **Hints engine**: encapsulated in a `Hints` class (with a small custom `LinkedList`) that evaluates recommended moves.

## Project structure

```
src/
  Board.java     // grid state, printing, win detection
  Game.java      // main game loop, command handling (help/board/restart/undo/hint)
  Player.java    // player model (name + token)
  ...            // Hints, TurnQueue, LinkedList, etc.
```

* The **game loop** (`Game.run()`) handles reading commands, switching turns, and delegating to handlers.
* `Player` lives in the `connectfour` package.

## Getting started

### Prerequisites

* **Java 17+** (earlier may work, but the project is modern Java-friendly).
* Optional: **IntelliJ IDEA** (project files like `.iml` and `.idea` are included).

### Option A — Run in IntelliJ (easiest)

1. **Open** the project folder in IntelliJ.
2. Let IntelliJ index and use its default project JDK (17+).
3. **Run** the application by executing the class with the `main` method (look in `Game.java`). The console will prompt for commands.

### Option B — Run from the command line

From the repo root:

```bash
# 1) Compile
javac -d out $(find src -name "*.java")

# 2) Run
java -cp out connectfour.Game
```

> If your shell doesn’t support `$(find ...)` (e.g., on Windows CMD), compile with a glob:
> `javac -d out src\*.java` then `java -cp out connectfour.Game`.

## How to play (commands)

At each turn, the prompt shows the **current player** (name and token). Enter:

* **`0`..`6`** — drop your piece in a column
* **`hint`** — show recommended/safe moves for your current position
* **`undo`** — take back the last move
* **`board`** — reprint the current board
* **`restart`** — new game
* **`help`** — list commands
* **`quit`** — exit

Command handling and the printed help/menus are implemented in `Game.java`.

## Implementation notes

* **Board rendering** prints an ASCII grid with headers for easy column selection.
* **Turn management** uses a queue abstraction (`TurnQueue`) to rotate players. Updates to turn handling and undo were part of recent commits.
* **Hints** logic is kept separate from `Board` (moved into `Hints` and backed by a small `LinkedList`). Hints are **not auto-printed** after each move; you request them via the `hint` command.

## Roadmap ideas

* Add **unit tests** for win detection and hint scoring
* Optional **AI opponent**
* Persist **match history** / PGN-style move logs
* Colorized console output

## Credits

* Built by **@bhoamikhona** and collaborators.
