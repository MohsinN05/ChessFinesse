package ChessFinesse;

public class Match {
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
    
}
