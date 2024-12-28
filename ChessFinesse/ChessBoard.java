package ChessFinesse;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EmptyStackException;
import java.util.Stack;

import javax.swing.JButton;
import javax.swing.JPanel;



public class ChessBoard extends JPanel {

    private static final int BOARD_SIZE = 8;
    ButtonFunc[][] buttons;
    Piece[][] board;
    Match begin;

    public ChessBoard() {
        
        buttons = new ButtonFunc[BOARD_SIZE][BOARD_SIZE];
        board = new Piece[BOARD_SIZE][BOARD_SIZE];
        begin = new Match();
        this.setLayout(new GridLayout(8,8));
        this.setPreferredSize(new Dimension(700, 700));
        setMaximumSize(new Dimension(700,700));
        setMinimumSize(new Dimension(700,700));
        initializeBoard();
        initializeButtons();
        System.out.println("done");
    }

    public Dimension getPreferredSize(){
        return new Dimension(700,700);
    }
    private void initializeBoard() {
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
                        board[3][4] = i == 0 ? new King("Black") : new King("White");
                    }
                }
                if(i == 1 || i == 6){
                    board[i][j] = i == 1 ? new Pawn("Black") : new Pawn("White");
                }
            }
        }
    }

    public void resetButton(int x,int y){
        buttons[x][y].setBackground((x + y) % 2 == 0 ? new Color(230,230,235) : new Color(44,40,85));
    }

    

    private void initializeButtons() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                ButtonFunc button = new ButtonFunc();
                button.setState(i, j);
                button.setFocusPainted(true);
                int row = i, col = j;
                buttons[i][j] = button;
                // resetButtons();
                button.addActionListener(new ActionListener() {
                    //private boolean selected = false;
                    private int thisRow, thisCol;
                    
                    
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if(board[row][col] != null && board[row][col].team.equals(begin.turn)) {
                                updateButtons();
                                thisRow = row;
                                thisCol = col;
                                buttons[row][col].setBackground(Color.CYAN);
                                shiftBoard();
                                shiftButtons();
                                board[row][col].showPossibleMoves(board, buttons, row, col,new int[]{row,col});
                                while(begin.lastMoves.size()>begin.moves){begin.lastMoves.pop();}
                                begin.lastMoves.push(new int[]{row,col});
                                return;
                            }          
                            else if((board[row][col] == null || !board[row][col].team.equals(begin.turn)) && (begin.lastMoves.empty() || board[begin.lastMove()[0]][begin.lastMove()[1]].team.equals(begin.turn)) && begin.moves < begin.lastMoves.size() && buttons[row][col].isValidMove()){
                                    int[]save = begin.lastMove();
                                    board[row][col] = board[save[0]][save[1]];
                                    board[save[0]][save[1]] = null;
                                    board[row][col].update();
                                    searchForChecks(begin.turn,findKing(begin.turn));
                                    shiftButtons();
                                    shiftBoard();
                                    begin.nextTurn();
                                    return;
                            }
                            updateButtons();
                            
                        
                    }
                });
                add(button);     
            }
        }
        updateButtons();
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

    private void shiftButtons(){
        ButtonFunc [][] copy2 = new ButtonFunc[8][8];
        for(int i = 0;i < buttons.length;i++){
            for(int j = 0;j < buttons.length;j++){
                copy2[i][j] = buttons[BOARD_SIZE-i-1][BOARD_SIZE-j-1];
            }
        }
        buttons = copy2;
    }

    private void updateButtons() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] != null) {
                    buttons[i][j].setIcon(board[i][j].pic);
                } else {
                    buttons[i][j].setIcon(null);
                }
                buttons[i][j].setBackground((i + j) % 2 == 0 ? new Color(230,230,235) : new Color(44,40,85));
            }
        }
    }

    

    
    public Stack<int[]> searchForChecks(String turn,int[]king){
        Stack<int[]> checking = new Stack<>();
        king = findKing(turn);
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if(board[i][j]!=null && board[i][j].team.equals(turn) && board[i][j].singleCheckCheck(board, buttons,i,j,king)){
                    checking.push(new int[]{i,j});
                }
            }
        }
        return checking;
    }

    public int[] findKing(String turn){
        int i=0,j = 0;
        for(i = 0;i<8;i++){
            for(j = 0;j<8;j++){
                if(board[i][j]!= null && board[i][j] instanceof King && !board[i][j].team.equals(turn)){
                    return (new int[]{i,j});
                }
            }
        }
        return (new int[]{-1,-1});
    }
        // if(board[i][j]!=null && board[i][j].team.equals(turn)){
        //     board[i][j].singleCheckCheck(board, buttons,i,j);
        // }
        // for(int x = 0;x<8;x++){
        //     for(int y =0;y<8;y++){
        //         if(buttons[x][y].getBackground().equals(Color.BLACK))
        //         buttons[x][y].setBackground(Color.cyan);
        //         else
        //         buttons[x][y].setState(x, y);
        //     }
        // }
        // shiftButtons();
}
