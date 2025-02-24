package org.chat;

import java.util.Arrays;
import java.util.List;

public class Command implements message{

    private String command = null;
    private  List<String> Target = null;
    @Override
    public void mesparse(String mes){
        mes = mes.strip();
        if(!mes.startsWith("/")) return;
        int end = mes.indexOf(' ');
        if(end == -1) end = mes.length();
        System.out.println(end);
        if(end == 1) return;
        else command = mes.substring(1,end);
        mes = mes.substring(end);
        Target = Arrays.asList(mes.split("\\s*\\|\\s*"));
        System.out.println(command);
        System.out.println(Target);
    };

    public List<String> getTarget(){
        return Target;
    }

    public String getCommand(){
        return command;
    }
}
