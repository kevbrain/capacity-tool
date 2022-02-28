package com.its4u.buildfactory.utils;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MailUtils {
	
	private static Logger logger = LoggerFactory.getLogger(MailUtils.class);
	
	public static void SendMail(String text,String recipient,String subject) throws AddressException, MessagingException {
		
		String smtpHost = System.getenv("app.mail.smtp.host");
		String smtpPort = System.getenv("app.mail.smtp.port");
		String ocpName = System.getenv("app.ocp.instance.name");
		String defaultRecipient = System.getenv("app.mail.default.recipient");
		final String smtpuser = System.getenv("app.mail.user");
		final String smtppwd= System.getenv("app.mail.password");
		
		Properties prop = new Properties();
		prop.setProperty("mail.smtp.auth", "true");
		prop.setProperty("mail.smtp.starttls.enable", "true");
		prop.setProperty("mail.smtp.ssl.protocols", "TLSv1.2");
		prop.setProperty("mail.smtp.host", smtpHost);
		prop.setProperty("mail.smtp.port", smtpPort);
		prop.setProperty("mail.user", smtpuser);
		prop.setProperty("mail.password", smtppwd);
		
		
		if (recipient==null) {
			recipient = defaultRecipient;
		}
		String from = ocpName+"@bil.com";
		
		
		Session session = Session.getInstance(prop, new Authenticator() {
		    @Override
		    protected PasswordAuthentication getPasswordAuthentication() {
		        
				return new PasswordAuthentication(smtpuser,smtppwd);
		    }
		});
		
		MimeMessage message = new MimeMessage(session);
		
		message.setFrom(new InternetAddress(from));

        // Set To: header field of the header.
        message.addRecipient(Message.RecipientType.TO,new InternetAddress(recipient));

        // Set Subject: header field
        message.setSubject(subject);
        //message.setSubject("OCNP1 Capacity Report And Simulation [mode "+ha+"]");
        message.setContent(text, "text/html; charset=utf-8");
        
        // Send message
        Transport.send(message);
        logger.info("Sent message successfully....");
	}
}
