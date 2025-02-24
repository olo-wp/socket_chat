package org.chat;

public class Marcos {
    public static int PORT = 6666;
    public static String HOST = "127.0.0.1";
    public static String USER_INFO = "To write a message to all simply type it. \n if you want to " +
            "send a private message to a certain user, format it this way: ./[USERNAME] [MESSAGE]\n" +
            "for command list type /help";
    public static String ASK_USERNAME = "Enter your username:";

    public static String USERNAME_TAKEN = "Username is already taken or is incorrect, choose a different username \n" +
            "username cannot start with . nor / and cannot contain | ";
    public static String COM_HELP = "silence <USER1> | <USER2> ...  = silence a user\n" +
            "unsilence <USER1> | <USER2> ...  = unsilence a user\n" +
            "exit = disconnect\n" +
            "help = list of commands";
}
