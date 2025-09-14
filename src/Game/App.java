package Game;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class App extends JFrame {
    private CardLayout cardLayout;
    private JPanel cardPanel;
    public JButton continueButton;
    private ChessGUI currentGame;

    public App() {
        // Frame configuration
        setTitle("Chess Finesse");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 750);
        setResizable(false);

        // Set dark gray background
        Color darkGray = new Color(50, 50, 50);
        getContentPane().setBackground(darkGray);

        // Create CardLayout manager
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(darkGray);

        // Create and add panels
        cardPanel.add(createMainMenuPanel(), "MainMenu");
        cardPanel.add(createGameModeSelectionPanel(), "GameModeSelection");

        add(cardPanel, BorderLayout.CENTER);

        // Center the frame
        setLocationRelativeTo(null);
    }

    private JPanel createMainMenuPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(50, 50, 50));

        // Add image to top
        mainPanel.add(createImagePanel(), BorderLayout.NORTH);

        // Create button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(50, 50, 50));
        buttonPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 0, 15, 0);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Create buttons
        continueButton = createMenuButton("Continue");
        JButton newGameButton = createMenuButton("New Game");
        JButton historyButton = createMenuButton("Game History");
        JButton exitButton = createMenuButton("Exit");

        // Initially hide continue button
        continueButton.setVisible(false);

        // Add action listeners
        newGameButton.addActionListener(e -> {
            if (currentGame != null) {
                int choice = JOptionPane.showConfirmDialog(
                    this,
                    "A game is already in progress. Do you want to start a new one?",
                    "Confirm New Game",
                    JOptionPane.YES_NO_OPTION
                );

                if (choice != JOptionPane.YES_OPTION) {
                    return; // User chose NO, so don't start a new game
                }
            }
            
            // Show game mode selection instead of directly getting player names
            cardLayout.show(cardPanel, "GameModeSelection");
        });

        historyButton.addActionListener(e -> showGameHistory());
        continueButton.addActionListener(e -> cardLayout.show(cardPanel, "GAME"));
        exitButton.addActionListener(e -> System.exit(0));

        // Add buttons to panel
        buttonPanel.add(continueButton, gbc);
        buttonPanel.add(newGameButton, gbc);
        buttonPanel.add(historyButton, gbc);
        buttonPanel.add(exitButton, gbc);

        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        return mainPanel;
    }

    private JPanel createGameModeSelectionPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(50, 50, 50));

        // Title
        JLabel titleLabel = new JLabel("Select Game Mode", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(50, 0, 30, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Create button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(50, 50, 50));
        buttonPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 0, 20, 0);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Create mode selection buttons
        JButton humanVsHumanButton = createMenuButton("Human vs Human");
        JButton humanVsAIButton = createMenuButton("Human vs AI");
        JButton backButton = createMenuButton("Back to Menu");

        // Make these buttons slightly larger for better visibility
        Dimension buttonSize = new Dimension(250, 50);
        humanVsHumanButton.setPreferredSize(buttonSize);
        humanVsAIButton.setPreferredSize(buttonSize);
        backButton.setPreferredSize(buttonSize);

        // Add action listeners
        humanVsHumanButton.addActionListener(e -> {
            String[] names = getPlayerNames(false); // false = Human vs Human
            if (names != null) {
                newGame(names[0], names[1]);
                continueButton.setVisible(true);
            }
        });

        humanVsAIButton.addActionListener(e -> {
            String[] names = getPlayerNames(true); // true = Human vs AI
            if (names != null) {
                newGame(names[0], names[1]);
                continueButton.setVisible(true);
            }
        });

        backButton.addActionListener(e -> cardLayout.show(cardPanel, "MainMenu"));

        // Add buttons to panel
        buttonPanel.add(humanVsHumanButton, gbc);
        buttonPanel.add(humanVsAIButton, gbc);
        
        // Add some space before back button
        gbc.insets = new Insets(40, 0, 20, 0);
        buttonPanel.add(backButton, gbc);

        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        return mainPanel;
    }

    public void toMenu() {
        cardLayout.show(cardPanel, "MainMenu");
    }

    private void showGameHistory() {
        java.util.List<GameRecord> history = GameHistoryHandler.loadGameHistory();

        if (history.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No game history available.", "Game History", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (GameRecord record : history) {
            sb.append(record.getPlayerName())
              .append(" vs ")
              .append(record.getOpponentName())
              .append(" - ");

            if ("Draw".equalsIgnoreCase(record.getWinner())) {
                sb.append("Result: Draw");
            } else {
                sb.append("Winner: ").append(record.getWinner());
            }
            sb.append("\n");
        }

        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 400));

        JOptionPane.showMessageDialog(this, scrollPane, "Game History", JOptionPane.INFORMATION_MESSAGE);
    }

    public void newGame(String player1, String player2) {
        currentGame = new ChessGUI(this, player1, player2);
        cardPanel.add(currentGame, "GAME");
        cardLayout.show(cardPanel, "GAME");
    }

    private JPanel createImagePanel() {
        JPanel imagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        imagePanel.setBackground(new Color(50, 50, 50));
        imagePanel.setPreferredSize(new Dimension(1100, 300));

        try {
            BufferedImage originalImage = ImageIO.read(new File("resources//images//logo.png"));
            int maxWidth = 800;
            int maxHeight = 250;
            Dimension scaledDim = getScaledDimension(
                new Dimension(originalImage.getWidth(), originalImage.getHeight()),
                new Dimension(maxWidth, maxHeight)
            );

            Image scaledImage = originalImage.getScaledInstance(
                scaledDim.width, scaledDim.height, Image.SCALE_SMOOTH);
            JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
            imagePanel.add(imageLabel);
        } catch (IOException e) {
            JLabel errorLabel = new JLabel("Image not found", SwingConstants.CENTER);
            errorLabel.setForeground(Color.WHITE);
            errorLabel.setFont(new Font("Arial", Font.BOLD, 24));
            imagePanel.add(errorLabel);
            e.printStackTrace();
        }

        return imagePanel;
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(200, 40));
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(70, 70, 70));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 2));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(new Color(90, 90, 90));
            }

            public void mouseExited(MouseEvent evt) {
                button.setBackground(new Color(70, 70, 70));
            }
        });

        return button;
    }

    private Dimension getScaledDimension(Dimension imgSize, Dimension boundary) {
        int originalWidth = imgSize.width;
        int originalHeight = imgSize.height;
        int boundWidth = boundary.width;
        int boundHeight = boundary.height;
        int newWidth = originalWidth;
        int newHeight = originalHeight;

        if (originalWidth > boundWidth) {
            newWidth = boundWidth;
            newHeight = (newWidth * originalHeight) / originalWidth;
        }

        if (newHeight > boundHeight) {
            newHeight = boundHeight;
            newWidth = (newHeight * originalWidth) / originalHeight;
        }

        return new Dimension(newWidth, newHeight);
    }

    public String[] getPlayerNames(boolean isHumanVsAI) {
        JTextField player1Field = new JTextField();
        JTextField player2Field = new JTextField();

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        if (isHumanVsAI) {
            // Human vs AI mode - only ask for human player name
            panel.add(new JLabel("Enter Your Name (White Player):"));
            panel.add(player1Field);
            
            int result = JOptionPane.showConfirmDialog(
                this, panel, "Human vs AI", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                String humanPlayer = player1Field.getText().trim();
                if (humanPlayer.isEmpty()) humanPlayer = "Human";
                return new String[]{humanPlayer, "stockfish-windows-x86-64-sse41-popcnt"};
            }
        } else {
            // Human vs Human mode - ask for both player names
            panel.add(new JLabel("Enter White Player's Name:"));
            panel.add(player1Field);
            panel.add(Box.createVerticalStrut(10));
            panel.add(new JLabel("Enter Black Player's Name:"));
            panel.add(player2Field);

            int result = JOptionPane.showConfirmDialog(
                this, panel, "Human vs Human", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                String p1 = player1Field.getText().trim();
                String p2 = player2Field.getText().trim();
                if (p1.isEmpty()) p1 = "Player 1";
                if (p2.isEmpty()) p2 = "Player 2";
                return new String[]{p1, p2};
            }
        }
        return null;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            App menu = new App();
            menu.setVisible(true);
        });
    }
}