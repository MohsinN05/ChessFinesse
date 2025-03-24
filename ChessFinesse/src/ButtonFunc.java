package ChessFinesse.src;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EmptyStackException;

import javax.swing.JButton;

public class ButtonFunc extends JButton {

    
    

    public void setState(int x,int y){
        setBackground((x + y) % 2 == 0 ? new Color(230,230,235) : new Color(44,40,85));
    }

    public ButtonFunc clone() {
        return new ButtonFunc();
    }

    public void resetState(){
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                setBackground((i + j) % 2 == 0 ? new Color(230,230,235) : new Color(44,40,85));
            }
        }
    }

    public boolean isValidMove(ChessBoard cb,int i,int j){
        if(getBackground().equals(Color.CYAN)){
            // cb.begin.oldMoves.push(new Move(cb.board.getButton(cb.begin.lastMoves[0], cb.begin.lastMoves[0]), cb.board.getButton(i, j)));
            return true;
        }
        return false;
    }


}
