package x10.trainup.media.infra.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.retry.RetryPolicy;
import software.amazon.awssdk.core.retry.backoff.BackoffStrategy;
import software.amazon.awssdk.core.retry.conditions.RetryCondition;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.time.Duration;

@Configuration
public class AwsConfig {

    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Value("${cloud.aws.region.static}")
    private String awsRegion;


    @Value("${cloud.aws.s3.timeout-connect-ms:5000}")
    private long connectionTimeoutMs;

    @Value("${cloud.aws.s3.max-retries:3}")
    private int maxRetries;


    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.of(awsRegion))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(accessKey, secretKey)
                        )
                )
                // 1. 💡 Cấu hình HTTP Client TỐI ƯU
                .httpClient(ApacheHttpClient.builder()
                        .connectionTimeout(Duration.ofMillis(connectionTimeoutMs))
                        // Có thể thêm socket timeout nếu cần
                        .build())

                // 2. 💡 Cấu hình Client Override: Thêm Retry Policy
                .overrideConfiguration(
                        ClientOverrideConfiguration.builder()
                                .apiCallAttemptTimeout(Duration.ofSeconds(30)) // Thời gian tối đa cho 1 request
                                .retryPolicy(RetryPolicy.builder()
                                        .numRetries(3) // maxRetries là biến bạn đã inject
                                        .retryCondition(RetryCondition.defaultRetryCondition())
                                        .backoffStrategy(BackoffStrategy.defaultStrategy())
                                        .build())
                                .build())
                .build();
    }

    @Bean
    public software.amazon.awssdk.services.s3.presigner.S3Presigner s3Presigner() {

        return software.amazon.awssdk.services.s3.presigner.S3Presigner.builder()
                .region(Region.of(awsRegion))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(accessKey, secretKey)
                        )
                )
                .build();
    }
}