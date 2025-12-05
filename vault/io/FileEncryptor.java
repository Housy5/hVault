package vault.io;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;

import vault.encrypt.GlobalKeyManager;

public class FileEncryptor {

    public static void encryptFile(File inputFile, File outputFile) {

        try {
            var key = GlobalKeyManager.getKey();

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);

            FileInputStream inputStream = new FileInputStream(inputFile);
            FileOutputStream outputStream = new FileOutputStream(outputFile);

            outputStream.write(cipher.getIV());

            // Encrypt the input data in 1 MB chunks and write it to the output file
            byte[] inputBytes = new byte[1024 * 1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(inputBytes)) != -1) {
                byte[] outputBytes = cipher.update(inputBytes, 0, bytesRead);
                outputStream.write(outputBytes);
            }
            byte[] outputBytes = cipher.doFinal();
            outputStream.write(outputBytes);

            inputStream.close();
            outputStream.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void decryptFile(File inputFile, File outputFile) {
        try {

            var key = GlobalKeyManager.getKey();

            byte[] iv = new byte[16];
            FileInputStream inputStream = new FileInputStream(inputFile);
            inputStream.read(iv);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));

            inputStream = new FileInputStream(inputFile);
            inputStream.skip(16);

            FileOutputStream outputStream = new FileOutputStream(outputFile);

            // Decrypt the input data in 1 MB chunks and write it to the output file
            byte[] inputBytes = new byte[1024 * 1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(inputBytes)) != -1) {
                byte[] outputBytes = cipher.update(inputBytes, 0, bytesRead);
                outputStream.write(outputBytes);
            }
            byte[] outputBytes = cipher.doFinal();
            outputStream.write(outputBytes);

            // Close the streams
            inputStream.close();
            outputStream.close();
        } catch (Exception e) {
            throw new RuntimeException("Error decrypting file: " + e.getMessage(), e);
        }
    }

    public static byte[] decryptFile(File inputFile) {
        try {

            var key = GlobalKeyManager.getKey();

            byte[] iv = new byte[16];
            FileInputStream inputStream = new FileInputStream(inputFile);
            inputStream.read(iv);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));

            inputStream = new FileInputStream(inputFile);
            inputStream.skip(16);

            var outputStream = new ByteArrayOutputStream();

            // Decrypt the input data in 1 MB chunks and write it to the output file
            byte[] inputBytes = new byte[1024 * 1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(inputBytes)) != -1) {
                byte[] outputBytes = cipher.update(inputBytes, 0, bytesRead);
                outputStream.write(outputBytes);
            }
            byte[] outputBytes = cipher.doFinal();
            outputStream.write(outputBytes);

            // Close the streams
            inputStream.close();
            outputStream.close();
            
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error decrypting file: " + e.getMessage(), e);
        }
    }
}
