package ChessFinesse.src;

import java.awt.Image;

import javax.swing.ImageIcon;

public interface PictureHandler {

    static ImageIcon resizeIcon(String path){
        ImageIcon icon = new ImageIcon(path);
        Image img = icon.getImage().getScaledInstance(55, 55, Image.SCALE_SMOOTH); 
        return new ImageIcon(img);
    }

}
