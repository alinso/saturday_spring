package com.alinso.myapp.pushNotification;

import com.alinso.myapp.entity.User;
import com.alinso.myapp.repository.SystemMessageRepository;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class ExpoPushNotificationService {

    @Autowired
    SystemMessageRepository systemMessageRepository;


    private void send(String title, String to, String message,String screen, Long idOfScreen) {

        if(to==null)
            return;

        try {

            JSONObject body = new JSONObject();
            body.put("to", to);
            body.put("Content-Type", "application/json");

            JSONObject notification = new JSONObject();
            notification.put("screen",screen);
            notification.put("idOfScreen",idOfScreen);

            body.put("title", title);
            body.put("body", message);
            body.put("data",notification);


            HttpEntity<String> request = new HttpEntity<>(body.toString());


            //CompletableFuture<String> pushNotification = sendToFirebase(request);
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));


            ArrayList<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
            interceptors.add(new HeaderRequestInterceptor("Content-Type", "application/json"));

            restTemplate.setInterceptors(interceptors);

            String firebaseRequest = restTemplate.postForObject("https://exp.host/--/api/v2/push/send", request, String.class);

            CompletableFuture<String> pushNotification = CompletableFuture.completedFuture(firebaseRequest);
            CompletableFuture.allOf(pushNotification).join();
            pushNotification.get();


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Async
    public Boolean newMessage(User trigger, User target) {
        String message = trigger.getName() + " " + trigger.getSurname() + " sana bir mesaj gönderdi";
        send("Yeni Mesaj", target.getFirebaseId(), message,"MessagePage",trigger.getId());
        if(target.getFirebaseId()==null){
            return false;
        }
        else{
            return true;
        }
    }

    @Async
    public Boolean newReviewAvailable(User target,Long activityId) {
        String message =  "Yakın zamanda bir aktiviteye katıldın, katılımcılarla ilgili yorum yazabilirsin";
        send("Yorum Yap", target.getFirebaseId(), message,"ActivityDetail",activityId);
        if(target.getFirebaseId()==null){
            return false;
        }
        else{
            return true;
        }
    }

    @Async
    public Boolean newRequest(User trigger, User target,Long activityId) {
        String message = trigger.getName() + " " + trigger.getSurname() + " paylaştığın aktiviteye katılmak istiyor";
        send("Yeni İstek", target.getFirebaseId(), message,"ActivityRequests",activityId);
        if(target.getFirebaseId()==null){
            return false;
        }
        else{
            return true;
        }
    }

    @Async
    public Boolean newRequestApproval(User trigger, User target,Long activityId) {
        String message = trigger.getName() + " " + trigger.getSurname() + " aktivitesi için seni onayladı !";
        send("İyi eğlenceler!", target.getFirebaseId(), message,"ActivityDetail",activityId);
        if(target.getFirebaseId()==null){
            return false;
        }
        else{
            return true;
        }
    }

//    @Async
//    public Boolean newReview(User trigger, User target) {
//        String message = trigger.getName() + " " + trigger.getSurname() + " senin için bir yorum yazdı";
//      //  send("Yeni yorum", target.getFirebaseId(), message);
//        if(target.getFirebaseId()==null){
//            return false;
//        }
//        else{
//            return true;
//        }
//    }

    @Async
    public Boolean newMeeting(User trigger, User target,Long activityId) {
        String message = "Listendeki " + trigger.getName() + " " + trigger.getSurname() + " yeni bir  aktivite paylaştı";
        send("Yeni aktivite", target.getFirebaseId(), message,"ActivityDetail",activityId);
        if(target.getFirebaseId()==null){
            return false;
        }
        else{
            return true;
        }
    }

}
