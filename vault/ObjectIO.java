package vault;

import java.io.*;

public class ObjectIO {
    
    public static Object loadFromFile(File file) throws IOException, ClassNotFoundException {
        try (var in = new ObjectInputStream(new FileInputStream(file))) {
            return in.readObject();
        }
    }
    
    public static void saveToFile(File file, Object obj) throws IOException {
        try (var out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(obj);
        }
    }
}
