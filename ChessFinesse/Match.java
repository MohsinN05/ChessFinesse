package ChessFinesse;

import java.util.Stack;

public class Match {

    public Stack<int[]> lastMoves = new Stack<>();
    private String [] teams = {"White","Black"};
    public String turn;
    int moves;

    public Match(){
        moves = 0;
        turn = teams[moves];
    }

    public void nextTurn(){
        turn = teams[++moves%2];
    }

    public int[] lastMove(){
        return lastMoves.peek();
    }

    public void addInStack(Piece[][]bo,int x, int y){

    }
    
}
