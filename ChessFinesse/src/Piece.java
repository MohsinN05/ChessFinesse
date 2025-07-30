package ChessFinesse.src;
import javax.swing.*;
import java.awt.*;
import java.util.List;
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

    public abstract Map<String,ArrayList<Integer[]>> showPossibleMoves(Pieces bo,ChessBoard cb, int x, int y);

    public abstract ArrayList<Integer[]> savingMoves(Pieces bo,ChessBoard cb, int x, int y,int[] z);

    public void promote(ChessBoard cb,Pieces pi,int x,int y){}


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

    private Set<List<Integer>> coordinateOfChecking(Set<Integer[]> a){
        Set<List<Integer>> listSet = new HashSet<>();
        for(Integer[] i:a){
            i[0] = 7 - i[0];
            i[1] = 7 - i[1];
            listSet.add(Arrays.asList(i));
        }
        return listSet;
    }
    
    public Map<String,ArrayList<Integer[]>> showPossibleMoves(Pieces bo,ChessBoard cb, int x, int y){
        Map<String,ArrayList<Integer[]>> moves = new HashMap<>();
        moves.put("Defence", new ArrayList<>());
        moves.put("Attack", new ArrayList<>());
        moves.put("Neighbor", new ArrayList<>());
        if(team.equals(cb.match.turn)){
            bo.shiftBoard();
            Set<Integer[]> save = bo.searchForChecks(cb);
            bo.shiftBoard();
            Set<List<Integer>> a = coordinateOfChecking(save);
            for(int i = 1;Math.abs(i)<2;i--){
                for(int j = 1;Math.abs(j)<2;j--){
                    try{
                        if(!a.contains(Arrays.asList(x+i, y+j))){
                            if((bo.board[x+i][y+j]==null)){
                                moves.get("Defence").add(new Integer[]{x+i,y+j});
                            }
                            else if(!bo.board[x+i][y+j].team.equals(this.team)){
                                moves.get("Attack").add(new Integer[]{x+i,y+j});
                            }
                        }
                        
                    }catch(Exception e){}      
                }
            }
            System.out.println(moves);
        }
        else{
            for(int i = 1;Math.abs(i)<2;i--){
                for(int j = 1;Math.abs(j)<2;j--){
                    if(i == 0 && j == 0)continue;
                    try{
                        if((bo.board[x+i][y+j]==null)){
                            moves.get("Defence").add(new Integer[]{x+i,y+j});
                        }
                        else if(!bo.board[x+i][y+j].team.equals(this.team)){
                            moves.get("Attack").add(new Integer[]{x+i,y+j});
                        }
                    }catch(Exception e){}      
                }
            }
        }
        return moves; 
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

    public Map<String,ArrayList<Integer[]>> showPossibleMoves(Pieces bo,ChessBoard cb, int x, int y){
        Map<String,ArrayList<Integer[]>> moves = new HashMap<>();
        moves.put("Defence", new ArrayList<>());
        moves.put("Attack", new ArrayList<>());
        moves.put("Neighbor", new ArrayList<>());
        moves.put("Check", new ArrayList<>());
        int i = 1;
        while((i<=1+(hasMoved?0:1))&&(x-i>=0)){
            if(bo.board[x-i][y]==null){        
                    moves.get("Defence").add(new Integer[]{x-i,y});
                }
            else{
                break;
            }
            i++;
        }
        try{
            if(x-1 >= 0 && y+1<8){
                if(bo.board[x-1][y+1]==null || bo.board[x-1][y+1].team.equals(this.team)){
                    moves.get("Neighbor").add(new Integer[]{x-1,y+1});
                }
                else{
                    if(bo.board[x-1][y+1].getClass().equals(King.class)){
                        moves.get("Check").add(new Integer[]{x-1,y+1});
                    }
                    else{
                        moves.get("Attack").add(new Integer[]{x-1,y+1});
                    }
                }          
            }
        }catch(ArrayIndexOutOfBoundsException e){}
        try{
            if(x-1 >= 0 && y-1>=0){
                if(bo.board[x-1][y-1]==null || bo.board[x-1][y-1].team.equals(this.team)){
                    moves.get("Neighbor").add(new Integer[]{x-1,y-1});
                }
                else{
                    if(bo.board[x-1][y-1].getClass().equals(King.class)){
                        moves.get("Check").add(new Integer[]{x-1,y-1});
                    }
                    else{
                        moves.get("Attack").add(new Integer[]{x-1,y-1});
                    }
                }
            }
        }catch(ArrayIndexOutOfBoundsException e){}
        return moves;
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

    public Map<String,ArrayList<Integer[]>> showPossibleMoves(Pieces bo,ChessBoard cb, int x, int y){
        Map<String,ArrayList<Integer[]>> moves = new HashMap<>();
        moves.put("Defence", new ArrayList<>());
        moves.put("Attack", new ArrayList<>());
        moves.put("Neighbor", new ArrayList<>());
        moves.put("Check", new ArrayList<>());
        for(int i = 2;Math.abs(i)<=2;i--){
            for(int j = 2;Math.abs(j)<=2;j--){
                if(Math.abs(i) == Math.abs(j) || i == 0 || j == 0)continue;
                try{
                    if(bo.board[x+i][y+j]==null){
                        moves.get("Defence").add(new Integer[]{x+i,y+j});
                    }
                    else if(bo.board[x+i][y+j].team.equals(this.team)){
                        moves.get("Neighbor").add(new Integer[]{x+i,y+j});
                    }
                    else{
                        if(bo.board[x+i][y+j].getClass().equals(King.class)){
                            moves.get("Check").add(new Integer[]{x+i,y+j});
                        }
                        else{
                            moves.get("Attack").add(new Integer[]{x+i,y+j});
                        }
                    }
                }catch(Exception e){}
            }
        }
        return moves;
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

    public Map<String,ArrayList<Integer[]>> showPossibleMoves(Pieces bo,ChessBoard cb, int x, int y){
        Map<String,ArrayList<Integer[]>> moves = new HashMap<>();
        moves.put("Defence", new ArrayList<>());
        moves.put("Attack", new ArrayList<>());
        moves.put("Neighbor", new ArrayList<>());
        moves.put("Check", new ArrayList<>());
        moves.put("Restrict", new ArrayList<>());
        ArrayList<Integer[]> hL = new ArrayList<>(), hR = new ArrayList<>(), vU = new ArrayList<>(), vD = new ArrayList<>(), rU = new ArrayList<>(), rD = new ArrayList<>(), lU = new ArrayList<>(), lD = new ArrayList<>();
        boolean isCheckhLeft=false,isCheckhRight = false,isCheckvUp=false,isCheckvDown=false,isCheckrightUp=false,isCheckrightDown=false,isCheckleftUp=false,isCheckleftDown=false;
        boolean CheckvUp=false,CheckvDown=false,CheckhLeft=false,CheckhRight = false;
        boolean vUp=true,vDown=true,hLeft=true,hRight = true;
        boolean CheckleftUp=false,CheckleftDown=false,CheckrightUp=false,CheckrightDown = false;
        boolean leftUp=true,leftDown=true,rightUp=true,rightDown = true;
        
        for(int i = 1;i<=8;i++){
            try{
                if(hRight){
                    if(bo.board[x][y+i]!=null){
                        if(bo.board[x][y+i].team.equals(this.team)){
                            hRight = false;
                            moves.get("Neighbor").add(new Integer[]{x,y+i});
                            moves.get("Defence").addAll(hR);
                            hR.clear();
                        }
                        else{
                            if(bo.board[x][y+i].getClass().equals(King.class)){
                                if(!CheckhRight){
                                    moves.get("Check").add(new Integer[]{x,y+i});
                                    moves.get("Check").addAll(hR);
                                    hR.clear();
                                    isCheckhRight = true;
                                }
                                else{
                                    hRight = false;
                                    for(int j = 1;j<8;j++){
                                        String[] keys = new String[]{"Defence", "Attack"};
                                        boolean found = false;
                                        for (String key : keys) {
                                            for (Integer[] arr : moves.get(key)) {
                                                if(Arrays.equals(arr, new Integer[]{x,y+i-j})) {
                                                    moves.get("Restrict").add(new Integer[]{x,y+i-j});
                                                    found = true;
                                                    break;
                                                }
                                            }
                                            if(found) break;    
                                        }        
                                    }
                                }
                            }
                            else{
                                if(!CheckhRight){
                                    CheckhRight = true;
                                    moves.get("Attack").add(new Integer[]{x,y+i});
                                    moves.get("Defence").addAll(hR);
                                }
                                else{
                                    if(isCheckhRight){
                                    moves.get("Restrict").addAll(hR);
                                    }
                                    hRight = false;
                                }
                                hR.clear();
                            }      
                        }
                    }
                    else{
                        hR.add(new Integer[]{x,y+i});
                    }
                }
            }catch(Exception e){
                if(isCheckhRight){
                    moves.get("Restrict").addAll(hR);
                }
                else if(!CheckhRight){
                    moves.get("Defence").addAll(hR);
                }
                hR.clear();
                hRight=false;
            }
            try{
                if(hLeft){
                    if(bo.board[x][y-i]!=null){
                        if(bo.board[x][y-i].team.equals(this.team)){
                            hLeft = false;
                            moves.get("Neighbor").add(new Integer[]{x,y-i});
                            moves.get("Defence").addAll(hL);
                            hL.clear();
                        }
                        else{
                            if(bo.board[x][y-i].getClass().equals(King.class)){
                                if(!CheckhLeft){
                                    moves.get("Check").add(new Integer[]{x,y-i});
                                    moves.get("Check").addAll(hL);
                                    hL.clear();
                                    isCheckhLeft = true;
                                }
                                else{
                                    hLeft = false;
                                    for(int j = 1;j<8;j++){
                                        String[] keys = new String[]{"Defence", "Attack"};
                                        boolean found = false;
                                        for (String key : keys) {
                                            for (Integer[] arr : moves.get(key)) {
                                                if(Arrays.equals(arr, new Integer[]{x,y-i+j})) {
                                                    moves.get("Restrict").add(new Integer[]{x,y-i+j});
                                                    found = true;
                                                    break;
                                                }
                                            }
                                            if(found) break;    
                                        }        
                                    }
                                }
                            }
                            else{
                                if(!CheckhLeft){
                                    CheckhLeft = true;
                                    moves.get("Attack").add(new Integer[]{x,y-i});
                                    moves.get("Defence").addAll(hL);}
                                else{
                                    if(isCheckhLeft){
                                        moves.get("Restrict").addAll(hL);}
                                    hLeft = false;
                                } 
                                hL.clear();
                            }      
                        }
                    }
                    else{
                        hL.add(new Integer[]{x,y-i});
                    }
                }
            }catch(Exception e){
                if(isCheckhLeft){
                    moves.get("Restrict").addAll(hL);
                }
                else if(!CheckhLeft){
                    moves.get("Defence").addAll(hL);
                }
                hL.clear();
                hLeft=false;
            }
            try{
                if(vUp){
                    if(bo.board[x+i][y]!=null){
                        if(bo.board[x+i][y].team.equals(this.team)){
                            vUp = false;
                            moves.get("Neighbor").add(new Integer[]{x+i,y});
                            moves.get("Defence").addAll(vU);
                            vU.clear();
                        }
                        else{
                            if(bo.board[x+i][y].getClass().equals(King.class)){
                                if(!CheckvUp){
                                    moves.get("Check").add(new Integer[]{x+i,y});
                                    moves.get("Check").addAll(vU);
                                    vU.clear();
                                    isCheckvUp = true;
                                }
                                else{
                                    vUp = false;
                                    for(int j = 1;j<8;j++){
                                        String[] keys = new String[]{"Defence", "Attack"};
                                        boolean found = false;
                                        for (String key : keys) {
                                            for (Integer[] arr : moves.get(key)) {
                                                if(Arrays.equals(arr, new Integer[]{x+i-j,y})) {
                                                    moves.get("Restrict").add(new Integer[]{x+i-j,y});
                                                    found = true;
                                                    break;
                                                }
                                            }
                                            if(found) break;    
                                        }        
                                    }
                                }
                            }
                            else{
                                if(!CheckvUp){
                                    CheckvUp = true;
                                    moves.get("Attack").add(new Integer[]{x+i,y});
                                    moves.get("Defence").addAll(vU);}
                                else{
                                    if(isCheckvUp){
                                        moves.get("Restrict").addAll(vU);
                                    }
                                    vUp = false;
                                }
                                vU.clear();
                            }      
                        }
                    }
                    else{
                        vU.add(new Integer[]{x+i,y});
                    }
                }
            }catch(Exception e){
                if(isCheckvUp){
                    moves.get("Restrict").addAll(vU);
                }
                else if(!CheckvUp){
                    moves.get("Defence").addAll(vU);
                }
                vU.clear();
                vUp=false;
            }
            try{
                if(vDown){
                    if(bo.board[x-i][y]!=null){
                        if(bo.board[x-i][y].team.equals(this.team)){
                            vDown = false;
                            moves.get("Neighbor").add(new Integer[]{x-i,y});
                            moves.get("Defence").addAll(vD);
                            vD.clear();
                        }
                        else{
                            if(bo.board[x-i][y].getClass().equals(King.class)){
                                if(!CheckvDown){
                                    moves.get("Check").add(new Integer[]{x-i,y});
                                    moves.get("Check").addAll(vD);
                                    vD.clear();
                                    isCheckvDown = true;
                                }
                                else{
                                    vDown = false;
                                    for(int j = 1;j<8;j++){
                                        String[] keys = new String[]{"Defence", "Attack"};
                                        boolean found = false;
                                        for (String key : keys) {
                                            for (Integer[] arr : moves.get(key)) {
                                                if(Arrays.equals(arr, new Integer[]{x-i+j,y})) {
                                                    moves.get("Restrict").add(new Integer[]{x-i+j,y});
                                                    found = true;
                                                    break;
                                                }
                                            }
                                            if(found) break;    
                                        }        
                                    }
                                }
                            }
                            else{
                                if(!CheckvDown){
                                    CheckvDown = true;
                                    moves.get("Attack").add(new Integer[]{x-i,y});
                                    moves.get("Defence").addAll(vD);}
                                else{
                                    if(isCheckvDown){
                                        moves.get("Restrict").addAll(vD);}
                                    vDown = false;
                                    }
                                    vD.clear();
                            }      
                        }
                    }
                    else{
                        vD.add(new Integer[]{x-i,y});
                    }
                }
            }catch(Exception e){
                if(isCheckvDown){
                    moves.get("Restrict").addAll(vD);
                }
                else if(!CheckvDown){
                    moves.get("Defence").addAll(vD);
                }
                vD.clear();
                vDown=false;
            }
            try{
                if(rightUp){
                    if(bo.board[x+i][y+i]!=null){
                        if(bo.board[x+i][y+i].team.equals(this.team)){
                            rightUp = false;
                            moves.get("Neighbor").add(new Integer[]{x+i,y+i});
                            moves.get("Defence").addAll(rU);
                            rU.clear();
                        }
                        else{
                            if(bo.board[x+i][y+i].getClass().equals(King.class)){
                                if(!CheckrightUp){
                                    moves.get("Check").add(new Integer[]{x+i,y+i});
                                    moves.get("Check").addAll(rU);
                                    rU.clear();
                                    isCheckrightUp = true;
                                }
                                else{
                                    rightUp = false;
                                    for(int j = 1;j<8;j++){
                                        String[] keys = new String[]{"Defence", "Attack"};
                                        boolean found = false;
                                        for (String key : keys) {
                                            for (Integer[] arr : moves.get(key)) {
                                                if(Arrays.equals(arr, new Integer[]{x+i-j,y+i-j})) {
                                                    moves.get("Restrict").add(new Integer[]{x+i-j,y+i-j});
                                                    found = true;
                                                    break;
                                                }
                                            }
                                            if(found) break;    
                                        }        
                                    }
                                }
                            }
                            else{
                                if(!CheckrightUp){
                                    CheckrightUp = true;
                                    moves.get("Attack").add(new Integer[]{x+i,y+i});
                                    moves.get("Defence").addAll(rU);}
                                else{
                                    if(isCheckrightUp){
                                        moves.get("Restrict").addAll(rU);
                                    }
                                    rightUp = false;
                                }
                                rU.clear();
                            }      
                        }
                    }
                    else{
                        rU.add(new Integer[]{x+i,y+i});
                    }
                }
            }catch(Exception e){
                if(isCheckrightUp){
                    moves.get("Restrict").addAll(rU);
                }
                else if(!CheckrightUp){
                    moves.get("Defence").addAll(rU);
                }
                rU.clear();
                rightUp=false;
            }
            try{
                if(leftDown){
                    if(bo.board[x+i][y-i]!=null){
                        if(bo.board[x+i][y-i].team.equals(this.team)){
                            leftDown = false;
                            moves.get("Neighbor").add(new Integer[]{x+i,y-i});
                            moves.get("Defence").addAll(lD);
                            lD.clear();
                        }
                        else{
                            if(bo.board[x+i][y-i].getClass().equals(King.class)){
                                if(!CheckleftDown){
                                    moves.get("Check").add(new Integer[]{x+i,y-i});
                                    moves.get("Check").addAll(lD);
                                    lD.clear();
                                    isCheckleftDown = true;
                                }
                                else{
                                    leftDown = false;
                                    for(int j = 1;j<8;j++){
                                        String[] keys = new String[]{"Defence", "Attack"};
                                        boolean found = false;
                                        for (String key : keys) {
                                            for (Integer[] arr : moves.get(key)) {
                                                if(Arrays.equals(arr, new Integer[]{x+i-j,y-i+j})) {
                                                    moves.get("Restrict").add(new Integer[]{x+i-j,y-i+j});
                                                    found = true;
                                                    break;
                                                }
                                            }
                                            if(found) break;    
                                        }        
                                    }
                                }
                            }
                            else{
                                if(!CheckleftDown){
                                    CheckleftDown = true;
                                    moves.get("Attack").add(new Integer[]{x+i,y-i});
                                    moves.get("Defence").addAll(lD);
                                }
                                else{
                                    if(isCheckleftDown){
                                        moves.get("Restrict").addAll(lD);
                                    }
                                    leftDown = false;
                                }
                                lD.clear();
                            }      
                        }
                    }
                    else{
                        lD.add(new Integer[]{x+i,y-i});
                    }
                }
            }catch(Exception e){
                if(isCheckleftDown){
                    moves.get("Restrict").addAll(lD);
                }
                else if(!CheckleftDown){
                    moves.get("Defence").addAll(lD);
                }
                lD.clear();
                leftDown=false;
            }
            try{
                if(leftUp){
                    if(bo.board[x-i][y+i]!=null){
                        if(bo.board[x-i][y+i].team.equals(this.team)){
                            leftUp = false;
                            moves.get("Neighbor").add(new Integer[]{x-i,y+i});
                            moves.get("Defence").addAll(lU);
                            lU.clear();
                        }
                        else{
                            if(bo.board[x-i][y+i].getClass().equals(King.class)){
                                if(!CheckleftUp){
                                    moves.get("Check").add(new Integer[]{x-i,y+i});
                                    moves.get("Check").addAll(lU);
                                    lU.clear();
                                    isCheckleftUp = true;
                                }
                                else{
                                    leftUp = false;
                                    for(int j = 1;j<8;j++){
                                        String[] keys = new String[]{"Defence", "Attack"};
                                        boolean found = false;
                                        for (String key : keys) {
                                            for (Integer[] arr : moves.get(key)) {
                                                if(Arrays.equals(arr, new Integer[]{x-i+j,y+i-j})) {
                                                    moves.get("Restrict").add(new Integer[]{x-i+j,y+i-j});
                                                    found = true;
                                                    break;
                                                }
                                            }
                                            if(found) break;    
                                        }        
                                    }
                                }
                            }
                            else{
                                if(!CheckleftUp){
                                    CheckleftUp = true;
                                    moves.get("Attack").add(new Integer[]{x-i,y+i});
                                    moves.get("Defence").addAll(lU);
                                }
                                else{
                                    if(isCheckleftUp){
                                        moves.get("Restrict").addAll(lU);
                                    }
                                    leftUp = false;
                                }
                                lU.clear();
                            }      
                        }
                    }
                    else{
                        lU.add(new Integer[]{x-i,y+i});
                    }
                }
            }catch(Exception e){
                if(isCheckleftUp){
                    moves.get("Restrict").addAll(lU);
                }
                else if(!CheckleftUp){
                    moves.get("Defence").addAll(lU);
                }
                lU.clear();
                leftUp=false;
            }
            try{
                if(rightDown){
                    if(bo.board[x-i][y-i]!=null){
                        if(bo.board[x-i][y-i].team.equals(this.team)){
                            rightDown = false;
                            moves.get("Neighbor").add(new Integer[]{x-i,y-i});
                            moves.get("Defence").addAll(rD);
                            rD.clear();
                        }
                        else{
                            if(bo.board[x-i][y-i].getClass().equals(King.class)){
                                if(!CheckrightDown){
                                    moves.get("Check").add(new Integer[]{x-i,y-i});
                                    moves.get("Check").addAll(rD);
                                    rD.clear();
                                    isCheckrightDown = true;
                                }
                                else{
                                    rightDown = false;
                                    for(int j = 1;j<8;j++){
                                        String[] keys = new String[]{"Defence", "Attack"};
                                        boolean found = false;
                                        for (String key : keys) {
                                            for (Integer[] arr : moves.get(key)) {
                                                if(Arrays.equals(arr, new Integer[]{x-i+j,y-i+j})) {
                                                    moves.get("Restrict").add(new Integer[]{x-i+j,y-i+j});
                                                    found = true;
                                                    break;
                                                }
                                            }
                                            if(found) break;    
                                        }        
                                    }
                                }
                            }
                            else{
                                if(!CheckrightDown){
                                    CheckrightDown = true;
                                    moves.get("Attack").add(new Integer[]{x-i,y-i});
                                    moves.get("Defence").addAll(rD);}
                                else{
                                    if(isCheckrightDown){
                                        moves.get("Restrict").addAll(rD);
                                    }
                                    rightDown = false;
                                }
                                rD.clear();
                            }      
                        }
                    }
                    else{
                        rD.add(new Integer[]{x-i,y-i});
                    }
                }
            }catch(Exception e){
                if(isCheckrightDown){
                    moves.get("Restrict").addAll(rD);
                }
                else if(!CheckrightDown){
                    moves.get("Defence").addAll(rD);
                }
                rD.clear();
                rightDown=false;
            }
        } 
        return moves;
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

    public Map<String,ArrayList<Integer[]>> showPossibleMoves(Pieces bo,ChessBoard cb, int x, int y){
        Map<String,ArrayList<Integer[]>> moves = new HashMap<>();
        moves.put("Defence", new ArrayList<>());
        moves.put("Attack", new ArrayList<>());
        moves.put("Neighbor", new ArrayList<>());
        moves.put("Check", new ArrayList<>());
        moves.put("Restrict", new ArrayList<>());
        ArrayList<Integer[]> hL = new ArrayList<>(), hR = new ArrayList<>(), vU = new ArrayList<>(), vD = new ArrayList<>();
        boolean isCheckhLeft=false,isCheckhRight = false,isCheckvUp=false,isCheckvDown=false;
        boolean CheckvUp=false,CheckvDown=false,CheckhLeft=false,CheckhRight = false;
        boolean vUp=true,vDown=true,hLeft=true,hRight = true;
        for(int i = 1;i<=8;i++){
            try{
                if(hRight){
                    if(bo.board[x][y+i]!=null){
                        if(bo.board[x][y+i].team.equals(this.team)){
                            hRight = false;
                            moves.get("Neighbor").add(new Integer[]{x,y+i});
                            moves.get("Defence").addAll(hR);
                            hR.clear();
                        }
                        else{
                            if(bo.board[x][y+i].getClass().equals(King.class)){
                                if(!CheckhRight){
                                    moves.get("Check").add(new Integer[]{x,y+i});
                                    moves.get("Check").addAll(hR);
                                    hR.clear();
                                    isCheckhRight = true;
                                }
                                else{
                                    hRight = false;
                                    for(int j = 1;j<8;j++){
                                        String[] keys = new String[]{"Defence", "Attack"};
                                        boolean found = false;
                                        for (String key : keys) {
                                            for (Integer[] arr : moves.get(key)) {
                                                if(Arrays.equals(arr, new Integer[]{x,y+i-j})) {
                                                    moves.get("Restrict").add(new Integer[]{x,y+i-j});
                                                    found = true;
                                                    break;
                                                }
                                            }
                                            if(found) break;    
                                        }        
                                    }
                                }
                            }
                            else{
                                if(!CheckhRight){
                                    CheckhRight = true;
                                    moves.get("Attack").add(new Integer[]{x,y+i});
                                    moves.get("Defence").addAll(hR);
                                }
                                else{
                                    if(isCheckhRight){
                                    moves.get("Restrict").addAll(hR);
                                    }
                                    hRight = false;
                                }
                                hR.clear();
                            }      
                        }
                    }
                    else{
                        hR.add(new Integer[]{x,y+i});
                    }
                }
            }catch(Exception e){
                if(isCheckhRight){
                    moves.get("Restrict").addAll(hR);
                }
                else if(!CheckhRight){
                    moves.get("Defence").addAll(hR);
                }
                hR.clear();
                hRight=false;
            }
            try{
                if(hLeft){
                    if(bo.board[x][y-i]!=null){
                        if(bo.board[x][y-i].team.equals(this.team)){
                            hLeft = false;
                            moves.get("Neighbor").add(new Integer[]{x,y-i});
                            moves.get("Defence").addAll(hL);
                            hL.clear();
                        }
                        else{
                            if(bo.board[x][y-i].getClass().equals(King.class)){
                                if(!CheckhLeft){
                                    moves.get("Check").add(new Integer[]{x,y-i});
                                    moves.get("Check").addAll(hL);
                                    hL.clear();
                                    isCheckhLeft = true;
                                }
                                else{
                                    hLeft = false;
                                    for(int j = 1;j<8;j++){
                                        String[] keys = new String[]{"Defence", "Attack"};
                                        boolean found = false;
                                        for (String key : keys) {
                                            for (Integer[] arr : moves.get(key)) {
                                                if(Arrays.equals(arr, new Integer[]{x,y-i+j})) {
                                                    moves.get("Restrict").add(new Integer[]{x,y-i+j});
                                                    found = true;
                                                    break;
                                                }
                                            }
                                            if(found) break;    
                                        }        
                                    }
                                }
                            }
                            else{
                                if(!CheckhLeft){
                                    CheckhLeft = true;
                                    moves.get("Attack").add(new Integer[]{x,y-i});
                                    moves.get("Defence").addAll(hL);}
                                else{
                                    if(isCheckhLeft){
                                        moves.get("Restrict").addAll(hL);}
                                    hLeft = false;
                                } 
                                hL.clear();
                            }      
                        }
                    }
                    else{
                        hL.add(new Integer[]{x,y-i});
                    }
                }
            }catch(Exception e){
                if(isCheckhLeft){
                    moves.get("Restrict").addAll(hL);
                }
                else if(!CheckhLeft){
                    moves.get("Defence").addAll(hL);
                }
                hL.clear();
                hLeft=false;
            }
            try{
                if(vUp){
                    if(bo.board[x+i][y]!=null){
                        if(bo.board[x+i][y].team.equals(this.team)){
                            vUp = false;
                            moves.get("Neighbor").add(new Integer[]{x+i,y});
                            moves.get("Defence").addAll(vU);
                            vU.clear();
                        }
                        else{
                            if(bo.board[x+i][y].getClass().equals(King.class)){
                                if(!CheckvUp){
                                    moves.get("Check").add(new Integer[]{x+i,y});
                                    moves.get("Check").addAll(vU);
                                    vU.clear();
                                    isCheckvUp = true;
                                }
                                else{
                                    vUp = false;
                                    for(int j = 1;j<8;j++){
                                        String[] keys = new String[]{"Defence", "Attack"};
                                        boolean found = false;
                                        for (String key : keys) {
                                            for (Integer[] arr : moves.get(key)) {
                                                if(Arrays.equals(arr, new Integer[]{x+i-j,y})) {
                                                    moves.get("Restrict").add(new Integer[]{x+i-j,y});
                                                    found = true;
                                                    break;
                                                }
                                            }
                                            if(found) break;    
                                        }        
                                    }
                                }
                            }
                            else{
                                if(!CheckvUp){
                                    CheckvUp = true;
                                    moves.get("Attack").add(new Integer[]{x+i,y});
                                    moves.get("Defence").addAll(vU);}
                                else{
                                    if(isCheckvUp){
                                        moves.get("Restrict").addAll(vU);
                                    }
                                    vUp = false;
                                }
                                vU.clear();
                            }      
                        }
                    }
                    else{
                        vU.add(new Integer[]{x+i,y});
                    }
                }
            }catch(Exception e){
                if(isCheckvUp){
                    moves.get("Restrict").addAll(vU);
                }
                else if(!CheckvUp){
                    moves.get("Defence").addAll(vU);
                }
                vU.clear();
                vUp=false;
            }
            try{
                if(vDown){
                    if(bo.board[x-i][y]!=null){
                        if(bo.board[x-i][y].team.equals(this.team)){
                            vDown = false;
                            moves.get("Neighbor").add(new Integer[]{x-i,y});
                            moves.get("Defence").addAll(vD);
                            vD.clear();
                        }
                        else{
                            if(bo.board[x-i][y].getClass().equals(King.class)){
                                if(!CheckvDown){
                                    moves.get("Check").add(new Integer[]{x-i,y});
                                    moves.get("Check").addAll(vD);
                                    vD.clear();
                                    isCheckvDown = true;
                                }
                                else{
                                    vDown = false;
                                    for(int j = 1;j<8;j++){
                                        String[] keys = new String[]{"Defence", "Attack"};
                                        boolean found = false;
                                        for (String key : keys) {
                                            for (Integer[] arr : moves.get(key)) {
                                                if(Arrays.equals(arr, new Integer[]{x-i+j,y})) {
                                                    moves.get("Restrict").add(new Integer[]{x-i+j,y});
                                                    found = true;
                                                    break;
                                                }
                                            }
                                            if(found) break;    
                                        }        
                                    }
                                }
                            }
                            else{
                                if(!CheckvDown){
                                    CheckvDown = true;
                                    moves.get("Attack").add(new Integer[]{x-i,y});
                                    moves.get("Defence").addAll(vD);}
                                else{
                                    if(isCheckvDown){
                                        moves.get("Restrict").addAll(vD);}
                                    vDown = false;
                                    }
                                    vD.clear();
                            }      
                        }
                    }
                    else{
                        vD.add(new Integer[]{x-i,y});
                    }
                }
            }catch(Exception e){
                if(isCheckvDown){
                    moves.get("Restrict").addAll(vD);
                }
                else if(!CheckvDown){
                    moves.get("Defence").addAll(vD);
                }
                vD.clear();
                vDown=false;
            }
        }
        return moves;
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

    public Map<String,ArrayList<Integer[]>> showPossibleMoves(Pieces bo,ChessBoard cb, int x, int y){
        Map<String,ArrayList<Integer[]>> moves = new HashMap<>();
        moves.put("Defence", new ArrayList<>());
        moves.put("Attack", new ArrayList<>());
        moves.put("Neighbor", new ArrayList<>());
        moves.put("Check", new ArrayList<>());
        moves.put("Restrict", new ArrayList<>());
        ArrayList<Integer[]> rU = new ArrayList<>(), rD = new ArrayList<>(), lU = new ArrayList<>(), lD = new ArrayList<>();
        boolean isCheckrightUp=false,isCheckrightDown=false,isCheckleftUp=false,isCheckleftDown=false;
        boolean CheckleftUp=false,CheckleftDown=false,CheckrightUp=false,CheckrightDown = false;
        boolean leftUp=true,leftDown=true,rightUp=true,rightDown = true;
        for(int i = 1;i<8;i++){
            try{
                if(rightUp){
                    if(bo.board[x+i][y+i]!=null){
                        if(bo.board[x+i][y+i].team.equals(this.team)){
                            rightUp = false;
                            moves.get("Neighbor").add(new Integer[]{x+i,y+i});
                            moves.get("Defence").addAll(rU);
                            rU.clear();
                        }
                        else{
                            if(bo.board[x+i][y+i].getClass().equals(King.class)){
                                if(!CheckrightUp){
                                    moves.get("Check").add(new Integer[]{x+i,y+i});
                                    moves.get("Check").addAll(rU);
                                    rU.clear();
                                    isCheckrightUp = true;
                                }
                                else{
                                    rightUp = false;
                                    for(int j = 1;j<8;j++){
                                        String[] keys = new String[]{"Defence", "Attack"};
                                        boolean found = false;
                                        for (String key : keys) {
                                            for (Integer[] arr : moves.get(key)) {
                                                if(Arrays.equals(arr, new Integer[]{x+i-j,y+i-j})) {
                                                    moves.get("Restrict").add(new Integer[]{x+i-j,y+i-j});
                                                    found = true;
                                                    break;
                                                }
                                            }
                                            if(found) break;    
                                        }        
                                    }
                                }
                            }
                            else{
                                if(!CheckrightUp){
                                    CheckrightUp = true;
                                    moves.get("Attack").add(new Integer[]{x+i,y+i});
                                    moves.get("Defence").addAll(rU);}
                                else{
                                    if(isCheckrightUp){
                                        moves.get("Restrict").addAll(rU);
                                    }
                                    rightUp = false;
                                }
                                rU.clear();
                            }      
                        }
                    }
                    else{
                        rU.add(new Integer[]{x+i,y+i});
                    }
                }
            }catch(Exception e){
                if(isCheckrightUp){
                    moves.get("Restrict").addAll(rU);
                }
                else if(!CheckrightUp){
                    moves.get("Defence").addAll(rU);
                }
                rU.clear();
                rightUp=false;
            }
            try{
                if(leftDown){
                    if(bo.board[x+i][y-i]!=null){
                        if(bo.board[x+i][y-i].team.equals(this.team)){
                            leftDown = false;
                            moves.get("Neighbor").add(new Integer[]{x+i,y-i});
                            moves.get("Defence").addAll(lD);
                            lD.clear();
                        }
                        else{
                            if(bo.board[x+i][y-i].getClass().equals(King.class)){
                                if(!CheckleftDown){
                                    moves.get("Check").add(new Integer[]{x+i,y-i});
                                    moves.get("Check").addAll(lD);
                                    lD.clear();
                                    isCheckleftDown = true;
                                }
                                else{
                                    leftDown = false;
                                    for(int j = 1;j<8;j++){
                                        String[] keys = new String[]{"Defence", "Attack"};
                                        boolean found = false;
                                        for (String key : keys) {
                                            for (Integer[] arr : moves.get(key)) {
                                                if(Arrays.equals(arr, new Integer[]{x+i-j,y-i+j})) {
                                                    moves.get("Restrict").add(new Integer[]{x+i-j,y-i+j});
                                                    found = true;
                                                    break;
                                                }
                                            }
                                            if(found) break;    
                                        }        
                                    }
                                }
                            }
                            else{
                                if(!CheckleftDown){
                                    CheckleftDown = true;
                                    moves.get("Attack").add(new Integer[]{x+i,y-i});
                                    moves.get("Defence").addAll(lD);
                                }
                                else{
                                    if(isCheckleftDown){
                                        moves.get("Restrict").addAll(lD);
                                    }
                                    leftDown = false;
                                }
                                lD.clear();
                            }      
                        }
                    }
                    else{
                        lD.add(new Integer[]{x+i,y-i});
                    }
                }
            }catch(Exception e){
                if(isCheckleftDown){
                    moves.get("Restrict").addAll(lD);
                }
                else if(!CheckleftDown){
                    moves.get("Defence").addAll(lD);
                }
                lD.clear();
                leftDown=false;
            }
            try{
                if(leftUp){
                    if(bo.board[x-i][y+i]!=null){
                        if(bo.board[x-i][y+i].team.equals(this.team)){
                            leftUp = false;
                            moves.get("Neighbor").add(new Integer[]{x-i,y+i});
                            moves.get("Defence").addAll(lU);
                            lU.clear();
                        }
                        else{
                            if(bo.board[x-i][y+i].getClass().equals(King.class)){
                                if(!CheckleftUp){
                                    moves.get("Check").add(new Integer[]{x-i,y+i});
                                    moves.get("Check").addAll(lU);
                                    lU.clear();
                                    isCheckleftUp = true;
                                }
                                else{
                                    leftUp = false;
                                    for(int j = 1;j<8;j++){
                                        String[] keys = new String[]{"Defence", "Attack"};
                                        boolean found = false;
                                        for (String key : keys) {
                                            for (Integer[] arr : moves.get(key)) {
                                                if(Arrays.equals(arr, new Integer[]{x-i+j,y+i-j})) {
                                                    moves.get("Restrict").add(new Integer[]{x-i+j,y+i-j});
                                                    found = true;
                                                    break;
                                                }
                                            }
                                            if(found) break;    
                                        }        
                                    }
                                }
                            }
                            else{
                                if(!CheckleftUp){
                                    CheckleftUp = true;
                                    moves.get("Attack").add(new Integer[]{x-i,y+i});
                                    moves.get("Defence").addAll(lU);
                                }
                                else{
                                    if(isCheckleftUp){
                                        moves.get("Restrict").addAll(lU);
                                    }
                                    leftUp = false;
                                }
                                lU.clear();
                            }      
                        }
                    }
                    else{
                        lU.add(new Integer[]{x-i,y+i});
                    }
                }
            }catch(Exception e){
                if(isCheckleftUp){
                    moves.get("Restrict").addAll(lU);
                }
                else if(!CheckleftUp){
                    moves.get("Defence").addAll(lU);
                }
                lU.clear();
                leftUp=false;
            }
            try{
                if(rightDown){
                    if(bo.board[x-i][y-i]!=null){
                        if(bo.board[x-i][y-i].team.equals(this.team)){
                            rightDown = false;
                            moves.get("Neighbor").add(new Integer[]{x-i,y-i});
                            moves.get("Defence").addAll(rD);
                            rD.clear();
                        }
                        else{
                            if(bo.board[x-i][y-i].getClass().equals(King.class)){
                                if(!CheckrightDown){
                                    moves.get("Check").add(new Integer[]{x-i,y-i});
                                    moves.get("Check").addAll(rD);
                                    rD.clear();
                                    isCheckrightDown = true;
                                }
                                else{
                                    rightDown = false;
                                    for(int j = 1;j<8;j++){
                                        String[] keys = new String[]{"Defence", "Attack"};
                                        boolean found = false;
                                        for (String key : keys) {
                                            for (Integer[] arr : moves.get(key)) {
                                                if(Arrays.equals(arr, new Integer[]{x-i+j,y-i+j})) {
                                                    moves.get("Restrict").add(new Integer[]{x-i+j,y-i+j});
                                                    found = true;
                                                    break;
                                                }
                                            }
                                            if(found) break;    
                                        }        
                                    }
                                }
                            }
                            else{
                                if(!CheckrightDown){
                                    CheckrightDown = true;
                                    moves.get("Attack").add(new Integer[]{x-i,y-i});
                                    moves.get("Defence").addAll(rD);}
                                else{
                                    if(isCheckrightDown){
                                        moves.get("Restrict").addAll(rD);
                                    }
                                    rightDown = false;
                                }
                                rD.clear();
                            }      
                        }
                    }
                    else{
                        rD.add(new Integer[]{x-i,y-i});
                    }
                }
            }catch(Exception e){
                if(isCheckrightDown){
                    moves.get("Restrict").addAll(rD);
                }
                else if(!CheckrightDown){
                    moves.get("Defence").addAll(rD);
                }
                rD.clear();
                rightDown=false;
            }
        }
        return moves;
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

}