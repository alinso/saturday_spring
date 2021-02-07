package com.alinso.myapp.service;


import com.alinso.myapp.entity.Application;
import com.alinso.myapp.entity.Reference;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.entity.enums.ApplicationStatus;
import com.alinso.myapp.entity.enums.UserStatus;
import com.alinso.myapp.repository.ApplicationRepository;
import com.alinso.myapp.repository.ReferenceRepository;
import com.alinso.myapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class ApplicationService {

    @Autowired
    ApplicationRepository applicationRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ReferenceRepository referenceRepository;

    public void save(Application a){
        a.setApplicationStatus(ApplicationStatus.WAITING);
        applicationRepository.save(a);

        if(!a.getReferenceCode().equals("")){
            Reference reference  = referenceRepository.getValidReference(a.getReferenceCode());
            reference.setApplication(a);
            referenceRepository.save(reference);
        }

    }

    public List<Application> all(int pageNum){
        Pageable pageable=  PageRequest.of(pageNum,10);
        List<Application> applications =  applicationRepository.all(pageable);
        return applications;
    }

    public void decline(Long id){
        Application application  = applicationRepository.findById(id).get();
        application.setApplicationStatus(ApplicationStatus.DECLINED);
        applicationRepository.save(application);
    }

    public void approve(Long id){

        Application application = applicationRepository.findById(id).get();
        User user = userRepository.findByPhone(application.getPhone());

        application.setApplicationStatus(ApplicationStatus.APPROVED);
        applicationRepository.save(application);

        if(user==null)
        user= new User();

        String approvalCode = makeApprovalCode();
        while (!isApprovalCodeUnique(approvalCode)){
            approvalCode=makeApprovalCode();
        }
        user.setAbout(application.getAbout());
        user.setName(application.getName());
        user.setSurname(application.getSurname());
        user.setPhone(application.getPhone());
        user.setApprovalCode(approvalCode);
        user.setStatus(UserStatus.APPROVED);
        user.setEnabled(false);
        userRepository.save(user);

        if(!application.getReferenceCode().equals("")) {
            Reference reference = referenceRepository.getReferenceByApplication(application);
            reference.setChild(user);
            referenceRepository.save(reference);
        }
        //todo:enable sms
        //SendSms.send("Your Saturday account approved. Register at: https://saturdayapp.net/reg/" +approvalCode,application.getPhone() );
    }

    public void removeReferenceCodeFromApplications(String referenceCode){
        List<Application> applications = applicationRepository.findByReferenceCode(referenceCode);
        for(Application a:applications){
            a.setReferenceCode("");
            applicationRepository.save(a);
        }
    }

    public String makeApprovalCode() {
        Character[] characterArray = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'r', 's', 't', 'u', 'v', 'y', 'z',
                '1', '2', '3', '4', '5', '6', '8', '9', '0', '_', '-'};

        Random rnd = new Random();
        Character c1 = characterArray[rnd.nextInt(33)];
        Character c2 = characterArray[rnd.nextInt(33)];
        Character c3 = characterArray[rnd.nextInt(33)];
        Character c4 = characterArray[rnd.nextInt(33)];
        Character c5 = characterArray[rnd.nextInt(33)];
        Character c6 = characterArray[rnd.nextInt(33)];
        Character c7 = characterArray[rnd.nextInt(33)];
        Character c8 = characterArray[rnd.nextInt(33)];
        Character c9 = characterArray[rnd.nextInt(33)];
        Character c10 = characterArray[rnd.nextInt(33)];
        Character c11 = characterArray[rnd.nextInt(33)];

        String newName = c1.toString() + c2.toString() + c4.toString() + c3.toString() + c5.toString() + c6.toString()
                +c7.toString()+c8.toString()+c9.toString()+c10.toString()+c11.toString();
        return newName;
    }

    public Boolean isApprovalCodeUnique(String code){
        int count   =userRepository.findCountByApprovalCode(code);
        if(count==0)
            return true;
        else
            return false;
    }




}
