package com.alinso.myapp.mail.service;

import com.alinso.myapp.entity.SystemMessage;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.mail.util.SMTPMailUtil;
import com.alinso.myapp.repository.SystemMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MailService {

    @Value("${application.url}")
    String applicationUrl;

    @Autowired
    SystemMessageRepository systemMessageRepository;

    private void sendMail(User target, String message, String subject) {
        //TODO: checkk if user wants to get notifications

        //sending mail takes time, so we open a new thread for not to make user wait
        Thread thread = new Thread() {
            public void run() {
                SMTPMailUtil email = new SMTPMailUtil();
                email.setRecipient(target.getEmail());
                email.setSubject(subject);
                email.setMessage(message);
                email.sendMail();
            }
        };
        thread.start();
    }


    public void sendForgottenPasswordMail(User target, String token) {
        SystemMessage systemMessage = systemMessageRepository.findByMessageCode("FORGOTTEN_PASSWORD_MAIL");

        String link = "<a href='" + applicationUrl + "resetPassword/" + token + "'><strong>[Şifre Yenilemek İçin Tıklayın]</strong></a>";
        String message = systemMessage.getMessageText().replace("{link}", link);

        sendMail(target, message, systemMessage.getMailSubject());
    }

    public void sendMailVerificationMail(User target, String token) {
        SystemMessage systemMessage = systemMessageRepository.findByMessageCode("MAIL_VERIFICATION_MAIL");
        String link = "<a href='" + applicationUrl + "verifyMail/" + token + "'><strong>[Hesabınızı Aktifleştirmek için Tıklayın]</strong></a>";
        String message = systemMessage.getMessageText().replace("{link}", link);

        sendMail(target, message, systemMessage.getMailSubject());
    }


    public void sendNewMessageMail(User target, User trigger) {
        SystemMessage systemMessage = systemMessageRepository.findByMessageCode("NEW_MESSAGE_MAIL");

        String personLink = applicationUrl + "profile/" + trigger.getId();
        String messageLink = applicationUrl + "conversations";
        String message = systemMessage.getMessageText()
                .replace("{personLink}", personLink)
                .replace("{personName}", trigger.getName() + " " + trigger.getSurname())
                .replace("{messageLink}", messageLink);

    //    sendMail(target, message, systemMessage.getMailSubject());
    }

    public void sendNewActivityMail(User target, User trigger, Long activityID) {
        SystemMessage systemMessage = systemMessageRepository.findByMessageCode("NEW_ACTIVITY_MAIL");
        String personLink = applicationUrl + "profile/" + trigger.getId();
        String activityLink = applicationUrl + "activityDetail/" + activityID;
        String message = systemMessage.getMessageText()
                .replace("{activityLink}", activityLink)
                .replace("{personName}", trigger.getName() + " " + trigger.getSurname())
                .replace("{personLink}", personLink);
    //    sendMail(target, message, systemMessage.getMailSubject());
    }

    public void sendNewRequestMail(User target, User trigger, Long activityID) {
        SystemMessage systemMessage = systemMessageRepository.findByMessageCode("NEW_REQUEST_MAIL");
        String personLink = applicationUrl + "profile/" + trigger.getId();
        String activityLink = applicationUrl + "activityDetail/" + activityID;
        String message = systemMessage.getMessageText()
                .replace("{activityLink}", activityLink)
                .replace("{personName}", trigger.getName() + " " + trigger.getSurname())
                .replace("{personLink}", personLink);
     //   sendMail(target, message, systemMessage.getMailSubject());
    }

    public void sendNewRequestApprovalMail(User target, User trigger, Long activityID) {
        SystemMessage systemMessage = systemMessageRepository.findByMessageCode("NEW_REQUEST_APPROVAL_MAIL");
        String personLink = applicationUrl + "profile/" + trigger.getId();
        String activityLink = applicationUrl + "activityDetail/" + activityID;
        String message = systemMessage.getMessageText()
                .replace("{activityLink}", activityLink)
                .replace("{personName}", trigger.getName() + " " + trigger.getSurname())
                .replace("{personLink}", personLink);
    //    sendMail(target, message, systemMessage.getMailSubject());
    }

    public void newReviewMail(User target, User trigger, Long reviewId) {
        SystemMessage systemMessage = systemMessageRepository.findByMessageCode("NEW_REVIEW_MAIL");
        String personLink = applicationUrl + "profile/" + trigger.getId();
        String reviewyLink = applicationUrl + "review/" + reviewId;
        String message = systemMessage.getMessageText()
                .replace("{reviewLink}", reviewyLink)
                .replace("{personName}", trigger.getName() + " " + trigger.getSurname())
                .replace("{personLink}", personLink);
    //    sendMail(target, message, systemMessage.getMailSubject());
    }

    public void newReviewAvailableMail(User target, Long activityID) {
        SystemMessage systemMessage = systemMessageRepository.findByMessageCode("NEW_REVIEW_AVAILABLE_MAIL");
        String activityLink = applicationUrl + "activityDetail/" + activityID;
        String message = systemMessage.getMessageText()
                .replace("{activityLink}", activityLink);
    //    sendMail(target, message, systemMessage.getMailSubject());
    }


    public void newPremiumFor5ReferenceMail(User target) {
        SystemMessage systemMessage = systemMessageRepository.findByMessageCode("NEW_PREMIUM_FOR_5_REFERENCE_MAIL");
        sendMail(target, systemMessage.getMessageText(), systemMessage.getMailSubject());

    }

}
