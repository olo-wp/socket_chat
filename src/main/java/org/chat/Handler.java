package org.chat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

import static org.chat.Error_descriptions.NO_MSG_ERROR;
import static org.chat.Error_descriptions.NO_TARGET_ERROR;
import static org.chat.Marcos.*;

public class Handler implements Runnable{

    private final ChatServer chatServ;
    private final Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private String Name = "";

    private boolean stopped = false;
    private final Set<Handler> silenced;

    public Handler(ChatServer chatServ ,Socket socket){
        this.chatServ = chatServ;
        clientSocket = socket;
        silenced = new HashSet<>();
        try{
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(),true);
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    private String setName(){
        out.println(ASK_USERNAME);
        try {
            return in.readLine();
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public void sendToAll(StandardMessage message, Handler source){
        for(Handler h : chatServ.currentClients){
            if(h != source && !h.getSilenced().contains(source)){
                h.out.println(source.getName() + ": " + message.getMessage());
            }
        }
    }

    @Override
    public void run(){
        while(true){
            Name = setName();
            if(!chatServ.isNameUsed(Name, this)) break;
            out.println(USERNAME_TAKEN);
        }
        System.out.println(Name + " connected");
        out.println(USER_INFO);
        try {

            String mes;
            while (!stopped && (mes = in.readLine()) != null) {
                if (mes.strip().startsWith("/")) {
                    Command com = new Command();
                    com.mesparse(mes);
                    if(com.getCommand().equals("silence")){
                        for(String s : com.getTarget()){
                            silence(this.chatServ.returnHandlerWithGivenUsername(s.strip()),true);
                        }
                    }
                    if(com.getCommand().equals("unsilence")){
                        for(String s : com.getTarget()){
                            silence(this.chatServ.returnHandlerWithGivenUsername(s.strip()),false);
                        }
                    }
                    if(com.getCommand().equals("help") || com.getCommand().equals("h")){
                        out.println(COM_HELP);
                    }
                    if(com.getCommand().equals("exit") || com.getCommand().equals("h")){
                        this.stop();
                    }
                } else {
                    StandardMessage parsedMessage = new StandardMessage();
                    parsedMessage.mesparse(mes);
                    if (parsedMessage.getMessage() == null) {
                        this.out.println(NO_MSG_ERROR);
                    } else if (parsedMessage.getTarget() == null) {
                        this.out.println(NO_TARGET_ERROR);
                    } else if (!parsedMessage.getTarget().isEmpty()) {
                        System.out.println(Name + " >> " + parsedMessage.getTarget() + "[PV]: " + parsedMessage.getMessage());
                        sendMes(parsedMessage);
                    } else {
                        System.out.println(Name + ": " + parsedMessage.getMessage());
                        sendToAll(parsedMessage, this);
                    }
                }
            }
            this.stop();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getName(){
        return Name;
    }
    public Set<Handler> getSilenced(){
        return silenced;
    }

    public void sendMes(StandardMessage mes){
        Handler target = chatServ.returnHandlerWithGivenUsername(mes.getTarget());
        if(!target.getSilenced().contains(this)) {
            target.out.println("<<" + this.getName() + ">>: " + mes.getMessage());
        }
    }


    public synchronized void stop(){
        if(stopped) return;
        try {
            stopped = true;
            chatServ.currentClients.remove(this);
            out.println("Disconnected");
            in.close();
            out.close();
            clientSocket.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void silence(Handler h, boolean sil){
        if(sil){
            if(this.silenced.contains(h)) return;
            this.silenced.add(h);
        }
        else{
            if(!this.silenced.contains(h)) return;
            this.silenced.remove(h);
        }
    }


}
