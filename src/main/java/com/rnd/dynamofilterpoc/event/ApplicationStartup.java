package com.rnd.dynamofilterpoc.event;

import com.rnd.dynamofilterpoc.entity.Person;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.CreateTableEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.EnhancedGlobalSecondaryIndex;
import software.amazon.awssdk.services.dynamodb.model.Projection;
import software.amazon.awssdk.services.dynamodb.model.ProjectionType;
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughput;

@Component
@Slf4j
public class ApplicationStartup {

    private final DynamoDbEnhancedClient dynamoDbEnhancedClient;
    private final ProvisionedThroughput defaultProvisionedThroughput;


    @Value("${person.table.name}")
    private String personTableName;

    public ApplicationStartup(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        this.dynamoDbEnhancedClient = dynamoDbEnhancedClient;
        this.defaultProvisionedThroughput = ProvisionedThroughput.builder()
                .readCapacityUnits(1L)
                .writeCapacityUnits(2L)
                .build();
    }

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("Trying to create {} table", personTableName);
        DynamoDbTable<Person> personDynamoDbTable = dynamoDbEnhancedClient.table(personTableName, TableSchema.fromBean(Person.class));

        Projection projection = Projection.builder()
                .projectionType(ProjectionType.ALL)
                .build();

        EnhancedGlobalSecondaryIndex isEnabledStatusGsi = EnhancedGlobalSecondaryIndex.builder()
                .indexName(Person.FIRST_NAME_AGE_INDEX)
                .projection(projection)
                .provisionedThroughput(this.defaultProvisionedThroughput)
                .build();

        CreateTableEnhancedRequest createTableEnhancedRequest = CreateTableEnhancedRequest.builder()
                .globalSecondaryIndices(isEnabledStatusGsi)
                .provisionedThroughput(this.defaultProvisionedThroughput)
                .build();

        try {
            personDynamoDbTable.createTable(createTableEnhancedRequest);
        } catch (Exception exception) {
            log.error(String.format("%s could not be created due to %s", personTableName, exception.getLocalizedMessage()));
        }

        for (int i = 1; i <= 21; i++) {
            personDynamoDbTable.deleteItem(Key.builder().partitionValue(String.valueOf(i)).build());
        }

        for(int i = 1; i <= 21; i++) {
            String status = "B";
            if(i <= 2 || (i >= 13 && i<=14)) {
                status = "A";
            }
            personDynamoDbTable.putItem(Person.builder()
                    .personId(String.valueOf(i))
                    .firstName("Newaz")
                    .personStatus(status)
                    .age(18+i)
                    .build());
        }
    }

}
