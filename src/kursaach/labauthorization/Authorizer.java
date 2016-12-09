/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kursaach.labauthorization;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

/**
 *
 * @author user
 */
public class Authorizer {

    public Authorizer() {

    }

    public boolean authorization(User user) throws FileNotFoundException, IOException {
        File file = new File(System.getProperty("user.dir") + "/bdTest.txt");
        Reader reader;
        try {
            reader = new FileReader(file);
        } catch (Exception e) {
            return false;
        }
        
        BufferedReader bReader = new BufferedReader(reader);
        
        String line;
        User oldUser;

        while ((line = bReader.readLine()) != null) {
            oldUser = parseUser(line);
            if (user.getLogin().equals(oldUser.getLogin())) {
                user.setSault(oldUser.getSault());
                if (user.getMD5Password().equals(oldUser.getPassword())) {
                    System.out.println("Authorization success!");
                    return true;
                }
            }
        }
        System.out.println("Error. Wrong login or password.");
        return false;
    }

    public boolean checkLogin(String login) throws IOException {
        try {
            File file = new File(System.getProperty("user.dir") + "/bdTest.txt");
            Reader reader = new FileReader(file);
            BufferedReader bReader = new BufferedReader(reader);
            String line;

            while ((line = bReader.readLine()) != null) {
                if (parseUser(line).getLogin().equals(login)) {
                    System.out.println("This login is already in use.");
                    return false;
                }
            }
        } catch (Exception e) {
            return true;
        }
        
        return true;
    }

    private User parseUser(String src) {
        User user = new User();
        String[] arr = src.split(" ");
        user.setLogin(arr[0]);
        user.setPassword(arr[1]);
        user.setSault(arr[2]);
        return user;
    }
}
