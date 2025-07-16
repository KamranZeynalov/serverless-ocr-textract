package com.company.model;


public class S3Info {
    private String bucketName;
    private String objectKey;

    public S3Info(String bucketName, String objectKey) {
        this.bucketName = bucketName;
        this.objectKey = objectKey;
    }

    public String getBucketName() {
        return bucketName;
    }

    public String getObjectKey() {
        return objectKey;
    }
}
