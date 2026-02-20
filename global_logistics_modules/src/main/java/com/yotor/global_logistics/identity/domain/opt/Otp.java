package com.yotor.global_logistics.identity.domain.opt;

import java.util.concurrent.ThreadLocalRandom;

public record Otp(String value) {

    public static Otp generate() {
        int code = ThreadLocalRandom.current().nextInt(100_000, 1_000_000);
        return new Otp(String.valueOf(code));
    }
}

