package ChessFinesse.src;

import java.awt.Color;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Pieces {
    Piece[][] board;
    Match match;
    private static final int BOARD_SIZE = 8;
    HashMap<Integer[],ArrayList<Integer[]>> movemap;

    public Pieces(){
        board = new Piece[8][8];
        match = new Match();
        initialize();
        movemap = specify(null);
    }

    private void initialize() {
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
        if(match.check)
        match.check = false;
        board[tx][ty] = board[fo[0]][fo[1]];
        board[fo[0]][fo[1]] = null;
        getButton(tx, ty).update();
        if(tx == 0 && getButton(tx, ty)instanceof Pawn){ 
            getButton(tx, ty).promote(cb,this,tx,ty);
        }
    }

    boolean valid(int row, int col){
        if(movemap == null || movemap.isEmpty()){
            return false;
        }
        for (Integer[] entry : movemap.keySet()) {
            if(entry[0] == row && entry[1] == col) {
                match.lastMoves = new int[]{entry[0], entry[1]};
                return true;
            }
            
        }
        return false;
    }

    boolean validChoice(int row, int col){
        for(Integer[] entry : movemap.keySet()) {
            if(entry[0] == match.lastMoves[0] && entry[1] == match.lastMoves[1]) {
                for(Integer[] move : movemap.get(entry)) {
                    if(move[0] == row && move[1] == col) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    Piece getButton(int i,int j){
        return board[i][j];
    }


    Map<String,ArrayList<Integer[]>> whichChecking(int i, int j){
        return board[i][j].showPossibleMoves(this, match.turn, i, j);
    }

    
    public void restrainer(){
        HashMap<String,ArrayList<Integer[]>> restrict = restrictMoves();
        shiftBoard();
        match.nextTurn();
        movemap = specify(restrict);
    }

    boolean isCheckMate(){
        restrainer();
        if(movemap.isEmpty() && match.check){
            return true;
        }
        return false;
    }

    

    Map<String,ArrayList<Integer[]>> showValidMoves(int i, int j){
        return board[i][j].showPossibleMoves(this,match.turn, i, j);
    }

   public HashMap<Integer[],ArrayList<Integer[]>> specify(HashMap<String,ArrayList<Integer[]>> restrict){
        HashMap<Integer[],ArrayList<Integer[]>> save = new HashMap<>();
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if(getButton(i, j) != null && getButton(i, j).team.equals(match.turn)){
                    if(match.check){
                        Integer[] key = new Integer[]{i, j};
                        ArrayList<Integer[]> moves = getButton(i, j).savingMoves(this, restrict, i, j);
                        if(moves == null) continue;
                        save.put(key, moves);
                    }
                    else{
                        if(restrict != null &&  restrict.get("Defence") != null && restrict.get("Defence").contains(new Integer[]{i,j})){
                            System.out.println("here");
                            Integer[] key = new Integer[]{i, j};
                            ArrayList<Integer[]> moves = getButton(i, j).savingMoves(this, restrict, i, j);
                            if(moves.size() == 0) continue;
                            save.put(key, moves);
                        }
                        else{
                            Integer[] key = new Integer[]{i, j};
                            Map<String,ArrayList<Integer[]>> moves = getButton(i, j).showPossibleMoves(this, match.turn, i, j);
                            save.put(key, moves.get("Attack"));
                            save.get(key).addAll(moves.get("Defence"));
                        }
                    }
                }
            }
        }
        return save;
    }

    public  HashMap<String,ArrayList<Integer[]>> restrictMoves(){
        Map<String,ArrayList<Integer[]>> save;
        int count = 0;
        HashMap<String,ArrayList<Integer[]>> restrict = new HashMap<>();
        restrict.put("Defence", new ArrayList<>());
        restrict.put("Attack", new ArrayList<>());
        restrict.put("Path", new ArrayList<>());
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if(getButton(i, j) != null && getButton(i, j).team.equals(match.turn)){
                    save = getButton(i, j).showPossibleMoves(this, match.turn, i, j);
                if((save.get("Check") != null && save.get("Check").size() > 0)){
                        match.check = true;
                        restrict.get("Attack").addAll(save.get("Check"));
                        count++; 
                        restrict.get("Attack").add(new Integer[]{i,j});
                        
                    }
                    else{
                        if(save.containsKey("Restrict") && save.get("Restrict").size() > 0) {
                            for (Integer[] move : save.get("Restrict")) {
                                if (board[move[0]][move[1]]!=null) {
                                    restrict.get("Defence").add(move);
                                }
                                else {
                                    restrict.get("Path").add(move);
                                }
                            }
                        }
                    }
                }
            }
        }
        if(count > 1){
            restrict.get("Attack").clear();
            restrict.get("Attack").add(new Integer[]{-1,-1});
        }
        return coordinateTransformation(restrict);

    }


    protected HashMap<String,ArrayList<Integer[]>> coordinateTransformation(HashMap<String,ArrayList<Integer[]>> restrict){
        for (Map.Entry<String, ArrayList<Integer[]>> entry : restrict.entrySet()) {
            ArrayList<Integer[]> list = entry.getValue();
            for (Integer[] arr : list) {
                arr[0] = 7 - arr[0];
                arr[1] = 7 - arr[1];
            }
        }
        return restrict;
    }
    protected Set<List<Integer>> coordinateOfChecking(Set<Integer[]> a){
        Set<List<Integer>> listSet = new HashSet<>();
        for(Integer[] i:a){
            i[0] = 7 - i[0];
            i[1] = 7 - i[1];
            listSet.add(Arrays.asList(i));
        }
        return listSet;
    }
 
    public Set<Integer[]> searchForChecks(){
        Map<String,ArrayList<Integer[]>> moves = new HashMap<>();
        Set<Integer[]> checking = new HashSet<>();
        String[] keys = new String[]{"Defence","Neighbor","Check"};
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if(getButton(i, j) != null && getButton(i, j).team.equals(match.notTurn())){
                    moves = whichChecking(i, j);
                    for (String key : keys) {
                        if(moves.get(key) != null) {
                        checking.addAll(moves.get(key));
                    }
                    if(moves.get("Check") != null && moves.containsKey("Restrict")) {
                        checking.addAll(moves.get("Restrict"));
                    }
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
