package com.rnd.dynamofilterpoc.entity;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamoDbBean
public class Person {

    public final static String FIRST_NAME_AGE_INDEX = "first_name_age";

    @Getter(onMethod_ = {@DynamoDbPartitionKey, @DynamoDbAttribute("person_id")})
    private String personId;


    @Getter(onMethod_ = {@DynamoDbAttribute("first_name"),
            @DynamoDbSecondaryPartitionKey(indexNames = {FIRST_NAME_AGE_INDEX})})
    private String firstName;

    @Getter(onMethod_ = {@DynamoDbAttribute("age"),
            @DynamoDbSecondarySortKey(indexNames = {FIRST_NAME_AGE_INDEX})})
    private Integer age;

    @Getter(onMethod_ = {@DynamoDbAttribute("person_status")})
    private String personStatus;

}

