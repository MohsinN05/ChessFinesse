package Game;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class ChessGUI extends JPanel {
    private static final int BOARD_SIZE = 8;
    ButtonFunc[][] buttons;
    char al[] = {'a','b','c','d','e','f','g','h'};
    char n[] = {'1','2','3','4','5','6','7','8'};
    JLabel alpha[] = new JLabel[8];
    JLabel num[] = new JLabel[8];
    JLabel k = new JLabel();
    ChessBoard board;
    ArrayList<int[]> attack;
    JPanel chess = new JPanel();
    JPanel left = new JPanel(new FlowLayout());
    JPanel right = new JPanel(new FlowLayout());
    JLabel turn = new JLabel();
    public App parent;
    JLabel player1Label;
    JLabel player2Label;
    String player1Name;
    String player2Name;

    JTextArea moveList = new JTextArea();
    
    public ChessGUI(App c, String player1Name, String player2Name) {
    this.parent = c;
    this.player1Name = player1Name;
    if(player2Name.equals("stockfish-windows-x86-64-sse41-popcnt"))
    this.player2Name = "AI";
    else
    this.player2Name = player2Name;
    setLayout(new BorderLayout());
    designLeft();
    addBoard(player1Name,player2Name);
    addTurnlabel();
    designRight();
}

    public void designLeft(){
        left.setBackground(Color.black);
        left.setPreferredSize(new Dimension(200, 700));
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        add(left, BorderLayout.WEST);

        addOnLeft(parent);

        // Player names
        Font nameFont = new Font("Serif", Font.BOLD, 18);
        player1Label = new JLabel("White: " + player1Name, SwingConstants.CENTER);
        player1Label.setForeground(Color.ORANGE);
        player1Label.setFont(nameFont);
        player1Label.setAlignmentX(CENTER_ALIGNMENT);
        
        player2Label = new JLabel("Black: " + player2Name, SwingConstants.CENTER);
        player2Label.setForeground(Color.ORANGE);
        player2Label.setFont(nameFont);
        player2Label.setAlignmentX(CENTER_ALIGNMENT);

        left.add(Box.createVerticalStrut(100));
        left.add(player1Label);
        left.add(Box.createVerticalStrut(20));
        left.add(player2Label);
    }
    
    public void designRight(){
        right.setBackground(Color.black);
        right.setPreferredSize(new Dimension(200, 700));
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        add(right, BorderLayout.EAST);

        addTurnlabel();

        // Move list area (placeholder)
        JLabel movesLabel = new JLabel("Move List", SwingConstants.CENTER);
        movesLabel.setFont(new Font("Serif", Font.BOLD, 16));
        movesLabel.setForeground(Color.ORANGE);
        movesLabel.setAlignmentX(CENTER_ALIGNMENT);

        moveList.setEditable(false);
        moveList.setBackground(Color.DARK_GRAY);
        moveList.setForeground(Color.WHITE);
        moveList.setFont(new Font("Monospaced", Font.PLAIN, 14));
        moveList.setLineWrap(true);
        moveList.setWrapStyleWord(true);
        moveList.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        JScrollPane scrollPane = new JScrollPane(moveList);
        scrollPane.setPreferredSize(new Dimension(180, 500));
        scrollPane.setAlignmentX(CENTER_ALIGNMENT);

        right.add(Box.createVerticalStrut(50));
        right.add(movesLabel);
        right.add(Box.createVerticalStrut(10));
        right.add(scrollPane);
    }
    
    public void addOnLeft(App ca){
        JButton goBack = new JButton("Go back");
        goBack.setPreferredSize(new Dimension(150, 50));
        goBack.setMaximumSize(new Dimension(150, 50));
        goBack.setBackground(new Color(60, 63, 65));
        goBack.setFocusable(false);
        goBack.setForeground(Color.WHITE);
        goBack.setFont(new Font("SansSerif", Font.BOLD, 14));
        goBack.setBorderPainted(false);
        goBack.setFocusPainted(false);
        goBack.setAlignmentX(CENTER_ALIGNMENT);
        goBack.addActionListener(e -> {
            ca.toMenu();
        });
        left.add(Box.createVerticalStrut(30));
        left.add(goBack);
    }
    
    public void addTurnlabel(){
        setTurn();
        turn.setForeground(Color.WHITE);
        turn.setHorizontalAlignment(SwingConstants.LEFT);
        turn.setFont(new Font("Times new Roman", Font.BOLD, 20));
        turn.setPreferredSize(new Dimension(200, 100));
        turn.setMaximumSize(new Dimension(200, 100));
        right.add(turn);
    }

    public void setTurn(){
        turn.setText(board.turn + "'s turn");
    }

    public void setResult(String r){
        if (r.equals("Checkmate")) {
        turn.setText("<html><center>" + r + ".<br>" + board.notTurn() + " wins.</center></html>");
    } else {
        turn.setText(r);
    }
    }

    public void addBoard(String player1Name,String player2Name){
        buttons = new ButtonFunc[8][8];
        board = new ChessBoard(this,player1Name,player2Name);
        chess.setLayout(new GridLayout(9,9));
        chess.setPreferredSize(new Dimension(700, 700));
        chess.setMaximumSize(new Dimension(700,700));
        chess.setMinimumSize(new Dimension(700,700));
        initializeButtons();
        add(chess,BorderLayout.CENTER);
    }

    public Dimension getPreferredSize(){
        return new Dimension(700,700);
    }

    public void move(int[] fo,int tx,int ty){
        board.move(fo[0],fo[1], tx, ty);
    }
    

    

    private void initializeButtons() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            num[i] = new JLabel(String.valueOf(BOARD_SIZE-i), SwingConstants.CENTER);
            num[i].setOpaque(true);
            num[i].setBackground(Color.DARK_GRAY);
            num[i].setForeground(Color.ORANGE);
            chess.add(num[i]);
            for (int j = 0; j < BOARD_SIZE; j++) {
                ButtonFunc button = new ButtonFunc();
                button.setState(i, j);
                button.setFocusPainted(false);
                int row = i, col = j;
                buttons[i][j] = button;
                button.addActionListener(customAction(row, col));
                chess.add(button);     
            }
        }
            k.setOpaque(true);
            k.setBackground(Color.DARK_GRAY);
            chess.add(k);
        for (int col = 0; col < 8; col++) {
            alpha[col] = new JLabel(String.valueOf(al[col]), SwingConstants.CENTER);
            alpha[col].setOpaque(true);
            alpha[col].setBackground(Color.darkGray);
            alpha[col].setForeground(Color.ORANGE);
            chess.add(alpha[col]);
        }
        updateButtons();
    }



    private void showPossibleMoves(int i,int j){
        onlyMove(board.movemap, i, j);
        buttons[i][j].setBackground(Color.lightGray);
    }


    public  void onlyMove(HashMap<Integer[],LinkedList<Integer[]>> moves, int row, int col){
        for (Integer[] key : moves.keySet()) {
            if (key[0] == row && key[1] == col) {
                for(Integer[] move : moves.get(key)) {
                        buttons[move[0]][move[1]].setBackground(Color.cyan);
                }
            }
        }
    }

    public void highlightCheck(){
        if(board.check){
            for(Integer[] key: board.movemap.keySet()){
                if(board.board[key[0]][key[1]] instanceof King)
                buttons[key[0]][key[1]].setBackground(Color.red);
            }
        }
    }




    private ActionListener customAction(int row,int col){
        return _ -> {
            if(board.valid(row, col)){
                updateButtons();
                highlightCheck();
                showPossibleMoves(row, col);
                return;
            }
            else if(board.validChoice(row, col)){
                int[]save = board.records.lastMoves;
                move(save, row, col);
                updateButtons();
                setButtonState(false);
                new Thread(() -> {
                    result();
                    updateButtons();
                    highlightCheck();
                    setButtonState(true);
                }).start();
                return;
            }
            else{
                updateButtons();
                highlightCheck();
            }
        };
    }

    boolean result(){
        String concl = board.conclusion();

        if (concl.equals("Checkmate") || concl.equals("Stalemate")) {
            setButtonState(false);
            setResult(concl);
            parent.continueButton.setVisible(false);

            // Determine winner based on turn logic
            String winner;
            if (concl.equals("Checkmate")) {
                winner = board.notTurn().equals("White") ? player1Name : player2Name;
            } else {
                winner = "Draw";
            }

            // Save to game history
            GameRecord record = new GameRecord(player1Name, player2Name, winner);
            java.util.List<GameRecord> history = GameHistoryHandler.loadGameHistory();
            history.add(record);
            GameHistoryHandler.saveGameHistory(history);

            System.out.println("Game Over. Winner: " + winner);
            return true;
        } else {
            board.records.lastMoves = null;
        }
        setTurn();

        return false;
    }

    void setButtonState(boolean state){
        for(int i = 0;i<BOARD_SIZE;i++){
            for(int j = 0;j<BOARD_SIZE;j++){
                buttons[i][j].setEnabled(state);
            }
        }
    }
    

 

    public void updateButtons() {

        for (int i = 0; i < BOARD_SIZE; i++) {
            if(board.whitesTurn()){
                alpha[i].setText(String.valueOf(al[i]));
                num[i].setText(String.valueOf(n[BOARD_SIZE-i-1]));
            }
            else{
                alpha[i].setText(String.valueOf(al[BOARD_SIZE-i-1]));
                num[i].setText(String.valueOf(n[i]));
            }
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board.getButton(i, j) != null) {
                    buttons[i][j].setIcon(board.getButton(i,j).pic);
                    buttons[i][j].setDisabledIcon(board.getButton(i, j).pic);
                } else {
                    buttons[i][j].setIcon(null);
                }
                buttons[i][j].setBackground((i + j) % 2 == 0 ? new Color(230,230,235) : new Color(44,40,85));
            }
        }
    }
}
