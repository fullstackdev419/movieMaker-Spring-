package com.example.amproiect2.buckets;

public enum BucketName {
    PROFILE_IMAGE("am-project-bucket");

    private final String bucketName;

    BucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getBucketName() {
        return bucketName;
    }
}
