package client;

import java.util.Scanner;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class App {

    static int participantId;
    static int nrOfChatMessagesOnServer;
    static int lastMessageId = 0;
    static final String ROOT_URL = "https://endpunkt4.onrender.com";
    static Map<Integer, String> chat = new HashMap<Integer, String>();

    static String fetchContentOfUrl(String urlStr, boolean oneLine) {
         try {
            URL url = new URL(urlStr);
            URLConnection connection = url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                if (oneLine) {
                    content.append(line);
                } else {
                    content.append(line).append("\n");
                }
            }
            reader.close();
            return content.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    static void fetchParticipantId() {
        participantId = (int) Integer.parseInt(fetchContentOfUrl(ROOT_URL + "/participant", true));
    }

    static int getNrOfChatMessagesOnServer() {
        return ((int) Integer.parseInt(fetchContentOfUrl(ROOT_URL + "/getNrOfChatMessages", true)));
    }

    static Map<Integer, String> fetchChat() {
        String pageContent = fetchContentOfUrl(ROOT_URL + "/getMessage" + "?lastMessageId=" + lastMessageId, false);
        return pageContent;
    }

    static void printAndSaveChat() {
        Map<Integer, String> map = fetchChat();
        System.out.println(map);
        chat.putAll(map);
        lastMessageId = nrOfChatMessagesOnServer;
    }

    static void executeCommand(String command) {
        if (command.equals("##clear")) {
            chat = new HashMap<Integer, String>();
            fetchContentOfUrl(ROOT_URL + "/clearStorage", true);
        } else if (command.equals("##printAll")) {
            System.out.println(chat);
        } else {
            System.out.println("Command do not exist.");
        }
    }

    static void readAndSendMessage() {
        String myMessage = CommandLineFunctions.getString();
        if (myMessage.charAt(0) == '#' && myMessage.charAt(1) == '#') {
            executeCommand(myMessage);
        } else {
            fetchContentOfUrl(ROOT_URL + "/sendMessage?id=" + participantId + "&message=" + myMessage, true);
        }
    }
    
    public static void main(String[] args) {
    	fetchParticipantId();
    	while (true) {
        	int nrOfServerMessages = getNrOfChatMessagesOnServer();
         	if (nrOfServerMessages == lastMessageId) {
         		readAndSendMessage();
         	} else {
         		printAndSaveChat();
         		lastMessageId = nrOfServerMessages;
         	}
    	}
    }
}
