package com.example.policee.util.config.helper;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class EmailCodeCreater {
    public static String createCode() {
        Random random = new Random();
        int nums = random.nextInt(1000, 9999);
        return String.valueOf(nums);
    }
}