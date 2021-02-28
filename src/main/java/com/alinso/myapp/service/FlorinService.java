package com.alinso.myapp.service;

import com.alinso.myapp.entity.User;
import com.alinso.myapp.exception.UserWarningException;
import com.alinso.myapp.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class FlorinService {

    UserRepository repository;
    private int REFERENCE_POINT = 10;
    private int SEND_REQUEST=1;
    private int GIVE_VOTE=2;
    private int EVENT_UPVOTE=1;


    private int EVENT_EXCESS=-6;
    private int REQUEST_EXCESS=-3;
    private int APPROVAL_EXCESS=-3;
    private int INVITE=-2;
    private int DM=-10;
    private int EVENT_DOWNVOTE=-2;


    public void calculateFlorin(int change,User u){
        int florin = u.getFlorin();
        florin = florin + change;

        if(florin<0){
            throw  new UserWarningException("You dont have any florin, please read florin rules");
        }

        u.setFlorin(florin);
        repository.save(u);
    }

    public void eventDownvote(User u){
        calculateFlorin(EVENT_DOWNVOTE,u);
    }
    public void dm(User u){
        calculateFlorin(DM,u);
    }

    public void invite(User u){
        calculateFlorin(INVITE,u);
    }

    public void approvalExcess(User u){
        calculateFlorin(APPROVAL_EXCESS,u);
    }

    public void requestExcess(User u){
        calculateFlorin(REQUEST_EXCESS,u);
    }

    public void eventExcess(User u){
        calculateFlorin(EVENT_EXCESS,u);
    }

    public void sendRequest(User u){
        calculateFlorin(SEND_REQUEST,u);
    }

    public void giveVoted(User u){
        calculateFlorin(GIVE_VOTE,u);
    }

    public void eventUpvoted(User u){
       calculateFlorin(EVENT_UPVOTE,u);
    }
    public void reference(User u){
        calculateFlorin(REFERENCE_POINT,u);
    }

}













