package server;

import Utilities.Resource;
import jdk.internal.dynalink.beans.StaticClass;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import javax.net.ServerSocketFactory;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.apache.commons.cli.*;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/*
1- open server for connections SERVER.JAVA

2- while there is a client SERVER.JAVA

3- initiate client server SERVER.JAVA

4- while true, take in command and arguments -------- serveClient.JAVA

5- parse the command --------- parseCommand.JAVA

6- process commands (from serverCommands) and return response ---------serverCommands.JAVA

7- still take in any commands while true ------- serveClient.JAVA
 */
 /*
Communication: 
 
client: REQUEST command
server: REPLY command

(if fetch or query, REPLY is long so listen many times until end)

(if query:
 

server1: REQUEST command (query)
server2: REPLY command (query list)

)





 */
public class Server {

    public static String host = "localhost";
    public static int port = 3000;
    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());

    private static String randomString() {
        String SALTCHARS = "abcdefghijklmnopqrstuvwxyz1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 30) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }
    public static String secret = randomString();
    public static ArrayList serverResources = new ArrayList();
    public static ArrayList serverRecords = new ArrayList();

    public Server(String host, int port) {
        host = host;
        port = port;
    }
    // Identifies the user number connected
    private static int counter = 0;
    static int exchangeInterval = 10000;//10*60*1000;

    public static void main(String[] args) throws org.apache.commons.cli.ParseException, InterruptedException {

        Options options = new Options();
        options.addOption("advertisedhostname", true, "advertised hostname");
        options.addOption("connectionintervallimit", true, "connection interval limit in seconds");
        options.addOption("exchangeinterval", true, "exchange interval in seconds");
        options.addOption("port", true, "server port, an integer");
        options.addOption("secret", true, "secret");
        options.addOption("debug", true, "print debug information");
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;
        cmd = parser.parse(options, args);
        /*if (cmd.hasOption("advertisedhostname")){
			ServerConfig.HOST_NAME=cmd.getOptionValue("advertisedhostname");
		}*/
 /*if (cmd.hasOption("connectionintervallimit")){
			ServerConfig.CONNECTION_INTERVAL=(int)Double.parseDouble(cmd.getOptionValue("connectionintervallimit"))*1000;
		}*/
        if (cmd.hasOption("exchangeinterval")) {
            exchangeInterval = (int) Double.parseDouble(cmd.getOptionValue("exchangeinterval")) * 1000;
        }
        if (cmd.hasOption("port")) {
            port = Integer.parseInt(cmd.getOptionValue("port"));
        }
        if (cmd.hasOption("secret")) {
            secret = cmd.getOptionValue("secret");
        }
        if (cmd.hasOption("debug")) {
            boolean debug = Boolean.parseBoolean(cmd.getOptionValue("debug"));
        }

        /*
            1- OPEN SERVER FOR CONNECTIONS
         */
        ServerSocketFactory factory = ServerSocketFactory.getDefault();

        try (ServerSocket server = factory.createServerSocket(port)) {
            String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
            System.out.println(time + " - [INFO] - Starting the EZShare Server");
            //  LOGGER.info("Starting the EZShare server");
            time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
            System.out.println(time + " - [INFO] - using secret: " + secret);
            // LOGGER.info("Using Secret:"+secret);
            time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
            System.out.println(time + " - [INFO] - using advertised hostname: " + host);
            //LOGGER.info("Using advertised Hostname:"+host);
            time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
            System.out.println(time + " - [INFO] - bound to port: " + port);
            // LOGGER.info("Bound to port:"+port);
            time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
            System.out.println(time + " - [INFO] - EXCHANGE stared ");
            // LOGGER.info("Exchange started");

            /*
                2- WHILE TRUE, WAIT FOR ANY CLIENT
             */
            class serverExchanges extends TimerTask {

                public void run() {
                     serverInteractions si = new serverInteractions();
                    try {
                        si.exchange();
                    } catch (IOException ex) {
                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

            // And From your main() method or any other method
            Timer timer = new Timer();
            timer.schedule(new serverExchanges(), 0, exchangeInterval);
            
            while (true) {
                serveClient sc = new serveClient();
                Socket client = server.accept();
                counter++;
                time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
                System.out.println(time + " - [INFO] - client " + counter + " requesting connection");
                //  LOGGER.info("Client"+counter+"requesting connection");
                // Start a new thread for a connection 

                Thread t = new Thread(() -> {
                    try {
                        sc.serveClient(client, counter, exchangeInterval);
                    } catch (URISyntaxException ex) {
                        Logger.getLogger(Server.class.getName()).log(Level.ALL, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
                t.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
