package org.chat;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.chat.Marcos.PORT;

public class ChatServer {
    private ServerSocket serverSocket;
    public final CopyOnWriteArrayList<Handler> currentClients = new CopyOnWriteArrayList<>();

    private boolean running;

    public ChatServer(){
        try {
            serverSocket = new ServerSocket(PORT);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    public boolean isNameUsed(String name, Handler me){
        if( name.startsWith(".") || name.startsWith("/") || name.startsWith(" ") || name.contains("|")) return true;
        for(Handler h : currentClients){
            if(h != me) {
                if (name.equals(h.getName())) return true;
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
}
