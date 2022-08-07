package vault.encrypt.spi;

public interface Encryptable {

    public byte[] encrypt(byte[] arr);

    public byte[] decode(byte[] arr);
}
