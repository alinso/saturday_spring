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
public class AndroidPushNotificationsService {
    private static final String FIREBASE_SERVER_KEY = "AAAAul1ABXA:APA91bEhbiYV3mBnoqMExYz9aGQsWeR4q-JStDPG6Y5BWx-7cvSYwnMMAHhrlcB8oGJvwECjM1HLidOr0PUJ9GQfi3XED2vohrA6CZiW63Ebr8Ry3AwSSKzkSuLxgJOWTYmcCGdcrn6b";
    private static final String FIREBASE_API_URL = "https://fcm.googleapis.com/fcm/send";

    @Autowired
    SystemMessageRepository systemMessageRepository;


    private void send(String title, String to, String message) {

        if(to==null)
            return;

        try {

            JSONObject body = new JSONObject();
            body.put("to", to);
            body.put("Content-Type", "application/json");
            body.put("priority", "high");

            JSONObject notification = new JSONObject();
            notification.put("title", title);
            notification.put("body", message);

            body.put("notification", notification);
            HttpEntity<String> request = new HttpEntity<>(body.toString());


            //CompletableFuture<String> pushNotification = sendToFirebase(request);
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));

            /**
             https://fcm.googleapis.com/fcm/send
             Content-Type:application/json
             Authorization:key=FIREBASE_SERVER_KEY*/

            ArrayList<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
            interceptors.add(new HeaderRequestInterceptor("Authorization", "key=" + FIREBASE_SERVER_KEY));
            interceptors.add(new HeaderRequestInterceptor("Content-Type", "application/json"));

            restTemplate.setInterceptors(interceptors);

            String firebaseRequest = restTemplate.postForObject(FIREBASE_API_URL, request, String.class);

            CompletableFuture<String> pushNotification = CompletableFuture.completedFuture(firebaseRequest);
            CompletableFuture.allOf(pushNotification).join();
            String firebaseResponse = pushNotification.get();

            System.out.println(firebaseResponse);
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
        String message = trigger.getName() + " " + trigger.getSurname() + " sana/gruba bir mesaj gönderdi";
        send("Yeni Mesaj", target.getFirebaseId(), message);
        if(target.getFirebaseId()==null){
            return false;
        }
        else{
            return true;
        }
    }

    @Async
    public Boolean newReviewAvailable(User target) {
        String message =  "Yakın zamanda bir aktiviteye katıldın, katılımcılarla ilgili yorum yazabilirsin";
        send("Yorum Yap", target.getFirebaseId(), message);
        if(target.getFirebaseId()==null){
            return false;
        }
        else{
            return true;
        }
    }

    @Async
    public Boolean newRequest(User trigger, User target) {
        String message = trigger.getName() + " " + trigger.getSurname() + " paylaştığın aktiviteye katılmak istiyor";
        send("Yeni İstek", target.getFirebaseId(), message);
        if(target.getFirebaseId()==null){
            return false;
        }
        else{
            return true;
        }
    }

    @Async
    public Boolean newRequestApproval(User trigger, User target) {
        String message = trigger.getName() + " " + trigger.getSurname() + " aktivitesi için seni onayladı !";
        send("İyi eğlenceler!", target.getFirebaseId(), message);
        if(target.getFirebaseId()==null){
            return false;
        }
        else{
            return true;
        }
    }

    @Async
    public Boolean newReview(User trigger, User target) {
        String message = trigger.getName() + " " + trigger.getSurname() + " senin için bir yorum yazdı";
        send("Yeni yorum", target.getFirebaseId(), message);
        if(target.getFirebaseId()==null){
            return false;
        }
        else{
            return true;
        }
    }

    @Async
    public Boolean newMeeting(User trigger, User target) {
        String message = "Listendeki " + trigger.getName() + " " + trigger.getSurname() + " yeni bir  aktivite paylaştı";
        send("Yeni aktivite", target.getFirebaseId(), message);
        if(target.getFirebaseId()==null){
            return false;
        }
        else{
            return true;
        }
    }

    public boolean newInvitation(User trigger, User reader) {
        String message =  trigger.getName() + " " + trigger.getSurname() + " seni aktivitesine davet ediyor";
        send("Yeni davet", reader.getFirebaseId(), message);
        if(reader.getFirebaseId()==null){
            return false;
        }
        else{
            return true;
        }

    }

    public boolean newGhostMessage(User reader) {
        String message =  "Birisi duvara yeni bir mesaj yazdı!";
        send("Duvarda yeni mesaj!", reader.getFirebaseId(), message);
        if(reader.getFirebaseId()==null){
            return false;
        }
        else{
            return true;
        }
    }

    public boolean newFollow(User trigger, User target) {
        String message =  trigger.getName() + " " + trigger.getSurname() + " seni takip etti";
        send("Yeni Takipçi", target.getFirebaseId(), message);
        if(target.getFirebaseId()==null){
            return false;
        }
        else{
            return true;
        }

    }

    public boolean newReminder(User key, User value) {
        String message =  "Bugün "+value.getName()+" "+value.getSurname()+" ile bir aktiviten var!";
        send("Yaklaşan Aktivite!", key.getFirebaseId(), message);
        if(key.getFirebaseId()==null){
            return false;
        }
        else{
            return true;
        }
    }
}
