# Chess Game

Desktop chess in Java 21 and JavaFX. Play a friend on one screen or the computer, with a clock, move hints and sounds.

## Features
- Full rules: castling, en passant, promotion, check, checkmate, stalemate, insufficient material, fifty-move rule, threefold repetition
- Play vs Player, or vs Computer (Easy / Medium / Hard) as White or Black
- Legal-move and last-move highlights, move list, resign, rematch
- Chess clock, piece styles, board colours and sounds (Settings)
- Local accounts (BCrypt-hashed passwords)

## Run it
You need **JDK 21** and Maven.

```bash
mvn javafx:run
```

Or build one jar for your operating system and run that:

```bash
mvn package
java -jar target/chess-game-1.0.0.jar
```

The jar only contains the JavaFX libraries of the OS it was built on.

## Test
```bash
mvn test
```

Includes perft tests (move-generation node counts against published values) and a UI smoke test that loads every screen and plays a move against the computer. The UI test is skipped when no display is available.

## Layout
| Path | What |
|---|---|
| `src/chess/model` | Board, pieces, moves |
| `src/chess/logic` | Rules (`GameManager`), AI (`ChessAI`), clock |
| `src/chess/controller`, `view`, `src/resources/*.fxml` | JavaFX UI |
| `src/chess/util` | Move validation, persistence, passwords, sound |
| `test` | JUnit 5 tests |

`users.json` and `settings.json` are created in the folder you run from. Copy `users.json.example` to see the user file format. Neither file is tracked by git.

See [ROADMAP.md](ROADMAP.md) for the plan and what is next.
