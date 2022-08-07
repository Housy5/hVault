package vault.encrypt;

import java.math.BigInteger;

public class InvertStrategy extends EncryptionStrategy {

    private class Mixer {
        
        /**
         * softly mixes an array of bytes
         * @param arr 
         */
        static void mix(byte[] arr) {
            for (int i = 0; i < arr.length - 1; i += 2) {
                var temp = arr[i];
                arr[i] = arr[i + 1];
                arr[i + 1] = temp;
            }
        }
    }

    /**
     * Flips all the bits in a the specified byte
     * @param b the specified byte
     * @return the specified byte with all of it's bits flipped
     */
    private byte flipBits(byte b) {
        var bigInteger = BigInteger.valueOf(b);
        int count = 0;

        while (count < 8) {
            bigInteger = bigInteger.flipBit(count);
            count++;
        }

        return bigInteger.byteValue();
    }

    /**
     * decodes an encoded byte array
     * @param arr the array to be decoded
     * @return the decoded array
     */
    @Override
    public byte[] decode(byte[] arr) {
        return encrypt(arr);
    }

    /**
     * encodes a byte array
     * @param arr the array to be encoded
     * @return the encoded array
     */
    @Override
    public byte[] encrypt(byte[] arr) {
        var newArr = new byte[arr.length];
        for (int i = 0; i < arr.length; i++) {
            newArr[i] = flipBits(arr[i]);
        }

        Mixer.mix(newArr);

        return newArr;
    }

}
