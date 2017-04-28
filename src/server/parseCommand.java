package server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;

public class parseCommand {
     private static final Logger LOGGER=Logger.getLogger(parseCommand.class.getName());

    public static void parseCommand(JSONObject command, DataOutputStream output,int exchangeInterval) throws IOException, URISyntaxException, ParseException {

        /*
        SERVER COMMANDS ALWAYS GO THROUGH HERE FIRST
        to get parsed
        
         */
          JSONObject response = new JSONObject();
                  
        if (command.containsKey("command")) {
             String  time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
             System.out.println(time + " - [INFO] - command " + command.get("command") + " received.");
          // LOGGER.info("Command "+command.get("command")+" received");
            serverCommands cmd = new serverCommands();
            /*
            some commands return an object (not needed). the updated ones return void only
            since all messages will be passed through output and input streams.
            */
            switch ((String) command.get("command")) {
                case "EXCHANGE":
                     cmd.exchange(command, output,exchangeInterval);
                    break;
                case "FETCH":
                    cmd.fetch(command, output);
                    break;
                case "PUBLISH":
                    cmd.publish(command, output);
                    break;
                case "QUERY":
                     cmd.query(command, output);
                    break;
                case "REMOVE":
                     cmd.remove(command, output);
                    break;
                case "SHARE":
                    cmd.share(command, output);
                    break;

                default: 
                {   
                      //time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
                //System.out.println(time + " - [SEND] - command is incorrect" );
                    response.put("response", "error");
                    response.put("errorMessage", "invalid command");
                    output.writeUTF(response.toJSONString());
                    boolean debug=true;
                Handler consoleHandler=null;
                consoleHandler=new ConsoleHandler();
                LOGGER.addHandler(consoleHandler);
                consoleHandler.setLevel(Level.ALL);
                LOGGER.setLevel(Level.ALL);
                if(debug)
                LOGGER.fine("[SEND]:"+"Command is incorrect");
                
                }
                
                     
            }
        }
        else
        {
            //if the command is missing 
             response.put("response", "error");
                    response.put("errorMessage", "missing or incorrect type for command");
                    output.writeUTF(response.toJSONString());
        }
        

         
    }
}