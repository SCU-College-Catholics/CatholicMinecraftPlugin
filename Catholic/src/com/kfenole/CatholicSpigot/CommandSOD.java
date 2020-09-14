package com.kfenole.CatholicSpigot;


import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.time.LocalDateTime; // import the LocalDateTime class
import java.time.LocalDate; // import the LocalDate class


public class CommandSOD implements CommandExecutor {

    // This method is called, when somebody uses our command
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            //First get the date for today
            LocalDateTime myObj = LocalDateTime.now(); // Create a date object
            //need to subtract 7 hours to compensate for server time, then convert to string
            LocalDateTime t = myObj.minusHours(7);
            LocalDate ld = t.toLocalDate();
            String dateString = (String) ld.toString();
            String fixedString = dateString.replace('-', '/');

            String url = "http://calapi.inadiutorium.cz/api/v0/en/calendars/default/" + fixedString;

            try {
                URL urlOBJ = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlOBJ.openConnection();

                // Submit GET request
                connection.setRequestMethod("GET");
                connection.setRequestProperty("User-Agent", "Mozilla/5.0");


                Integer responseCode = connection.getResponseCode();


                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader inputReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();

                    while ((inputLine = inputReader.readLine()) != null) {
                        response.append(inputLine);
                    }

                    inputReader.close();

                    JSONParser jsonParser = new JSONParser();

                    try {
                        /* Example Response:
                        {"date":"2020-07-22","season":"ordinary","season_week":16,"celebrations":[{"title":"Saint Mary Magdalene","colour":"white","rank":"feast","rank_num":2.7}],"weekday":"wednesday"}
                         */
                        JSONObject obj = (JSONObject) jsonParser.parse(response.toString());
                        JSONArray sCelL = (JSONArray) obj.get("celebrations");

                        int i = 0;
                        while (i != sCelL.size()) {
                            JSONObject sCel = (JSONObject) sCelL.get(i);

                            // Init Variables
                            String sName = "";
                            int sColor = 0;
                            String sRank = "";

                            // Get info
                            sName = (String) sCel.get("title");
                            sRank = (String) sCel.get("rank");
                            String stringColor = (String) sCel.get("colour");
                            // Send the message with the color matching the liturgical color
                            if (stringColor.equals("green")) {
                                player.sendMessage(ChatColor.GREEN + "Today is the " + sRank + " of " + sName);
                            } else if (stringColor.equals("violet")) {
                                player.sendMessage(ChatColor.DARK_PURPLE + "Today is the " + sRank + " of " + sName);
                            } else if (stringColor.equals("white")) {
                                player.sendMessage("Today is the " + sRank + " of " + sName);
                            } else if (stringColor.equals("red")) {
                                player.sendMessage(ChatColor.RED + "Today is the " + sRank + " of " + sName);
                            } else {
                                player.sendMessage(stringColor);
                            }
                            i++;
                        }


                    } catch (ParseException ef) {
                        // JSON Parse Error
                        player.sendMessage("&4Error Parsing JSON");
                    }

                }
            } catch (IOException e) {
                // Error with HTTP (or something else)
                throw new RuntimeException(e);
            }
        }

        // If the player (or console) uses our command correct, we can return true
        return true;
    }

}