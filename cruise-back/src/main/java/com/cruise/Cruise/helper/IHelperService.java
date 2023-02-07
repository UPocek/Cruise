package com.cruise.Cruise.helper;

import com.sendgrid.helpers.mail.Mail;

import java.util.List;

public interface IHelperService {

    String prepareMailTemplate(String confirmationLink, String mailTemplatePath, String fallBackEmailString);

    String prepareComplexMailTemplate(List<String> data, String mailTemplatePath, String fallBackEmailString);

    Mail prepareMail(String emailFrom, String emailTo, String subject, String contentText);

    void sendEmail(String apiKey, Mail mail);

    Object getConfigValue(String keyName);


}
