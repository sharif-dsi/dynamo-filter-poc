package com.rnd.dynamofilterpoc.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rnd.dynamofilterpoc.entity.Person;
import com.rnd.dynamofilterpoc.bean.Request;
import com.rnd.dynamofilterpoc.bean.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.utils.StringUtils;

import java.lang.reflect.Type;
import java.util.*;


@Slf4j
@Service
public class PersonService {

    @Value("${person.table.name}")
    private String personTableName;

    @Autowired
    DynamoDbEnhancedClient dynamoDbEnhancedClient;

    public Response list(Request request) {


        DynamoDbTable<Person> personDynamoDbTable = dynamoDbEnhancedClient.table(personTableName, TableSchema.fromClass(Person.class));

        if (ObjectUtils.isEmpty(request)) {
            return Response.builder().personList(personDynamoDbTable.scan().iterator().next().items()).build();
        }
        Map<String, AttributeValue> expressionValueMap = new HashMap<>();
        log.info("NextToken {}", request.getNextToken());
        var exclusiveStartKey = convertJsonStringToDynamoDbItem(request.getNextToken());
        if (ObjectUtils.isEmpty(exclusiveStartKey)) {
            log.info("exclusiveStartKey is empty ");
        }
        expressionValueMap.put(":person_status", AttributeValue.builder().s(request.getPersonStatus()).build());
        String expression = "person_status = :person_status";

        Iterator<Page<Person>> pageIterator = personDynamoDbTable.index(Person.FIRST_NAME_AGE_INDEX).query(
                QueryEnhancedRequest.builder()
                        .limit(5)
                        .filterExpression(Expression.builder()
                                .expression(expression)
                                .expressionValues(expressionValueMap)
                                .build())
                        .queryConditional(QueryConditional.keyEqualTo(Key.builder()
                                .partitionValue(request.getFirstName())
                                .build()))
                        .exclusiveStartKey(exclusiveStartKey)
                        .scanIndexForward(true)
                        .build()
        ).iterator();

        Page<Person> personPage = pageIterator.next();

        List<Person> personList = personPage.items();
        Map<String, AttributeValue> lastEvaluatedKey = personPage.lastEvaluatedKey();
        String nextToken = convertDynamoDbItemToJsonString(lastEvaluatedKey);

        return Response.builder()
                .personList(personList)
                .nextToken(nextToken)
                .build();
    }


    private String convertDynamoDbItemToJsonString(Map<String, AttributeValue> item) {
        String jsonString = null;
        Type DynamoDbItemType = new TypeToken<Map<String, AttributeValue>>() {
        }.getType();
        if (ObjectUtils.isNotEmpty(item)) {
            try {
                Gson gson = new Gson();
                jsonString = gson.toJson(item, DynamoDbItemType);
            } catch (Exception exception) {
                log.error("Failed due to {}", exception.getLocalizedMessage());
            }
        }
        return jsonString;
    }

    private Map<String, AttributeValue> convertJsonStringToDynamoDbItem(String jsonString) {
        Map<String, AttributeValue> item = new HashMap<>();
        Type DynamoDbItemType = new TypeToken<Map<String, AttributeValue>>() {
        }.getType();

        if (StringUtils.isNotBlank(jsonString)) {
            try {
                Gson gson = new Gson();
                Map<String, AttributeValue> map = gson.fromJson(jsonString, DynamoDbItemType);
                map.forEach((k, v) -> item.put(k, getAttributeValue(v)));
            } catch (Exception exception) {
                log.error("Failed due to {}", exception.getLocalizedMessage());
            }
        }
        return item.isEmpty() ? null : item;
    }

    private AttributeValue getAttributeValue(AttributeValue attributeValue) {
        if (attributeValue.hasL() && !attributeValue.l().isEmpty()) {
            return AttributeValue.builder().l(attributeValue.l()).build();
        } else if (attributeValue.hasSs() && !attributeValue.ss().isEmpty()) {
            return AttributeValue.builder().ss(attributeValue.ss()).build();
        } else if (attributeValue.hasNs() && !attributeValue.ns().isEmpty()) {
            return AttributeValue.builder().ns(attributeValue.ns()).build();
        } else if (attributeValue.hasBs() && !attributeValue.bs().isEmpty()) {
            return AttributeValue.builder().bs(attributeValue.bs()).build();
        } else if (attributeValue.hasM() && !attributeValue.m().isEmpty()) {
            return AttributeValue.builder().m(attributeValue.m()).build();
        } else if (Objects.nonNull(attributeValue.n())) {
            return AttributeValue.builder().n(attributeValue.n()).build();
        } else if (Objects.nonNull(attributeValue.s())) {
            return AttributeValue.builder().s(attributeValue.s()).build();
        } else if (Boolean.TRUE.equals(attributeValue.nul())) {
            return AttributeValue.builder().nul(true).build();
        } else return null;
    }
}
