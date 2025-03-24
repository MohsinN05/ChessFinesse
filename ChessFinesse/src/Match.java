package ChessFinesse.src;

import java.util.Stack;

public class Match {

    public int[] lastMoves;
    Stack<Move> oldMoves;
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

    public String translate(int i,int j){
        String move = String.format("%s%d%s%d",'a'+lastMoves[0],lastMoves[1],'a'+i,j);
        return move;
    }

    public void prevTurn() {
        moves -= 1;
        turn = teams[(moves % 2 + 2) % 2];
    }

    public String notTurn(){
        return teams[(moves+1)%2];
    }
    

    public void addInStack(Piece[][]bo,int x, int y){

    }
    
}
