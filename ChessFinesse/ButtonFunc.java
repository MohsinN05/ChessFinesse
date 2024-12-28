package ChessFinesse;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EmptyStackException;

import javax.swing.JButton;

public class ButtonFunc extends JButton {

    
    

    public void setState(int x,int y){
        setBackground((x + y) % 2 == 0 ? new Color(230,230,235) : new Color(44,40,85));
    }

    public void resetState(){
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                setBackground((i + j) % 2 == 0 ? new Color(230,230,235) : new Color(44,40,85));
            }
        }
    }

    public boolean isValidMove(){
        if(getBackground().equals(Color.CYAN))
        return true;
        return false;
    }


}
