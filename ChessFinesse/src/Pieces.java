package ChessFinesse.src;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
        if(!board[i][j].showPossibleMoves(this, cb, i, j, findKing(cb.match.notTurn()))) 
        board[i][j].onlyMove(cb);
        cb.buttons[i][j].setBackground(Color.lightGray);
    }

    boolean whichChecking(int i, int j,ChessBoard cb,int[] king){
        return board[i][j].showPossibleMoves(this, cb, i, j, king);
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
 
    public ArrayList<int[]> searchForChecks(ChessBoard cb,int[]king){
        ArrayList<int[]> checking = new ArrayList<>();
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if(getButton(i, j)!=null && !getButton(i, j).team.equals(board[king[0]][king[1]].team)){
                    if(whichChecking(i,j,cb,king)){
                        checking.add(new int[]{i,j});
                    }
                }
            }
        }
        return checking;
    }

    

    

    public ArrayList<int[]> coordinateOfChecking(ArrayList<int[]> a){
        for(int[] i:a){
            i[0] = 7 - i[0];
            i[1] = 7 - i[1];
        }
        return a;
    }

    public void hereCheck(int[]king,ArrayList<int[]> a,ChessBoard cb){
        for(int[] i:a){
            System.out.printf("%d,%d\n",i[0],i[1]);
        }
        for(int i = 1;Math.abs(i)<2;i--){
            for(int j = 1;Math.abs(j)<2;j--){
                try{
                    if((getButton(king[0]+i, king[1]+j)==null || !getButton(king[0]+i, king[1]+j).team.equals(cb.match.turn) || a.contains(new int[]{king[0]+i,king[1]+j})) && (!cb.buttons[king[0]+i][king[1]+j].getBackground().equals(Color.cyan) && !cb.buttons[king[0]+i][king[1]+j].getBackground().equals(Color.red) && !cb.buttons[king[0]+i][king[1]+j].getBackground().equals(Color.orange) && !cb.buttons[king[0]+i][king[1]+j].getBackground().equals(Color.green))){
                        cb.buttons[king[0]+i][king[1]+j].setBackground(Color.black);
                    }
                }catch(Exception e){}      
            }
        }
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

    public int[] findKing(String turn){
        int i=-1,j = -1;
        for(i = 0;i<8;i++){
            for(j = 0;j<8;j++){
                if(board[i][j]!= null && board[i][j] instanceof King && board[i][j].team.equals(turn)){
                    return (new int[]{i,j});
                }
            }
        }
        return (new int[]{i,j});
    }

}
