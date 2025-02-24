package org.chat.start;

import org.chat.ChatServer;

public class Server {
    public static void main(String... args){
        ChatServer cc = new ChatServer();
        cc.start();
    }
}
