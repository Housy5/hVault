package vault.encrypt;

public class HayStackStrategy extends EncryptionStrategy {

    /**
     * WARNING: Artificially grows the size by a factor of n^4.
     *
     * @param arr
     * @return
     */
    @Override
    public byte[] encrypt(byte[] arr) {
        return matrixToArray(encrypt(arr, true));
    }

    /**
     * WARINING: Artificially grows the size by a factor of n^2 when !flag and
     * n^4 when flag == true
     *
     * @param arr
     * @param flag indicates if it should encrypt it's output recursively.
     * @return
     */
    private static byte[][] encrypt(byte[] arr, boolean flag) {
        var matrix = new byte[arr.length][arr.length];
        var index = 0;

        randomize(matrix);

        for (int i = 0; i < arr.length; i++) {
            byte[] bs = matrix[i];

            for (int j = 0; j < bs.length; j++) {
                if (j == index) {
                    bs[j] = arr[i];
                    var next = j + 1;
                    if (next == bs.length) {
                        next = 0;
                    }
                    index = bs[next] % bs.length;
                    break;
                }
            }
        }

        if (flag) {
            matrix = encrypt(matrixToArray(matrix), false);
        }
        return matrix;
    }

    private static byte[] decode(byte[][] matrix, boolean flag) {
        var index = 0;
        var arr = new byte[matrix.length];

        for (int i = 0; i < matrix.length; i++) {
            byte[] bs = matrix[i];

            for (int j = 0; j < bs.length; j++) {
                byte b = bs[j];
                if (j == index) {
                    arr[i] = bs[j];

                    var next = j + 1;
                    if (next == bs.length) {
                        next = 0;
                    }
                    index = bs[next] % bs.length;
                    break;
                }
            }
        }

        if (flag) {
            arr = decode(arrayToMatrix(arr), false);
        }

        return arr;
    }

    @Override
    public byte[] decode(byte[] arr) {
        return decode(arrayToMatrix(arr), true);
    }

}
