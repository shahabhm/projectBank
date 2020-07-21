import java.io.IOException;
import java.net.ServerSocket;
import  com.gilecode.yagson.YaGson;
public class Main {

    public static void main(String[] args) throws Exception {
        if (args.length==0) new Bank(9094,true).run();
        int port = Integer.parseInt(args[0]);
        boolean debug;
        switch (args[1]){
            case "0":{
                debug = false;
                break;
            }

            case "1":{
                debug = true;
                break;
            }

            default :{
                throw new Exception("wrong debug value");
            }
        }
        new Bank(Integer.parseInt(args[0]) , debug).run();
    }
}