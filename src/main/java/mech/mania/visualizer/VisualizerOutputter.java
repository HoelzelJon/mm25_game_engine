package mech.mania.visualizer;
import com.google.gson.Gson;
import mech.mania.Game;
import mech.mania.visualizer.initial.InitialGameRepresentation;
import mech.mania.visualizer.perTurn.TurnRepresentation;

import java.io.FileWriter;
import java.io.IOException;

public class VisualizerOutputter {
    Gson gson;
    private boolean useStdOut;
    private FileWriter fileWriter;

    public VisualizerOutputter() {
        useStdOut = true;
        gson = new Gson();
    }

    public VisualizerOutputter(String outputFile) {
        useStdOut = false;
        gson = new Gson();
        try {
            fileWriter = new FileWriter(outputFile, true);
        } catch (IOException e){
            System.err.println("IOException reached for visualizer output file. Outputting to stdout instead. " +
                    "Error info: " + e.getMessage());
            useStdOut = true;
        }
    }

    public void printInitialVisualizerJson(InitialGameRepresentation game) throws IOException{
        if (useStdOut) { // Print to stdout
            System.out.println(gson.toJson(game));
        } else { // Print to outputFile
            fileWriter.write(gson.toJson(game) + "\n");
            fileWriter.flush();
        }
    }

    public void printSingleTurnVisualizerJson(TurnRepresentation turn) throws IOException {
        if (useStdOut) { // Print to stdout
            System.out.println(gson.toJson(turn));
        } else { // Print to outputFile
            fileWriter.write(gson.toJson(turn) + "\n");
            fileWriter.flush();
        }
    }

    public void printWinnerJSON(int winner) throws IOException {
        if (useStdOut) {
            if (winner == Game.TIE) {
                System.out.println("{\"Winner\": 0}");
            } else if (winner == Game.P1_WINNER) {
                System.out.println("{\"Winner\": 1}");
            } else if (winner == Game.P2_WINNER) {
                System.out.println("{\"Winner\": 2}");
            }
        } else {
            if (winner == Game.TIE) {
                fileWriter.write("{\"Winner\": 0}" + "\n");
            } else if (winner == Game.P1_WINNER) {
                fileWriter.write("{\"Winner\": 1}" + "\n");
            } else if (winner == Game.P2_WINNER) {
                fileWriter.write("{\"Winner\": 2}" + "\n");
            }
            fileWriter.flush();
        }
    }

    public void sendGameOver() throws IOException{
        if (!useStdOut){
            fileWriter.close();
        }
    }
}
