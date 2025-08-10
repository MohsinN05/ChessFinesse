package ChessFinesse.src;

import java.io.*;

public class StockfishConnector {
    private Process engine;
    private BufferedReader reader;
    private OutputStreamWriter writer;

    public boolean startEngine(String path) {
        try {
            ProcessBuilder pb = new ProcessBuilder(path);
            pb.redirectErrorStream(true);
            engine = pb.start();
            reader = new BufferedReader(new InputStreamReader(engine.getInputStream()));
            writer = new OutputStreamWriter(engine.getOutputStream());
            sendCommand("uci"); // Initialize UCI
            readOutput();       // Flush initial engine messages
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void sendCommand(String command) {
        try {
            writer.write(command + "\n");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readOutput() {
        StringBuilder sb = new StringBuilder();
        try {
            while (reader.ready()) {
                sb.append(reader.readLine()).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public String getBestMove(String moves) {
        sendCommand("position startpos moves " + moves);
        sendCommand("go movetime 1000"); // 1 second thinking time
        String output;
        String bestMove = null;

        try {
            while ((output = reader.readLine()) != null) {
                if (output.startsWith("bestmove")) {
                    bestMove = output.split(" ")[1];
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bestMove;
    }

    public void stopEngine() {
        try {
            sendCommand("quit");
            reader.close();
            writer.close();
            engine.destroy();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
