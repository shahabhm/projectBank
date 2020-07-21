import com.gilecode.yagson.YaGson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Scanner;

public class Bank {
    YaGson yaGson;
    int port;
    boolean debug;
    int connectedClients = 0;
    public static final int ID_LENGTH = 5;
    public Bank(int port, boolean debug) {
        this.port = port;
        this.debug = debug;
    }

    public void run() throws Exception {
        try{
            //readFiles();
        }catch (Exception e){
            e.printStackTrace();
            throw new Exception("there was a problem connectiong do database");
        }
        ServerSocket serverSocket = new ServerSocket(port);

        System.out.println("IP : 127.0.0.1\nPORT : 9090");
        while (true){
            new BankServer(serverSocket.accept(),this , debug).start();
            connectedClients++;
        }
    }

    public int getConnectedClients() {
        return connectedClients;
    }

    public void reduceClientCount() {
        connectedClients--;
    }

    private void readFiles() throws IOException, ClassNotFoundException {
        yaGson = new YaGson();
        File dir = new File("resources/acc");
        for (File file : dir.listFiles()){
            Account.addAccount((Account) ObjectSaver.serializeDataIn(file.getAbsolutePath() , Account.class));
        }
        dir = new File("resources/rec");
        for (File file : dir.listFiles()){
            Receipt.addReceipt((Receipt)ObjectSaver.serializeDataIn(file.getAbsolutePath(),Receipt.class));
        }
    } // fixme
}
