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
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;



public class ChessBoard extends JPanel {
    GameView safe;

    private static final int BOARD_SIZE = 8;
    ButtonFunc[][] buttons;
    Pieces board;
    ArrayList<int[]> attack;

    public ChessBoard(GameView s){
        safe = s;
        buttons = new ButtonFunc[BOARD_SIZE][BOARD_SIZE];
        board = new Pieces();
        this.setLayout(new GridLayout(BOARD_SIZE,BOARD_SIZE));
        this.setPreferredSize(new Dimension(700, 700));
        setMaximumSize(new Dimension(700,700));
        setMinimumSize(new Dimension(700,700));
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



    private void showPossibleMoves(int i,int j){
        onlyMove(board.movemap, i, j);
        buttons[i][j].setBackground(Color.lightGray);
    }


    public  void onlyMove(HashMap<Integer[],LinkedList<Integer[]>> moves, int row, int col){
        for (Integer[] key : moves.keySet()) {
            if (key[0] == row && key[1] == col) {
                for(Integer[] move : moves.get(key)) {
                        buttons[move[0]][move[1]].setBackground(Color.cyan);
                }
            }
        }
    }

    public void highlightCheck(){
        if(board.match.check){
            for(Integer[] key: board.movemap.keySet()){
                if(board.board[key[0]][key[1]] instanceof King)
                buttons[key[0]][key[1]].setBackground(Color.red);
            }
        }
    }




    private ActionListener customAction(int row,int col){
        return e -> {
            if(board.valid(row, col)){
                updateButtons();
                highlightCheck();
                showPossibleMoves(row, col);
                return;
            }
            else if(board.validChoice(row, col)){
                int[]save = board.match.lastMoves;
                move(save, row, col);
                if(!board.isCheckMate())
                board.match.lastMoves = null;
                updateButtons();
                highlightCheck();
                    return;
            }
            else{
                updateButtons();
                highlightCheck();
            }
        };
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
