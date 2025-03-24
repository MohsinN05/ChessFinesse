package ChessFinesse.src;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;

public class Promotion implements ActionListener {
    JDialog promo;
    JButton[] jo = new JButton[4];

    public Promotion(ChessBoard cb,String team,Pieces pi,int x,int y){

    
        promo = new JDialog(cb.safe.game,"Pawn Promotion",true);
        promo.setLocationRelativeTo(null);
        promo.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        promo.setLayout(new GridLayout(2, 2));
        promo.setSize(400, 400);
        initializeButtons(team,pi,x,y);
        promo.setVisible(true);
        
    }

    public void setImage1(String team){
        jo[0].setIcon(resizeIcon(new Queen(team).decideTeam(team)));
    }
    public void setImage2(String team){
        jo[1].setIcon(resizeIcon(new Knight(team).decideTeam(team)));
    }
    public void setImage3(String team){
        jo[2].setIcon(resizeIcon(new Bishop(team).decideTeam(team)));
    }
    public void setImage4(String team){
        jo[3].setIcon(resizeIcon(new Rook(team).decideTeam(team)));
    }


    protected ImageIcon resizeIcon(String path){
        ImageIcon icon = new ImageIcon(path);
        Image img = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH); 
        return new ImageIcon(img);
    }

    private void toQueen(String team,Pieces pi,int x,int y){
        pi.board[x][y] = new Queen(team);
    }

    private void toKnight(String team,Pieces pi,int x,int y){
        pi.board[x][y] = new Knight(team);
    }

    private void toBishop(String team,Pieces pi,int x,int y){
        pi.board[x][y] = new Bishop(team);
    }

    private void toRook(String team,Pieces pi,int x,int y){
        pi.board[x][y] = new Rook(team);
    }
    
    private void initializeButtons(String team,Pieces pi,int x,int y) {
        for (int i = 0; i < 4; i++) {
                JButton button = new JButton();
                button.setBackground(Color.DARK_GRAY);
                //button.set(resizeIcon());
                int row = i;
                jo[i] = button;
                button.addActionListener(new ActionListener() {
                    //private boolean selected = false;
                    private int thisRow, thisCol;
                    
                    
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if(row==0){toQueen(team, pi, x, y);}
                        if(row==1){toKnight(team, pi, x, y);}
                        if(row==2){toBishop(team, pi, x, y);}
                        if(row==3){toRook(team, pi, x, y);}
                        promo.dispose();
                    }
                }
                );
                promo.add(button);     
            }
            setImage1(team);
            setImage2(team);
            setImage3(team);
            setImage4(team);
        }
        //updateButtons();

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'actionPerformed'");
    }

}
