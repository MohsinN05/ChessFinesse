import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChessGame {
    private static final int BOARD_SIZE = 8;
    private JFrame frame;
    private JButton[][] buttons;
    private String[][] board;
    private ImageIcon[][] pieceImages;

    public ChessGame() {
        frame = new JFrame("Chess Game");
        frame.setSize(600, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLayout(new GridLayout(BOARD_SIZE, BOARD_SIZE));

        buttons = new JButton[BOARD_SIZE][BOARD_SIZE];
        board = new String[BOARD_SIZE][BOARD_SIZE];
        pieceImages = new ImageIcon[2][6];

        loadPieceImages();
        initializeBoard();
        initializeButtons();

        frame.setVisible(true);
    }

    private void loadPieceImages() {
        pieceImages[0][0] = resizeIcon("1x\\b_rook_1x.png");
        pieceImages[0][1] = resizeIcon("1x\\b_knight_1x.png");
        pieceImages[0][2] = resizeIcon("1x\\b_bishop_1x.png");
        pieceImages[0][3] = resizeIcon("1x\\b_queen_1x.png");
        pieceImages[0][4] = resizeIcon("1x\\b_king_1x.png");
        pieceImages[0][5] = resizeIcon("1x\\b_pawn_1x.png");
    
        pieceImages[1][0] = resizeIcon("1x\\w_rook_1x.png");
        pieceImages[1][1] = resizeIcon("1x\\w_knight_1x.png");
        pieceImages[1][2] = resizeIcon("1x\\w_bishop_1x.png");
        pieceImages[1][3] = resizeIcon("1x\\w_queen_1x.png");
        pieceImages[1][4] = resizeIcon("1x\\w_king_1x.png");
        pieceImages[1][5] = resizeIcon("1x\\w_pawn_1x.png");
    }
    
    private ImageIcon resizeIcon(String path) {
        ImageIcon icon = new ImageIcon(path);
        Image img = icon.getImage().getScaledInstance(55, 55, Image.SCALE_SMOOTH); 
        return new ImageIcon(img);
    }

    private void initializeBoard() {

        board[0] = new String[]{"R", "N", "B", "Q", "K", "B", "N", "R"};
        board[1] = new String[]{"P", "P", "P", "P", "P", "P", "P", "P"};
        for (int i = 2; i < 6; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j] = ""; // Empty spaces
            }
        }
        board[6] = new String[]{"P", "P", "P", "P", "P", "P", "P", "P"};
        board[7] = new String[]{"R", "N", "B", "Q", "K", "B", "N", "R"};
    }

    private void initializeButtons() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                JButton button = new JButton();
                button.setFont(new Font("Arial", Font.BOLD, 20));
                button.setBackground((i + j) % 2 == 0 ? new Color(230,230,235) : new Color(44,40,85));
                button.setFocusPainted(false);

                int row = i, col = j; // Capture current row and column for event listener
                button.addActionListener(new ActionListener() {
                    private boolean selected = false;
                    private int fromRow, fromCol;

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (!selected && !board[row][col].isEmpty()) {
                            // Select piece to move
                            selected = true;
                            fromRow = row;
                            fromCol = col;
                            button.setBackground(Color.YELLOW); // Highlight selected
                        } else if (selected) {
                            // Move the piece
                            board[row][col] = board[fromRow][fromCol];
                            board[fromRow][fromCol] = "";
                            updateButtons();
                            selected = false;
                        }
                    }
                });

                buttons[i][j] = button;
                frame.add(button);
            }
        }
        updateButtons();
    }

    private void updateButtons() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                buttons[i][j].setIcon(getPieceIcon(board[i][j], i));
                buttons[i][j].setText(""); // Clear any text
                buttons[i][j].setBackground((i + j) % 2 == 0 ? new Color(230,230,235) : new Color(44,40,85));
            }
        }
    }

    private ImageIcon getPieceIcon(String piece, int row) {
        if (piece.isEmpty()) {
            return null;
        }

        int colorIndex = row < BOARD_SIZE / 2 ? 0 : 1; // Black for top rows, white for bottom rows
        switch (piece) {
            case "R": return pieceImages[colorIndex][0];
            case "N": return pieceImages[colorIndex][1];
            case "B": return pieceImages[colorIndex][2];
            case "Q": return pieceImages[colorIndex][3];
            case "K": return pieceImages[colorIndex][4];
            case "P": return pieceImages[colorIndex][5];
            default: return null;
        }
    }

    public static void main(String[] args) {
        new ChessGame();
    }
}
