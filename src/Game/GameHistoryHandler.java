package Game;
import java.util.List;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class GameHistoryHandler {
    private static final String FILE_PATH = "Data//game_history.bin";

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
