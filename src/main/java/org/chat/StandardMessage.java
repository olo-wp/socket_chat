package org.chat;

public class StandardMessage implements message{
    private String message;
    private String target;

    @Override
    public void mesparse(String mes) {
        mes = mes.strip();
        if (mes.isEmpty()) return;
        if(mes.length() <= 2){
            message = mes;
            target = "";
        }
        else if (mes.startsWith("./")) {
            int end = mes.indexOf(' ');
            if(end == -1){
                message = null;
                return;
            }
            if(end == 2){
                target = null;
                return;
            }
            if(end + 1 >= mes.length()){
                message = null;
                return;
            }
            target = mes.substring(2,end);
            message = mes.substring(end+1);
        }
        else{
            target = "";
            message = mes;
        }
    }
    public String getMessage(){
        return message;
    }
    public String getTarget() {
        return target;
    }

}
