import com.gilecode.yagson.YaGson;

import java.io.*;
import java.util.Scanner;

import com.gilecode.yagson.YaGson;
import com.gilecode.yagson.com.google.gson.JsonElement;
import com.gilecode.yagson.com.google.gson.internal.$Gson$Preconditions;

public class ObjectSaver {
    static YaGson yaGson = new YaGson();

    public static void serializeDataOut(Saveable ish, String folder)throws IOException {
        String fileName= "resources/"+folder + "/" + ish.getId();
        FileOutputStream fos = new FileOutputStream(fileName);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(yaGson.toJson(ish));
        oos.close();
    }

    public static Object serializeDataIn(String fileName , Class t) throws IOException, ClassNotFoundException {
        FileInputStream fin = new FileInputStream(fileName);
        String c = new Scanner(fin).nextLine().replaceFirst("^.*\\{\"@type\"" , "{\"@type\"");
        System.out.println(c);
        Object iHandler = yaGson.fromJson(c, t);
        fin.close();
        return iHandler;
    }
}
