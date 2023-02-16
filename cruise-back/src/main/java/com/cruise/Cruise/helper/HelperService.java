package com.cruise.Cruise.helper;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@Service
public class HelperService implements IHelperService {

    @Override
    public String prepareMailTemplate(String confirmationLink, String mailTemplatePath, String fallBackEmailString) {
        String secretKey = "SECRET_TOKEN_LINK_PLACEHOLDER";
        String html = fallBackEmailString + confirmationLink;
        try {
            html = Files.readString(Paths.get(mailTemplatePath)).replaceFirst(secretKey, confirmationLink);
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.toString());
        }
        return html;
    }

    @Override
    public String prepareComplexMailTemplate(List<String> data, String mailTemplatePath, String fallBackEmailString) {
        String html = fallBackEmailString;
        try {
            html = Files.readString(Paths.get(mailTemplatePath));
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.toString());
        }
        for (int i = 0; i < data.size(); i++) {
            html = html.replace("#?" + i, data.get(i));
        }

        return html;
    }

    @Override
    public Mail prepareMail(String emailFrom, String emailTo, String subject, String contentText) {
        Email from = new Email(emailFrom);
        Email to = new Email(emailTo);
        Content content = new Content("text/html", contentText);
        return new Mail(from, subject, to, content);
    }

    @Override
    public void sendEmail(String apiKey, Mail mail) {
        SendGrid sg = new SendGrid(apiKey);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.toString());
        }
    }

    @Override
    public Object getConfigValue(String keyName) {
        InputStream inputStream;
        try {
            inputStream = new FileInputStream(new File("src/main/resources/config.yaml"));
        } catch (IOException e) {
            try {
                inputStream = new FileInputStream(new File("cruise-back/src/main/resources/config.yaml"));
            } catch (IOException ex) {
                try {
                    inputStream = new FileInputStream(new File("target/classes/config.yaml"));
                } catch (IOException exx) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.toString());
                }
            }
        }

        Yaml yaml = new Yaml();
        Map<String, Object> data = yaml.load(inputStream);
        return data.get(keyName);
    }

}
