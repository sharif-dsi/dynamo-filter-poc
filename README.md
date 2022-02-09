# dynamo-filter-poc

## Requirements
1. Jdk 1.8+
2. docker
3. aws configuration for local

## Aws Configure:
1. create ~/.aws directory.
2. create a file named "config" and paste the following.
3. Edit "config" file and paste the following.

```
[default]
region = ap-southeast-1
```

## Build and Run Instructions

1. Go to project directory. Run docker-compose up.
2. After the services in docker are ready then build with **mvn clean build** and  to run **mvn spring-boot:run** 

## Database:
 - Application will automatically create a person table. It can be seen at browser- **localhost:8001**.
 - See **com/rnd/dynamofilterpoc/event/ApplicationStartup.java** file to see table creation and data delete/insert code.

### Table Structure:
     
   ```
    Table Name: Person
    1. person_id (partition Key)
    2. first_name
    3. age
    5. person_status
    GSI: first_name_age, Parition Key - first_name, Sort Key- age
```  
### API: ``` /person/list ```
```
{
    "firstName": "Newaz",
    "personStatus": "A",
    "nextToken": "<token>" 
}
```
