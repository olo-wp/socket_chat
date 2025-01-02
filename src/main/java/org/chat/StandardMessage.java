package org.chat;

public class StandardMessage implements message{
    private String message;
    private String target;

    //TO DO: naprawic to:
    @Override
    public void mesparse(String mes) {
        if(mes.length() <= 2) message = mes;
        else if (mes.charAt(0) == '.' && mes.charAt(1) == '/') {
            int end = mes.indexOf(' ');
            if(end == 2){
                target = null;
                return;
            }
            target = mes.substring(2,end);
            if(end + 1 >= mes.length()){
                message = null;
                return;
            }
            message = mes.substring(end+1);
        }
        else{
            target = "";
            message = mes;
        }
    }
    @Override
    public String getMessage(){
        return message;
    }
    @Override
    public String getTarget(){
        return target;
    }
}
