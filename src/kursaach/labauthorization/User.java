/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kursaach.labauthorization;

import java.security.MessageDigest;

/**
 *
 * @author user
 */
public class User {

    private String login;
    private String password;
    private String sault;

    public User() {

    }

    public User(String lgn, String psw) {
        this.login = lgn;
        this.password = psw;
    }

    public void setLogin(String d) {
        this.login = d;
    }

    public String getLogin() {
        return this.login;
    }

    public void setPassword(String d) {
        this.password = d;
    }

    public String getPassword() {
        return this.password;
    }

    public void setSault(String d) {
        this.sault = d;
    }

    public String getSault() {
        return this.sault;
    }

    @Override
    public String toString() {
        return login + " " + getMD5Password() + " " + sault;
    }

    public String getMD5Password() {
        String tmp = password.concat(sault);
        MessageDigest md;
        byte byteData[] = null;
        try {
            md = MessageDigest.getInstance("MD5");
            md.update(tmp.getBytes());
            byteData = md.digest();
        } catch (Exception e) {
        }

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

}
