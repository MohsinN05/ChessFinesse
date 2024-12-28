package ChessFinesse;
import javax.swing.*;
import java.awt.*;
import java.util.*;


abstract class Piece {
    boolean hasMoved;
    protected final String team;
    protected ImageIcon pic;

    protected Piece(String t, String p){
        team = t;
        pic = resizeIcon(p);
        hasMoved = false;
    }

    public abstract boolean showPossibleMoves(Piece[][]bo,ButtonFunc[][] bu, int x, int y,int[] z);

    // public int[] findKing(Piece[][] bo,String turn){
    //     int i=0,j = 0;
    //     for(i = 0;i<8;i++){
    //         for(j = 0;j<8;j++){
    //             if(bo[i][j].isKing() && !bo[i][j].team.equals(turn)){
    //                 break;
    //             }
    //         }
    //     }
    //     return (new int[]{i,j});
    // }

    public void update(){
        hasMoved = true;
    }

    public boolean isKing(){
        return false;
    }

    public boolean singleCheckCheck(Piece[][]bo,ButtonFunc[][] bu, int x, int y,int[] z){
        return showPossibleMoves(bo, bu, x, y,z);
    }

    protected ImageIcon resizeIcon(String path){
        ImageIcon icon = new ImageIcon(path);
        Image img = icon.getImage().getScaledInstance(55, 55, Image.SCALE_SMOOTH); 
        return new ImageIcon(img);
    }
}

class King extends Piece{
    
    public King(String t){
        super(t, decideTeam(t));   
    }

    public Stack<int[]> searchForChecks(Piece[][]board,ButtonFunc[][] buttons,String turn,int[]king){
        Stack<int[]> checking = new Stack<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if(board[i][j]!=null && !board[i][j].team.equals(turn) && board[i][j].singleCheckCheck(board, buttons,i,j,king)){
                    checking.push(new int[]{i,j});
                }
            }
        }
        return checking;
    }

    public int[] findKing(Piece[][]board,String turn){
        int i=0,j = 0;
        for(i = 0;i<8;i++){
            for(j = 0;j<8;j++){
                if(board[i][j]!= null && board[i][j] instanceof King && !board[i][j].team.equals(turn)){
                    return (new int[]{i,j});
                }
            }
        }
        return (new int[]{-1,-1});
    }
    
    private void onlyKing(Piece[][]bo,ButtonFunc[][] bu){
        for(int i =0;i<8;i++){
            for(int j=0;j<8;j++){
                if(bu[i][j].getBackground().equals(Color.CYAN) && bo[i][j]!=this){
                    bu[i][j].setState(i, j);
                }
                else if(bu[i][j].getBackground().equals(Color.black)){
                    bu[i][j].setBackground(Color.CYAN);
                }
            }
        }
    }
    public boolean showPossibleMoves(Piece[][]bo,ButtonFunc[][] bu, int x, int y,int[] z){
        searchForChecks(bo, bu, team, z);
        for(int i = 1;Math.abs(i)<2;i--){
            for(int j = 1;Math.abs(j)<2;j--){
                try{
                    if((!bu[x+i][y+j].getBackground().equals(Color.cyan)) && (bo[x+i][y+j]==null ||!bo[x+i][y+j].team.equals(this.team)) && (Math.abs(i)<2)&&(Math.abs(j)<2)){
                        bu[x+i][y+j].setBackground(Color.black);
                    }
                }catch(Exception e){}      
            }
        }
        onlyKing(bo,bu);
        return false; 
    }

    public boolean isKing(){
        return true;
    }


    public void castling(){}

    private static String decideTeam(String team){
        if(team.equals("White"))
        return "ChessFinesse//1x//w_king_1x.png";
        return "ChessFinesse//1x//b_king_1x.png";
    }
}

class Pawn extends Piece{

    public Pawn(String t){
        super(t, decideTeam(t));   
    }
      
    private static String decideTeam(String team){
        if(team.equals("White"))
        return "ChessFinesse//1x//w_pawn_1x.png";
        return "ChessFinesse//1x//b_pawn_1x.png";
    }

    public boolean showPossibleMoves(Piece[][]bo,ButtonFunc[][] bu, int x, int y,int[] z){
        boolean isCheck = false;
        int i = 1;
        while((i<=1+(hasMoved?0:1))&&(x>=0 && x < 8)&&(y>=0 && y < 8)){
            if(bo[x-i][y]==null){
                bu[x-i][y].setBackground(Color.CYAN);
            }
            else{
                break;
            }
            i++;
        }
        try{
            if(bo[x-1][y+1]!= null && !bo[x-1][y+1].team.equals(this.team)){
                bu[x-1][y+1].setBackground(Color.CYAN);
                if((x-1)==z[0] && (y+1)==z[1])isCheck = true;
            }
        }catch(ArrayIndexOutOfBoundsException e){}
        try{
            if(bo[x-1][y-1]!= null && !bo[x-1][y-1].team.equals(this.team)){
                bu[x-1][y-1].setBackground(Color.CYAN);
                if((x-1)==z[0] && (y-1)==z[1])isCheck = true;
            }
        }catch(ArrayIndexOutOfBoundsException e){}
        return isCheck;
    }
}

class Knight extends Pawn{
    public Knight(String t){
        super(t);
        pic = resizeIcon(decideTeam(t));   
    }
        
    private static String decideTeam(String team){
        if(team.equals("White"))
        return "ChessFinesse//1x//w_knight_1x.png";
        return "ChessFinesse//1x//b_knight_1x.png";
    }

    public boolean showPossibleMoves(Piece[][]bo,ButtonFunc[][] bu, int x, int y,int[] z){
        boolean isCheck = false;
        int a = 2,b = 1;
        try{
            if(bo[x+a][y+b]==null ||  !bo[x+a][y+b].team.equals(this.team)){
                bu[x+a][y+b].setBackground(Color.CYAN);
                if((x+a)==z[0] && (y+b)==z[1])isCheck = true;
            }
        }catch(ArrayIndexOutOfBoundsException e){}
        try{
            if(bo[x-a][y-b]==null ||  !bo[x-a][y-b].team.equals(this.team)){
                bu[x-a][y-b].setBackground(Color.CYAN);
                if((x-a)==z[0] && (y-b)==z[1])isCheck = true;
            }
        }catch(ArrayIndexOutOfBoundsException e){}
        try{
            if(bo[x-b][y-a]==null ||  !bo[x-b][y-a].team.equals(this.team)){
                bu[x-b][y-a].setBackground(Color.CYAN);
                if((x-b)==z[0] && (y-a)==z[1])isCheck = true;
            }
        }catch(ArrayIndexOutOfBoundsException e){}
        try{
            if(bo[x+b][y+a]==null ||  !bo[x+b][y+a].team.equals(this.team)){
                bu[x+b][y+a].setBackground(Color.CYAN);
                if((x+b)==z[0] && (y+a)==z[1])isCheck = true;
            }
        }catch(ArrayIndexOutOfBoundsException e){}
        try{
            if(bo[x+a][y-b]==null ||  !bo[x+a][y-b].team.equals(this.team)){
                bu[x+a][y-b].setBackground(Color.CYAN);
                if((x+a)==z[0] && (y-b)==z[1])isCheck = true;
            }
        }catch(ArrayIndexOutOfBoundsException e){}
        try{
            if(bo[x-a][y+b]==null ||  !bo[x-a][y+b].team.equals(this.team)){
                bu[x-a][y+b].setBackground(Color.CYAN);
                if((x-a)==z[0] && (y+b)==z[1])isCheck = true;
            }
        }catch(ArrayIndexOutOfBoundsException e){}
        try{
            if(bo[x+b][y-a]==null ||  !bo[x+b][y-a].team.equals(this.team)){
                bu[x+b][y-a].setBackground(Color.CYAN);
                if((x+b)==z[0] && (y-a)==z[1])isCheck = true;
            }
        }catch(ArrayIndexOutOfBoundsException e){}
        try{
            if(bo[x-b][y+a]==null ||  !bo[x-b][y+a].team.equals(this.team)){
                bu[x-b][y+a].setBackground(Color.CYAN);
                if((x-b)==z[0] && (y+a)==z[1])isCheck = true;
            }
        }catch(ArrayIndexOutOfBoundsException e){}
        return isCheck;
    }

}

class Queen extends Pawn{
    public Queen(String t){
        super(t);
        pic = resizeIcon(decideTeam(t));   
    }
        
    private static String decideTeam(String team){
        if(team.equals("White"))
        return "ChessFinesse//1x//w_queen_1x.png";
        return "ChessFinesse//1x//b_queen_1x.png";
    }

    public boolean showPossibleMoves(Piece[][]bo,ButtonFunc[][] bu, int x, int y,int[] z){
        boolean isCheck = false;
        boolean vUp=true,vDown=true,hLeft=true,hRight = true;
        boolean leftUp=true,leftDown=true,rightUp=true,rightDown = true;
        for(int i = 1;i<8;i++){
            try{
                if((bo[x][y+i]==null ||  !bo[x][y+i].team.equals(this.team))&&(y+i < 8)&&hRight){
                    if(bo[x][y+i]!=null)hRight=false;
                    bu[x][y+i].setBackground(Color.CYAN);
                    if((x)==z[0] && (y+i)==z[1])isCheck = true;
                }else{hRight=false;}
            }catch(ArrayIndexOutOfBoundsException e){}
            try{
                if((bo[x][y-i]==null || !bo[x][y-i].team.equals(this.team))&&(y-i >= 0)&&hLeft){
                    if(bo[x][y-i]!=null)hLeft=false;
                    bu[x][y-i].setBackground(Color.CYAN);
                    if((x)==z[0] && (y-i)==z[1])isCheck = true;
                }else{hLeft=false;}
            }catch(ArrayIndexOutOfBoundsException e){}
            try{
                if((bo[x+i][y]==null || !bo[x+i][y].team.equals(this.team))&&(x+i<8)&&vUp){
                    if(bo[x+i][y]!=null)vUp=false;
                    bu[x+i][y].setBackground(Color.CYAN);
                    if((x+i)==z[0] && (y)==z[1])isCheck = true;
                }else{vUp=false;}
            }catch(ArrayIndexOutOfBoundsException e){}
            try{
                if((bo[x-i][y]==null || !bo[x-i][y].team.equals(this.team))&&(x - i >= 0)&&vDown){
                    if(bo[x-i][y]!=null)vDown=false;
                    bu[x-i][y].setBackground(Color.CYAN);
                    if((x-i)==z[0] && (y)==z[1])isCheck = true;
                }else{vDown=false;}
            }catch(ArrayIndexOutOfBoundsException e){}
            try{
                if((bo[x+i][y+i]==null ||  !bo[x+i][y+i].team.equals(this.team))&&(x+i < 8&&y+i < 8)&&rightUp){
                    if(bo[x+i][y+i]!=null)rightUp=false;
                    bu[x+i][y+i].setBackground(Color.CYAN);
                    if((x+i)==z[0] && (y+i)==z[1])isCheck = true;
                }else{rightUp=false;}
            }catch(ArrayIndexOutOfBoundsException e){}
            try{
                if((bo[x+i][y-i]==null || !bo[x+i][y-i].team.equals(this.team))&&(y-i >= 0&&x+i<8)&&leftDown){
                    if(bo[x+i][y-i]!=null)leftDown=false;
                    bu[x+i][y-i].setBackground(Color.CYAN);
                    if((x+i)==z[0] && (y-i)==z[1])isCheck = true;
                }else{leftDown=false;}
            }catch(ArrayIndexOutOfBoundsException e){}
            try{
                if((bo[x-i][y+i]==null || !bo[x-i][y+i].team.equals(this.team))&&(y+i<8&&x-i>=0)&&leftUp){
                    if(bo[x-i][y+i]!=null)leftUp=false;
                    bu[x-i][y+i].setBackground(Color.CYAN);
                    if((x-i)==z[0] && (y+i)==z[1])isCheck = true;
                }else{leftUp=false;}
            }catch(ArrayIndexOutOfBoundsException e){}
            try{
                if((bo[x-i][y-i]==null || !bo[x-i][y-i].team.equals(this.team))&&(x - i >= 0&&y-i>=0)&&rightDown){
                    if(bo[x-i][y-i]!=null)rightDown=false;
                    bu[x-i][y-i].setBackground(Color.CYAN);
                    if((x-i)==z[0] && (y-i)==z[1])isCheck = true;
                }else{rightDown=false;}
            }catch(ArrayIndexOutOfBoundsException e){}
        }  
        return isCheck;
    }
}

class Rook extends Pawn{
    public Rook(String t){
        super(t);
        pic = resizeIcon(decideTeam(t));   
    }
        
    private static String decideTeam(String team){
        if(team.equals("White"))
        return "ChessFinesse//1x//w_rook_1x.png";
        return "ChessFinesse//1x//b_rook_1x.png";
    }

    public boolean showPossibleMoves(Piece[][]bo,ButtonFunc[][] bu, int x, int y,int[] z){
        boolean isCheck = false;
        boolean vUp=true,vDown=true,hLeft=true,hRight = true;
        for(int i = 1;i<8;i++){
            try{
                if((bo[x][y+i]==null ||  !bo[x][y+i].team.equals(this.team))&&(y+i < 8)&&hRight){
                    if(bo[x][y+i]!=null)hRight=false;
                    bu[x][y+i].setBackground(Color.CYAN);
                    if((x)==z[0] && (y+i)==z[1])isCheck = true;
                }else{hRight=false;}
            }catch(ArrayIndexOutOfBoundsException e){}
            try{
                if((bo[x][y-i]==null || !bo[x][y-i].team.equals(this.team))&&(y-i >= 0)&&hLeft){
                    if(bo[x][y-i]!=null)hLeft=false;
                    bu[x][y-i].setBackground(Color.CYAN);
                    if((x)==z[0] && (y-i)==z[1])isCheck = true;
                }else{hLeft=false;}
            }catch(ArrayIndexOutOfBoundsException e){}
            try{
                if((bo[x+i][y]==null || !bo[x+i][y].team.equals(this.team))&&(x+i<8)&&vUp){
                    if(bo[x+i][y]!=null)vUp=false;
                    bu[x+i][y].setBackground(Color.CYAN);
                    if((x+i)==z[0] && (y)==z[1])isCheck = true;
                }else{vUp=false;}
            }catch(ArrayIndexOutOfBoundsException e){}
            try{
                if((bo[x-i][y]==null || !bo[x-i][y].team.equals(this.team))&&(x - i >= 0)&&vDown){
                    if(bo[x-i][y]!=null)vDown=false;
                    bu[x-i][y].setBackground(Color.CYAN);
                    if((x-i)==z[0] && (y)==z[1])isCheck = true;
                }else{vDown=false;}
            }catch(ArrayIndexOutOfBoundsException e){}
        }
        return isCheck;
    }
}

class Bishop extends Pawn{
    public Bishop(String t){
        super(t);
        pic = resizeIcon(decideTeam(t));   
    }
        
    private static String decideTeam(String team){
        if(team.equals("White"))
        return "ChessFinesse//1x//w_bishop_1x.png";
        return "ChessFinesse//1x//b_bishop_1x.png";
    }

    public boolean showPossibleMoves(Piece[][]bo,ButtonFunc[][] bu, int x, int y,int[] z){
        boolean isCheck = false;
        boolean leftUp=true,leftDown=true,rightUp=true,rightDown = true;
        for(int i = 1;i<8;i++){
            try{
                if((bo[x+i][y+i]==null ||  !bo[x+i][y+i].team.equals(this.team))&&(x+i < 8&&y+i < 8)&&rightUp){
                    if(bo[x+i][y+i]!=null)rightUp=false;
                    bu[x+i][y+i].setBackground(Color.CYAN);
                    if((x+i)==z[0] && (y+i)==z[1])isCheck = true;
                }else{rightUp=false;}
            }catch(ArrayIndexOutOfBoundsException e){}
            try{
                if((bo[x+i][y-i]==null || !bo[x+i][y-i].team.equals(this.team))&&(y-i >= 0&&x+i<8)&&leftDown){
                    if(bo[x+i][y-i]!=null)leftDown=false;
                    bu[x+i][y-i].setBackground(Color.CYAN);
                    if((x+i)==z[0] && (y-i)==z[1])isCheck = true;
                }else{leftDown=false;}
            }catch(ArrayIndexOutOfBoundsException e){}
            try{
                if((bo[x-i][y+i]==null || !bo[x-i][y+i].team.equals(this.team))&&(y+i<8&&x-i>=0)&&leftUp){
                    if(bo[x-i][y+i]!=null)leftUp=false;
                    bu[x-i][y+i].setBackground(Color.CYAN);
                    if((x-i)==z[0] && (y+i)==z[1])isCheck = true;
                }else{leftUp=false;}
            }catch(ArrayIndexOutOfBoundsException e){}
            try{
                if((bo[x-i][y-i]==null || !bo[x-i][y-i].team.equals(this.team))&&(x - i >= 0&&y-i>=0)&&rightDown){
                    if(bo[x-i][y-i]!=null)rightDown=false;
                    bu[x-i][y-i].setBackground(Color.CYAN);
                    if((x-i)==z[0] && (y-i)==z[1])isCheck = true;
                }else{rightDown=false;}
            }catch(ArrayIndexOutOfBoundsException e){}
        }
        return isCheck;
    }
}


