package ChessFinesse;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EmptyStackException;

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
                        board[4][4] = i == 0 ? new King("Black") : new King("White");
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
                        try{
                            if(board[row][col] != null && board[row][col].team.equals(begin.turn)) {
                                updateButtons();
                                thisRow = row;
                                thisCol = col;
                                buttons[row][col].setBackground(Color.CYAN);
                                board[row][col].showPossibleMoves(board, buttons, row, col);
                                while(begin.lastMoves.size()>begin.moves){begin.lastMoves.pop();}
                                begin.lastMoves.push(new int[]{row,col});
                            } 
                            else if((board[row][col] == null || !board[row][col].team.equals(begin.turn)) && board[begin.lastMove()[0]][begin.lastMove()[1]].team.equals(begin.turn) && begin.moves < begin.lastMoves.size() && buttons[row][col].isValidMove()){
                                int[]save = begin.lastMove();
                                board[row][col] = board[save[0]][save[1]];
                                board[save[0]][save[1]] = null;
                                board[row][col].update();
                                shiftBoard();
                                updateButtons();
                                begin.nextTurn();
                            } 
                        }catch(EmptyStackException c){}
                    }
                });
                add(button);     
            }
        }
        updateButtons();
    }

    private void shiftBoard(){
        Piece [][] copy = new Piece[8][8];
        for(int i = 0;i < board.length;i++){
            for(int j = 0;j < board.length;j++){
                copy[i][j] = board[BOARD_SIZE-i-1][BOARD_SIZE-j-1];
            }
        }
        board = copy;
    }

    private void shiftButtons(){
        ButtonFunc [][] copy = new ButtonFunc[8][8];
        for(int i = 0;i < buttons.length;i++){
            for(int j = 0;j < buttons.length;j++){
                copy[i][j] = buttons[BOARD_SIZE-i-1][BOARD_SIZE-j-1];
            }
        }
        buttons = copy;
    }

    private void updateButtons() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] != null) {
                    buttons[i][j].setIcon(board[i][j].pic);
                } else {
                    buttons[i][j].setIcon(null);
                }
                buttons[i][j].setState(i, j);
            }
        }
    }

    private void searchForChecks(){
        shiftBoard();
        for(int i = 0;i<8;i++){
            for(int j = 0;j<8;j++){
                board[i][j].singleCheckCheck(board, buttons,i,j);
            }
        }
        shiftButtons();
    }
}
