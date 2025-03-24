package ChessFinesse.src;
import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameView{
    public Promotion promo;
    public JDialog game;
    public GameView(JFrame gi){
        game = new JDialog(gi,"ChessBoard",true);
        ChessBoard chessBoard = new ChessBoard(this);
        JPanel leftPane = new JPanel();
        JPanel rightPane = new JPanel();
        game.setTitle("ChessBoard");
        game.setSize(700, 700);
        game.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        game.setResizable(true);
        game.setLocationRelativeTo(null);
        game.add(chessBoard,BorderLayout.CENTER);
        game.add(leftPane,BorderLayout.EAST);
        game.add(rightPane,BorderLayout.WEST);
        game.setVisible(true);

    }

    public static void main(String[] args) {
        new GameView(null);
    }

    

    
    
    

}