package ChessFinesse.src;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Consumer;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;

public class Promotion extends JDialog implements PictureHandler{
    JButton[] jo = new JButton[4];

    public Promotion(Window owner,String team, Consumer<Integer> record){
        super(owner);
        setTitle("Pawn Promotion");
        setModal(true);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLayout(new GridLayout(1, 4));
        setSize(500, 150);
        initializeButtons(team,record);
        setVisible(true);
    }

    public void setImage1(String team){
        jo[0].setIcon(PictureHandler.resizeIcon(Queen.decideTeam(team)));
    }
    public void setImage2(String team){
        jo[1].setIcon(PictureHandler.resizeIcon(Knight.decideTeam(team)));
    }
    public void setImage3(String team){
        jo[2].setIcon(PictureHandler.resizeIcon(Bishop.decideTeam(team)));
    }
    public void setImage4(String team){
        jo[3].setIcon(PictureHandler.resizeIcon(Rook.decideTeam(team)));
    }
    
    private void initializeButtons(String team, Consumer<Integer> record) {
        for (int i = 0; i < 4; i++) {
                final int index = i;
                jo[i] = new JButton();
                jo[i].setBackground(Color.red);
                jo[i].setFocusable(false);
                jo[i].addActionListener(new ActionListener() {      
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        record.accept(index);
                        dispose();
                    }
                }
                );
                add(jo[i]);     
            }
            setImage1(team);
            setImage2(team);
            setImage3(team);
            setImage4(team);
        }

}
