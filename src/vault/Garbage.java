/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vault;

import java.awt.event.ActionEvent;
import javax.swing.Timer;

/**
 *
 * @author olivi
 */
public class Garbage {

    public static void start() {
        new Timer(5 * 1000, (ActionEvent e) -> {
            Runtime.getRuntime().gc();
        }).start();
    }

}
