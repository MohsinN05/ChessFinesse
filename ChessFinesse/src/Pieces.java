package ChessFinesse.src;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Pieces {
    Piece[][] board;
    private static final int BOARD_SIZE = 8;

    public Pieces(){
        board = new Piece[8][8];
    }

    void initialize() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if(i == 0 || i == 7){
                    if(j == 0 || j == 7){
                        board[i][j] = i == 0 ? new Rook("Black") : new Rook("White");
                    }
                    else if(j == 1 || j == 6){
                        board[i][j] = i == 0 ? new Knight("Black") : new Knight("White");
                    }
                    else if(j == 2 || j == 5){
                        board[i][j] = i == 0 ? new Bishop("Black") : new Bishop("White");
                    }
                    else if(j == 3){
                        board[i][j] = i == 0 ? new Queen("Black") : new Queen("White");
                    }
                    else if(j == 4){
                        board[i][j] = i == 0 ? new King("Black") : new King("White");
                    }
                }
                if(i == 1 || i == 6){
                        board[i][j] = i == 1 ? new Pawn("Black") : new Pawn("White");
                }
            }
        }
    }

    void move(int[] fo,int tx,int ty,ChessBoard cb){
        board[tx][ty] = board[fo[0]][fo[1]];
        board[fo[0]][fo[1]] = null;
        getButton(tx, ty).update();
        if(tx == 0 && getButton(tx, ty)instanceof Pawn){ 
            getButton(tx, ty).promote(cb,this,tx,ty);
        }
    }


    Piece getButton(int i,int j){
        return board[i][j];
    }

    void showValidMoves(int i, int j,ChessBoard cb){
        onlyMove(cb,board[i][j].showPossibleMoves(this, cb, i, j));
        cb.buttons[i][j].setBackground(Color.lightGray);
    }

    Map<String,ArrayList<Integer[]>> whichChecking(int i, int j,ChessBoard cb){
        return board[i][j].showPossibleMoves(this, cb, i, j);
    }

    public  void onlyMove(ChessBoard cb,Map<String,ArrayList<Integer[]>> moves){
        String[] keys = new String[]{"Defence", "Attack"};
        for (String key : keys) {
            if (moves.containsKey(key)) {
                for(Integer[] move : moves.get(key)) {
                        cb.buttons[move[0]][move[1]].setBackground(Color.cyan);
                }
            }
        }
    }

    public HashMap<Integer[],ArrayList<Integer[]>> defendCheck(ChessBoard cb){
        shiftBoard();
        cb.shiftButtons();
        HashMap<Integer[],ArrayList<Integer[]>> defence = onlyMove(cb);
        return defence;
    }

    public HashMap<Integer[],ArrayList<Integer[]>> onlyMove(ChessBoard cb){
        HashMap<Integer[],ArrayList<Integer[]>> canSave = new HashMap<>();
        ArrayList<Integer[]> checking;
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if(getButton(i, j)!=null && !getButton(i, j).team.equals(cb.match.notTurn())){
                    if(cb.check || cb.buttons[i][j].getBackground().equals(Color.yellow)){
                        checking = getButton(i, j).savingMoves(this, cb, i, j, null);
                        canSave.put(new Integer[]{i,j},checking);
                    }
                    else
                    canSave.put(new Integer[]{i,j},null);
                }
            }
        }
        return canSave;
    }
 
    public Set<Integer[]> searchForChecks(ChessBoard cb){
        Map<String,ArrayList<Integer[]>> moves = new HashMap<>();
        Set<Integer[]> checking = new HashSet<>();
        String[] keys = new String[]{"Defence","Neighbor","Check"};
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if(getButton(i, j) != null && getButton(i, j).team.equals(cb.match.notTurn())){
                    moves = whichChecking(i, j, cb);
                    for (String key : keys) {
                        if(moves.get(key) != null) {
                        checking.addAll(moves.get(key));
                    }
                }
                try{

                    for(Integer[] key : moves.get("Restrict")) {
                        if (board[key[0]][key[1]] != null) {
                            checking.addAll(moves.get("Restrict"));
                            break;
                        }
                    }
                } catch (Exception e) {
                }
                }
            }
        }
        return checking;
    }

    

    void shiftBoard(){
        Piece [][] copy1 = new Piece[8][8];
        for(int i = 0;i < board.length;i++){
            for(int j = 0;j < board.length;j++){
                copy1[i][j] = board[BOARD_SIZE-i-1][BOARD_SIZE-j-1];
            }
        }
        board = copy1;
    }

}
