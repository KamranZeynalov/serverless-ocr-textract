package com.company;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.URL;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.company.util.ResponseBuilder.buildResponse;

public class GeneratePreSignedUrlHandler implements RequestHandler<Map<String, Object>, Map<String, Object>> {

    private static final String BUCKET_NAME = System.getenv("BUCKET_NAME");
    private static final String AWS_REGION = Optional.ofNullable(System.getenv("AWS_REGION")).orElse("eu-west-1");
    private static final Set<String> ALLOWED_TYPES = Set.of("jpg", "jpeg", "png", "webp");
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final S3Presigner PRESIGNER = S3Presigner.builder()
                                                            .region(Region.of(AWS_REGION))
                                                            .credentialsProvider(DefaultCredentialsProvider.create())
                                                            .build();

    @Override
    public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {
        context.getLogger().log("Lambda triggered");

        try {
            String bodyJson = (String) input.get("body");
            Map<String, String> body = objectMapper.readValue(bodyJson, Map.class);

            String fileType = Optional.ofNullable(body.get("fileType")).orElse("png").toLowerCase();
            if (!ALLOWED_TYPES.contains(fileType)) {
                return buildResponse(400, Map.of("error", "Unsupported file type: " + fileType));
            }

            String objectKey = generateObjectKey(fileType);
            String contentType = getContentType(fileType);
            URL presignedUrl = createPresignedUrl(objectKey, contentType);

            Map<String, Object> responseBody = Map.of(
                    "uploadUrl", presignedUrl.toString(),
                    "key", objectKey
            );

            return buildResponse(200, responseBody);


        }
        catch (Exception ex) {
            return buildResponse(500, Map.of("error", "Internal server error: " + ex.getMessage()));
        }
    }

    private URL createPresignedUrl(String objectKey, String contentType) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                                                            .bucket(BUCKET_NAME)
                                                            .key(objectKey)
                                                            .contentType(contentType)
                                                            .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                                                                        .putObjectRequest(putObjectRequest)
                                                                        .signatureDuration(Duration.ofMinutes(10))
                                                                        .build();

        return PRESIGNER.presignPutObject(presignRequest).url();
    }

    private String generateObjectKey(String fileType) {
        return "uploads/" + UUID.randomUUID() + "." + fileType;
    }

    private String getContentType(String fileType) {
        return switch (fileType) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "webp" -> "image/webp";
            default -> "application/octet-stream";
        };
    }
}
