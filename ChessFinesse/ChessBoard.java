package ChessFinesse;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;



public class ChessBoard extends JPanel {

    private static final int BOARD_SIZE = 8;
    private JButton[][] buttons;
    private Piece[][] board;
    private Match begin;

    public ChessBoard() {
        
        buttons = new JButton[BOARD_SIZE][BOARD_SIZE];
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
                        board[5][4] = i == 0 ? new Rook("White") : new Rook("Black");
                    }
                    else if(j == 1 || j == 6){
                        board[3][3] = i == 0 ? new Knight("Black") : new Knight("White");
                    }
                    else if(j == 2 || j == 5){
                        board[5][3] = i == 0 ? new Bishop("Black") : new Bishop("White");
                    }
                    else if(j == 3){
                        board[4][4] = i == 0 ? new Queen("Black") : new Queen("White");
                    }
                    else if(j == 4){
                        board[2][2] = i == 0 ? new King("Black") : new King("White");
                    }
                }
                if(i == 1 || i == 6){
                    board[i][j] = i == 1 ? new Pawn("Black") : new Pawn("White");
                }
            }
        }
    }

    private void initializeButtons() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                JButton button = new JButton();
                button.setBackground((i + j) % 2 == 0 ? new Color(230,230,235) : new Color(44,40,85));
                button.setFocusPainted(true);

                int row = i, col = j;
                buttons[i][j] = button;
                button.addActionListener(new ActionListener() {
                    private boolean selected = false;
                    private int fromRow, fromCol;
                    
                    
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (!selected && board[row][col] != null && board[row][col].team.equals(begin.turn)) {
                            selected = true;
                            fromRow = row;
                            fromCol = col;
                            buttons[row][col].setBackground(Color.CYAN);
                            board[row][col].showPossibleMoves(board, buttons, row, col);
                        } else if (selected) {
                            shiftBoard();
                            updateButtons();
                            begin.nextTurn();
                            selected = false;
                        }
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

}
