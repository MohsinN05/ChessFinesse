package Game;
import java.io.Serializable;

public class GameRecord implements Serializable {
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