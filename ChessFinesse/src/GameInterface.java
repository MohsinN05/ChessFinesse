package ChessFinesse.src;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.awt.*;

import javax.sound.sampled.*;

public class GameInterface {
    private static Clip soundClip = null; // For playing sound
    private static final String SOUND_FILE_PATH = "your_audio_file.wav"; // Replace with the path to your audio file
    private static boolean isSoundPlaying = true; // Flag to track the sound state
    static GameView currentGame;
    
        public static void main(String[] args) {
            // Create the main frame
            JFrame frame = new JFrame("Chess Finesse");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setLayout(null);
    
            // Background panel with black background
            JPanel backgroundPanel = new JPanel();
            backgroundPanel.setBackground(Color.BLACK);
            backgroundPanel.setLayout(null);
            frame.setContentPane(backgroundPanel);
    
            // Get screen width and height
            int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
            int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
    
            // Calculate vertical part width
            int verticalPartWidth = screenWidth / 4;
    
            // Single Player Button (Leftmost part)
            JButton singlePlayerButton = new JButton();
            singlePlayerButton.setBounds(0, 0, verticalPartWidth, screenHeight);
            styleImageButton(singlePlayerButton, "ChessFinesse//Pics&Vids//Pics//pawn.jpeg"); // Replace with your image
            singlePlayerButton.addActionListener(e -> System.out.println("Single Player Game Selected"));
            backgroundPanel.add(singlePlayerButton);
    
            // Two Player Button (Rightmost part)
            JButton twoPlayerButton = new JButton();
            twoPlayerButton.setBounds(3 * verticalPartWidth, 0, verticalPartWidth, screenHeight);
            styleImageButton(twoPlayerButton, "ChessFinesse//Pics&Vids//Pics//two_pawns.jpeg"); // Replace with your image
            twoPlayerButton.addActionListener(e -> showPlayerForm(frame));
            backgroundPanel.add(twoPlayerButton);
    
            // Logo in the center of the middle two parts
            JLabel logoLabel = new JLabel(new ImageIcon(new ImageIcon("ChessFinesse//Pics&Vids//Pics//logo.png").getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH))); // Replace with your logo image
            logoLabel.setBounds(screenWidth / 2 - 150, screenHeight / 3 - 150, 300, 300);
            backgroundPanel.add(logoLabel);
    
            // Bottom Panel Buttons (Settings, Game History, Quit)
            int buttonWidth = 150, buttonHeight = 50, spacing = 20;
            JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, spacing, 10));
            bottomPanel.setOpaque(false);
            bottomPanel.setBounds(screenWidth / 4, 3 * screenHeight / 4, 2 * verticalPartWidth, buttonHeight);
    
            JButton settingsButton = new JButton("Settings");
            styleTextButton(settingsButton);
            settingsButton.addActionListener(e -> showSettings(frame));
    
            JButton gameHistoryButton = new JButton("Game History");
            styleTextButton(gameHistoryButton);
            gameHistoryButton.addActionListener(e -> gameHistoryClicked());
    
            JButton quitButton = new JButton("Quit");
            styleTextButton(quitButton);
            quitButton.addActionListener(e -> System.exit(0));
    
            bottomPanel.add(settingsButton);
            bottomPanel.add(gameHistoryButton);
            bottomPanel.add(quitButton);
    
            backgroundPanel.add(bottomPanel);
    
            // Show frame
            frame.setVisible(true);
    
            // Repaint components after resizing
            frame.addComponentListener(new java.awt.event.ComponentAdapter() {
                public void componentResized(java.awt.event.ComponentEvent evt) {
                    int newScreenWidth = frame.getWidth();
                    int newScreenHeight = frame.getHeight();
                    int newVerticalPartWidth = newScreenWidth / 4;
    
                    singlePlayerButton.setBounds(0, 0, newVerticalPartWidth, newScreenHeight);
                    twoPlayerButton.setBounds(3 * newVerticalPartWidth, 0, newVerticalPartWidth, newScreenHeight);
                    logoLabel.setBounds(newScreenWidth / 2 - 150, newScreenHeight / 3 - 150, 300, 300);
                    bottomPanel.setBounds(newScreenWidth / 4, 3 * newScreenHeight / 4, 2 * newVerticalPartWidth, buttonHeight);
                    backgroundPanel.repaint();
                }
            });
    
            // Play sound by default
            playSound(true);
        }
    
        private static void gameHistoryClicked(){
            List<GameRecord> gameRecords = GameHistoryHandler.loadGameHistory();
    
            // Create a new JFrame to display the game history
            JFrame frame = new JFrame("Game History");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setSize(700, 700);
            frame.setLayout(new BorderLayout());
    
            // Define column names for the table
            String[] columnNames = {"Player 1", "Player 2", "Winner"};
    
            // Prepare data for the table from the loaded game records
            Object[][] data = new Object[gameRecords.size()][3];
            for (int i = 0; i < gameRecords.size(); i++) {
                GameRecord record = gameRecords.get(i);
                data[i][0] = record.getPlayerName();
                data[i][1] = record.getOpponentName();
                data[i][2] = record.getWinner();            
            }
    
            // Create a table with the data and column names
            DefaultTableModel tableModel = new DefaultTableModel(data, columnNames);
            JTable table = new JTable(tableModel);
    
            // Set table properties (e.g., make the table non-editable)
            table.setFillsViewportHeight(true);
            table.setDefaultEditor(Object.class, null); // Makes the table non-editable
    
            // Add the table to a JScrollPane so that it is scrollable
            JScrollPane scrollPane = new JScrollPane(table);
            frame.add(scrollPane, BorderLayout.CENTER);
    
            // Set the frame visible
            frame.setVisible(true);
    
    
        }
    
        private static void showSettings(JFrame parentFrame) {
            JDialog settingsDialog = new JDialog(parentFrame, "Settings", true);
            settingsDialog.setSize(500, 400);
            settingsDialog.setLayout(new GridBagLayout());
            settingsDialog.setLocationRelativeTo(parentFrame);
    
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.fill = GridBagConstraints.HORIZONTAL;
    
            // Sound Toggle Button (styled similar to Close Button)
            JButton soundButton = new JButton("Sound: ON");
            soundButton.setFont(new Font("Arial", Font.BOLD, 18));
            soundButton.setBackground(new Color(0, 0, 0, 150));
            soundButton.setForeground(Color.WHITE);
            soundButton.setFocusPainted(false);
            soundButton.addActionListener(e -> toggleSound(soundButton));
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 2;
            settingsDialog.add(soundButton, gbc);
    
            // About Game Button (Show version and developer info)
            JButton aboutGameButton = new JButton("About Game");
            aboutGameButton.setFont(new Font("Arial", Font.BOLD, 18));
            aboutGameButton.setBackground(new Color(0, 0, 0, 150));
            aboutGameButton.setForeground(Color.WHITE);
            aboutGameButton.setFocusPainted(false);
            aboutGameButton.addActionListener(e -> showAboutDialog(settingsDialog));
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.gridwidth = 2;
            settingsDialog.add(aboutGameButton, gbc);
    
            // Credits Button (Show version and developer info)
            JButton creditsButton = new JButton("Credits");
            creditsButton.setFont(new Font("Arial", Font.BOLD, 18));
            creditsButton.setBackground(new Color(0, 0, 0, 150));
            creditsButton.setForeground(Color.WHITE);
            creditsButton.setFocusPainted(false);
            creditsButton.addActionListener(e -> showCreditsDialog(settingsDialog));
            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.gridwidth = 2;
            settingsDialog.add(creditsButton, gbc);
    
            // Close Button
            JButton closeButton = new JButton("Close");
            closeButton.setFont(new Font("Arial", Font.BOLD, 18));
            closeButton.setBackground(new Color(0, 0, 0, 150));
            closeButton.setForeground(Color.WHITE);
            closeButton.setFocusPainted(false);
            closeButton.addActionListener(e -> settingsDialog.dispose());
            gbc.gridx = 0;
            gbc.gridy = 3;
            gbc.gridwidth = 2;
            gbc.anchor = GridBagConstraints.CENTER;
            settingsDialog.add(closeButton, gbc);
    
            settingsDialog.setVisible(true);
        }
    
        private static void toggleSound(JButton soundButton) {
            if (isSoundPlaying) {
                soundButton.setText("Sound: OFF");
                stopSound();
            } else {
                soundButton.setText("Sound: ON");
                playSound(true);
            }
            isSoundPlaying = !isSoundPlaying;
        }
    
        private static void playSound(boolean loop) {
            try {
                if (soundClip != null && soundClip.isRunning()) {
                    soundClip.stop();
                }
                File soundFile = new File("ChessFinesse//Pics&Vids//Audio//videoplayback.wav"); // Replace with your sound file path
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
                soundClip = AudioSystem.getClip();
                soundClip.open(audioStream);
                if (loop) {
                    soundClip.loop(Clip.LOOP_CONTINUOUSLY); // Loop the sound
                } else {
                    soundClip.start();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Unable to play the selected sound file.", "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    
        private static void stopSound() {
            if (soundClip != null && soundClip.isRunning()) {
                soundClip.stop();
            }
        }
    
        private static void showAboutDialog(JDialog parentDialog) {
            JOptionPane.showMessageDialog(parentDialog,
                    "Chess Game\nVersion 1.0\nDeveloped by:\nAwais\nSaad\nMoshin",
                    "About Game",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    
        private static void showCreditsDialog(JDialog parentDialog) {
            JOptionPane.showMessageDialog(parentDialog,
                    "Chess Game\nVersion 1.0\nDeveloped by:\nAwais\nSaad\nMoshin",
                    "Credits",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    
        private static void showPlayerForm(JFrame parentFrame) {
            JDialog dialog = new JDialog(parentFrame, "Enter Player Names", true);
            dialog.setSize(400, 300);
            dialog.setLayout(new GridBagLayout());
            dialog.setLocationRelativeTo(parentFrame);
    
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.fill = GridBagConstraints.HORIZONTAL;
    
            JLabel playerNameLabel = new JLabel("Player Name:");
            gbc.gridx = 0;
            gbc.gridy = 0;
            dialog.add(playerNameLabel, gbc);
    
            JTextField playerNameField = new JTextField(20);
            gbc.gridx = 1;
            gbc.gridy = 0;
            dialog.add(playerNameField, gbc);
    
            JLabel opponentNameLabel = new JLabel("Opponent Name:");
            gbc.gridx = 0;
            gbc.gridy = 1;
            dialog.add(opponentNameLabel, gbc);
    
            JTextField opponentNameField = new JTextField(20);
            gbc.gridx = 1;
            gbc.gridy = 1;
            dialog.add(opponentNameField, gbc);
    
            JButton enterGameButton = new JButton("Enter the Game");
            enterGameButton.setEnabled(true);
            enterGameButton.addActionListener(e -> {
                String playerName = playerNameField.getText().trim();
                String opponentName = opponentNameField.getText().trim();
                String winner = "Stalemate";
    
                List<GameRecord> gameRecords = GameHistoryHandler.loadGameHistory();
                gameRecords.add(new GameRecord(playerName, opponentName, winner));
                GameHistoryHandler.saveGameHistory(gameRecords);
                currentGame = new GameView(parentFrame);
                dialog.dispose();
        });

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        dialog.add(enterGameButton, gbc);

        DocumentListener validateInputs = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateButtonState();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateButtonState();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateButtonState();
            }

            private void updateButtonState() {
                boolean isValid = !playerNameField.getText().trim().isEmpty() && !opponentNameField.getText().trim().isEmpty();
                enterGameButton.setEnabled(isValid);
            }
        };

        playerNameField.getDocument().addDocumentListener(validateInputs);
        opponentNameField.getDocument().addDocumentListener(validateInputs);

        dialog.setVisible(true);
    }

    private static void styleImageButton(JButton button, String imagePath) {
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setIcon(new ImageIcon(new ImageIcon(imagePath).getImage().getScaledInstance(button.getWidth(), button.getHeight(), Image.SCALE_SMOOTH)));
    }

    private static void styleTextButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setBackground(new Color(0, 0, 0, 150));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
    }
}

class GameRecord implements Serializable {
    private String playerName;
    private String opponentName;
    private String winner;

    public GameRecord(String playerName, String opponentName, String winner) {
        this.playerName = playerName;
        this.opponentName = opponentName;
        this.winner = winner;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getOpponentName() {
        return opponentName;
    }

    public String getWinner() {
        return winner;
    }
}

class GameHistoryHandler {
    private static final String FILE_PATH = "ChessFinesse//Data//game_history.bin";

    public static void saveGameHistory(List<GameRecord> gameRecords) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(gameRecords);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error saving game history: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static List<GameRecord> loadGameHistory() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            return (List<GameRecord>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>(); // Return an empty list if file doesn't exist or is corrupted
        }
    }
}