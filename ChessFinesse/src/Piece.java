package ChessFinesse.src;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;


abstract class Piece implements PictureHandler {
    boolean hasMoved;
    protected final String team;
    protected ImageIcon pic;

    public static Piece createPawn(String t){
        return new Pawn(t,false);
    }

    public static Piece createKing(String t){
        return new King(t,false);
    }

    public static Piece createQueen(String t){
        return new Queen(t,false);
    }

    public static Piece createBishop(String t){
        return new Bishop(t,false);
    }

    public static Piece createKnight(String t){
        return new Knight(t,false);
    }

    public static Piece createRook(String t){
        return new Rook(t,false);
    }

    Piece(String t,String p,boolean M){
        team = t;
        pic = PictureHandler.resizeIcon(p);
        hasMoved = M;
    }

    public abstract Map<String,LinkedList<Integer[]>> showPossibleMoves(ChessBoard bo, int x, int y);

    public abstract LinkedList<Integer[]> savingMoves(ChessBoard bo, HashMap<String,LinkedList<Integer[]>> ref, int x, int y);

    public void promote(ChessBoard pi,int x,int y){}

    public abstract String getPrefix();

    public void update(){
        hasMoved = true;
    }
}

class King extends Piece{
    private final String pre = "K";
    public King(String t, boolean m){
        super(t, decideTeam(t),m);   
    }

    public String getPrefix(){
        return pre;
    }

    private boolean[] castling(ChessBoard board,Set<LinkedList<Integer>> ref, int x, int y) {
        boolean queenSide = false, kingSide = false;
        if(!hasMoved && !board.check){
            for(int i = 1;y-i>=0;i++){
                if((y-i == 0 && board.board[x][y-i] != null && board.board[x][y-i].getClass().equals(Rook.class) && !board.board[x][y-i].hasMoved) || (y-i != 0 && board.board[x][y-i] == null && ((y-i == 1 && board.turn.equals("White")) || !ref.contains(Arrays.asList(x, y-i))))){
                    queenSide = true;
                }
                else{
                    queenSide = false;
                    break;
                }
            }
            for(int i = 1;y+i<8;i++){
                if((y+i == 7 && board.board[x][y+i] != null && board.board[x][y+i].getClass().equals(Rook.class) && !board.board[x][y+i].hasMoved) || (y+i != 7 && board.board[x][y+i] == null && ((y+i == 6 && board.turn.equals("Black")) || !ref.contains(Arrays.asList(x, y+i))))){
                    kingSide = true;
                }
                else{
                    kingSide = false;
                    break;
                }
            }
        }
        return new boolean[]{queenSide, kingSide};
    }

    private boolean foundIt(Set<Integer[]> save, int x ,int y){
        for(Integer[]i:save){
            if(Arrays.equals(i,new Integer[]{x,y}))
            return true;
        }
        return false;
    }

    private Set<LinkedList<Integer>> coordinateOfChecking(Set<Integer[]> a) {
        Set<LinkedList<Integer>> listSet = new HashSet<>();
        for (Integer[] i : a) {
                LinkedList<Integer> list = new LinkedList<>(Arrays.asList(i));
                listSet.add(list);
        }
        return listSet;
    }
    
    public Map<String,LinkedList<Integer[]>> showPossibleMoves(ChessBoard bo, int x, int y){
        Map<String,LinkedList<Integer[]>> moves = new HashMap<>();
        moves.put("Defence", new LinkedList<>());
        moves.put("Attack", new LinkedList<>());
        moves.put("Neighbor", new LinkedList<>());
        if(team.equals(bo.turn())){
            HashMap<Integer[],LinkedList<Integer[]>> s1 = bo.SearchForMoves();
            Set<Integer[]> save = new HashSet<>();
            for (LinkedList<Integer[]> list : s1.values()) {
                for(Integer[] l:list){
                     l[0] = 7 - l[0];
                    l[1] = 7 - l[1];
                    save.add(new Integer[]{l[0],l[1]});
                }
            }
            for(int i = 1;Math.abs(i)<2;i--){
                for(int j = 1;Math.abs(j)<2;j--){
                    if(i == 0 && j == 0)continue;
                    try{
                        if(!foundIt(save, x+i, y+j)){
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
            if(!bo.check){
                boolean[] castling = castling(bo, coordinateOfChecking(save), x, y);
                if(this.team.equals("White")){
                    if(castling[0]){
                        moves.put("QueenSide", new LinkedList<>());
                        moves.get("QueenSide").add(new Integer[]{x, y-2});
                    }
                    if(castling[1]){
                        moves.put("KingSide", new LinkedList<>());
                        moves.get("KingSide").add(new Integer[]{x, y+2});
                    }
                }
                else{
                    if(castling[0]){
                        moves.put("KingSide", new LinkedList<>());
                        moves.get("KingSide").add(new Integer[]{x, y-2});
                    }
                    if(castling[1]){
                        moves.put("QueenSide", new LinkedList<>());
                        moves.get("QueenSide").add(new Integer[]{x, y+2});
                    }
                }
            }
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

    public LinkedList<Integer[]> savingMoves(ChessBoard bo, HashMap<String,LinkedList<Integer[]>> ref , int x, int y){
        Map<String,LinkedList<Integer[]>> moves = showPossibleMoves(bo, x, y);
        LinkedList<Integer[]> saving = new LinkedList<>();
        saving.addAll(moves.get("Defence"));
        saving.addAll(moves.get("Attack"));
        return saving;
    }

    static String decideTeam(String team){
        if(team.equals("White"))
        return "ChessFinesse//Pics&Vids//Pics//w_king_1x.png";
        return "ChessFinesse//Pics&Vids//Pics//b_king_1x.png";
    }
}

class Pawn extends Piece{
    private final String pre = "";

    public Pawn(String t,boolean m){
        super(t, decideTeam(t),m);   
    }
    
    public String getPrefix(){
        return pre;
    }
     
    static String decideTeam(String team){
        if(team.equals("White"))
        return "ChessFinesse//Pics&Vids//Pics//w_pawn_1x.png";
        return "ChessFinesse//Pics&Vids//Pics//b_pawn_1x.png";
    }

    private boolean leftEnpassant(ChessBoard bo, int x, int y){
        if(bo.board[x-1][y-1]==null && bo.board[x][y-1]!=null && bo.board[x][y-1] instanceof Pawn && !bo.board[x][y-1].team.equals(this.team)){
            int[] last = bo.records.decodeNotation(bo.notTurn());
            if(last != null && bo.getButton(last[2], last[3]) instanceof Pawn){
                if(last[2] == (x) && last[3] == y-1 && last[2]-last[0]==2){
                    return true;
                }
            }
        }
        return false;
    }

    private boolean rightEnpassant(ChessBoard bo, int x, int y){
        if(bo.board[x-1][y+1]==null && bo.board[x][y+1]!=null && bo.board[x][y+1] instanceof Pawn && !bo.board[x][y+1].team.equals(this.team)){
            int[] last = bo.records.decodeNotation(bo.notTurn());
            if(last != null && bo.getButton(last[2], last[3]) instanceof Pawn){
                if(last[2] == (x) && last[3] == y+1 && last[2]-last[0]==2){
                    return true;
                }
            }
        }
        return false;
    }

    public Map<String,LinkedList<Integer[]>> showPossibleMoves(ChessBoard bo, int x, int y){
        Map<String,LinkedList<Integer[]>> moves = new HashMap<>();
        moves.put("Defence", new LinkedList<>());
        moves.put("Attack", new LinkedList<>());
        moves.put("Neighbor", new LinkedList<>());
        moves.put("Check", new LinkedList<>());
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
                    if(rightEnpassant(bo, x, y))
                    moves.get("Attack").add(new Integer[]{x-1,y+1});
                    else
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
                    if(leftEnpassant(bo, x, y))
                    moves.get("Attack").add(new Integer[]{x-1,y-1});
                    else
                    moves.get("Neighbor").add(new Integer[]{x-1,y-1});
                }
                else{
                    if(bo.board[x-1][y-1] instanceof King){
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

    public LinkedList<Integer[]> savingMoves(ChessBoard bo, HashMap<String,LinkedList<Integer[]>> ref, int x, int y){
        LinkedList<Integer[]> saving = new LinkedList<>();
        int i = 1;
        while((i<=1+(hasMoved?0:1))&&(x-i>=0)){
            if(bo.board[x-i][y]==null){
                if(ref.get("Attack") != null && ref.get("Attack").size() > 0){
                    for (Integer[] arr3 : ref.get("Attack")) {
                        if(Arrays.equals(arr3, new Integer[]{x-i,y})) {
                            saving.add(new Integer[]{x-i,y});
                        }
                    }
                }
                else{
                    for(String arr : ref.keySet()) {
                        if(ref.get(arr) != null && ref.get(arr).size() > 1 && Arrays.equals(ref.get(arr).getFirst(), new Integer[]{x,y})){
                            for (int idx = 1; idx < ref.get(arr).size(); idx++) {
                                Integer[] arr2 = ref.get(arr).get(idx);
                                if(Arrays.equals(arr2, new Integer[]{x-i,y})) {
                                    saving.add(new Integer[]{x-i,y});
                                }
                            }
                        }
                    }
                }
            }
            else{
                break;
            }
            i++;
        }
        try{
            if(ref.get("Attack") != null && ref.get("Attack").size() > 0){
                for (Integer[] arr : ref.get("Attack")) {
                    if((Arrays.equals(arr, new Integer[]{x-1,y+1}) && bo.board[x-1][y+1]!=null && !bo.board[x-1][y+1].team.equals(this.team)) || (rightEnpassant(bo, x, y) && Arrays.equals(arr, new Integer[]{x,y+1}))) {
                        saving.add(new Integer[]{x-1,y+1});
                    }
                }
            }
            else{
                for(String arr : ref.keySet()) {
                    if(ref.get(arr) != null && ref.get(arr).size() > 1 && Arrays.equals(ref.get(arr).getFirst(), new Integer[]{x,y})){
                        for (int idx = 1; idx < ref.get(arr).size(); idx++) {
                                Integer[] arr2 = ref.get(arr).get(idx);
                            if(Arrays.equals(arr2, new Integer[]{x-1,y+1}) && ((bo.board[x-1][y+1]!=null && !bo.board[x-1][y+1].team.equals(this.team)) || rightEnpassant(bo, x, y))) {
                                saving.add(new Integer[]{x-1,y+1});
                            }
                        }
                    }
                }
            }
        }catch(Exception e){}
        try{
            if(ref.get("Attack") != null && ref.get("Attack").size() > 0){
                for (Integer[] arr : ref.get("Attack")) {
                    if((Arrays.equals(arr, new Integer[]{x-1,y-1}) && bo.board[x-1][y-1]!=null && !bo.board[x-1][y-1].team.equals(this.team)) || (leftEnpassant(bo, x, y) && Arrays.equals(arr, new Integer[]{x,y-1}))) {
                        saving.add(new Integer[]{x-1,y-1});
                    }
                }
            }
            else{
                for(String arr : ref.keySet()) {
                    if(ref.get(arr) != null && ref.get(arr).size() > 1 && Arrays.equals(ref.get(arr).getFirst(), new Integer[]{x,y})){
                        for (int idx = 1; idx < ref.get(arr).size(); idx++) {
                                Integer[] arr2 = ref.get(arr).get(idx);
                            if(Arrays.equals(arr2, new Integer[]{x-1,y-1}) && ((bo.board[x-1][y-1]!=null && !bo.board[x-1][y-1].team.equals(this.team)) || leftEnpassant(bo, x, y))) {
                                saving.add(new Integer[]{x-1,y-1});
                            }
                        }
                    }
                }
            }
        }catch(Exception e){}
        return saving;
    }

    private Piece toQueen(){
        return new Queen(team,true);
    }

    private Piece toKnight(){
        return new Knight(team,true);
    }

    private Piece toBishop(){
        return new Bishop(team,true);
    }

    private Piece toRook(){
        return new Rook(team,true);
    }

    public void promote(ChessBoard pi,int x,int y){
        new Promotion(pi.cboard.parent,team, (Integer index) ->{
            pi.board[x][y] = handleDialogResult(index);
        });
        
        
    }

    private Piece handleDialogResult(int index){
        if(index == 0){
            return toQueen();
        }
        else if(index == 1){
            return toKnight();
        }
        else if(index == 2){
            return toBishop();
        }
        else if(index == 3){
            return toRook();
        }
        return null;
    }
}

class Knight extends Piece{
    private final String pre = "N";

    public Knight(String t, boolean h){
        super(t, decideTeam(t),h);
    }

    public String getPrefix(){
        return pre;
    }
        
    static String decideTeam(String team){
        if(team.equals("White"))
        return "ChessFinesse//Pics&Vids//Pics//w_knight_1x.png";
        return "ChessFinesse//Pics&Vids//Pics//b_knight_1x.png";
    }

    public Map<String,LinkedList<Integer[]>> showPossibleMoves(ChessBoard bo, int x, int y){
        Map<String,LinkedList<Integer[]>> moves = new HashMap<>();
        moves.put("Defence", new LinkedList<>());
        moves.put("Attack", new LinkedList<>());
        moves.put("Neighbor", new LinkedList<>());
        moves.put("Check", new LinkedList<>());
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

    public LinkedList<Integer[]> savingMoves(ChessBoard bo, HashMap<String,LinkedList<Integer[]>> ref, int x, int y){
        LinkedList<Integer[]> saving = new LinkedList<>();
        for(int i = 2;Math.abs(i)<=2;i--){
            for(int j = 2;Math.abs(j)<=2;j--){
                if(Math.abs(i) == Math.abs(j) || i == 0 || j == 0)continue;
                try{
                    if(bo.board[x+i][y+j]==null || !bo.board[x+i][y+j].team.equals(this.team)){
                        if(ref.get("Attack") != null && ref.get("Attack").size() > 0){
                            for (Integer[] arr : ref.get("Attack")) {
                                if(Arrays.equals(arr, new Integer[]{x+i,y+j})) {
                                    saving.add(new Integer[]{x+i,y+j});
                                }
                            }
                        }
                        else{
                            for(String arr : ref.keySet()) {
                                if(ref.get(arr) != null && ref.get(arr).size() > 1 && Arrays.equals(ref.get(arr).getFirst(), new Integer[]{x,y})){
                                    for (int idx = 1; idx < ref.get(arr).size(); idx++) {
                                        Integer[] arr2 = ref.get(arr).get(idx);
                                        if(Arrays.equals(arr2, new Integer[]{x+i,y+j})) {
                                            saving.add(new Integer[]{x+i,y+j});
                                        }
                                    }
                                }
                            }
                        }
                    }
                }catch(Exception e){}
            }
        }
        return saving;
    }
}

class Queen extends Piece{
    private final String pre = "Q";

    public String getPrefix(){
        return pre;
    }

    public Queen(String t,boolean M){
        super(t, decideTeam(t),M);
    }
        
    static String decideTeam(String team){
        if(team.equals("White"))
        return "ChessFinesse//Pics&Vids//Pics//w_queen_1x.png";
        return "ChessFinesse//Pics&Vids//Pics//b_queen_1x.png";
    }

    public Map<String,LinkedList<Integer[]>> showPossibleMoves(ChessBoard bo, int x, int y){
        Map<String,LinkedList<Integer[]>> moves = new HashMap<>();
        moves.put("Defence", new LinkedList<>());
        moves.put("Attack", new LinkedList<>());
        moves.put("Neighbor", new LinkedList<>());
        moves.put("Check", new LinkedList<>());
        moves.put("Restrict", new LinkedList<>());
        LinkedList<Integer[]> hL = new LinkedList<>(), hR = new LinkedList<>(), vU = new LinkedList<>(), vD = new LinkedList<>(), rU = new LinkedList<>(), rD = new LinkedList<>(), lU = new LinkedList<>(), lD = new LinkedList<>();
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
                            if(!CheckhRight)
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
                                    moves.get("Restrict").addAll(hR);
                                    hR.clear();
                                }
                            }
                            else{
                                if(!CheckhRight && !isCheckhRight){
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
                            if(!CheckhLeft)
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
                                    moves.get("Restrict").addAll(hL);
                                    hL.clear();
                                }
                            }
                            else{
                                if(!CheckhLeft && !isCheckhLeft){
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
                            if(!CheckvUp)
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
                                    moves.get("Restrict").addAll(vU);
                                    vU.clear();
                                }
                            }
                            else{
                                if(!CheckvUp && !isCheckvUp){
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
                            if(!CheckvDown)
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
                                    moves.get("Restrict").addAll(vD);
                                    vD.clear();
                                }
                            }
                            else{
                                if(!CheckvDown && !isCheckvDown){
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
                            if(!CheckrightUp)
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
                                    moves.get("Restrict").addAll(rU);
                                    rU.clear();
                                }
                            }
                            else{
                                if(!CheckrightUp && !isCheckrightUp){
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
                            if(!CheckleftDown)
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
                                    moves.get("Restrict").addAll(lD);
                                    lD.clear();
                                }
                            }
                            else{
                                if(!CheckleftDown && !isCheckleftDown){
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
                            if(!CheckleftUp)
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
                                    moves.get("Restrict").addAll(lU);
                                    lU.clear();
                                }
                            }
                            else{
                                if(!CheckleftUp && !isCheckleftUp){
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
                            if(!CheckrightDown)
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
                                    moves.get("Restrict").addAll(rD);
                                    rD.clear();
                                }
                            }
                            else{
                                if(!CheckrightDown && !isCheckrightDown){
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

    public LinkedList<Integer[]> savingMoves(ChessBoard bo, HashMap<String,LinkedList<Integer[]>> ref, int x, int y){
        LinkedList<Integer[]> saving = new LinkedList<>();
        boolean vUp=true,vDown=true,hLeft=true,hRight = true;
        boolean leftUp=true,leftDown=true,rightUp=true,rightDown = true;
        for(int i = 1;i<8;i++){
            if(hRight){
                try{
                    if(bo.board[x][y+i]==null || (bo.board[x][y+i]!=null && !bo.board[x][y+i].team.equals(this.team))){
                        if(bo.board[x][y+i]!=null)
                        hRight = false;
                        if(ref.get("Attack") != null && ref.get("Attack").size() > 0 && !bo.matchesKey(ref, x,y)){
                            for (Integer[] arr3 : ref.get("Attack")) {
                                if(Arrays.equals(arr3, new Integer[]{x,y+i})) {
                                    saving.add(new Integer[]{x,y+i});
                                }
                            }
                        }
                        else{
                            for(String arr : ref.keySet()) {
                                if(ref.get(arr) != null && ref.get(arr).size() > 1 && Arrays.equals(ref.get(arr).getFirst(), new Integer[]{x, y})){
                                    for (int idx = 1; idx < ref.get(arr).size(); idx++) {
                                        Integer[] arr2 = ref.get(arr).get(idx);
                                        if(Arrays.equals(arr2, new Integer[]{x,y+i})) {
                                            saving.add(new Integer[]{x,y+i});
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else{
                        hRight = false;
                    }
                }catch(Exception e){
                    hRight = false;
                }
            }
            if(hLeft){
                try{
                    if(bo.board[x][y-i]==null || (bo.board[x][y-i]!=null && !bo.board[x][y-i].team.equals(this.team))){
                        if(bo.board[x][y-i]!=null)
                        hLeft = false;
                        if(ref.get("Attack") != null && ref.get("Attack").size() > 0 && !bo.matchesKey(ref, x,y)){
                            for (Integer[] arr3 : ref.get("Attack")) {
                                if(Arrays.equals(arr3, new Integer[]{x,y-i})) {
                                    saving.add(new Integer[]{x,y-i});
                                }
                            }
                        }
                        else{
                            for(String arr : ref.keySet()) {
                                if(ref.get(arr) != null && ref.get(arr).size() > 1 && Arrays.equals(ref.get(arr).getFirst(), new Integer[]{x, y})){
                                    for (int idx = 1; idx < ref.get(arr).size(); idx++) {
                                        Integer[] arr2 = ref.get(arr).get(idx);
                                        if(Arrays.equals(arr2, new Integer[]{x,y-i})) {
                                            saving.add(new Integer[]{x,y-i});
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else{
                        hLeft = false;
                    }
                }catch(Exception e){
                    hLeft = false;
                }
            }
            if(vUp){
                try{
                    if(bo.board[x+i][y]==null || (bo.board[x+i][y]!=null && !bo.board[x+i][y].team.equals(this.team))){
                        if(bo.board[x+i][y]!=null)
                        vUp = false;
                        if(ref.get("Attack") != null && ref.get("Attack").size() > 0 && !bo.matchesKey(ref, x,y)){
                            for (Integer[] arr3 : ref.get("Attack")) {
                                if(Arrays.equals(arr3, new Integer[]{x+i,y})) {
                                    saving.add(new Integer[]{x+i,y});
                                }
                            }
                        }
                        else{
                            for(String arr : ref.keySet()) {
                                if(ref.get(arr) != null && ref.get(arr).size() > 1 && Arrays.equals(ref.get(arr).getFirst(), new Integer[]{x, y})){
                                    for (int idx = 1; idx < ref.get(arr).size(); idx++) {
                                        Integer[] arr2 = ref.get(arr).get(idx);
                                        if(Arrays.equals(arr2, new Integer[]{x+i,y})) {
                                            saving.add(new Integer[]{x+i,y});
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else{
                        vUp = false;
                    }
                }catch(Exception e){
                    vUp = false;
                }
            }
            if(vDown){
                try{
                    if(bo.board[x-i][y]==null || (bo.board[x-i][y]!=null && !bo.board[x-i][y].team.equals(this.team))){
                        if(bo.board[x-i][y]!=null)
                        vDown = false;
                        if(ref.get("Attack") != null && ref.get("Attack").size() > 0 && !bo.matchesKey(ref, x,y)){
                            for (Integer[] arr3 : ref.get("Attack")) {
                                if(Arrays.equals(arr3, new Integer[]{x-i,y})) {
                                    saving.add(new Integer[]{x-i,y});
                                }
                            }
                        }
                        else{
                            for(String arr : ref.keySet()) {
                                if(ref.get(arr) != null && ref.get(arr).size() > 1 && Arrays.equals(ref.get(arr).getFirst(), new Integer[]{x, y})){
                                    for (int idx = 1; idx < ref.get(arr).size(); idx++) {
                                        Integer[] arr2 = ref.get(arr).get(idx);
                                        if(Arrays.equals(arr2, new Integer[]{x-i,y})) {
                                            saving.add(new Integer[]{x-i,y});
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else{
                        vDown = false;
                    }
                }catch(Exception e){
                    vDown = false;
                }
            }
            if(rightUp){
                try{
                    if(bo.board[x+i][y+i]==null || (bo.board[x+i][y+i]!=null && !bo.board[x+i][y+i].team.equals(this.team))){
                        if(bo.board[x+i][y+i]!=null)
                        rightUp = false;
                        if(ref.get("Attack") != null && ref.get("Attack").size() > 0 && !bo.matchesKey(ref, x,y)){
                            for (Integer[] arr3 : ref.get("Attack")) {
                                if(Arrays.equals(arr3, new Integer[]{x+i,y+i})) {
                                    saving.add(new Integer[]{x+i,y+i});
                                }
                            }
                        }
                        else{
                            for(String arr : ref.keySet()) {
                                if(ref.get(arr) != null && ref.get(arr).size() > 1 && Arrays.equals(ref.get(arr).getFirst(), new Integer[]{x, y})){
                                    for (int idx = 1; idx < ref.get(arr).size(); idx++) {
                                        Integer[] arr2 = ref.get(arr).get(idx);
                                        if(Arrays.equals(arr2, new Integer[]{x+i,y+i})) {
                                            saving.add(new Integer[]{x+i,y+i});
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else{
                        rightUp = false;
                    }
                }catch(Exception e){
                    rightUp = false;
                }
            }
            if(leftDown){
                try{
                    if(bo.board[x+i][y-i]==null || (bo.board[x+i][y-i]!=null && !bo.board[x+i][y-i].team.equals(this.team))){
                        if(bo.board[x+i][y-i]!=null)
                        leftDown = false;
                        if(ref.get("Attack") != null && ref.get("Attack").size() > 0 && !bo.matchesKey(ref, x,y)){
                            for (Integer[] arr3 : ref.get("Attack")) {
                                if(Arrays.equals(arr3, new Integer[]{x+i,y-i})) {
                                    saving.add(new Integer[]{x+i,y-i});
                                }
                            }
                        }
                        else{
                            for(String arr : ref.keySet()) {
                                if(ref.get(arr) != null && ref.get(arr).size() > 1 && Arrays.equals(ref.get(arr).getFirst(), new Integer[]{x, y})){
                                    for (int idx = 1; idx < ref.get(arr).size(); idx++) {
                                        Integer[] arr2 = ref.get(arr).get(idx);
                                        if(Arrays.equals(arr2, new Integer[]{x+i,y-i})) {
                                            saving.add(new Integer[]{x+i,y-i});
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else{
                        leftDown = false;
                    }
                }catch(Exception e){
                    leftDown = false;
                }
            }
            if(leftUp){
                try{
                    if(bo.board[x-i][y+i]==null || (bo.board[x-i][y+i]!=null && !bo.board[x-i][y+i].team.equals(this.team))){
                        if(bo.board[x-i][y+i]!=null)
                        leftUp = false;
                        if(ref.get("Attack") != null && ref.get("Attack").size() > 0 && !bo.matchesKey(ref, x,y)){
                            for (Integer[] arr3 : ref.get("Attack")) {
                                if(Arrays.equals(arr3, new Integer[]{x-i,y+i})) {
                                    saving.add(new Integer[]{x-i,y+i});
                                }
                            }
                        }
                        else{
                            for(String arr : ref.keySet()) {
                                if(ref.get(arr) != null && ref.get(arr).size() > 1 && Arrays.equals(ref.get(arr).getFirst(), new Integer[]{x, y})){
                                    for (int idx = 1; idx < ref.get(arr).size(); idx++) {
                                        Integer[] arr2 = ref.get(arr).get(idx);
                                        if(Arrays.equals(arr2, new Integer[]{x-i,y+i})) {
                                            saving.add(new Integer[]{x-i,y+i});
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else{
                        leftUp = false;
                    }
                }catch(Exception e){
                    leftUp = false;
                }
            }
            if(rightDown){
                try{
                    if(bo.board[x-i][y-i]==null || (bo.board[x-i][y-i]!=null && !bo.board[x-i][y-i].team.equals(this.team))){
                        if(bo.board[x-i][y-i]!=null)
                        rightDown = false;
                        if(ref.get("Attack") != null && ref.get("Attack").size() > 0 && !bo.matchesKey(ref, x,y)){
                            for (Integer[] arr3 : ref.get("Attack")) {
                                if(Arrays.equals(arr3, new Integer[]{x-i,y-i})) {
                                    saving.add(new Integer[]{x-i,y-i});
                                }
                            }
                        }
                        else{
                            for(String arr : ref.keySet()) {
                                if(ref.get(arr) != null && ref.get(arr).size() > 1 && Arrays.equals(ref.get(arr).getFirst(), new Integer[]{x, y})){
                                    for (int idx = 1; idx < ref.get(arr).size(); idx++) {
                                        Integer[] arr2 = ref.get(arr).get(idx);
                                        if(Arrays.equals(arr2, new Integer[]{x-i,y-i})) {
                                            saving.add(new Integer[]{x-i,y-i});
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else{
                        rightDown = false;
                    }
                }catch(Exception e){
                    rightDown = false;
                }
            }
        } 
        return saving;
    }
}

class Rook extends Piece{
    private final String pre = "R";
    
    public String getPrefix(){
        return pre;
    }

    public Rook(String t,boolean M){
        super(t, decideTeam(t),M);
    }
        
    static String decideTeam(String team){
        if(team.equals("White"))
        return "ChessFinesse//Pics&Vids//Pics//w_rook_1x.png";
        return "ChessFinesse//Pics&Vids//Pics//b_rook_1x.png";
    }

    public Map<String,LinkedList<Integer[]>> showPossibleMoves(ChessBoard bo, int x, int y){
        Map<String,LinkedList<Integer[]>> moves = new HashMap<>();
        moves.put("Defence", new LinkedList<>());
        moves.put("Attack", new LinkedList<>());
        moves.put("Neighbor", new LinkedList<>());
        moves.put("Check", new LinkedList<>());
        moves.put("Restrict", new LinkedList<>());
        LinkedList<Integer[]> hL = new LinkedList<>(), hR = new LinkedList<>(), vU = new LinkedList<>(), vD = new LinkedList<>();
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
                            if(!CheckhRight)
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
                                    moves.get("Restrict").addAll(hR);
                                    hR.clear();
                                }
                            }
                            else{
                                if(!CheckhRight && !isCheckhRight){
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
                            if(!CheckhLeft)
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
                                    moves.get("Restrict").addAll(hL);
                                    hL.clear();
                                }
                            }
                            else{
                                if(!CheckhLeft && !isCheckhLeft){
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
                            if(!CheckvUp)
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
                                    moves.get("Restrict").addAll(vU);
                                    vU.clear();
                                }
                            }
                            else{
                                if(!CheckvUp && !isCheckvUp){
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
                            if(!CheckvDown)
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
                                    moves.get("Restrict").addAll(vD);
                                    vD.clear();
                                }
                            }
                            else{
                                if(!CheckvDown && !isCheckvDown){
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

    public LinkedList<Integer[]> savingMoves(ChessBoard bo, HashMap<String,LinkedList<Integer[]>> ref, int x, int y){
        LinkedList<Integer[]> saving = new LinkedList<>();
        boolean vUp=true,vDown=true,hLeft=true,hRight = true;
        for(int i = 1;i<8;i++){
            if(hRight){
                try{
                    if(bo.board[x][y+i]==null || (bo.board[x][y+i]!=null && !bo.board[x][y+i].team.equals(this.team))){
                        if(bo.board[x][y+i]!=null)
                        hRight = false;
                        if(ref.get("Attack") != null && ref.get("Attack").size() > 0 && !bo.matchesKey(ref, x,y)){
                            for (Integer[] arr3 : ref.get("Attack")) {
                                if(Arrays.equals(arr3, new Integer[]{x,y+i})) {
                                    saving.add(new Integer[]{x,y+i});
                                }
                            }
                        }
                        else{
                            for(String arr : ref.keySet()) {
                                if(ref.get(arr) != null && ref.get(arr).size() > 1 && Arrays.equals(ref.get(arr).getFirst(), new Integer[]{x, y})){
                                    for (int idx = 1; idx < ref.get(arr).size(); idx++) {
                                        Integer[] arr2 = ref.get(arr).get(idx);
                                        if(Arrays.equals(arr2, new Integer[]{x,y+i})) {
                                            saving.add(new Integer[]{x,y+i});
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else{
                        hRight = false;
                    }
                }catch(Exception e){
                    hRight = false;
                }
            }
            if(hLeft){
                try{
                    if(bo.board[x][y-i]==null || (bo.board[x][y-i]!=null && !bo.board[x][y-i].team.equals(this.team))){
                        if(bo.board[x][y-i]!=null)
                        hLeft = false;
                        if(ref.get("Attack") != null && ref.get("Attack").size() > 0 && !bo.matchesKey(ref, x,y)){
                            for (Integer[] arr3 : ref.get("Attack")) {
                                if(Arrays.equals(arr3, new Integer[]{x,y-i})) {
                                    saving.add(new Integer[]{x,y-i});
                                }
                            }
                        }
                        else{
                            for(String arr : ref.keySet()) {
                                if(ref.get(arr) != null && ref.get(arr).size() > 1 && Arrays.equals(ref.get(arr).getFirst(), new Integer[]{x, y})){
                                    for (int idx = 1; idx < ref.get(arr).size(); idx++) {
                                        Integer[] arr2 = ref.get(arr).get(idx);
                                        if(Arrays.equals(arr2, new Integer[]{x,y-i})) {
                                            saving.add(new Integer[]{x,y-i});
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else{
                        hLeft = false;
                    }
                }catch(Exception e){
                    hLeft = false;
                }
            }
            if(vUp){
                try{
                    if(bo.board[x+i][y]==null || (bo.board[x+i][y]!=null && !bo.board[x+i][y].team.equals(this.team))){
                        if(bo.board[x+i][y]!=null)
                        vUp = false;
                        if(ref.get("Attack") != null && ref.get("Attack").size() > 0 && !bo.matchesKey(ref, x,y)){
                            for (Integer[] arr3 : ref.get("Attack")) {
                                if(Arrays.equals(arr3, new Integer[]{x+i,y})) {
                                    saving.add(new Integer[]{x+i,y});
                                }
                            }
                        }
                        else{
                            for(String arr : ref.keySet()) {
                                if(ref.get(arr) != null && ref.get(arr).size() > 1 && Arrays.equals(ref.get(arr).getFirst(), new Integer[]{x, y})){
                                    for (int idx = 1; idx < ref.get(arr).size(); idx++) {
                                        Integer[] arr2 = ref.get(arr).get(idx);
                                        if(Arrays.equals(arr2, new Integer[]{x+i,y})) {
                                            saving.add(new Integer[]{x+i,y});
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else{
                        vUp = false;
                    }
                }catch(Exception e){
                    vUp = false;
                }
            }
            if(vDown){
                try{
                    if(bo.board[x-i][y]==null || (bo.board[x-i][y]!=null && !bo.board[x-i][y].team.equals(this.team))){
                        if(bo.board[x-i][y]!=null)
                        vDown = false;
                        if(ref.get("Attack") != null && ref.get("Attack").size() > 0 && !bo.matchesKey(ref, x,y)){
                            for (Integer[] arr3 : ref.get("Attack")) {
                                if(Arrays.equals(arr3, new Integer[]{x-i,y})) {
                                    saving.add(new Integer[]{x-i,y});
                                }
                            }
                        }
                        else{
                            for(String arr : ref.keySet()) {
                                if(ref.get(arr) != null && ref.get(arr).size() > 1 && Arrays.equals(ref.get(arr).getFirst(), new Integer[]{x, y})){
                                    for (int idx = 1; idx < ref.get(arr).size(); idx++) {
                                        Integer[] arr2 = ref.get(arr).get(idx);
                                        if(Arrays.equals(arr2, new Integer[]{x-i,y})) {
                                            saving.add(new Integer[]{x-i,y});
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else{
                        vDown = false;
                    }
                }catch(Exception e){
                    vDown = false;
                }
            }
        }
        return saving;
    }
}

class Bishop extends Piece{
    private final String pre = "B";
    
    public String getPrefix(){
        return pre;
    }

    public Bishop(String t,boolean M){
        super(t, decideTeam(t),M);
    }
        
    static String decideTeam(String team){
        if(team.equals("White"))
        return "ChessFinesse//Pics&Vids//Pics//w_bishop_1x.png";
        return "ChessFinesse//Pics&Vids//Pics//b_bishop_1x.png";
    }

    public Map<String,LinkedList<Integer[]>> showPossibleMoves(ChessBoard bo, int x, int y){
        Map<String,LinkedList<Integer[]>> moves = new HashMap<>();
        moves.put("Defence", new LinkedList<>());
        moves.put("Attack", new LinkedList<>());
        moves.put("Neighbor", new LinkedList<>());
        moves.put("Check", new LinkedList<>());
        moves.put("Restrict", new LinkedList<>());
        LinkedList<Integer[]> rU = new LinkedList<>(), rD = new LinkedList<>(), lU = new LinkedList<>(), lD = new LinkedList<>();
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
                            if(!CheckrightUp)
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
                                    moves.get("Restrict").addAll(rU);
                                    rU.clear();
                                }
                            }
                            else{
                                if(!CheckrightUp && !isCheckrightUp){
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
                            if(!CheckleftDown)
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
                                    moves.get("Restrict").addAll(lD);
                                    lD.clear();
                                }
                            }
                            else{
                                if(!CheckleftDown && !isCheckleftDown){
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
                            if(!CheckleftUp)
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
                                    moves.get("Restrict").addAll(lU);
                                    lU.clear();
                                }
                            }
                            else{
                                if(!CheckleftUp && !isCheckleftUp){
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
                            if(!CheckrightDown)
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
                                    moves.get("Restrict").addAll(rD);
                                    rD.clear();
                                }
                            }
                            else{
                                if(!CheckrightDown && !isCheckrightDown){
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

    public LinkedList<Integer[]> savingMoves(ChessBoard bo, HashMap<String,LinkedList<Integer[]>> ref, int x, int y){
        LinkedList<Integer[]> saving = new LinkedList<>();
        boolean leftUp=true,leftDown=true,rightUp=true,rightDown = true;
        for(int i = 1;i<8;i++){
            if(rightUp){
                try{
                    if(bo.board[x+i][y+i]==null || (bo.board[x+i][y+i]!=null && !bo.board[x+i][y+i].team.equals(this.team))){
                        if(bo.board[x+i][y+i]!=null)
                        rightUp = false;
                        if(ref.get("Attack") != null && ref.get("Attack").size() > 0 && !bo.matchesKey(ref, x,y)){
                            for (Integer[] arr3 : ref.get("Attack")) {
                                if(Arrays.equals(arr3, new Integer[]{x+i,y+i})) {
                                    saving.add(new Integer[]{x+i,y+i});
                                }
                            }
                        }
                        else{
                            for(String arr : ref.keySet()) {
                                if(ref.get(arr) != null && ref.get(arr).size() > 1 && Arrays.equals(ref.get(arr).getFirst(), new Integer[]{x, y})){
                                    for (int idx = 1; idx < ref.get(arr).size(); idx++) {
                                        Integer[] arr2 = ref.get(arr).get(idx);
                                        if(Arrays.equals(arr2, new Integer[]{x+i,y+i})) {
                                            saving.add(new Integer[]{x+i,y+i});
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else{
                        rightUp = false;
                    }
                }catch(Exception e){
                    rightUp = false;
                }
            }
            if(leftDown){
                try{
                    if(bo.board[x+i][y-i]==null || (bo.board[x+i][y-i]!=null && !bo.board[x+i][y-i].team.equals(this.team))){
                        if(bo.board[x+i][y-i]!=null)
                        leftDown = false;
                        if(ref.get("Attack") != null && ref.get("Attack").size() > 0 && !bo.matchesKey(ref, x,y)){
                            for (Integer[] arr3 : ref.get("Attack")) {
                                if(Arrays.equals(arr3, new Integer[]{x+i,y-i})) {
                                    saving.add(new Integer[]{x+i,y-i});
                                }
                            }
                        }
                        else{
                            for(String arr : ref.keySet()) {
                                if(ref.get(arr) != null && ref.get(arr).size() > 1 && Arrays.equals(ref.get(arr).getFirst(), new Integer[]{x, y})){
                                    for (int idx = 1; idx < ref.get(arr).size(); idx++) {
                                        Integer[] arr2 = ref.get(arr).get(idx);
                                        if(Arrays.equals(arr2, new Integer[]{x+i,y-i})) {
                                            saving.add(new Integer[]{x+i,y-i});
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else{
                        leftDown = false;
                    }
                }catch(Exception e){
                    leftDown = false;
                }
            }
            if(leftUp){
                try{
                    if(bo.board[x-i][y+i]==null || (bo.board[x-i][y+i]!=null && !bo.board[x-i][y+i].team.equals(this.team))){
                        if(bo.board[x-i][y+i]!=null)
                        leftUp = false;
                        if(ref.get("Attack") != null && ref.get("Attack").size() > 0 && !bo.matchesKey(ref, x,y)){
                            for (Integer[] arr3 : ref.get("Attack")) {
                                if(Arrays.equals(arr3, new Integer[]{x-i,y+i})) {
                                    saving.add(new Integer[]{x-i,y+i});
                                }
                            }
                        }
                        else{
                            for(String arr : ref.keySet()) {
                                if(ref.get(arr) != null && ref.get(arr).size() > 1 && Arrays.equals(ref.get(arr).getFirst(), new Integer[]{x, y})){
                                    for (int idx = 1; idx < ref.get(arr).size(); idx++) {
                                        Integer[] arr2 = ref.get(arr).get(idx);
                                        if(Arrays.equals(arr2, new Integer[]{x-i,y+i})) {
                                            saving.add(new Integer[]{x-i,y+i});
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else{
                        leftUp = false;
                    }
                }catch(Exception e){
                    leftUp = false;
                }
            }
            if(rightDown){
                try{
                    if(bo.board[x-i][y-i]==null || (bo.board[x-i][y-i]!=null && !bo.board[x-i][y-i].team.equals(this.team))){
                        if(bo.board[x-i][y-i]!=null)
                        rightDown = false;
                        if(ref.get("Attack") != null && ref.get("Attack").size() > 0 && !bo.matchesKey(ref, x,y)){
                            for (Integer[] arr3 : ref.get("Attack")) {
                                if(Arrays.equals(arr3, new Integer[]{x-i,y-i})) {
                                    saving.add(new Integer[]{x-i,y-i});
                                }
                            }
                        }
                        else{
                            for(String arr : ref.keySet()) {
                                if(ref.get(arr) != null && ref.get(arr).size() > 1 && Arrays.equals(ref.get(arr).getFirst(), new Integer[]{x, y})){
                                    for (int idx = 1; idx < ref.get(arr).size(); idx++) {
                                        Integer[] arr2 = ref.get(arr).get(idx);
                                        if(Arrays.equals(arr2, new Integer[]{x-i,y-i})) {
                                            saving.add(new Integer[]{x-i,y-i});
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else{
                        rightDown = false;
                    }
                }catch(Exception e){
                    rightDown = false;
                }
            }
        }
        return saving;
    }
}