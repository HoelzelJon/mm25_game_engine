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
    String urlString;

    private static final int MAX_TURN_TIME_MILLIS = 5000;

    public ServerPlayerCommunicator(int playerNum, String urlString) {
        super(playerNum);
        this.urlString = urlString;
    }

    @Override
    public UnitSetup[] getUnitsSetup() {
        return new UnitSetup[3];
    }

    public Decision getDecision(Game gameState) {

        HttpURLConnection connection;

        try {
            URL url = new URL(urlString + "turn");

            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(MAX_TURN_TIME_MILLIS);
        } catch (MalformedURLException ex) {
            System.err.println("MalformedURLException found when connecting to player #" + playerNum);
            System.err.println("URL= " + urlString);
            return null;
        } catch (IOException ex) {
            System.err.println("IOException when opening URL connection to player #" + playerNum);
            System.err.print("URL= " + urlString);
            return null;
        }

        String gameJson = gameState.getRecentPlayerJson();
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
            os.write(gameJson.getBytes());
        } catch (IOException ex) {
            System.err.println("IOException when doing getOutputStream on HTTPConnection");
            return null;
        }

        Gson gson = new Gson();

        try {
            InputStream is = connection.getInputStream();
            // taken from https://stackoverflow.com/questions/309424/how-do-i-read-convert-an-inputstream-into-a-string-in-java
            Scanner s = new Scanner(is).useDelimiter("\\A");
            String result = s.hasNext() ? s.next() : "";
            return gson.fromJson(result, Decision.class);
        } catch (IOException ex) {
            System.err.println("IOException when doing getInputStream on HTTPConnection");
            return null;
        }
    }
}
