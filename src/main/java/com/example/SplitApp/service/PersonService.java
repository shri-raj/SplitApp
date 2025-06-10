package com.example.SplitApp.service;

import com.example.SplitApp.entity.Person;

import java.util.List;
import java.util.Map;

public interface PersonService {
    List<Person> getAllPeople();
    Person createOrUpdatePerson(String name);
    void updatePersonBalances();
    Map<String, Object> getBalances();
    Map<String, Object> getSettlements();
}
