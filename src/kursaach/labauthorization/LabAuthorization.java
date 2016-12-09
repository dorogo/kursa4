/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kursaach.labauthorization;

import java.io.IOException;

/**
 *
 * @author user
 */
public class LabAuthorization {
    
    User user;
    String login;
    String psw;
    Authorizer authorizer;
    Registrator registrator;
    
    public LabAuthorization() throws IOException {
        // TODO code application logic here
        user = new User();
        
        authorizer = new Authorizer();
        registrator = new Registrator();
    }

    public int registrate(String name, String psw) throws IOException {
        if (!authorizer.checkLogin(name)) {
            return 1;   //error - this login is already in use
        } else {
            user.setLogin(name);
            user.setPassword(psw);
            registrator.writeData(user);
        }
        return 0;
    }

    public int authorize(String name, String psw) throws IOException {
        user.setLogin(name);
        user.setPassword(psw);
        if (!authorizer.authorization(user)) {
            return 2;
        }
        
        return 0;
    }

    
}
