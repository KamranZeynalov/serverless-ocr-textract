package com.company;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.company.model.S3Info;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.textract.TextractClient;
import software.amazon.awssdk.services.textract.model.*;

import java.util.List;
import java.util.stream.Collectors;

public class ProcessImageHandler implements RequestHandler<S3Event, String> {

    private static final String AWS_REGION = System.getenv("AWS_REGION");

    private final S3Client s3Client = S3Client.builder()
                                              .region(Region.of(System.getenv(AWS_REGION)))
                                              .credentialsProvider(DefaultCredentialsProvider.create())
                                              .build();

    private final TextractClient textractClient = TextractClient.builder()
                                                                .region(Region.of(System.getenv(AWS_REGION)))
                                                                .credentialsProvider(DefaultCredentialsProvider.create())
                                                                .build();


    @Override
    public String handleRequest(S3Event event, Context context) {
        context.getLogger().log("Lambda triggered");

        try{
            S3Info s3Info = getS3Info(event);

            context.getLogger().log("Bucket: " + s3Info.getBucketName() + "\n");
            context.getLogger().log("Key: " + s3Info.getObjectKey() + "\n");


            String extractedText = getTextFromImage(s3Info);
            context.getLogger().log("Extracted Text:\n" + extractedText);

            return "Extraction complete";
        }
        catch (Exception ex) {
            context.getLogger().log("Exception: " + ex.getMessage());
            return "Operation failed";
        }
    }

    private S3Info getS3Info(S3Event event) {
        if (event.getRecords() == null || event.getRecords().isEmpty()) {
            throw new IllegalArgumentException("No S3 records found in event");
        }
        var s3Entity = event.getRecords().get(0).getS3();
        if (s3Entity == null || s3Entity.getBucket() == null || s3Entity.getObject() == null) {
            throw new IllegalArgumentException("Invalid S3 event structure");
        }

        return new S3Info(s3Entity.getBucket().getName(),s3Entity.getObject().getKey());
    }

    private String getTextFromImage(S3Info s3Info) {
        Document document = Document.builder()
                .s3Object(S3Object.builder()
                        .bucket(s3Info.getBucketName())
                        .name(s3Info.getObjectKey())
                        .build())
                .build();

        DetectDocumentTextRequest request = DetectDocumentTextRequest.builder()
                .document(document)
                .build();

        DetectDocumentTextResponse response = textractClient.detectDocumentText(request);

        List<String> lines = response.blocks().stream()
                .filter(block -> block.blockType() == BlockType.LINE)
                .map(Block::text)
                .collect(Collectors.toList());

        return String.join("\n", lines);
    }
}
