package com.lfx.db;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.EmailException;

public class DBMailClient
{
    public final static void sendMail(String server,String user, String pass, String from, String to, String subject, String body)
    {
	HtmlEmail smtp = null;

	if (server != null && from != null && to != null && body != null)
	{
	   try
	   {
	       smtp = new HtmlEmail();
	       smtp.setCharset("UTF-8");
	       smtp.setHostName(server);
	       String tolist[] = TextUtils.toStringArray(TextUtils.getFields(to,";"));
               for(int i=0;i<tolist.length;i++)
                   smtp.addTo(tolist[i],tolist[i]);
	       if (user != null && pass != null)
	           smtp.setAuthenticator(new DBMailAuthor(user,pass));
	       smtp.setFrom(from, from);
	       if (subject != null) smtp.setSubject(subject);
	       smtp.setHtmlMsg(body);
	       smtp.send();
           }
           catch (EmailException ex)
           {
		ex.printStackTrace();
           }
        }
    }
    public final static void sendMail(String server, String port,String user, String pass, String from, String to, String subject, String body)
    {
	HtmlEmail smtp = null;

	if (server != null && from != null && to != null && body != null)
	{
	   try
	   {
	       smtp = new HtmlEmail();
	       smtp.setCharset("UTF-8");
	       smtp.setSSL(true);
	       smtp.setHostName(server);
	       smtp.setSmtpPort(Integer.valueOf(port).intValue());
	       String tolist[] = TextUtils.toStringArray(TextUtils.getFields(to,";"));
               for(int i=0;i<tolist.length;i++)
                   smtp.addTo(tolist[i],tolist[i]);
	       if (user != null && pass != null)
	           smtp.setAuthenticator(new DBMailAuthor(user,pass));
	       smtp.setFrom(from, from);
	       if (subject != null) smtp.setSubject(subject);
	       smtp.setHtmlMsg(body);
	       smtp.send();
           }
           catch (EmailException ex)
           {
		ex.printStackTrace();
           }
        }
    }
}