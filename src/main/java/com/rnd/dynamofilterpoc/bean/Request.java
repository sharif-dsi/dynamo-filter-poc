package com.rnd.dynamofilterpoc.bean;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Request {
    String firstName;
    String personStatus;
    String nextToken;
}
