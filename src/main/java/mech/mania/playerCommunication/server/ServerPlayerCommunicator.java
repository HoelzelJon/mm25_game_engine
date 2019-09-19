package mech.mania.playerCommunication.server;

import com.google.gson.*;
import mech.mania.Board;
import mech.mania.Game;
import mech.mania.playerCommunication.*;
import mech.mania.playerCommunication.server.GameState;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class ServerPlayerCommunicator extends PlayerCommunicator {
    private String urlString;
    private Gson gson;

    private static final int MAX_TURN_TIME_MILIS = 5000;
    private static final int MAX_INIT_DECISION_TIME_MILIS = 5000;

    public ServerPlayerCommunicator(int playerNum, String urlString) {
        super(playerNum);
        this.urlString = urlString;
        gson = new Gson();
    }

    private String getResponse(String argument, int timeout, String data) {
        HttpURLConnection connection;

        try {
            URL url = new URL(urlString + argument);

            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(timeout);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }

        try {
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
        } catch (ProtocolException ex) {
            System.err.println("ProtocolException when setting request method to POST");
            ex.printStackTrace();
            return null;
        }

        try  {
            OutputStream os = connection.getOutputStream();
            os.write(data.getBytes());
        } catch (IOException ex) {
            System.err.println("IOException when doing getOutputStream on HTTPConnection");
            ex.printStackTrace();
            return null;
        }

        try {
            InputStream is = connection.getInputStream();
            // taken from https://stackoverflow.com/questions/309424/how-do-i-read-convert-an-inputstream-into-a-string-in-java
            Scanner s = new Scanner(is).useDelimiter("\\A");
            return s.hasNext() ? s.next() : "";
        } catch (IOException ex) {
            System.err.println("IOException when doing getInputStream on HTTPConnection");
            ex.printStackTrace();
            return null;
        } finally {
            connection.disconnect();
        }
    }

    @Override
    public List<UnitSetup> getUnitsSetup(Board board) throws InvalidSetupException {
        String initJson = "{\n\"playerNum\":" + this.playerNum + ",\n\"gameId\":\"" + board.getGameId() + "\"\n}";

        String setupString = getResponse("game_init", MAX_INIT_DECISION_TIME_MILIS, initJson);

        if (setupString == null) {
            throw new InvalidSetupException("No response received from server");
        } else {
            try {
                UnitSetup[] setup = gson.fromJson(setupString, UnitSetup[].class);
                if (setup == null) {
                    throw new InvalidSetupException("Json parsed to NULL");
                } else {
                    return Arrays.asList(setup);
                }
            } catch (JsonSyntaxException ex) {
                throw new InvalidSetupException("Invalid JSON syntax: " + ex.getMessage());
            }
        }
    }

    public List<UnitDecision> getDecision(Game gameState) throws InvalidDecisionException {
        String gameJson = gson.toJson(new GameState(gameState));

        String decString = getResponse("turn", MAX_TURN_TIME_MILIS, gameJson);

        if (decString == null) {
            return null;
        } else {
            try {
                UnitDecision[] decisions = gson.fromJson(decString, UnitDecision[].class);
                if (decisions == null) {
                    throw new InvalidDecisionException("Gson returned null array of decisions");
                } else {
                    return Arrays.asList(decisions);
                }
            } catch (JsonSyntaxException ex) {
                throw new InvalidDecisionException("Exception found while parsing decision JSON");
            }

        }
    }

    @Override
    public void sendGameOver(String gameId, int winner) {
        String overMsg = "{\"gameId\": \"" + gameId + "\", \"winner\": " + winner + "}";

        getResponse("game_over", MAX_INIT_DECISION_TIME_MILIS, overMsg);
    }
}
