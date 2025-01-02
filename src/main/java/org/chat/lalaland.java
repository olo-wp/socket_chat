package org.chat;
import static org.chat.Marcos.*;

public class lalaland {
    public static void main(String... args){
        ChatClient c = new ChatClient(HOST,PORT);
        c.runClient();
    }
}
