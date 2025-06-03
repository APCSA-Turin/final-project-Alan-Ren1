package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class API {
    //API Token
    private static final String API_TOKEN = "lip_u2UvDGqYikyhkWxPeLOf";


   /*
   This method retrieves data from lichess api by using the argument username and insert it at the end of the endpoint
   This method returns all data about the user
    */
    public static String getPlayerData(String username) throws Exception {
        String endpoint = "https://lichess.org/api/user/" + username;
        URL url = new URL(endpoint);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Bearer " + API_TOKEN);
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("User-Agent", "Java-Chess-Application");

        BufferedReader buff = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = buff.readLine()) != null) {
            content.append(inputLine);
        }
        buff.close();
        connection.disconnect();

        return content.toString();
    }

    /*
    This method reads the Players file by using the argument "Players"
    it reads each line of code, trims the endspaces (if any)
    and places all of the names into an arraylist called playerNames
     */
    public static ArrayList<String> getPlayerNames(String filename) throws Exception {
        ArrayList<String> playerNames = new ArrayList<>();

        BufferedReader reader = new BufferedReader(new InputStreamReader(API.class.getClassLoader().getResourceAsStream(filename)));
        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (!line.isEmpty()) {
                playerNames.add(line);
            }
        }
        reader.close();

        return playerNames;
    }

    /*
    This method returns a random playerName from the arraylist playerNames
    If the playerNames is empty, it throws an exception
     */
    public static String getRandomPlayerName(String filename) throws Exception {
        ArrayList<String> playerNames = getPlayerNames(filename);
        if (playerNames.isEmpty()) {
            throw new Exception("No player names found in file");
        }
        int index = (int) (Math.random() * playerNames.size());
        return playerNames.get(index);
    }
}