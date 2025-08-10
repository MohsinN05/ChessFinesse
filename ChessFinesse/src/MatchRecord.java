package ChessFinesse.src;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Set;
import java.util.Stack;

public class MatchRecord {

    public int[] lastMoves;
    Deque<String> oldMoves;

    public MatchRecord(){
        lastMoves = new int[2];
        oldMoves = new ArrayDeque<>();
    }

    void savePiece(int x, int y){
        lastMoves = new int[]{x,y};
    }

    void emptySavedPiece(){
        lastMoves = null;
    }

    void addMove(String turn,int a, int b, String pieceTo){
        String from = uciNotation(turn, lastMoves[0], lastMoves[1]);
        String to = uciNotation(turn, a, b);
        oldMoves.add(from + to + pieceTo);
    }

    String allMoves(){
        String moves = "";
        for(String i: oldMoves){
            moves = moves + i + " ";
        }
        moves.substring(0,moves.length()-1);
        return moves;
    }

    private String uciNotation(String turn,int a, int b){
        if(turn.equals("White")){
            return String.format("%c%d", 'a'+b,8-a);
        }
        return String.format("%c%d", 'h'-b,a+1);
    }

    int[] convertMove(String turn, String move){
        if(turn.equals("White")){
            return new int[]{Math.abs(move.charAt(1)-'1'),Math.abs(move.charAt(0)-'h')};
        }
        return new int[]{Math.abs(move.charAt(1)-'8'),Math.abs(move.charAt(0)-'a')};
    }

    int[] decodeNotation(String turn){
        String last = oldMoves.peekLast();
        if(last == null) return null;
        String from = last.substring(0,2);
        String to = last.substring(2);
        if(to.length() == 3){
            to = to.substring(0, to.length()-1);
        }
        if(turn.equals("White")){
            return new int[]{Math.abs(from.charAt(1)-'1'),Math.abs(from.charAt(0)-'h'),Math.abs(to.charAt(1)-'1'),Math.abs(to.charAt(0)-'h')};
        }
        return new int[]{Math.abs(from.charAt(1)-'8'),Math.abs(from.charAt(0)-'a'),Math.abs(to.charAt(1)-'8'),Math.abs(to.charAt(0)-'a')};
    }

    // public void addQueenSide(){
    //     oldMoves.add("0-0-0");
    // }

    // public void addKingSide() {
    //     oldMoves.add("0-0");
    // }


    // String algebraicNotation(int tx, int ty){
    //     String send = "";
    //     if(whitesTurn())
    //     send = String.format("%c%d", 'a'+ty,8-tx);
    //     else
    //     send = String.format("%c%d", 'h'-ty,tx+1);
    //     return send;
    // }

    // String refine(Integer[]f,Set<Integer[]> e){
    //     boolean files = false, ranks = false;
    //     String send = "";
    //     for(Integer[]a : e){
    //         if(a[0].equals(f[0])){
    //             files = true;
    //         }
    //         if(a[1].equals(f[1])){
    //             ranks = true;
    //         }
    //     }
    //     if(files || ranks){
    //         if(files && ranks){
    //             if(whitesTurn())
    //             send = String.format("%c%d", 'a'+f[1],8-f[0]);
    //             else
    //             send = String.format("%c%d", 'h'-f[1],f[0]+1);
    //         }
    //         else if(files){
    //             if(whitesTurn())
    //             send = String.format("%c", 'a'+f[1]);
    //             else
    //             send = String.format("%c", 'h'-f[1]);
    //         }
    //         else{
    //             if(whitesTurn())
    //             send = String.format("%d", 8-f[0]);
    //             else
    //             send = String.format("%d", f[0]+1);
    //         }
    //     }
    //     return send;
    // }

    // public void updateMove(String p, int[]f, int tx, int ty){
        
    // }

    // public void updateToCheck(){
    //     String s = oldMoves.peek();
    //     s.concat("+");
    // }

    // public void updateToCheckmate(){
    //     String s = oldMoves.peek();
    //     s.concat("#");
    // }
}
