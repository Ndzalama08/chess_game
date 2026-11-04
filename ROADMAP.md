# Chess Game: MVP Plan, Language Decision, Roadmap

Written 2026-11-04 from a read of the code (about 2,450 lines of Java, 8 test classes, 20+ merged fix branches). Nothing here was run: `mvn test` and the app itself are still unchecked (see Phase 0).

## Status (2026-11-04)

Phases 0–4 are done and released as v1.0.0. Decisions made on the way: resume-game and the history screen were deleted rather than wired in (post-MVP); Hard = search depth 3 (depth 4 costs about half a second from the opening, more in the middlegame). Nothing was pushed or published. The backlog at the bottom is what is next.

## 1. Where the project stood at the start

**Works (per code + last surefire reports, all 33 tests green):**
- Piece movement, castling, en passant, promotion, check, checkmate, stalemate
- Minimax AI with alpha-beta (material-only eval)
- Chess clock, settings, BCrypt login/signup, Maven build, GitHub Actions CI

**Problems found, ordered by how badly they block an MVP:**

| # | Problem | Evidence |
|---|---|---|
| 1 | Resource paths mix two conventions. Maven puts `src/resources` at the classpath root, so `/resources/login.fxml` does not exist there. Only the IDE output (`out/production`) has both layouts. | `Main.java:35,63,68,78,128`, `GameController` sounds use `/resources/...`; `mainMenu.fxml` and `/images/...` use root paths |
| 2 | `Main.startMatch` loads `Board.fxml` but the file is `board.fxml`. Fails on Linux/macOS. | `Main.java:35` |
| 3 | No way to run via Maven (no `javafx-maven-plugin`), no packaged artifact. `JAVA_HOME` here is JDK 17 but the pom needs 21. | `pom.xml`, `mvn -v` |
| 4 | AI search runs on the JavaFX thread and freezes the UI. | `GameController.java:182` |
| 5 | The pawn-promotion dialog also fires for AI promotions (same chooser on the real `GameManager`). | `GameController.java:65`, `GameManager.performPromotion` |
| 6 | Draw rules missing: insufficient material, 50-move, threefold repetition. | `GameManager` has only stalemate |
| 7 | `isValid` check-simulation ignores en passant (captured pawn not removed) and castling rook move. Rare wrong answers. | `GameManager.java:73-79` |
| 8 | Selecting a piece then clicking another own piece flashes "illegal" and deselects. No legal-move hints, no resign, no new game or rematch button. | `GameController.handleClick` |
| 9 | Dead or unwired code: `saveLastGame`/`loadLastGame`/`GameState`, `HistoryController`/`history.fxml`, `ClickHandler`, `BoardState` partly, commented-out `loadScene` in `Main`. No resume-game feature. | grep |
| 10 | AI difficulty fixed at depth 2, human always White, eval is material only (plays weak, no positional sense). | `GameController.java:43`, `ChessAI.evaluateBoard` |
| 11 | Move generation is brute force (every piece × 64 squares × full validation). Fine at depth 2, too slow beyond about 3. | `GameManager.generateLegalMoves` |
| 12 | No README. No tests for controllers, promotion, en-passant-exposes-king, or castling-through-check. | repo root, `test/` |

## 2. MVP definition

> A player can start the app on Windows, macOS or Linux from a single command or jar, log in, play a **complete legal game** (PvP or vs computer) to a result (mate, stalemate, draw, resign, timeout), with a UI that never freezes.

Explicitly **out of MVP:** online play, accounts stats/ratings, PGN/SAN, undo, opening book, strong AI, themes beyond what exists.

## 3. Language / stack decision

**Decision: stay on Java 21 + JavaFX + Maven. No rewrite.**

Why:
- 2.4k lines of working, tested rules/AI code plus 20 reviewed fix branches. A rewrite throws that away to reach the same feature set.
- The course folder is "Software development & refactoring". The deliverable is evidence of improving an existing codebase, not a new one.
- Everything the MVP needs (desktop GUI, JSON, BCrypt, audio, CI) already works in this stack.

Alternatives considered:
- **Kotlin:** full Java interop, so it could be adopted per-file later. Little gain now; skip.
- **Python (pygame/Tk):** faster to prototype, but a full rewrite and a slower AI.
- **TypeScript web app:** best reach and easy online play, but a full rewrite and a new toolchain.

**Revisit only if** the goal changes to web/mobile reach or online multiplayer. In that case keep the engine (`logic/`, `model/`) in Java behind a small API instead of porting it.

*Assumption:* this is a school or portfolio project with no hard deadline given. If there is a deadline, tell me and I will cut Phase 3.

## 4. Roadmap

Each phase is a few small branches, merged like the existing history (`fix/...`, `feat/...`, `test/...`).

### Phase 0: Make it run everywhere (about 1 day, do first)
Nothing else is verifiable until the app launches the same way for everyone.
1. Install or select JDK 21 locally; run `mvn test` to confirm the green baseline.
2. Pick one resource convention (root-relative: `/login.fxml`, `/styles.css`, `/sounds/...`, `/images/...`) and fix every path. Rename the `Board.fxml` reference to `board.fxml`.
3. Add `javafx-maven-plugin` (`mvn javafx:run`).
4. Smoke check: launch, sign up, log in, play a move, hear a sound, open settings.
**Done when:** `mvn javafx:run` works on Windows, and CI still passes.

### Phase 1: Rules correctness (2–3 days)
1. Add a **perft test** (node counts from the start position: 20 / 400 / 8,902 at depths 1–3, plus the "Kiwipete" position). This is the cheapest way to prove move generation is right.
2. Fix `isValid` check simulation to apply en passant and castling properly (the same apply/undo code `attemptMove` uses).
3. Draw rules: insufficient material, 50-move, threefold repetition. Surface as `GameManager.gameResult()` instead of the controller calling `isCheckmate`/`isStalemate` separately.
4. Promotion chooser applies to the human only; the AI promotes to Queen without a dialog.
5. Tests: promotion, en passant exposing king, castling through/out of check, each draw rule.
**Done when:** perft passes and each new rule has a failing-then-passing test.

### Phase 2: Playable UX (3–4 days)
1. Run the AI search on a background thread (`Task`) with a "thinking…" indicator and a short pause so replies do not feel instant.
2. Click another own piece to re-select; highlight legal destinations and the last move.
3. Game-over dialog with Rematch / Main Menu; Resign button; Back to Menu confirmation mid-game.
4. Menu choices: AI difficulty (depth 1–3) and play-as White/Black.
5. Fix PvAI result text (message currently hardcoded as "AI wins").
**Done when:** a full game vs the AI can be played with the mouse only and the window never hangs.

### Phase 3: Persistence and cleanup (2 days, optional for MVP)
1. Either wire up resume-last-game (`GameState` save on exit, "Continue" menu button) or delete `GameState`, `saveLastGame`, `loadLastGame`.
2. Same decision for `HistoryController`/`history.fxml`/`ClickHandler`: wire in or delete.
3. Delete commented-out code in `Main`, merge the duplicate `Gson` fields in `PersistenceUtil`, drop the empty `isPromotion` block in `GameManager.isValid`, untrack `lastGame.json`.
**Done when:** `grep` shows no unused persistence/controller code.

### Phase 4: Ship (1–2 days)
1. README: what it is, JDK 21 requirement, `mvn javafx:run`, `mvn test`, screenshots.
2. Build a runnable artifact (shaded jar, or `jpackage` for a native Windows installer).
3. Tag `v1.0.0`, attach the artifact to a GitHub release.
**Done when:** a fresh clone plus the README gets a classmate playing.

### After MVP (backlog, in rough value order)
- Stronger AI: piece-square tables, move ordering, quiescence search, time-limited iterative deepening.
- Faster move generation (per-piece move lists instead of 64×64 probing). Only needed once AI goes past depth 3.
- SAN move notation and PGN export; move list that shows `1. e4 e5`.
- Undo/takeback in PvP; clock increments.
- Per-user stats using the existing login (`AppSession`).
- Controller/UI tests with TestFX.

## 5. Suggested order of work

`Phase 0 → Phase 1 → Phase 2 → Phase 4 (MVP shipped) → Phase 3 → backlog`

Phase 0 first because every later check depends on the app launching. Phase 1 before UX because the perft test makes every later engine change safe. Phase 3 can slip past the MVP release without hurting a player.
