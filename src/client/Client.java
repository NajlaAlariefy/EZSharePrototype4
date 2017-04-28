package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import static java.lang.Compiler.command;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.apache.commons.cli.*;

import Utilities.Resource;
import sun.security.krb5.internal.HostAddress;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;

import javax.print.DocFlavor.STRING;

public class Client {

    // IP and port
    private static String ip = "localhost";
    private static int port = 3000;
    private static final Logger LOGGER = Logger.getLogger(Client.class.getName());

    @SuppressWarnings("empty-statement")
    public static void main(String[] args) throws ParseException, URISyntaxException, ParseException, org.apache.commons.cli.ParseException {

        boolean isValid = false;
        Options options = new Options();
        options.addOption("channel", true, "channel");
        options.addOption("debug", "print debug information");
        options.addOption("description", true, "resource description");
        options.addOption("exchange", "exchange server list with server");
        options.addOption("fetch", "fetch resources from server");
        options.addOption("host", true, "server host, a domain name or IP address");
        options.addOption("name", true, "resource name");
        options.addOption("owner", true, "owner");
        options.addOption("port", true, "server port, an integer");
        options.addOption("publish", false, "publish resource on server");
        options.addOption("query", "query for resources from server");
        options.addOption("remove", "remove resource from server");
        options.addOption("secret", true, "secret");
        options.addOption("servers", true, "server list, host1:port1,host2:port2,...");
        options.addOption("share", "share resource on server");
        options.addOption("tags", true, "resource tags, tag1,tag2,tag3,...");
        options.addOption("uri", true, "resource URI");

        CommandLineParser clparser = new DefaultParser();
        CommandLine cmd = null;

        cmd = clparser.parse(options, args);

        //  Scanner sc = new Scanner(System.in);    
        //String commandName = sc.nextLine();
        List<String> tags = Arrays.asList("css");
        String commandName = "PUBLISH";
        String name = "aname";
        String description = "descp";
        String URI = "abc.com";
        String owner = "an owner";
        String channel = "channel";
        String ezserver = "localhost:5000 ";
        String secret = "ticd8pais2dj4yku60fxpvtg3e9564";
        String servers = "localhost:3000, localhost:5000, localhost:8000";
        Boolean relay = true;
        /*Parsing the command line arg*/
        // CHECKING FOR HOST & PORT
        if (cmd.hasOption("servers")) {
            servers = cmd.getOptionValue("servers");
            if (servers == "") {
                isValid = false;
                System.out.println("No servers present");
            } else {
                isValid = true;
            }
        }
        //System.out.println(cmd.getOptionValue("port") + " - " + cmd.getOptionValue("host"));
        if (cmd.hasOption("host") && cmd.hasOption("port")) {
            //IF EITHER IS EMPTY, RETURN ERROR
            if (cmd.getOptionValue("host").equals(" ") || cmd.getOptionValue("port").equals(" ")) {
                // String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
                //System.out.println(time + " - [INFO] - Error: Invalid host/port ");
                LOGGER.info("Error:Invalid host/port");

                isValid = false;
            } else {
                //IF PORT IS NOT AN INTEGER OF LENGTH 4, RETURN ERROR
                //ELSE STORE IN IP & PORT
                String port1 = cmd.getOptionValue("port");
                if (cmd.getOptionValue("port").matches("^-?\\d+$") && port1.length() == 4) {

                    port = Integer.parseInt(port1);
                    isValid = true;
                    ip = cmd.getOptionValue("host");

                } else {
                    //String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
                    //System.out.println(time + " - [INFO] - Error: Invalid port");
                    LOGGER.info("Error:Invalid port");
                    isValid = false;
                }

            }

        } else {
            System.out.println("Missing host/port information");
        }
        if ((!cmd.hasOption("uri") || cmd.getOptionValue("uri").equals("")) && !cmd.hasOption("exchange")) {
            System.out.println("require uri");
            isValid = false;
        } else {
            URI = cmd.getOptionValue("uri");
            isValid = true;
        }
        if (cmd.hasOption("owner") && !cmd.hasOption("exchange")) {
            if (cmd.getOptionValue("owner").trim().equals("*")) {
                System.out.println("owner cannot be \"*\"");
                isValid = false;
            } else {

                isValid = true;
                owner = (cmd.getOptionValue("owner").trim());
            }

        }  
        if (cmd.hasOption("channel")) {
            channel = (cmd.getOptionValue("channel").trim());
        }  
        if (cmd.hasOption("name")) {
            name = (cmd.getOptionValue("name").trim());
        }  
        if (cmd.hasOption("description")) {
            description = cmd.getOptionValue("description").trim();
        }  
        List<String> tagList = new ArrayList<>();
        if (cmd.hasOption("tags")) {
            String[] tags1 = cmd.getOptionValue("tags").split(",");
            for (int i = 0; i < tags1.length; i++) {
                tagList.add(tags1[i].trim());
            }
        }
        if (cmd.hasOption("secret")) {
            secret = cmd.getOptionValue("secret");
        }
        if (cmd.hasOption("relay")) {
            relay = Boolean.valueOf(cmd.getOptionValue("relay"));
        }
        boolean debug = false;
        if (cmd.hasOption("debug")) {
           // debug = true;
        }
        if (cmd.hasOption("publish")) {
            commandName = "PUBLISH";
            isValid = true;
        } else if (cmd.hasOption("remove")) {
            commandName = "REMOVE";
            isValid = true;
        } else if (cmd.hasOption("share")) {
            commandName = "SHARE";
            isValid = true;
        } else if (cmd.hasOption("query")) {
            commandName = "QUERY";
            isValid = true;
        } else if (cmd.hasOption("fetch")) {
            commandName = "FETCH";
            isValid = true;
        } else if (cmd.hasOption("exchange")) {
            commandName = "EXCHANGE";
            isValid = true;
        } else {
            System.out.println("invalid command");
            isValid = false;
        }
        System.out.println(commandName);

        //REMOVE LATER
        isValid = true;
        //REMOVE LATER

        if (isValid) {
            try (Socket socket = new Socket(ip, port);) {
                /*
            INITIATING CONNECTION REQUEST
                 */
                DataInputStream input = new DataInputStream(socket.getInputStream());
                DataOutputStream output = new DataOutputStream(socket.getOutputStream());
                String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
                System.out.println(time + " - [INFO] - requesting connection with server");

             

                /*
            RECEIVING RESPONSE FROM SERVER
                 */
                
                  time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
                System.out.println(time + " - [INFO] - connection with server is established");

                
                
                /* DELETE LATER
                String message =  input.readUTF();
                JSONParser parser = new JSONParser();
                JSONObject JSONresponse = (JSONObject) parser.parse(message);
                time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
                System.out.println(time + " - [RECEIVE] - " + JSONresponse.toJSONString());

                Handler consoleHandler = null;
                consoleHandler = new ConsoleHandler();
                LOGGER.addHandler(consoleHandler);
                consoleHandler.setLevel(Level.ALL);
                LOGGER.setLevel(Level.ALL);
                
                // LOGGER.fine("[RECEIVED]:" + message);
             
                //time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
                //System.out.println(time + " - [RECEIVE] - " + message);
*/
                
                
                
                /*
            READING ARGUMENTS FROM CLI 
            (for now only command name is read, the rest is hardcoded.
                
                if (cmd.hasOption("publish")) {
                    commandName = "PUBLISH";
                } else if (cmd.hasOption("remove")) {
                    commandName = "REMOVE";
                } else if (cmd.hasOption("share")) {
                    commandName = "SHARE";
                } else if (cmd.hasOption("query")) {
                    commandName = "QUERY";
                } else if (cmd.hasOption("fetch")) {
                    commandName = "FETCH";
                } else if (cmd.hasOption("exchange")) {
                    commandName = "EXCHANGE";
                }
                 */
 /*
            CONVERTING INPUT TO JSON OBJECT
            using Resource class's function (inputToJSON)
                 */
                JSONObject command = new JSONObject();
                Resource resource = new Resource();
                command = resource.inputToJSON(commandName, name, owner, description, channel, URI, tags, ezserver, secret, relay, servers, input, output);
                 /*
            SEND COMMAND TO SERVER
                 */
                //System.out.println("Server: Sending: " + command.toString());
                output.writeUTF(command.toJSONString());
                System.out.println(time + " - [SEND] -  " + command.toJSONString());
                    
                /*
            RECEIVING RESPONSE FROM ANY COMMAND INVOKED 
            (could be an error, like 'invalid command')
                 */
                while (true) {

                    if (input.available() > 0) {
                        String response = input.readUTF();
                         JSONParser parser = new JSONParser();
                         JSONObject JSONresponse = (JSONObject) parser.parse(response);
                        time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
                        System.out.println(time + " - [RECEIVE] - " + JSONresponse.toJSONString());
                        //if(debug)
                        //  LOGGER.fine("[RECEIVED]:" + JSONresponse.toJSONString());

                        /*
                DOING CLIENT SIDE CODE FOR SOME OF COMMANDS
                some commands don't need code, like remove/share/publish
                         */
                        clientCommands clientCommand = new clientCommands();

                        switch (commandName) {

                            case "FETCH":
                                clientCommand.fetch(JSONresponse, input);
                                break;

                            case "QUERY":
                                clientCommand.query(JSONresponse, input);
                                break;

                        }

                        break;
                    }

                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException ex) {
                System.out.println(ex);
                Logger.getLogger(Client.class.getName()).log(Level.ALL, null, ex);
            } finally {
                // socket.close();
            }
        } else {
            //String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
            //System.out.println(time + " - [INFO] - Error: Connection aborted");
            LOGGER.info("Error:Connection aborted");

        }

    }

}
