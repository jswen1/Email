package cn.edu.scu.util.notification.email;

import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

import com.sun.org.apache.commons.logging.Log;
import com.sun.org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class Email {
	
	private Log log = LogFactory.getLog(Email.class);
	
	private String smtp_conf_file = "mail.properties";
	
    private String smtp_host_name = null;
    private String smtp_auth_user = null;
    private String smtp_auth_pwd  = null;
    
    private Properties smtp_properties = null;
    private Authenticator smtp_auth = null;
    
    private String send_to_default = null;

    
    public Email() throws IOException {
    	
    	Properties conf = new Properties();
    	InputStream inputStream = getClass().getClassLoader().getResourceAsStream(smtp_conf_file);
    	conf.load(inputStream);
    	
    	smtp_host_name = conf.getProperty("smtp_host_name");
    	smtp_auth_user = conf.getProperty("smtp_auth_user");
    	smtp_auth_pwd = conf.getProperty("smtp_auth_pwd");
    	
    	smtp_properties = new Properties();
        smtp_properties.put("mail.transport.protocol", "smtp");
        smtp_properties.put("mail.smtp.host", smtp_host_name);
        smtp_properties.put("mail.smtp.auth", "true");
        
        smtp_auth = new SMTPAuthenticator();   
    }
    
    /**
     * 向指定邮件地址发送邮件
     * 
     * @param mailMsg
     * @param mailAddrs
     * @throws Exception
     */
    public void send(String mailMsg, String [] mailAddrs) throws Exception{
    	
        Session mailSession = Session.getDefaultInstance(smtp_properties, smtp_auth);
        Transport transport = mailSession.getTransport();

        MimeMessage message = new MimeMessage(mailSession);
        message.setContent(mailMsg, "text/plain;charset=utf-8");
        
        String from = smtp_auth_user+"@"+smtp_host_name.split("smtp.")[1];
        message.setFrom(new InternetAddress(from));
        log.debug("邮件发件方："+from);
        
        for(String addr : mailAddrs) {
        	message.addRecipient(Message.RecipientType.TO, new InternetAddress(addr));
       	 	log.debug("邮件接收方："+addr);
        }
        transport.connect();
        //transport.connect(smtp_host_name, smtp_auth_user, smtp_auth_pwd);
        transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
        transport.close();
    }

    private class SMTPAuthenticator extends javax.mail.Authenticator {
        public PasswordAuthentication getPasswordAuthentication() {
           String username = smtp_auth_user;
           String password = smtp_auth_pwd;
           return new PasswordAuthentication(username, password);
        }
    }
    
}
