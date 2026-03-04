package com.yotor.global_logistics.notification.application;

import com.yotor.global_logistics.identity.event.OtpEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class OtpNotificationListener {

    @Async
    @TransactionalEventListener
    public void onOtpSent(OtpEvent event){
        IO.println("Otp send " + event.code() + " for phone number " + event.phone());
    }
}
