package com.rnd.dynamofilterpoc.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;

@Configuration
public class DynamoConfig {

    @Value("${aws.end.point:}")
    private String awsEndPoint;

    @Bean
    DynamoDbEnhancedClient dynamoDbEnhancedClient() {
        DynamoDbClient dynamoDbClient =  DynamoDbClient.builder().endpointOverride(URI.create(awsEndPoint)).region(Region.AP_SOUTHEAST_1).build();
        return DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDbClient).build();
    }
}
