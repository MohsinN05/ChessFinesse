package ChessFinesse;
import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameView extends JFrame{
    public GameView(){


        this.setTitle("Chess Finesse");
        this.setSize(700, 700);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(true);
        this.setLocationRelativeTo(null);
        this.add(new ChessBoard(),BorderLayout.CENTER);
        this.add(new JPanel(),BorderLayout.EAST);
        this.add(new JPanel(),BorderLayout.WEST);
        this.setVisible(true);

    }

    
    
    

    public static void main(String[] args) {
        new GameView();
    }
}