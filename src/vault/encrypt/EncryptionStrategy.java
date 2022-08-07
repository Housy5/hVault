/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vault.encrypt;

import java.util.Random;
import vault.encrypt.spi.Encryptable;

public abstract class EncryptionStrategy implements Encryptable {

    protected static void randomize(byte[][] matrix) {
        var rand = new Random();

        for (byte[] bs : matrix) {
            for (int j = 0; j < bs.length; j++) {
                bs[j] = (byte) rand.nextInt(0, Byte.MAX_VALUE + 1);
            }
        }
    }

    protected static byte[] matrixToArray(byte[][] matrix) {
        byte[] arr = new byte[matrix.length * matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            byte[] bs = matrix[i];

            for (int j = 0; j < matrix.length; j++) {
                byte c = bs[j];
                arr[i * bs.length + j] = c;
            }
        }
        return arr;
    }

    protected static byte[][] arrayToMatrix(byte[] arr) {
        var sideLength = (int) Math.floor(Math.sqrt(arr.length));
        var matrix = new byte[sideLength][sideLength];

        for (int i = 0; i < arr.length; i++) {
            var x = i / sideLength;
            var y = i % sideLength;

            matrix[x][y] = arr[i];
        }

        return matrix;
    }

    @Override
    public abstract byte[] encrypt(byte[] arr);

    @Override
    public abstract byte[] decode(byte[] arr);

}
