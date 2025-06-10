package com.example.SplitApp.controller;

import com.example.SplitApp.entity.Person;
import com.example.SplitApp.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class PersonController {

    @Autowired
    private PersonService personService;

    @GetMapping("/people")
    public ResponseEntity<Map<String, Object>> getAllPeople() {
        try {
            List<Person> people = personService.getAllPeople();
            return createSuccessResponse(people, "People retrieved successfully");
        } catch (Exception e) {
            return createErrorResponse("Failed to retrieve people: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/balances")
    public ResponseEntity<Map<String, Object>> getBalances() {
        try {
            Map<String, Object> balances = personService.getBalances();
            return createSuccessResponse(balances, "Balances calculated successfully");
        } catch (Exception e) {
            return createErrorResponse("Failed to calculate balances: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/settlements")
    public ResponseEntity<Map<String, Object>> getSettlements() {
        try {
            Map<String, Object> settlements = personService.getSettlements();
            return createSuccessResponse(settlements, "Settlements calculated successfully");
        } catch (Exception e) {
            return createErrorResponse("Failed to calculate settlements: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/people")
    public ResponseEntity<Map<String, Object>> createPerson(@RequestBody Map<String, String> request) {
        try {
            String name = request.get("name");
            if (name == null || name.trim().isEmpty()) {
                return createErrorResponse("Name is required", HttpStatus.BAD_REQUEST);
            }
            
            Person person = personService.createOrUpdatePerson(name);
            return createSuccessResponse(person, "Person created successfully");
        } catch (Exception e) {
            return createErrorResponse("Failed to create person: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<Map<String, Object>> createSuccessResponse(Object data, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", data);
        response.put("message", message);
        return ResponseEntity.ok(response);
    }

    private ResponseEntity<Map<String, Object>> createErrorResponse(String message, HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("data", null);
        response.put("message", message);
        return ResponseEntity.status(status).body(response);
    }
}