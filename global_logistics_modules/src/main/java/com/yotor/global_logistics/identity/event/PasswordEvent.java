package com.yotor.global_logistics.identity.event;

import lombok.Builder;

@Builder
public record PasswordEvent (
        String phone,
        String password,
        String reason
){
}
