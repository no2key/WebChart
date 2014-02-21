package com.lfx.db;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class DBMailAuthor extends Authenticator
{
     String user;
     String pw;
     public DBMailAuthor(String username, String password)
     {
        super();
        this.user = username;
        this.pw = password;
     }
     public PasswordAuthentication getPasswordAuthentication()
     {
       return new PasswordAuthentication(user, pw);
     }
}