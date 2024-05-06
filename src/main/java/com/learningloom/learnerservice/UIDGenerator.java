package com.learningloom.learnerservice;

public class UIDGenerator {
    private static int notificationCounter = 0;

    public static String generateEmailUID() {
        return generateUID("NID", ++notificationCounter);
    }

    private static String generateUID(String prefix, int counter) {
        return String.format("%s%04d", prefix, counter);
    }
}
