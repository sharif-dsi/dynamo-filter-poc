package com.rnd.dynamofilterpoc.bean;


import com.rnd.dynamofilterpoc.entity.Person;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Response {
    List<Person> personList;
    String nextToken;
}
