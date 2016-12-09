/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kursaach.labauthorization;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.security.SecureRandom;
import java.util.Random;

/**
 *
 * @author user
 */
public class Registrator {

    public Registrator() {

    }

    public boolean writeData(User user) {
        File file = new File(System.getProperty("user.dir") + "/bdTest.txt");
        Writer writer = null;
        user.setSault(generateString(new SecureRandom(), "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789", 8));
        try {
            FileWriter fw = new FileWriter(file, true);
            writer = new BufferedWriter(fw);
            writer.append(user.toString() + "\n");
        } catch (Exception e) {
            return false;
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (Exception e) {
                }
            }
        }

        return true;
    }

    public static String generateString(Random rng, String characters, int length) {
        char[] text = new char[length];
        for (int i = 0; i < length; i++) {
            text[i] = characters.charAt(rng.nextInt(characters.length()));
        }
        return new String(text);
    }

}
