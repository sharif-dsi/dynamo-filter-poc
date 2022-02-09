package com.rnd.dynamofilterpoc.controller;

import com.rnd.dynamofilterpoc.bean.Request;
import com.rnd.dynamofilterpoc.bean.Response;
import com.rnd.dynamofilterpoc.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/person")
public class PersonController {

    @Autowired
    PersonService personService;

    @PostMapping("/list")
    public ResponseEntity<Response> listPersons(@RequestBody(required = false) Request request) {
        return ResponseEntity.ok().body(personService.list(request));
    }
}
