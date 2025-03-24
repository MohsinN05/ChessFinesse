package ChessFinesse.src;
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

    protected Piece(String t,ImageIcon p,boolean M){
        team = t;
        pic = p;
        hasMoved = M;
    }

    public abstract Piece copy();

    public abstract boolean showPossibleMoves(Pieces bo,ChessBoard cb, int x, int y,int[] z);

    public abstract ArrayList<Integer[]> savingMoves(Pieces bo,ChessBoard cb, int x, int y,int[] z);

    public void promote(ChessBoard cb,Pieces pi,int x,int y){}

    public abstract int onlyMove(ChessBoard cb);


    public void update(){
        hasMoved = true;
    }

    public boolean isKing(){
        return false;
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

    public Piece copy(){
        return null;
    }

    public void promote(ChessBoard cb,Pieces pi,int x,int y){}
    
    public boolean showPossibleMoves(Pieces bo,ChessBoard cb, int x, int y,int[] z){
        if(team.equals(cb.match.turn)){
            bo.shiftBoard();
            z = bo.findKing(team);
            ArrayList save = bo.searchForChecks(cb, z);
            cb.shiftButtons();
            bo.shiftBoard();
            z = bo.findKing(team);
            bo.hereCheck(z, bo.coordinateOfChecking(save), cb);
            cb.resetState();
        }
        else{
            for(int i = 1;Math.abs(i)<2;i--){
                for(int j = 1;Math.abs(j)<2;j--){
                    if(i == 0 && j == 0)continue;
                    try{
                        if((bo.board[x+i][y+j]==null || !bo.board[x+i][y+j].team.equals(this.team))){
                            cb.buttons[x+i][y+j].setBackground(Color.red);
                        }
                    }catch(Exception e){}      
                }
            }
        }
        return false; 
    }

    public ArrayList<Integer[]> savingMoves(Pieces bo,ChessBoard cb, int x, int y,int[] z){
        ArrayList<Integer[]> canSave = new ArrayList<>();
        for(int i = 1;Math.abs(i)<2;i--){
            for(int j = 1;Math.abs(j)<2;j--){
                if(i == 0 && j == 0)continue;
                try{
                    if(!cb.buttons[x+i][y+j].getBackground().equals(Color.orange) && !cb.buttons[x+i][y+j].getBackground().equals(Color.red) && ((bo.getButton(x+i, y+j) == null && !cb.buttons[x+i][y+j].getBackground().equals(Color.cyan)) || (cb.buttons[x+i][y+j].getBackground().equals(Color.cyan) && !bo.getButton(x+i, y+j).team.equals(this.team))))
                    canSave.add(new Integer[]{x+i,y+j});
                }catch(Exception e){}      
            }
        }
        return canSave;
    }

    public int onlyMove(ChessBoard cb){
        int canDo = 0;
        for(int i =0;i<8;i++){
            for(int j=0;j<8;j++){
                if(!cb.buttons[i][j].getBackground().equals(Color.black)){
                    cb.buttons[i][j].setState(i, j);
                }
                else if(cb.buttons[i][j].getBackground().equals(Color.black)){
                    canDo++;
                    cb.buttons[i][j].setBackground(Color.CYAN);
                }
            }
        }
        return canDo;
    }

    public boolean isKing(){
        return true;
    }


    public void castling(){}

     static String decideTeam(String team){
        if(team.equals("White"))
        return "ChessFinesse//Pics&Vids//Pics//w_king_1x.png";
        return "ChessFinesse//Pics&Vids//Pics//b_king_1x.png";
    }
}

class Pawn extends Piece{

    public Pawn(String t){
        super(t, decideTeam(t));   
    }

    public Pawn(String t,ImageIcon p,boolean M){
        super(t, p,M);
    }


    public Piece copy(){
        return new Pawn(this.team,this.pic,this.hasMoved);
    }
    
      
     static String decideTeam(String team){
        if(team.equals("White"))
        return "ChessFinesse//Pics&Vids//Pics//w_pawn_1x.png";
        return "ChessFinesse//Pics&Vids//Pics//b_pawn_1x.png";
    }

    public boolean showPossibleMoves(Pieces bo,ChessBoard cb, int x, int y,int[] z){
        boolean isCheck = false;
        int i = 1;
        while((i<=1+(hasMoved?0:1))&&(x-i>=0)){
            if(bo.board[x-i][y]==null){
                if((!cb.buttons[x-i][y].getBackground().equals(Color.red) && !cb.buttons[x-i][y].getBackground().equals(Color.cyan))){
                    // if((cb.buttons[x-i][y].getBackground().equals(Color.cyan))){
                    //     isCheck = true;
                    //     cb.buttons[x-i][y].setBackground(Color.green);
                    // }
                    // else
                    cb.buttons[x-i][y].setBackground(Color.gray);
                }
            }
            else{
                break;
            }
            i++;
        }
        try{
            if(x-1 >= 0 && y+1<8){
                try{
                    if(!bo.board[x-1][y+1].team.equals(this.team)){
                        if((x-1)==z[0] && (y+1)==z[1]){
                            cb.buttons[x-1][y+1].setBackground(Color.cyan);
                            isCheck = true;
                        }
                        else
                        cb.buttons[x-1][y+1].setBackground(Color.green);
                    }
                    else
                    cb.buttons[x-1][y+1].setBackground(Color.orange);
                }catch(Exception e){
                    if(!cb.buttons[x-1][y+1].getBackground().equals(Color.cyan))
                    cb.buttons[x-1][y+1].setBackground(Color.red);}
            }
        }catch(ArrayIndexOutOfBoundsException e){}
        try{
            if(x-1 >= 0 && y-1>=0){
                try{
                    if(!bo.board[x-1][y-1].team.equals(this.team)){
                        if((x-1)==z[0] && (y-1)==z[1]){
                            cb.buttons[x-1][y-1].setBackground(Color.cyan);
                            isCheck = true;
                        }
                        else
                        cb.buttons[x-1][y-1].setBackground(Color.green);
                    }
                    else
                    cb.buttons[x-1][y-1].setBackground(Color.orange);
                }catch(Exception e){
                    if(!cb.buttons[x-1][y-1].getBackground().equals(Color.cyan))
                    cb.buttons[x-1][y-1].setBackground(Color.red);
                }
            }
        }catch(ArrayIndexOutOfBoundsException e){}
        return isCheck;
    }

    public ArrayList<Integer[]> savingMoves(Pieces bo,ChessBoard cb, int x, int y,int[] z){
        ArrayList<Integer[]> saving = new ArrayList<>();
        int i = 1;
        while((i<=1+(hasMoved?0:1))&&(x-i>=0)){
            if(bo.board[x-i][y]==null){
                if((cb.buttons[x-i][y].getBackground().equals(Color.cyan))){
                    saving.add(new Integer[]{x-i,y});
                }
            }
            else{
                break;
            }
            i++;
        }
        try{
            if(x-1 >= 0 && y+1<8 && (cb.buttons[x-1][y+1].getBackground().equals(Color.cyan)) && !bo.board[x-1][y+1].team.equals(this.team)){
                saving.add(new Integer[]{x-1,y+1});
            }
        }catch(Exception e){}
        try{
            if(x-1 >= 0 && y-1>=0 &&(cb.buttons[x-1][y-1].getBackground().equals(Color.cyan)) && !bo.board[x-1][y-1].team.equals(this.team)){
                saving.add(new Integer[]{x-1,y-1});
            }
        }catch(Exception e){}
        return saving;
    }

    public void promote(ChessBoard cb,Pieces pi,int x,int y){
        pawnPromotion(cb,pi,x,y);
    }
    private void pawnPromotion(ChessBoard cb,Pieces pi,int x,int y){
        cb.safe.promo = new Promotion(cb,team,pi,x,y);
    }

    public int onlyMove(ChessBoard cb){
        int canDo = 0;
        for(int i =0;i<8;i++){
            for(int j=0;j<8;j++){
                if(cb.buttons[i][j].getBackground().equals(Color.gray) || cb.buttons[i][j].getBackground().equals(Color.green)){
                    canDo++;
                    cb.buttons[i][j].setBackground(Color.CYAN);
                }
                else if(cb.buttons[i][j].getBackground().equals(Color.red) || cb.buttons[i][j].getBackground().equals(Color.orange)){
                    cb.buttons[i][j].setState(i, j);
                }
            }
        }
        return canDo;
    }
}

class Knight extends Pawn{
    public Knight(String t){
        super(t);
        pic = resizeIcon(decideTeam(t));   
    }

    public Knight(String t,ImageIcon p,boolean M){
        super(t, p,M);
    }


    public Piece copy(){
        return new Knight(this.team,this.pic,this.hasMoved);
    }
        
     static String decideTeam(String team){
        if(team.equals("White"))
        return "ChessFinesse//Pics&Vids//Pics//w_knight_1x.png";
        return "ChessFinesse//Pics&Vids//Pics//b_knight_1x.png";
    }

    public void promote(ChessBoard cb,Pieces pi,int x,int y){}

    public boolean showPossibleMoves(Pieces bo,ChessBoard cb, int x, int y,int[] z){
        boolean isCheck = false;
        for(int i = 2;Math.abs(i)<=2;i--){
            for(int j = 2;Math.abs(j)<=2;j--){
                if(Math.abs(i) == Math.abs(j) || i == 0 || j == 0)continue;
                try{
                    if(bo.board[x+i][y+j]==null ||  !bo.board[x+i][y+j].team.equals(this.team)){
                        if((x+i)==z[0] && (y+j)==z[1]){
                            cb.buttons[x+i][y+j].setBackground(Color.cyan);
                            isCheck = true;
                        }
                        else if(!cb.buttons[x+i][y+j].getBackground().equals(Color.cyan) && !cb.buttons[x+i][y+j].getBackground().equals(Color.yellow))
                        cb.buttons[x+i][y+j].setBackground(Color.red);
                    }
                    else if(!cb.buttons[x+i][y+j].getBackground().equals(Color.cyan))
                    cb.buttons[x+i][y+j].setBackground(Color.orange);
                }catch(Exception e){}
            }
        }
        return isCheck;
    }

    public ArrayList<Integer[]> savingMoves(Pieces bo,ChessBoard cb, int x, int y,int[] z){
        ArrayList<Integer[]> saving = new ArrayList<>();
        for(int i = 2;Math.abs(i)<=2;i--){
            for(int j = 2;Math.abs(j)<=2;j--){
                if(Math.abs(i) == Math.abs(j) || i == 0 || j == 0)continue;
                try{
                    if((cb.buttons[x+i][y+j].getBackground().equals(Color.cyan)) && ((bo.getButton(x+i, y+j) == null) || !bo.getButton(x+i, y+j).team.equals(this.team)))
                    saving.add(new Integer[]{x+i,y+j});
                }catch(Exception e){}
            }
        }
        return saving;
    }

    public int onlyMove(ChessBoard cb){
        int canDo = 0;
        for(int i =0;i<8;i++){
            for(int j=0;j<8;j++){
                if(cb.buttons[i][j].getBackground().equals(Color.red)){
                    canDo++;
                    cb.buttons[i][j].setBackground(Color.CYAN);
                }
                else
                cb.buttons[i][j].setState(i, j);
            }
        }
        return canDo;
    }
}

class Queen extends Pawn{
    public Queen(String t){
        super(t);
        pic = resizeIcon(decideTeam(t));   
    }

    public Queen(String t,ImageIcon p,boolean M){
        super(t, p,M);
    }


    public Piece copy(){
        return new Queen(this.team,this.pic,this.hasMoved);
    }
        
     static String decideTeam(String team){
        if(team.equals("White"))
        return "ChessFinesse//Pics&Vids//Pics//w_queen_1x.png";
        return "ChessFinesse//Pics&Vids//Pics//b_queen_1x.png";
    }


    public void promote(ChessBoard cb,Pieces pi,int x,int y){}

    public boolean showPossibleMoves(Pieces bo,ChessBoard cb, int x, int y,int[] z){
        boolean isCheckhLeft=false,isCheckhRight = false,isCheckvUp=false,isCheckvDown=false,isCheckrightUp=false,isCheckrightDown=false,isCheckleftUp=false,isCheckleftDown=false;
        boolean CheckvUp=false,CheckvDown=false,CheckhLeft=false,CheckhRight = false;
        boolean vUp=true,vDown=true,hLeft=true,hRight = true;
        boolean CheckleftUp=false,CheckleftDown=false,CheckrightUp=false,CheckrightDown = false;
        boolean leftUp=true,leftDown=true,rightUp=true,rightDown = true;
        boolean hasPassed = false;
        boolean hasPassed1 = false,hasPassed2 = false, hasPassed3 = false,hasPassed4 = false, hasPassed5 = false,hasPassed6 = false, hasPassed7 = false,hasPassed8 = false;
        for(int i = 1;i<8;i++){
            try{
                if((y+i < 8)&&hRight){
                    if(bo.board[x][y+i]!=null){
                        if(bo.board[x][y+i].team.equals(this.team)){
                            hRight = false;
                            if(!cb.buttons[x][y+i].getBackground().equals(Color.cyan))
                            cb.buttons[x][y+i].setBackground(Color.orange);
                        }
                        else{
                            if(((x)==z[0] && (y+i)==z[1])){
                                if(!isCheckhRight){
                                    i = 0;
                                    isCheckhRight = true;
                                    continue;
                                }
                                else if(isCheckhRight && CheckhRight){
                                    cb.buttons[x][y].setBackground(Color.cyan);
                                    isCheckhRight = false;
                                    hRight = false;
                                }
                                else{
                                    cb.buttons[x][y+i].setBackground(Color.cyan);
                                    hasPassed = true;
                                }
                            }
                            else{
                                if(isCheckhRight && !CheckhRight){
                                    hRight = false;
                                }
                                else if(isCheckhRight){
                                    cb.buttons[x][y+i].setBackground(Color.yellow);
                                }
                                //else if(hasPassed1)
                                else if(CheckhRight)
                                hRight = false;
                                else{
                                    CheckhRight = true;
                                    if(!cb.buttons[x][y+i].getBackground().equals(Color.cyan) && !cb.buttons[x][y+i].getBackground().equals(Color.yellow))
                                    cb.buttons[x][y+i].setBackground(Color.red);
                                }
                            }
                        }
                    }
                    else{
                        if(isCheckhRight && !hasPassed)
                        cb.buttons[x][y+i].setBackground(Color.cyan);
                        else if(!cb.buttons[x][y+i].getBackground().equals(Color.cyan))
                        cb.buttons[x][y+i].setBackground(Color.red);
                    }
                }else{hRight=false;}
            }catch(Exception e){}
            try{
                if((y-i >= 0)&&hLeft){
                    if(bo.board[x][y-i]!=null){
                        if(bo.board[x][y-i].team.equals(this.team)){
                            hLeft = false;
                            if(!cb.buttons[x][y-i].getBackground().equals(Color.cyan))
                            cb.buttons[x][y-i].setBackground(Color.orange);
                        }
                        else{
                            if(((x)==z[0] && (y-i)==z[1])){
                                if(!isCheckhLeft){
                                    i = 0;
                                    isCheckhLeft = true;
                                    continue;
                                }
                                else if(isCheckhLeft && CheckhLeft){
                                    cb.buttons[x][y].setBackground(Color.cyan);
                                    isCheckhLeft = false;
                                    hLeft = false;
                                }
                                else{
                                    cb.buttons[x][y-i].setBackground(Color.cyan);
                                    hasPassed = true;
                                }
                            }
                            else{
                                if(isCheckhLeft && !CheckhLeft){
                                    hLeft = false;
                                }
                                else if(isCheckhLeft){
                                    cb.buttons[x][y-i].setBackground(Color.yellow);
                                }
                                //else if(hasPassed2)
                                else if(CheckhLeft)
                                hLeft = false;
                                else{
                                    CheckhLeft = true;
                                    if(!cb.buttons[x][y-i].getBackground().equals(Color.cyan) && !cb.buttons[x][y-i].getBackground().equals(Color.yellow))
                                    cb.buttons[x][y-i].setBackground(Color.red);
                                }
                            }
                        }
                    }
                    else{
                        if(isCheckhLeft && !hasPassed)
                        cb.buttons[x][y-i].setBackground(Color.cyan);
                        else if(!cb.buttons[x][y-i].getBackground().equals(Color.cyan))
                        cb.buttons[x][y-i].setBackground(Color.red);
                    }
                }else{hLeft=false;}
            }catch(Exception e){}
            try{
                if((x+i<8)&&vUp){
                    if(bo.board[x+i][y]!=null){
                        if(bo.board[x+i][y].team.equals(this.team)){
                            vUp = false;
                            if(!cb.buttons[x+i][y].getBackground().equals(Color.cyan))
                            cb.buttons[x+i][y].setBackground(Color.orange);
                        }
                        else{
                            if(((x+i)==z[0] && (y)==z[1])){
                                if(!isCheckvUp){
                                    i = 0;
                                    isCheckvUp = true;
                                    continue;
                                }
                                else if(isCheckvUp && CheckvUp){
                                    cb.buttons[x][y].setBackground(Color.cyan);
                                    isCheckvUp = false;
                                    vUp = false;
                                }
                                else{
                                    cb.buttons[x+i][y].setBackground(Color.cyan);
                                    hasPassed = true;
                                }
                            }
                            else{
                                if(isCheckvUp && !CheckvUp){
                                    vUp = false;
                                }
                                else if(isCheckvUp){
                                    cb.buttons[x+i][y].setBackground(Color.yellow);
                                }
                                //else if(hasPassed3)
                                else if(CheckvUp)
                                vUp = false;
                                else{
                                    CheckvUp = true;
                                    if(!cb.buttons[x+i][y].getBackground().equals(Color.cyan) && !cb.buttons[x+i][y].getBackground().equals(Color.yellow))
                                    cb.buttons[x+i][y].setBackground(Color.red);
                                }
                            }
                        }
                    }
                    else{
                        if(isCheckvUp && !hasPassed)
                        cb.buttons[x+i][y].setBackground(Color.cyan);
                        else if(!cb.buttons[x+i][y].getBackground().equals(Color.cyan))
                        cb.buttons[x+i][y].setBackground(Color.red);
                    }
                }else{vUp=false;}
            }catch(Exception e){}
            try{
                if((x - i >= 0)&&vDown){
                    if(bo.board[x-i][y]!=null){
                        if(bo.board[x-i][y].team.equals(this.team)){
                            vDown = false;
                            if(!cb.buttons[x-i][y].getBackground().equals(Color.cyan))
                            cb.buttons[x-i][y].setBackground(Color.orange);
                        }
                        else{
                            if(((x-i)==z[0] && (y)==z[1])){
                                if(!isCheckvDown){
                                    i = 0;
                                    isCheckvDown = true;
                                    continue;
                                }
                                else if(isCheckvDown && CheckvDown){
                                    cb.buttons[x][y].setBackground(Color.cyan);
                                    isCheckvDown = false;
                                    CheckvDown = false;
                                }
                                else{
                                    cb.buttons[x-i][y].setBackground(Color.cyan);
                                    hasPassed = true;
                                }
                            }
                            else{
                                if(isCheckvDown && !CheckvDown){
                                    vDown = false;
                                }
                                else if(isCheckvDown){
                                    cb.buttons[x-i][y].setBackground(Color.yellow);
                                }
                                //else if(hasPassed4)
                                else if(CheckvDown)
                                vDown = false;
                                else{
                                    CheckvDown = true;
                                    if(!cb.buttons[x-i][y].getBackground().equals(Color.cyan) && !cb.buttons[x-i][y].getBackground().equals(Color.yellow))
                                    cb.buttons[x-i][y].setBackground(Color.red);
                                }
                            }
                        }
                    }
                    else{
                        if(isCheckvDown && !hasPassed)
                        cb.buttons[x-i][y].setBackground(Color.cyan);
                        else if(!cb.buttons[x-i][y].getBackground().equals(Color.cyan))
                        cb.buttons[x-i][y].setBackground(Color.red);
                    }
                }else{vDown=false;}
            }catch(Exception e){}
            try{
                if((x+i < 8&&y+i < 8)&&rightUp){
                    if(bo.board[x+i][y+i]!=null){
                        if(bo.board[x+i][y+i].team.equals(this.team)){
                            rightUp = false;
                            if(!cb.buttons[x+i][y+i].getBackground().equals(Color.cyan))
                            cb.buttons[x+i][y+i].setBackground(Color.orange);
                        }
                        else{
                            if(((x+i)==z[0] && (y+i)==z[1])){
                                if(!isCheckrightUp){
                                    i = 0;
                                    isCheckrightUp = true;
                                    continue;
                                }
                                else if(isCheckrightUp && CheckrightUp){
                                    cb.buttons[x][y].setBackground(Color.cyan);
                                    isCheckrightUp = false;
                                    rightUp = false;
                                }
                                else{
                                    cb.buttons[x+i][y+i].setBackground(Color.cyan);
                                    hasPassed = true;
                                }
                            }
                            else{
                                if(isCheckrightUp && !CheckrightUp){
                                    rightUp = false;
                                }
                                else if(isCheckrightUp){
                                    cb.buttons[x+i][y+i].setBackground(Color.yellow);
                                }
                                //else if(hasPassed5)
                                else if(CheckrightUp)
                                rightUp = false;
                                else{
                                    CheckrightUp = true;
                                    if(!cb.buttons[x+i][y+i].getBackground().equals(Color.cyan) && !cb.buttons[x+i][y+i].getBackground().equals(Color.yellow))
                                    cb.buttons[x+i][y+i].setBackground(Color.red);
                                }
                            }
                        }
                    }
                    else{
                        if(isCheckrightUp && !hasPassed)
                        cb.buttons[x+i][y+i].setBackground(Color.cyan);
                        else if(!cb.buttons[x+i][y+i].getBackground().equals(Color.cyan))
                        cb.buttons[x+i][y+i].setBackground(Color.red);
                    }
                }else{rightUp=false;}
            }catch(Exception e){}
            try{
                if((y-i >= 0&&x+i<8)&&leftDown){
                    if(bo.board[x+i][y-i]!=null){
                        if(bo.board[x+i][y-i].team.equals(this.team)){
                            leftDown = false;
                            if(!cb.buttons[x+i][y-i].getBackground().equals(Color.cyan))
                            cb.buttons[x+i][y-i].setBackground(Color.orange);
                        }
                        else{
                            if(((x+i)==z[0] && (y-i)==z[1])){
                                if(!isCheckleftDown){
                                    i = 0;
                                    isCheckleftDown = true;
                                    continue;
                                }
                                else if(isCheckleftDown && CheckleftDown){
                                    cb.buttons[x][y].setBackground(Color.cyan);
                                    isCheckleftDown = false;
                                    leftDown = false;
                                }
                                else{
                                    cb.buttons[x+i][y-i].setBackground(Color.cyan);
                                    hasPassed = true;
                                }
                            }
                            else{
                                if(isCheckleftDown && !CheckleftDown){
                                    leftDown = false;
                                }
                                else if(isCheckleftDown){
                                    cb.buttons[x+i][y-i].setBackground(Color.yellow);
                                }
                                //else if(hasPassed6)
                                else if(CheckleftDown)
                                leftDown = false;
                                else{
                                    CheckleftDown = true;
                                    if(!cb.buttons[x+i][y-i].getBackground().equals(Color.cyan) && !cb.buttons[x+i][y-i].getBackground().equals(Color.yellow))
                                    cb.buttons[x+i][y-i].setBackground(Color.red);
                                }
                            }
                        }
                    }
                    else{
                        if(isCheckleftDown && !hasPassed)
                        cb.buttons[x+i][y-i].setBackground(Color.cyan);
                        else if(!cb.buttons[x+i][y-i].getBackground().equals(Color.cyan))
                        cb.buttons[x+i][y-i].setBackground(Color.red);
                    }
                }else{leftDown=false;}
            }catch(Exception e){}
            try{
                if((y+i<8&&x-i>=0)&&leftUp){
                    if(bo.board[x-i][y+i]!=null){
                        if(bo.board[x-i][y+i].team.equals(this.team)){
                            leftUp = false;
                            if(!cb.buttons[x-i][y+i].getBackground().equals(Color.cyan))
                            cb.buttons[x-i][y+i].setBackground(Color.orange);
                        }
                        else{
                            if(((x-i)==z[0] && (y+i)==z[1])){
                                if(!isCheckleftUp){
                                    i = 0;
                                    isCheckleftUp = true;
                                    continue;
                                }
                                else if(isCheckleftUp && CheckleftUp){
                                    cb.buttons[x][y].setBackground(Color.cyan);
                                    isCheckleftUp = false;
                                    leftUp = false;
                                }
                                else{
                                    cb.buttons[x-i][y+i].setBackground(Color.cyan);
                                    hasPassed = true;
                                }
                            }
                            else{
                                if(isCheckleftUp && !CheckleftUp){
                                    leftUp = false;
                                }
                                else if(isCheckleftUp){
                                    cb.buttons[x-i][y+i].setBackground(Color.yellow);
                                }
                                //else if(hasPassed7)
                                else if(CheckleftUp)
                                leftUp = false;
                                else{
                                    CheckleftUp = true;
                                    if(!cb.buttons[x-i][y+i].getBackground().equals(Color.cyan) && !cb.buttons[x-i][y+i].getBackground().equals(Color.yellow))
                                    cb.buttons[x-i][y+i].setBackground(Color.red);
                                }
                            }
                        }
                    }
                    else{
                        if(isCheckleftUp && !hasPassed)
                        cb.buttons[x-i][y+i].setBackground(Color.cyan);
                        else if(!cb.buttons[x-i][y+i].getBackground().equals(Color.cyan))
                        cb.buttons[x-i][y+i].setBackground(Color.red);
                    }
                }else{leftUp=false;}
            }catch(Exception e){}
            try{
                if((x - i >= 0&&y-i>=0)&&rightDown){
                    if(bo.board[x-i][y-i]!=null){
                        if(bo.board[x-i][y-i].team.equals(this.team)){
                            rightDown = false;
                            if(!cb.buttons[x-i][y-i].getBackground().equals(Color.cyan))
                            cb.buttons[x-i][y-i].setBackground(Color.orange);
                        }
                        else{
                            if(((x-i)==z[0] && (y-i)==z[1])){
                                if(!isCheckrightDown){
                                    i = 0;
                                    isCheckrightDown = true;
                                    continue;
                                }
                                else if(isCheckrightDown && CheckrightDown){
                                    cb.buttons[x][y].setBackground(Color.cyan);
                                    isCheckrightDown = false;
                                    rightDown = false;
                                }
                                else{
                                    cb.buttons[x-i][y-i].setBackground(Color.cyan);
                                    hasPassed = true;
                                }
                            }
                            else{
                                if(isCheckrightDown && !CheckrightDown){
                                    rightDown = false;
                                }
                                else if(isCheckrightDown){
                                    cb.buttons[x-i][y-i].setBackground(Color.yellow);
                                }
                                //else if(hasPassed8)
                                else if(CheckrightDown)
                                rightDown = false;
                                else{
                                    CheckrightDown = true;
                                    if(!cb.buttons[x-i][y-i].getBackground().equals(Color.cyan) && !cb.buttons[x-i][y-i].getBackground().equals(Color.yellow))
                                    cb.buttons[x-i][y-i].setBackground(Color.red);
                                }
                            }
                        }
                    }
                    else{
                        if(isCheckrightDown && !hasPassed)
                        cb.buttons[x-i][y-i].setBackground(Color.cyan);
                        else if(!cb.buttons[x-i][y-i].getBackground().equals(Color.cyan))
                        cb.buttons[x-i][y-i].setBackground(Color.red);
                    }
                }else{rightDown=false;}
            }catch(Exception e){}
        } 
        return (isCheckhLeft || isCheckhRight || isCheckvUp || isCheckvDown || isCheckleftDown || isCheckleftUp || isCheckrightDown || isCheckrightUp);
    }

    public ArrayList<Integer[]> savingMoves(Pieces bo,ChessBoard cb, int x, int y,int[] z){
        ArrayList<Integer[]> saving = new ArrayList<>();
        boolean vUp=true,vDown=true,hLeft=true,hRight = true;
        boolean leftUp=true,leftDown=true,rightUp=true,rightDown = true;
        for(int i = 1;i<8;i++){
            try{
                if(hRight && (bo.board[x][y+i]==null || !bo.board[x][y+i].team.equals(this.team))){
                    if(cb.buttons[x][y+i].getBackground().equals(Color.cyan))
                    saving.add(new Integer[]{x,y+i});
                    if(bo.board[x][y+i]!=null)
                    hRight = false;
                }
                else
                hRight = false;
            }catch(Exception e){}
            try{
                if(hLeft && (bo.board[x][y-i]==null || !bo.board[x][y-i].team.equals(this.team))){
                    if(cb.buttons[x][y-i].getBackground().equals(Color.cyan))
                    saving.add(new Integer[]{x,y-i});
                    if(bo.board[x][y-i]!=null)
                    hLeft = false;
                }
                else
                hLeft = false;
            }catch(Exception e){}
            try{
                if(vDown && (bo.board[x+i][y]==null || !bo.board[x+i][y].team.equals(this.team))){
                    if(cb.buttons[x+i][y].getBackground().equals(Color.cyan))
                    saving.add(new Integer[]{x+i,y});
                    if(bo.board[x+i][y]!=null)
                    vDown = false;
                }
                else
                vDown = false;
            }catch(Exception e){}
            try{
                if(vUp && (bo.board[x-i][y]==null || !bo.board[x-i][y].team.equals(this.team))){
                    if(cb.buttons[x-i][y].getBackground().equals(Color.cyan))
                    saving.add(new Integer[]{x-i,y});
                    if(bo.board[x-i][y]!=null)
                    vUp = false;
                }
                else
                vUp = false;
            }catch(Exception e){}
            try{
                if(rightDown && (bo.board[x+i][y+i]==null || !bo.board[x+i][y+i].team.equals(this.team))){
                    if(cb.buttons[x+i][y+i].getBackground().equals(Color.cyan))
                    saving.add(new Integer[]{x+i,y+i});
                    if(bo.board[x+i][y+i]!=null)
                    rightDown = false;
                }
                else
                rightDown = false;
            }catch(Exception e){}
            try{
                if(leftUp && (bo.board[x+i][y-i]==null || !bo.board[x+i][y-i].team.equals(this.team))){
                    if(cb.buttons[x+i][y-i].getBackground().equals(Color.cyan))
                    saving.add(new Integer[]{x+i,y-i});
                    if(bo.board[x+i][y-i]!=null)
                    leftUp = false;
                }
                else
                leftUp = false;
            }catch(Exception e){}
            try{
                if(rightUp && (bo.board[x-i][y+i]==null || !bo.board[x-i][y+i].team.equals(this.team))){
                    if(cb.buttons[x-i][y+i].getBackground().equals(Color.cyan))
                    saving.add(new Integer[]{x-i,y+i});
                    if(bo.board[x-i][y+i]!=null)
                    rightUp = false;
                }
                else
                rightUp = false;
            }catch(Exception e){}
            try{
                if(leftDown && (bo.board[x-i][y-i]==null || !bo.board[x-i][y-i].team.equals(this.team))){
                    if(cb.buttons[x-i][y-i].getBackground().equals(Color.cyan))
                    saving.add(new Integer[]{x-i,y-i});
                    if(bo.board[x-i][y-i]!=null)
                    leftDown = false;
                }
                else
                leftDown = false;
            }catch(Exception e){}
        } 
        return saving;
    }

    public int onlyMove(ChessBoard cb){
        int canDo = 0;
        for(int i =0;i<8;i++){
            for(int j=0;j<8;j++){
                if(cb.buttons[i][j].getBackground().equals(Color.red) || cb.buttons[i][j].getBackground().equals(Color.cyan) || cb.buttons[i][j].getBackground().equals(Color.yellow)){
                    canDo++;
                    cb.buttons[i][j].setBackground(Color.CYAN);
                }
                else{
                    cb.buttons[i][j].setState(i, j);
                }
            }
        }
        return canDo;
    }
}

class Rook extends Pawn{
    public Rook(String t){
        super(t);
        pic = resizeIcon(decideTeam(t));   
    }

    public Rook(String t,ImageIcon p,boolean M){
        super(t, p,M);
    }


    public Piece copy(){
        return new Rook(this.team,this.pic,this.hasMoved);
    }
        
     static String decideTeam(String team){
        if(team.equals("White"))
        return "ChessFinesse//Pics&Vids//Pics//w_rook_1x.png";
        return "ChessFinesse//Pics&Vids//Pics//b_rook_1x.png";
    }

    public void promote(ChessBoard cb,Pieces pi,int x,int y){}

    public boolean showPossibleMoves(Pieces bo,ChessBoard cb, int x, int y,int[] z){
        boolean isCheckhLeft=false,isCheckhRight=false,isCheckvDown=false,isCheckvUp=false;
        boolean hasPassed1 = false,hasPassed2 = false, hasPassed3 = false,hasPassed4 = false;
        boolean CheckvUp=false,CheckvDown=false,CheckhLeft=false,CheckhRight = false;
        boolean vUp=true,vDown=true,hLeft=true,hRight = true;
        boolean hasPassed = false;
        for(int i = 1;i<8;i++){
            try{
                if((y+i < 8)&&hRight){
                    if(bo.board[x][y+i]!=null){
                        if(bo.board[x][y+i].team.equals(this.team)){
                            hRight = false;
                            if(!cb.buttons[x][y+i].getBackground().equals(Color.cyan))
                            cb.buttons[x][y+i].setBackground(Color.orange);
                        }
                        else{
                            if(((x)==z[0] && (y+i)==z[1])){
                                if(!isCheckhRight){
                                    i = 0;
                                    isCheckhRight = true;
                                    continue;
                                }
                                else if(isCheckhRight && CheckhRight){
                                    cb.buttons[x][y].setBackground(Color.cyan);
                                    isCheckhRight = false;
                                    hRight = false;
                                }
                                else{
                                    cb.buttons[x][y+i].setBackground(Color.cyan);
                                    hasPassed = true;
                                }
                            }
                            else{
                                if(isCheckhRight && !CheckhRight){
                                    hRight = false;
                                }
                                else if(isCheckhRight){
                                    cb.buttons[x][y+i].setBackground(Color.yellow);
                                }
                                //else if(hasPassed1)
                                else if(CheckhRight)
                                hRight = false;
                                else{
                                    CheckhRight = true;
                                    if(!cb.buttons[x][y+i].getBackground().equals(Color.cyan) && !cb.buttons[x][y+i].getBackground().equals(Color.yellow))
                                    cb.buttons[x][y+i].setBackground(Color.red);
                                }
                            }
                        }
                    }
                    else{
                        if(isCheckhRight && !hasPassed)
                        cb.buttons[x][y+i].setBackground(Color.cyan);
                        else if(!cb.buttons[x][y+i].getBackground().equals(Color.cyan))
                        cb.buttons[x][y+i].setBackground(Color.red);
                    }
                }else{hRight=false;}
            }catch(Exception e){}
            try{
                if((y-i >= 0)&&hLeft){
                    if(bo.board[x][y-i]!=null){
                        if(bo.board[x][y-i].team.equals(this.team)){
                            hLeft = false;
                            if(!cb.buttons[x][y-i].getBackground().equals(Color.cyan))
                            cb.buttons[x][y-i].setBackground(Color.orange);
                        }
                        else{
                            if(((x)==z[0] && (y-i)==z[1])){
                                if(!isCheckhLeft){
                                    i = 0;
                                    isCheckhLeft = true;
                                    continue;
                                }
                                else if(isCheckhLeft && CheckhLeft){
                                    cb.buttons[x][y].setBackground(Color.cyan);
                                    isCheckhLeft = false;
                                    hLeft = false;
                                }
                                else{
                                    cb.buttons[x][y-i].setBackground(Color.cyan);
                                    hasPassed = true;
                                }
                            }
                            else{
                                if(isCheckhLeft && !CheckhLeft){
                                    hLeft = false;
                                }
                                else if(isCheckhLeft){
                                    cb.buttons[x][y-i].setBackground(Color.yellow);
                                }
                                //else if(hasPassed2)
                                else if(CheckhLeft)
                                hLeft = false;
                                else{
                                    CheckhLeft = true;
                                    if(!cb.buttons[x][y-i].getBackground().equals(Color.cyan) && !cb.buttons[x][y-i].getBackground().equals(Color.yellow))
                                    cb.buttons[x][y-i].setBackground(Color.red);
                                }
                            }
                        }
                    }
                    else{
                        if(isCheckhLeft && !hasPassed)
                        cb.buttons[x][y-i].setBackground(Color.cyan);
                        else if(!cb.buttons[x][y-i].getBackground().equals(Color.cyan))
                        cb.buttons[x][y-i].setBackground(Color.red);
                    }
                }else{hLeft=false;}
            }catch(Exception e){}
            try{
                if((x+i<8)&&vUp){
                    if(bo.board[x+i][y]!=null){
                        if(bo.board[x+i][y].team.equals(this.team)){
                            vUp = false;
                            if(!cb.buttons[x+i][y].getBackground().equals(Color.cyan))
                            cb.buttons[x+i][y].setBackground(Color.orange);
                        }
                        else{
                            if(((x+i)==z[0] && (y)==z[1])){
                                if(!isCheckvUp){
                                    i = 0;
                                    isCheckvUp = true;
                                    continue;
                                }
                                else if(isCheckvUp && CheckvUp){
                                    cb.buttons[x][y].setBackground(Color.cyan);
                                    isCheckvUp = false;
                                    vUp = false;
                                }
                                else{
                                    cb.buttons[x+i][y].setBackground(Color.cyan);
                                    hasPassed = true;
                                }
                            }
                            else{
                                if(isCheckvUp && !CheckvUp){
                                    vUp = false;
                                }
                                else if(isCheckvUp){
                                    cb.buttons[x+i][y].setBackground(Color.yellow);
                                }
                                //else if(hasPassed3)
                                else if(CheckvUp)
                                vUp = false;
                                else{
                                    CheckvUp = true;
                                    if(!cb.buttons[x+i][y].getBackground().equals(Color.cyan) && !cb.buttons[x+i][y].getBackground().equals(Color.yellow))
                                    cb.buttons[x+i][y].setBackground(Color.red);
                                }
                            }
                        }
                    }
                    else{
                        if(isCheckvUp && !hasPassed)
                        cb.buttons[x+i][y].setBackground(Color.cyan);
                        else if(!cb.buttons[x+i][y].getBackground().equals(Color.cyan))
                        cb.buttons[x+i][y].setBackground(Color.red);
                    }
                }else{vUp=false;}
            }catch(Exception e){}
            try{
                if((x - i >= 0)&&vDown){
                    if(bo.board[x-i][y]!=null){
                        if(bo.board[x-i][y].team.equals(this.team)){
                            vDown = false;
                            if(!cb.buttons[x-i][y].getBackground().equals(Color.cyan))
                            cb.buttons[x-i][y].setBackground(Color.orange);
                        }
                        else{
                            if(((x-i)==z[0] && (y)==z[1])){
                                if(!isCheckvDown){
                                    i = 0;
                                    isCheckvDown = true;
                                    continue;
                                }
                                else if(isCheckvDown && CheckvDown){
                                    cb.buttons[x][y].setBackground(Color.cyan);
                                    isCheckvDown = false;
                                    CheckvDown = false;
                                }
                                else{
                                    cb.buttons[x-i][y].setBackground(Color.cyan);
                                    hasPassed = true;
                                }
                            }
                            else{
                                if(isCheckvDown && !CheckvDown){
                                    vDown = false;
                                }
                                else if(isCheckvDown){
                                    cb.buttons[x-i][y].setBackground(Color.yellow);
                                }
                                //else if(hasPassed4)
                                else if(CheckvDown)
                                vDown = false;
                                else{
                                    CheckvDown = true;
                                    if(!cb.buttons[x-i][y].getBackground().equals(Color.cyan) && !cb.buttons[x-i][y].getBackground().equals(Color.yellow))
                                    cb.buttons[x-i][y].setBackground(Color.red);
                                }
                            }
                        }
                    }
                    else{
                        if(isCheckvDown && !hasPassed)
                        cb.buttons[x-i][y].setBackground(Color.cyan);
                        else if(!cb.buttons[x-i][y].getBackground().equals(Color.cyan))
                        cb.buttons[x-i][y].setBackground(Color.red);
                    }
                }else{vDown=false;}
            }catch(Exception e){}
        }
        return (isCheckhLeft || isCheckhRight || isCheckvDown || isCheckvUp);
    }

    public ArrayList<Integer[]> savingMoves(Pieces bo,ChessBoard cb, int x, int y,int[] z){
        ArrayList<Integer[]> saving = new ArrayList<>();
        boolean vUp=true,vDown=true,hLeft=true,hRight = true;
        for(int i = 1;i<8;i++){
            try{
                if(hRight && (bo.board[x][y+i]==null || !bo.board[x][y+i].team.equals(this.team))){
                    if(cb.buttons[x][y+i].getBackground().equals(Color.cyan))
                    saving.add(new Integer[]{x,y+i});
                    if(bo.board[x][y+i]!=null)
                    hRight = false;
                }
                else
                hRight = false;
            }catch(Exception e){}
            try{
                if(hLeft && (bo.board[x][y-i]==null || !bo.board[x][y-i].team.equals(this.team))){
                    if(cb.buttons[x][y-i].getBackground().equals(Color.cyan))
                    saving.add(new Integer[]{x,y-i});
                    if(bo.board[x][y-i]!=null)
                    hLeft = false;
                }
                else
                hLeft = false;
            }catch(Exception e){}
            try{
                if(vDown && (bo.board[x+i][y]==null || !bo.board[x+i][y].team.equals(this.team))){
                    if(cb.buttons[x+i][y].getBackground().equals(Color.cyan))
                    saving.add(new Integer[]{x+i,y});
                    if(bo.board[x+i][y]!=null)
                    vDown = false;
                }
                else
                vDown = false;
            }catch(Exception e){}
            try{
                if(vUp && (bo.board[x-i][y]==null || !bo.board[x-i][y].team.equals(this.team))){
                    if(cb.buttons[x-i][y].getBackground().equals(Color.cyan))
                    saving.add(new Integer[]{x-i,y});
                    if(bo.board[x-i][y]!=null)
                    vUp = false;
                }
                else
                vUp = false;
            }catch(Exception e){}
        } 
        return saving;
    }

    public int onlyMove(ChessBoard cb){
        int canDo = 0;
        for(int i =0;i<8;i++){
            for(int j=0;j<8;j++){
                if(cb.buttons[i][j].getBackground().equals(Color.red) || cb.buttons[i][j].getBackground().equals(Color.cyan)){
                    canDo++;
                    cb.buttons[i][j].setBackground(Color.CYAN);
                }
                else{
                    cb.buttons[i][j].setState(i, j);
                }
            }
        }
        return canDo;
    }
}

class Bishop extends Pawn{

    public Bishop(String t){
        super(t);
        pic = resizeIcon(decideTeam(t));   
    }

    public Bishop(String t,ImageIcon p,boolean M){
        super(t, p,M);
    }

    public Piece copy(){
        return new Bishop(this.team,this.pic,this.hasMoved);
    }
        
    static String decideTeam(String team){
        if(team.equals("White"))
        return "ChessFinesse//Pics&Vids//Pics//w_bishop_1x.png";
        return "ChessFinesse//Pics&Vids//Pics//b_bishop_1x.png";
    }

    public void promote(ChessBoard cb,Pieces pi,int x,int y){}

    public boolean showPossibleMoves(Pieces bo,ChessBoard cb, int x, int y,int[] z){
        boolean isCheckrightDown=false,isCheckrightUp=false,isCheckleftDown=false,isCheckleftUp=false;
        boolean hasPassed5 = false,hasPassed6 = false, hasPassed7 = false,hasPassed8 = false;
        boolean CheckleftUp=false,CheckleftDown=false,CheckrightUp=false,CheckrightDown = false;
        boolean leftUp=true,leftDown=true,rightUp=true,rightDown = true;
        boolean hasPassed = false;
        for(int i = 1;i<8;i++){
            try{
                if((x+i < 8&&y+i < 8)&&rightUp){
                    if(bo.board[x+i][y+i]!=null){
                        if(bo.board[x+i][y+i].team.equals(this.team)){
                            rightUp = false;
                            if(!cb.buttons[x+i][y+i].getBackground().equals(Color.cyan))
                            cb.buttons[x+i][y+i].setBackground(Color.orange);
                        }
                        else{
                            if(((x+i)==z[0] && (y+i)==z[1])){
                                if(!isCheckrightUp){
                                    i = 0;
                                    isCheckrightUp = true;
                                    continue;
                                }
                                else if(isCheckrightUp && CheckrightUp){
                                    cb.buttons[x][y].setBackground(Color.cyan);
                                    isCheckrightUp = false;
                                    rightUp = false;
                                }
                                else{
                                    cb.buttons[x+i][y+i].setBackground(Color.cyan);
                                    hasPassed = true;
                                }
                            }
                            else{
                                if(isCheckrightUp && !CheckrightUp){
                                    rightUp = false;
                                }
                                else if(isCheckrightUp){
                                    cb.buttons[x+i][y+i].setBackground(Color.yellow);
                                }
                                //else if(hasPassed5)
                                else if(CheckrightUp)
                                rightUp = false;
                                else{
                                    CheckrightUp = true;
                                    if(!cb.buttons[x+i][y+i].getBackground().equals(Color.cyan) && !cb.buttons[x+i][y+i].getBackground().equals(Color.yellow))
                                    cb.buttons[x+i][y+i].setBackground(Color.red);
                                }
                            }
                        }
                    }
                    else{
                        if(isCheckrightUp && !hasPassed)
                        cb.buttons[x+i][y+i].setBackground(Color.cyan);
                        else if(!cb.buttons[x+i][y+i].getBackground().equals(Color.cyan))
                        cb.buttons[x+i][y+i].setBackground(Color.red);
                    }
                }else{rightUp=false;}
            }catch(Exception e){}
            try{
                if((y-i >= 0&&x+i<8)&&leftDown){
                    if(bo.board[x+i][y-i]!=null){
                        if(bo.board[x+i][y-i].team.equals(this.team)){
                            leftDown = false;
                            if(!cb.buttons[x+i][y-i].getBackground().equals(Color.cyan))
                            cb.buttons[x+i][y-i].setBackground(Color.orange);
                        }
                        else{
                            if(((x+i)==z[0] && (y-i)==z[1])){
                                if(!isCheckleftDown){
                                    i = 0;
                                    isCheckleftDown = true;
                                    continue;
                                }
                                else if(isCheckleftDown && CheckleftDown){
                                    cb.buttons[x][y].setBackground(Color.cyan);
                                    isCheckleftDown = false;
                                    leftDown = false;
                                }
                                else{
                                    cb.buttons[x+i][y-i].setBackground(Color.cyan);
                                    hasPassed = true;
                                }
                            }
                            else{
                                if(isCheckleftDown && !CheckleftDown){
                                    leftDown = false;
                                }
                                else if(isCheckleftDown){
                                    cb.buttons[x+i][y-i].setBackground(Color.yellow);
                                }
                                //else if(hasPassed6)
                                else if(CheckleftDown)
                                leftDown = false;
                                else{
                                    CheckleftDown = true;
                                    if(!cb.buttons[x+i][y-i].getBackground().equals(Color.cyan) && !cb.buttons[x+i][y-i].getBackground().equals(Color.yellow))
                                    cb.buttons[x+i][y-i].setBackground(Color.red);
                                }
                            }
                        }
                    }
                    else{
                        if(isCheckleftDown && !hasPassed)
                        cb.buttons[x+i][y-i].setBackground(Color.cyan);
                        else if(!cb.buttons[x+i][y-i].getBackground().equals(Color.cyan))
                        cb.buttons[x+i][y-i].setBackground(Color.red);
                    }
                }else{leftDown=false;}
            }catch(Exception e){}
            try{
                if((y+i<8&&x-i>=0)&&leftUp){
                    if(bo.board[x-i][y+i]!=null){
                        if(bo.board[x-i][y+i].team.equals(this.team)){
                            leftUp = false;
                            if(!cb.buttons[x-i][y+i].getBackground().equals(Color.cyan))
                            cb.buttons[x-i][y+i].setBackground(Color.orange);
                        }
                        else{
                            if(((x-i)==z[0] && (y+i)==z[1])){
                                if(!isCheckleftUp){
                                    i = 0;
                                    isCheckleftUp = true;
                                    continue;
                                }
                                else if(isCheckleftUp && CheckleftUp){
                                    cb.buttons[x][y].setBackground(Color.cyan);
                                    isCheckleftUp = false;
                                    leftUp = false;
                                }
                                else{
                                    cb.buttons[x-i][y+i].setBackground(Color.cyan);
                                    hasPassed = true;
                                }
                            }
                            else{
                                if(isCheckleftUp && !CheckleftUp){
                                    leftUp = false;
                                }
                                else if(isCheckleftUp){
                                    cb.buttons[x-i][y+i].setBackground(Color.yellow);
                                }
                                //else if(hasPassed7)
                                else if(CheckleftUp)
                                leftUp = false;
                                else{
                                    CheckleftUp = true;
                                    if(!cb.buttons[x-i][y+i].getBackground().equals(Color.cyan) && !cb.buttons[x-i][y+i].getBackground().equals(Color.yellow))
                                    cb.buttons[x-i][y+i].setBackground(Color.red);
                                }
                            }
                        }
                    }
                    else{
                        if(isCheckleftUp && !hasPassed)
                        cb.buttons[x-i][y+i].setBackground(Color.cyan);
                        else if(!cb.buttons[x-i][y+i].getBackground().equals(Color.cyan))
                        cb.buttons[x-i][y+i].setBackground(Color.red);
                    }
                }else{leftUp=false;}
            }catch(Exception e){}
            try{
                if((x - i >= 0&&y-i>=0)&&rightDown){
                    if(bo.board[x-i][y-i]!=null){
                        if(bo.board[x-i][y-i].team.equals(this.team)){
                            rightDown = false;
                            if(!cb.buttons[x-i][y-i].getBackground().equals(Color.cyan))
                            cb.buttons[x-i][y-i].setBackground(Color.orange);
                        }
                        else{
                            if(((x-i)==z[0] && (y-i)==z[1])){
                                if(!isCheckrightDown){
                                    i = 0;
                                    isCheckrightDown = true;
                                    continue;
                                }
                                else if(isCheckrightDown && CheckrightDown){
                                    cb.buttons[x][y].setBackground(Color.cyan);
                                    isCheckrightDown = false;
                                    rightDown = false;
                                }
                                else{
                                    cb.buttons[x-i][y-i].setBackground(Color.cyan);
                                    hasPassed = true;
                                }
                            }
                            else{
                                if(isCheckrightDown && !CheckrightDown){
                                    rightDown = false;
                                }
                                else if(isCheckrightDown){
                                    cb.buttons[x-i][y-i].setBackground(Color.yellow);
                                }
                                //else if(hasPassed8)
                                else if(CheckrightDown)
                                rightDown = false;
                                else{
                                    CheckrightDown = true;
                                    if(!cb.buttons[x-i][y-i].getBackground().equals(Color.cyan) && !cb.buttons[x-i][y-i].getBackground().equals(Color.yellow))
                                    cb.buttons[x-i][y-i].setBackground(Color.red);
                                }
                            }
                        }
                    }
                    else{
                        if(isCheckrightDown && !hasPassed)
                        cb.buttons[x-i][y-i].setBackground(Color.cyan);
                        else if(!cb.buttons[x-i][y-i].getBackground().equals(Color.cyan))
                        cb.buttons[x-i][y-i].setBackground(Color.red);
                    }
                }else{rightDown=false;}
            }catch(Exception e){}
        }
        return (isCheckleftDown || isCheckleftUp || isCheckrightDown || isCheckrightUp);
    }

    public ArrayList<Integer[]> savingMoves(Pieces bo,ChessBoard cb, int x, int y,int[] z){
        ArrayList<Integer[]> saving = new ArrayList<>();
        boolean leftUp=true,leftDown=true,rightUp=true,rightDown = true;
        for(int i = 1;i<8;i++){
            try{
                if(rightDown && (bo.board[x+i][y+i]==null || !bo.board[x+i][y+i].team.equals(this.team))){
                    if(cb.buttons[x+i][y+i].getBackground().equals(Color.cyan) || cb.buttons[x+i][y+i].getBackground().equals(Color.orange))
                    saving.add(new Integer[]{x+i,y+i});
                    if(bo.board[x+i][y+i]!=null)
                    rightDown = false;
                }
                else
                rightDown = false;
            }catch(Exception e){}
            try{
                if(leftUp && (bo.board[x+i][y-i]==null || !bo.board[x+i][y-i].team.equals(this.team))){
                    if(cb.buttons[x+i][y-i].getBackground().equals(Color.cyan))
                    saving.add(new Integer[]{x+i,y-i});
                    if(bo.board[x+i][y-i]!=null)
                    leftUp = false;
                }
                else
                leftUp = false;
            }catch(Exception e){}
            try{
                if(rightUp && (bo.board[x-i][y+i]==null || !bo.board[x-i][y+i].team.equals(this.team))){
                    if(cb.buttons[x-i][y+i].getBackground().equals(Color.cyan))
                    saving.add(new Integer[]{x-i,y+i});
                    if(bo.board[x-i][y+i]!=null)
                    rightUp = false;
                }
                else
                rightUp = false;
            }catch(Exception e){}
            try{
                if(leftDown && (bo.board[x-i][y-i]==null || !bo.board[x-i][y-i].team.equals(this.team))){
                    if(cb.buttons[x-i][y-i].getBackground().equals(Color.cyan))
                    saving.add(new Integer[]{x-i,y-i});
                    if(bo.board[x-i][y-i]!=null)
                    leftDown = false;
                }
                else
                leftDown = false;
            }catch(Exception e){}
        } 
        return saving;
    }

    public int onlyMove(ChessBoard cb){
        int canDo = 0;
        for(int i =0;i<8;i++){
            for(int j=0;j<8;j++){
                if(cb.buttons[i][j].getBackground().equals(Color.red) || cb.buttons[i][j].getBackground().equals(Color.cyan) || cb.buttons[i][j].getBackground().equals(Color.yellow)){
                    canDo++;
                    cb.buttons[i][j].setBackground(Color.CYAN);
                }
                else{
                    cb.buttons[i][j].setState(i, j);
                }
            }
        }
        return canDo;
    }
}