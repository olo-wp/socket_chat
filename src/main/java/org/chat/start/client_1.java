package org.chat.start;
import org.chat.ChatClient;

import static org.chat.Marcos.*;

public class client_1 {
    public static void main(String... args){
        ChatClient c = new ChatClient(HOST,PORT);
        c.runClient();
    }
}
