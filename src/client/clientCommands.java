/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import org.json.simple.JSONObject;
import Utilities.Resource;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;

public class clientCommands {

    private static final Logger LOGGER = Logger.getLogger(clientCommands.class.getName());

    public void fetch(JSONObject response, DataInputStream input) throws FileNotFoundException, IOException, ParseException {
        if (response.get("response").equals("error")) {

        } else {

            String serverResponse = input.readUTF();
            boolean debug = true;
            Handler consoleHandler = null;
            consoleHandler = new ConsoleHandler();
            LOGGER.addHandler(consoleHandler);
            consoleHandler.setLevel(Level.ALL);
            LOGGER.setLevel(Level.ALL);

            JSONParser parser = new JSONParser();
            JSONObject resource = (JSONObject) parser.parse(serverResponse);
            String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
            //System.out.println(time + " - [RECEIVE] - " +resource.toJSONString());
            if (debug) {
                time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));

                System.out.println(time + " - [RECEIVE] -  " + resource.toJSONString());

                //  LOGGER.fine("[RECEIVED]" + resource.toJSONString());
            }
            String filename = (String) resource.get("name");
            if (filename.equals("")) {
                time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HH.mm.ss"));

                filename = "File_" + time;
            }
            RandomAccessFile downloadingFile = new RandomAccessFile(filename + ".png", "rw");

            long fileSizeRemaining = (Long) resource.get("resourceSize");
            int chunkSize = 1024 * 1024;

            if (fileSizeRemaining < chunkSize) {
                chunkSize = (int) fileSizeRemaining;

            }
            byte[] receiveBuffer = new byte[chunkSize];
            int num;

            LOGGER.info("Ready to receive file");
            LOGGER.info("File size:" + fileSizeRemaining);
            //System.out.println("- [INFO] - ready to receive file");
            //System.out.println("- [INFO] - file size: " + fileSizeRemaining);
            while ((num = input.read(receiveBuffer)) > 0) {
                downloadingFile.write(Arrays.copyOf(receiveBuffer, num));

                fileSizeRemaining -= num;
                chunkSize = 1024 * 1024;
                if (fileSizeRemaining < chunkSize) {

                    chunkSize = (int) fileSizeRemaining;

                    receiveBuffer = new byte[chunkSize];
                    if (fileSizeRemaining == 0) {
                        break;
                    }
                }

            }
            //System.out.println("Client: File Received!");
            LOGGER.fine("FILE RECEIVED");
            downloadingFile.close();
        }
    }

    //This function will receive the query list from 
    public void query(JSONObject command, DataInputStream input) throws IOException, ParseException {
        Integer size = -1;
        Integer resultSize;
        Long result;
        Boolean done = false;
        Integer c = 0;

        while (c < 3) {
            c += 1;
            if (input.available() > 0) {
                String serverResponse = input.readUTF();
                boolean debug = true;
                Handler consoleHandler = null;
                consoleHandler = new ConsoleHandler();
                LOGGER.addHandler(consoleHandler);
                consoleHandler.setLevel(Level.ALL);
                LOGGER.setLevel(Level.ALL);
                JSONParser parser = new JSONParser();
                JSONObject response = (JSONObject) parser.parse(serverResponse);
                //String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));            
                //System.out.println(time + " - [RECEIVE] - " +response.toJSONString());
                if (debug) {

                    String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));

                    System.out.println(time + " - [RECEIVE] -  " + response.toJSONString());
                    // LOGGER.fine("[RECEIVED]:" + response.toJSONString());
                }
                size += 1;

                if (response.containsKey("resultSize")) {
                    result = (Long) response.get("resultSize");
                    resultSize = Integer.valueOf(result.intValue());
                    if (size == resultSize || resultSize == 0) {
                        done = true;
                    }
                }
            }

            if (done) {
                //     break;
            }

        }

    }

}
