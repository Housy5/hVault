/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vault.nfsys;

/**
 *
 * @author olivi
 */
public class OversizedFileException extends RuntimeException {

    public OversizedFileException() {

    }

    public OversizedFileException(String msg) {
        super(msg);
    }
}
