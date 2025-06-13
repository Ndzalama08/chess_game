package chess.model;

import java.util.List;

public class GameState {
    public String[][] board;     // piece IDs ("w_pawn", "b_queen", or null)
    public List<String> history; // algebraic move notation
    public boolean whiteTurn;    // who’s to move next
}
