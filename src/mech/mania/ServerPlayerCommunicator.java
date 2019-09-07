package mech.mania;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Scanner;

public class ServerPlayerCommunicator extends PlayerCommunicator {
    private String urlString;

    private static final int MAX_TURN_TIME_MILIS = 5000;
    private static final int MAX_INIT_DECISION_TIME_MILIS = 5000;

    public ServerPlayerCommunicator(int playerNum, String urlString) {
        super(playerNum);
        this.urlString = urlString;
    }

    private String getResponse(String argument, int timeout, String data) {
        HttpURLConnection connection;

        try {
            URL url = new URL(urlString + argument);

            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(timeout);
        } catch (MalformedURLException ex) {
            System.err.println("MalformedURLException found when connecting to player #" + playerNum);
            System.err.println("URL= " + urlString);
            return null;
        } catch (IOException ex) {
            System.err.println("IOException when opening URL connection to player #" + playerNum);
            System.err.print("URL= " + urlString);
            return null;
        }

        try {
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
        } catch (ProtocolException ex) {
            System.err.println("ProtocolException when setting request method to POST");
            return null;
        }

        try  {
            OutputStream os = connection.getOutputStream();
            os.write(data.getBytes());
        } catch (IOException ex) {
            System.err.println("IOException when doing getOutputStream on HTTPConnection");
            return null;
        }

        try {
            InputStream is = connection.getInputStream();
            // taken from https://stackoverflow.com/questions/309424/how-do-i-read-convert-an-inputstream-into-a-string-in-java
            Scanner s = new Scanner(is).useDelimiter("\\A");
            return s.hasNext() ? s.next() : "";
        } catch (IOException ex) {
            System.err.println("IOException when doing getInputStream on HTTPConnection");
            return null;
        } finally {
            connection.disconnect();
        }
    }

    @Override
    public UnitSetup[] getUnitsSetup(Map map) {
        String initJson = "{\n\"playerNum\":" + this.playerNum + ",\n\"gameId\":\"" + map.getGameId() + "\"\n}";

        String setupString = getResponse("game_init", MAX_INIT_DECISION_TIME_MILIS, initJson);

        if (setupString == null) {
            return null;
        } else {
            Gson gson = new Gson();
            return gson.fromJson(setupString, UnitSetup[].class);
        }
    }

    public Decision getDecision(Game gameState) {
        String gameJson = gameState.getRecentPlayerJson();

        String decString = getResponse("turn", MAX_TURN_TIME_MILIS, gameJson);

        if (decString == null) {
            return null;
        } else {
            Gson gson = new Gson();
            return gson.fromJson(decString, Decision.class);
        }
    }


    @Override
    public void sendGameOver(String gameId) {
        String overMsg = "{\"gameId\": \"" + gameId + "\"}";

        getResponse("game_over", MAX_INIT_DECISION_TIME_MILIS, overMsg);
    }
}
