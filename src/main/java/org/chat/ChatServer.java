package org.chat;
import java.net.*;
import java.io.*;
import java.util.concurrent.CopyOnWriteArrayList;
import static org.chat.Marcos.PORT;

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
            out.println("Enter your username:");
            try {
                String res = in.readLine();
                return res;
            } catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        public void sendToAll(String message, Handler source){
            for(Handler h : chatServ.currentClients){
                if(h != source){
                    h.sendMes(message, this.getName(), h.getName());
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
            out.println("To write a org.chat.message to all simply type it. \n if you want to " +
                    "send a org.chat.message to a certain user, format it this way: ./[USERNAME] [MESSAGE]");
            try {
                String mes;
                while ((mes = in.readLine()) != null){
                    StandardMessage parsedMessage = new StandardMessage();
                    parsedMessage.mesparse(mes);
                    if(parsedMessage.getMessage() == null){
                        this.out.println("SENDING FAILED: MESSAGE CAN NOT BE EMPTY");
                    } else if(parsedMessage.getTarget() == null){
                        this.out.println("SENDING FAILED: TARGET CAN NOT BE EMPTY IN A TARGETED MESSAGE");
                    } else {
                        System.out.println(Name + ": " + parsedMessage.getMessage());
                        sendToAll(mes, this);
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

        public void sendMes(String mes, String name, String targt){
            Handler target = chatServ.returnHandlerWithGivenUsername(targt);
            target.out.println(name + ": " + mes);
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
