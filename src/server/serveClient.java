/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;

/**
 *
 * @author najla
 */
public class serveClient {
    private static final Logger LOGGER=Logger.getLogger(serveClient.class.getName());

    public static void serveClient(Socket client, Integer counter,int exchangeInterval) throws URISyntaxException, IOException {
    	boolean debug=true;
            Handler consoleHandler=null;
            consoleHandler=new ConsoleHandler();
            LOGGER.addHandler(consoleHandler);
            consoleHandler.setLevel(Level.ALL);
            LOGGER.setLevel(Level.ALL);
        try (Socket clientSocket = client) {

            // The JSON Parser
            JSONParser parser = new JSONParser();
            // Input stream
            DataInputStream input = new DataInputStream(clientSocket.getInputStream());
            // Output Stream
            DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream());
            // receiving message 
           
            
           
           String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
           System.out.println(time + " - [INFO] - connection with client " + counter + " established.");
           
            
            
            while (true) {
                if (input.available() > 0) {
                    // Attempt to convert read data to JSON
                    JSONObject command = (JSONObject) parser.parse(input.readUTF());
                    parseCommand pc = new parseCommand();
                    pc.parseCommand(command, output,exchangeInterval);
                   

                }
            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

}
