package ChessFinesse.src;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;



public class ChessBoard extends JPanel {
    GameView safe;

    private static final int BOARD_SIZE = 8;
    ButtonFunc[][] buttons;
    Pieces board;
    Match match;
    HashMap<Integer[],ArrayList<Integer[]>> defence = new HashMap<>(); 
    ArrayList<int[]> attack;
    boolean check = false;

    public ChessBoard(GameView s){
        safe = s;
        buttons = new ButtonFunc[BOARD_SIZE][BOARD_SIZE];
        board = new Pieces();
        match = new Match();
        this.setLayout(new GridLayout(BOARD_SIZE,BOARD_SIZE));
        this.setPreferredSize(new Dimension(700, 700));
        setMaximumSize(new Dimension(700,700));
        setMinimumSize(new Dimension(700,700));
        board.initialize();
        initializeButtons();
    }

    public Dimension getPreferredSize(){
        return new Dimension(700,700);
    }

    public void move(int[] fo,int tx,int ty){
        board.move(fo, tx, ty,this);
    }
    

    

    private void initializeButtons() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                ButtonFunc button = new ButtonFunc();
                button.setState(i, j);
                button.setFocusPainted(false);
                int row = i, col = j;
                buttons[i][j] = button;
                button.addActionListener(customAction(row, col));
                add(button);     
            }
        }
        updateButtons();
    }



    private void showPossibleMoves(int i,int j,boolean check){
        for (Integer[] key : defence.keySet()) {
            if (Arrays.equals(key, new Integer[]{i,j})) {
                ArrayList<Integer[]> canMove=defence.get(key);
                if(canMove!=null){
                    for(Integer[] c:canMove){
                        buttons[c[0]][c[1]].setBackground(Color.cyan);
                    }
                    return;
                }
                else{
                    board.showValidMoves(i, j, this);
                    return;
                }
            }
        }
        board.showValidMoves(i, j, this);
    }

    private ButtonFunc[][] cloneButtons(){
        ButtonFunc [][] copy2 = new ButtonFunc[8][8];
        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons[i].length; j++) {
                if (buttons[i][j] != null) {
                    copy2[i][j] = buttons[i][j].clone(); 
                    int row = i, col = j;
                    copy2[i][j].addActionListener(customAction(row, col));
                    add(copy2[i][j]);
                }
            }
        }
        return copy2;
    }

    void shiftButtons(){
        ButtonFunc [][] copy2 = cloneButtons();
        for(int i = 0;i < buttons.length;i++){
            for(int j = 0;j < buttons.length;j++){
                if (board.getButton(i, j) != null) {
                    copy2[i][j].setIcon(board.getButton(i,j).pic);
                } else {
                    copy2[i][j].setIcon(null);
                }
                copy2[i][j].setBackground(buttons[8-i-1][8-j-1].getBackground());
                
            }
        }
        buttons = copy2;
        updateButtonLayout();
    }

    private boolean isValidMove(int i,int j){
        return buttons[i][j].isValidMove(this, i, j);
    }

    void changeButtons(){
        board.shiftBoard();
        ButtonFunc [][] copy2 = buttons.clone();
        for(int i = 0;i < buttons.length;i++){
            for(int j = 0;j < buttons.length;j++){
                copy2[i][j] = buttons[BOARD_SIZE-i-1][BOARD_SIZE-j-1];
            }
        }
        buttons = copy2;
        //updateButtonLayout();
    }

    private boolean correctMove(HashMap<Integer[],ArrayList<Integer[]>>map,Integer[]searchKey){
        boolean found = false;
        for (Integer[] key : map.keySet()) {
            if (Arrays.equals(key, searchKey)) {
                found = true;
                break;
            }
        }
        return found;
    }

    private ActionListener customAction(int row,int col){
        return e -> {
            if(board.getButton(row, col) != null && board.getButton(row,col).team.equals(match.turn)){
                updateButtons();
                showPossibleMoves(row, col, check);
                match.lastMoves = new int[]{row,col};
                return;
            }
            else if((board.getButton(row, col) == null || !board.getButton(row, col).team.equals(match.turn)) && (board.board[match.lastMoves[0]][match.lastMoves[1]].team.equals(match.turn)) && match.lastMoves!=null && isValidMove(row,col)){
                    check = false;
                    defence = null;
                    int[]save = match.lastMoves;
                    move(save, row, col);
                    match.nextTurn();
                    match.lastMoves = null;
                    isCheckMate();
                    updateButtons();
                    return;
            }
            else{
                updateButtons();
            }
        };
    }

    void isCheckMate() {
        updateButtons();
        if(canCheck()){
            check = true;
        }
        System.out.println(check);
        defence = board.defendCheck(this);
    }

    boolean isCheck(){
        if(!board.searchForChecks(this, board.findKing(match.turn)).isEmpty()){
            board.shiftBoard();
            updateButtons();
            int[] save = board.findKing(match.turn);
            buttons[save[0]][save[1]].setBackground(Color.red);
            return true;
        }
        board.shiftBoard();
        return false;
    }

    boolean canCheck(){
        ArrayList<int[]> attack = board.searchForChecks(this, board.findKing(match.turn));
        if(!attack.isEmpty()){
            for(int[]d:attack){
                if(!buttons[d[0]][d[1]].getBackground().equals(Color.orange))
                buttons[d[0]][d[1]].setBackground(Color.cyan);
            }
            return true;
        }
        return false;
    }

    boolean canBeValid(int row,int col){
        Piece copy = board.getButton(row, col).copy();
        board.board[row][col] = null;
        if(canCheck()){
            board.board[row][col] = copy;
            return false;
        }
        board.board[row][col] = copy;
        return true;
    }
    

    public void updateButtonLayout() {
        // Assuming you're using a JPanel to hold buttons
        removeAll(); // Remove existing buttons from the panel
        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons[i].length; j++) {
                add(buttons[i][j]); // Add buttons in the new order
            }
        }
        revalidate(); // Revalidate the panel to update the layout
        repaint();    // Repaint the panel to reflect changes
    }

    public void resetState(){
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board.getButton(i, j) != null) {
                    buttons[i][j].setIcon(board.getButton(i,j).pic);
                } else {
                    buttons[i][j].setIcon(null);
                }
            }
        }
    }

    void updateButtons() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board.getButton(i, j) != null) {
                    buttons[i][j].setIcon(board.getButton(i,j).pic);
                } else {
                    buttons[i][j].setIcon(null);
                }
                buttons[i][j].setBackground((i + j) % 2 == 0 ? new Color(230,230,235) : new Color(44,40,85));
            }
        }
    }
}
