package org.chat;
import java.net.*;
import java.io.*;
import java.util.concurrent.CopyOnWriteArrayList;
import static org.chat.Marcos.*;
import static org.chat.Error_descriptions.*;

public class ChatServer {
    private ServerSocket serverSocket;
    private final CopyOnWriteArrayList<Handler> currentClients = new CopyOnWriteArrayList<>();

    private boolean running;

    public ChatServer(){
        try {
            serverSocket = new ServerSocket(PORT);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    public boolean isNameUsed(String name, Handler me){
        for(Handler h : currentClients){
            if(h != me) {
                if (name.equals(h.Name)) return true;
            }
        }
        return false;
    }

    public Handler returnHandlerWithGivenUsername(String name){
        for(Handler h : currentClients){
            if(h.getName().equals(name)) return h;
        }
        return null;
    }

    public void start(){
        running = true;
        System.out.println("started");
        try {
            while (running) {
                Socket client = serverSocket.accept();
                System.out.println(client + "  connected");

                Handler h = new Handler(this,client);
                currentClients.add(h);
                new Thread(h).start();
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            this.stop();
        }
    }

    public void stop(){
        try {
            running = false;
            for(Handler h : currentClients){
                h.stop();
            }
            serverSocket.close();
            System.out.println("closed");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static class Handler implements Runnable{

        private final ChatServer chatServ;
        private final Socket clientSocket;
        private BufferedReader in;
        private PrintWriter out;
        private String Name = "";

        public Handler(ChatServer chatServ ,Socket socket){
            this.chatServ = chatServ;
            clientSocket = socket;
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
                String res = in.readLine();
                return res;
            } catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        public void sendToAll(StandardMessage message, Handler source){
            for(Handler h : chatServ.currentClients){
                if(h != source){
                    h.sendMes(message);
                }
            }
        }

        @Override
        public void run(){
            while(true){
                Name = setName();
                if(!chatServ.isNameUsed(Name, this)) break;
            }
            System.out.println(Name + " connected");
            out.println(USER_INFO);
            try {
                String mes;
                while ((mes = in.readLine()) != null){
                    StandardMessage parsedMessage = new StandardMessage();
                    parsedMessage.mesparse(mes);
                    if(parsedMessage.getMessage() == null){
                        this.out.println(NO_MSG_ERROR);
                    } else if(parsedMessage.getTarget() == null){
                        this.out.println(NO_TARGET_ERROR);
                    } else if(!parsedMessage.getTarget().isEmpty()){
                        System.out.println(Name + " >> " + parsedMessage.getTarget() + "[PV]: " + parsedMessage.getMessage());
                        sendMes(parsedMessage);
                    } else{
                        System.out.println(Name + ": " + parsedMessage.getMessage());
                        sendToAll(parsedMessage, this);
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

        public void sendMes(StandardMessage mes){
            Handler target = chatServ.returnHandlerWithGivenUsername(mes.getTarget());
            target.out.println("<<" + this.getName() + ">>: " + mes.getMessage());
        }


        public void stop(){
            try {
                chatServ.currentClients.remove(this);
                in.close();
                out.close();
                clientSocket.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

}
