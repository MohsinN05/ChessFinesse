package ChessFinesse.src;

import java.awt.Color;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ChessBoard {
    public Process stockfish;
    private java.io.BufferedReader stockfishReader;
    private java.io.BufferedWriter stockfishWriter;
    Piece[][] board;
    int whitePieces;
    int blackPieces;
    private static final int BOARD_SIZE = 8;
    HashMap<Integer[],LinkedList<Integer[]>> movemap;
    private String [] teams = {"White","Black"};
    boolean check;
    public String turn;
    int moves;
    ChessGUI cboard;
    MatchRecord records;
    String player1, player2;
    StockfishConnector sco = new StockfishConnector();
    
    public ChessBoard(ChessGUI c, String p1, String p2){
        sco.startEngine("ChessFinesse//Engines//stockfish-windows-x86-64-sse41-popcnt.exe");
        player1 = p1;
        player2 = p2;
        cboard = c;
        moves = 0;
        turn = teams[moves];
        check = false;
        board = new Piece[8][8];
        records = new MatchRecord();
        initialize();
        whitePieces = players("White");
        blackPieces = players("Black");
        movemap = specify(null);
    }



    private void initialize() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if(i == 0 || i == 7){
                    if(j == 0 || j == 7){
                        board[i][j] = i == 0 ? Piece.createRook("Black") : Piece.createRook("White");
                    }
                    else if(j == 1 || j == 6){
                        board[i][j] = i == 0 ? Piece.createKnight("Black") : Piece.createKnight("White");
                    }
                    else if(j == 2 || j == 5){
                        board[i][j] = i == 0 ? Piece.createBishop("Black") : Piece.createBishop("White");
                    }
                    else if(j == 3){
                        board[i][j] = i == 0 ? Piece.createQueen("Black") : Piece.createQueen("White");
                    }
                    else if(j == 4){
                        board[i][j] = i == 0 ? Piece.createKing("Black") : Piece.createKing("White");
                    }
                }
                if(i == 1 || i == 6){
                    board[i][j] = i == 1 ? Piece.createPawn("Black") : Piece.createPawn("White");
                }
            }
        }
    }

    protected String turn(){
        return turn;
    }

    public void nextTurn(){
        turn = teams[++moves%2];
    }

    boolean whitesTurn(){
        return turn.equals("White");
    }

    public void prevTurn() {
        moves -= 1;
        turn = teams[(moves % 2 + 2) % 2];
    }

    public String notTurn(){
        return teams[(moves+1)%2];
    }


    private int players(String team){
        int t = 0;
        for(int i = 0;i<BOARD_SIZE;i++){
            for(int j = 0;j<BOARD_SIZE;j++){
                if(board[i][j] != null && board[i][j].team.equals(team)){
                    t++;
                }
            }
        }
        return t;
    }

    boolean killed(int tx, int ty){
        if(board[tx][ty] != null)
        return true;
        return false;
    }

    // private boolean foundIt(LinkedList<Integer[]> save, int x ,int y){
    //     for(Integer[]i:save){
    //         if(Arrays.equals(i,new Integer[]{x,y}))
    //         return true;
    //     }
    //     return false;
    // }

    // private String similarMoves(int[] fo, int tx, int ty){
    //     Integer[] f = Arrays.stream(fo).boxed().toArray(Integer[]::new);
    //     Set<Integer[]> same = new HashSet<>();
    //     for(Integer[]i:movemap.keySet()){
    //         if(!Arrays.equals(i, f) && board[i[0]][i[1]].getClass().equals(board[f[0]][f[1]].getClass()) && movemap.get(i) != null && movemap.get(i).size() > 0 && foundIt(movemap.get(i), tx, ty)){
    //             same.add(i);
    //         }
    //     }
    //     return match.refine(f,same);
    // }

    void move(int fx, int fy,int tx,int ty){
        if(check)
        check = false;
        if(killed(tx, ty)){
            killPiece();
            records.addMove(turn, tx, ty, getButton(tx, ty).getPrefix().toLowerCase());
        }
        else
        records.addMove(turn, tx, ty, "");
        if(getButton(fx, fy) instanceof Pawn && getButton(tx,ty) == null && getButton(tx+1, ty) instanceof Pawn){
            board[tx][ty] = board[fx][fy];
            board[fx][fy] = null;
            getButton(tx, ty).update();
            board[tx+1][ty] = null;
            killPiece();
            return;
        }
        getButton(fx, fy).update();
        board[tx][ty] = board[fx][fy];
        board[fx][fy] = null;
        if(tx == 0){ 
            getButton(tx, ty).promote(this,tx,ty);
        }
        if(getButton(tx, ty) instanceof King && Math.abs(fy-ty) > 1){
            if(fy-ty > 1){
                board[tx][ty+1] = board[tx][0];
                board[tx][0] = null;
                board[tx][ty+1].update();
            }
            else{
                board[tx][ty-1] = board[tx][7];
                board[tx][7] = null;
                board[tx][ty-1].update();
            }
        }
    }

    boolean valid(int row, int col){
        if(movemap == null || movemap.isEmpty()){
            return false;
        }
        for (Integer[] entry : movemap.keySet()) {
            if(entry[0] == row && entry[1] == col) {
                records.lastMoves = new int[]{entry[0], entry[1]};
                return true;
            }
        }
        return false;
    }

    private void killPiece(){
        if(whitesTurn()){
            blackPieces -=1;
        }
        else{
            whitePieces -=1;
        }
    }

    boolean validChoice(int row, int col){
        for(Integer[] entry : movemap.keySet()) {
            if(records.lastMoves != null && (entry[0] == records.lastMoves[0] && entry[1] == records.lastMoves[1])) {
                for(Integer[] move : movemap.get(entry)) {
                    if(move[0] == row && move[1] == col) {
                        return true;
                    }
                }
            }
        }
        records.lastMoves = null;
        return false;
    }

    Piece getButton(int i,int j){
        return board[i][j];
    }

    private Map<String,LinkedList<Integer[]>> whichChecking(int i, int j){
        return board[i][j].showPossibleMoves(this, i, j);
    }
    
    private void restrainer(){
        if(!player2.equals("b")){
            HashMap<String,LinkedList<Integer[]>> restrict = restrictMoves();
            shiftBoard();
            nextTurn();
            movemap = specify(restrict);
        }
        else{
            cboard.updateButtons();
            shiftBoard();
            nextTurn();
            String ai = sco.getBestMove(records.allMoves());
            System.out.println(ai);
            int[] from = records.convertMove(notTurn(),ai.substring(0,2));
            records.lastMoves = from;
            int[] to = new int[2];
            if(ai.length() == 5)
            to = records.convertMove(notTurn(), ai.substring(2, 4));
            else
            to = records.convertMove(notTurn(), ai.substring(2));
            move(from[0], from[1], to[0], to[1]);
            HashMap<String,LinkedList<Integer[]>> restrict = restrictMoves();
            records.lastMoves = null;
            shiftBoard();
            nextTurn();
            movemap = specify(restrict);
        }
    }

    private boolean moveMap(){
        if(movemap == null || movemap.isEmpty()) {
            return false;
        }
        for (Integer[] entry : movemap.keySet()) {
            if(movemap.get(entry) != null && !movemap.get(entry).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public String conclusion(){
        restrainer();
        if(check){
            if(moveMap()){
                return "Check";
            }
            else{
                return "Checkmate";
            }
        }
        else{
            if(!moveMap() || (whitePieces == 1 && blackPieces == 1)){
                return "Stalemate";
            }
        }
        return "Move";
    }
    
    public boolean matchesKey(HashMap<String,LinkedList<Integer[]>> restrict, int i, int j){
        for (String key : restrict.keySet()) {
            if (key.matches("Defence\\d+")) {
                if(Arrays.equals(restrict.get(key).getFirst(), new Integer[]{i, j})) {
                    return true;
                }
            }
        }
        return false;
    }

   private HashMap<Integer[],LinkedList<Integer[]>> specify(HashMap<String,LinkedList<Integer[]>> restrict){
        HashMap<Integer[],LinkedList<Integer[]>> save = new HashMap<>();
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if(getButton(i, j) != null && getButton(i, j).team.equals(turn)){
                    if((check || (restrict != null && matchesKey(restrict,i,j)))){
                        Integer[] key = new Integer[]{i, j};
                        if(!(check && (restrict != null && matchesKey(restrict,i,j)))){
                            LinkedList<Integer[]> moves = getButton(i, j).savingMoves(this, restrict, i, j);
                            if(moves == null) continue;
                            save.put(key, moves);
                        }
                    }
                    else{
                        Integer[] key = new Integer[]{i, j};
                        Map<String,LinkedList<Integer[]>> moves = getButton(i, j).showPossibleMoves(this, i, j);
                        save.put(key, moves.get("Attack"));
                        save.get(key).addAll(moves.get("Defence"));
                        if(moves.containsKey("KingSide") && moves.get("KingSide") != null) {
                            save.get(key).addAll(moves.get("KingSide"));
                        }
                        if(moves.containsKey("QueenSide") && moves.get("QueenSide") != null) {
                            save.get(key).addAll(moves.get("QueenSide"));
                        }
                    }
                }
            }
        }
        return save;
    }

    private HashMap<String,LinkedList<Integer[]>> restrictMoves(){
        Map<String,LinkedList<Integer[]>> save;
        int count = 0;
        HashMap<String,LinkedList<Integer[]>> restrict = new HashMap<>();
        restrict.put("Attack", new LinkedList<>());
        int r = 0;
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if(getButton(i, j) != null && getButton(i, j).team.equals(turn())){
                    save = getButton(i, j).showPossibleMoves(this, i, j);
                if((save.get("Check") != null && save.get("Check").size() > 0)){
                        check = true;
                        restrict.get("Attack").addAll(save.get("Check"));
                        count++; 
                        restrict.get("Attack").add(new Integer[]{i,j});
                    }
                    else{
                        if(save.containsKey("Restrict") && save.get("Restrict").size() > 0) {
                            restrict.put("Defence" + ++r, new LinkedList<>());
                            for (Integer[] move : save.get("Restrict")) {
                                if (board[move[0]][move[1]]!=null) {
                                    restrict.get("Defence" + r).addFirst(move);
                                }
                                else {
                                    restrict.get("Defence" + r).add(move);
                                }
                            }
                            restrict.get("Defence" + r).add(new Integer[]{i, j});
                        }
                    }
                }
            }
        }
        if(count > 1){
            restrict.get("Attack").clear();
        }
        for(Integer[] i:restrict.get("Attack")){
            System.out.println(i[0] + " " + i[1]);
        }
        return coordinateTransformation(restrict);

    }

    private HashMap<String,LinkedList<Integer[]>> coordinateTransformation(HashMap<String,LinkedList<Integer[]>> restrict){
        for (Map.Entry<String, LinkedList<Integer[]>> entry : restrict.entrySet()) {
            LinkedList<Integer[]> list = entry.getValue();
            for (Integer[] arr : list) {
                arr[0] = 7 - arr[0];
                arr[1] = 7 - arr[1];
            }
        }
        return restrict;
    }

    private HashMap<String,LinkedList<Integer[]>> LimitMoves(){
        int r = 0;
        HashMap<String,LinkedList<Integer[]>> restrict = new HashMap<>();
        Map<String,LinkedList<Integer[]>> moves = new HashMap<>();
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if(getButton(i, j) != null && getButton(i, j).team.equals(turn()) && !(getButton(i, j) instanceof King)){
                    moves = whichChecking(i, j);
                    if(moves.get("Check").size() == 0 && moves.containsKey("Restrict") &&  moves.get("Restrict").size() > 0){
                        restrict.put("Defence" + ++r, new LinkedList<>());
                            for (Integer[] move : moves.get("Restrict")) {
                                if (board[move[0]][move[1]]!=null) {
                                    restrict.get("Defence" + r).addFirst(move);
                                }
                                else {
                                    restrict.get("Defence" + r).add(move);
                                }
                            }
                            restrict.get("Defence" + r).add(new Integer[]{i, j});
                    }
                }
            }
        }
        return coordinateTransformation(restrict);
    }

    public HashMap<Integer[],LinkedList<Integer[]>> SearchForMoves(){
        HashMap<String,LinkedList<Integer[]>> restrict = LimitMoves();
        shiftBoard();
        HashMap<Integer[],LinkedList<Integer[]>> save = new HashMap<>();
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if(getButton(i, j) != null && getButton(i, j).team.equals(notTurn())){
                    if((restrict != null && matchesKey(restrict,i,j))){
                        Integer[] key = new Integer[]{i, j};
                        LinkedList<Integer[]> moves = getButton(i, j).savingMoves(this, restrict, i, j);
                        if(moves == null) continue;
                        save.put(key, moves);
                    }
                    else{
                        Integer[] key = new Integer[]{i, j};
                        Map<String,LinkedList<Integer[]>> moves = getButton(i, j).showPossibleMoves(this, i, j);
                        save.put(key, moves.get("Attack"));
                        if(!(getButton(i, j) instanceof Pawn))
                        save.get(key).addAll(moves.get("Defence"));
                        save.get(key).addAll(moves.get("Neighbor"));
                        if(moves.get("Check") != null && moves.get("Check").size() != 0){
                            if(moves.get("Restrict") != null && moves.get("Restrict").size() > 0)
                            save.get(key).addAll(moves.get("Restrict"));
                            save.get(key).addAll(moves.get("Check"));
                        }
                    }
                }
            }
        }
        shiftBoard();
        return save;
    }

    

    private void shiftBoard(){
        Piece [][] copy1 = new Piece[8][8];
        for(int i = 0;i < board.length;i++){
            for(int j = 0;j < board.length;j++){
                copy1[i][j] = board[BOARD_SIZE-i-1][BOARD_SIZE-j-1];
            }
        }
        board = copy1;
    }
}
