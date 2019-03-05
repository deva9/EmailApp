package com.mycompany.myemailapp;

import android.content.Context;
import android.util.Log;

import java.io.UnsupportedEncodingException;
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
public class GMail {
    private static Context context;
    String emailPort = "587";// gmail's smtp port
    final String smtpAuth = "true";
    final String starttls = "true";
    String emailHost = "smtp.gmail.com";
    String fromEmail, fromPassword, toEmailList, ccEmailList, emailSubject, emailBody;
    Properties emailProperties;
    Session mailSession;
    MimeMessage emailMessage;


    public GMail(String fromEmail, String fromPassword, String toEmailList, String ccEmailList, String emailSubject, String emailBody) {
        this.context=context;
        this.fromEmail = fromEmail;
        this.fromPassword = fromPassword;
        this.toEmailList = toEmailList.trim();
        this.ccEmailList = ccEmailList.trim();
        this.emailSubject = emailSubject;
        this.emailBody = emailBody;
        if (this.fromEmail.contains("@google")) {
            emailHost = "smtp.gmail.com";
            emailPort = "587";
        }
        if (this.fromEmail.contains("@yahoo")) {
            emailHost = "smtp.mail.yahoo.com";
            emailPort = "587";
        }
        if (this.fromEmail.contains("@hotmail")) {
            emailHost = "smtp.gmail.com";
            emailPort = "587";
        }
        emailProperties = System.getProperties();
        //    emailProperties.put("mail.smtp.port", emailPort);
        emailProperties.put("mail.smtp.auth", smtpAuth);
        emailProperties.put("mail.smtp.port", emailPort);
        emailProperties.put("mail.debug", true);
        emailProperties.put("mail.smtp.starttls.enable", starttls);
        Log.i("GMail", "Mail server properties set.");
    }

    public MimeMessage createEmailMessage() throws AddressException,
            MessagingException, UnsupportedEncodingException {
        Authenticator auth = new Authenticator() {
            //override the getPasswordAuthentication method
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, fromPassword);
            }
        };

        mailSession = Session.getDefaultInstance(emailProperties,auth);
        emailMessage = new MimeMessage(mailSession);
        emailMessage.setFrom(new InternetAddress(fromEmail, fromPassword));

        if (toEmailList.indexOf(',') > 0) {
            emailMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmailList));
        } else {
            emailMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmailList));
        }

        if (!ccEmailList.contains("@")) {
            //emailMessage.setRecipient(Message.RecipientType.CC, null);
        } else {
            if (ccEmailList.indexOf(',') > 0) {
                emailMessage.setRecipients(Message.RecipientType.CC, InternetAddress.parse(ccEmailList));
            } else {
                emailMessage.setRecipient(Message.RecipientType.CC, new InternetAddress(ccEmailList));
            }
        }
        emailMessage.setSubject(emailSubject);
        //emailMessage.setContent(emailBody, "text/html");// for a html email
        emailMessage.setText(emailBody);// for a text email
        Log.i("GMail", "Email Message created.");
        return emailMessage;
    }

    public void sendEmail() throws AddressException, MessagingException {
        boolean t=false;
        Transport transport = mailSession.getTransport("smtp");
        transport.connect(emailHost, fromEmail, fromPassword);
        Log.i("GMail", "allrecipients: " + emailMessage.getAllRecipients());
        transport.sendMessage(emailMessage, emailMessage.getAllRecipients());
        transport.close();
        Log.i("GMail", "Email sent successfully.");
    }
}