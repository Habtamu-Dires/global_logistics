package com.yotor.global_logistics.notification.application;

import com.yotor.global_logistics.identity.event.PasswordEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class PasswordNotificationListener {

    @Async
    @TransactionalEventListener
    public void onPasswordSet(PasswordEvent event){
        IO.println(event.password() +  "Password set for user " + event.phone());
    }
}
