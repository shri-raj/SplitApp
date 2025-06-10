package com.example.SplitApp.service;

import com.example.SplitApp.entity.Person;
import com.example.SplitApp.repo.ExpenseRepository;
import com.example.SplitApp.repo.ExpenseSplitRepository;
import com.example.SplitApp.repo.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.*;

@Service
@Transactional
public class PersonServiceImpl implements PersonService {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private ExpenseSplitRepository expenseSplitRepository;

    @Override
    public List<Person> getAllPeople() {
        return personRepository.findAll();
    }

    @Override
    public Person createOrUpdatePerson(String name) {
        Optional<Person> existingPerson = personRepository.findByName(name);
        if (existingPerson.isPresent()) {
            return existingPerson.get();
        }

        Person person = new Person(name);
        return personRepository.save(person);
    }

    @Override
    public void updatePersonBalances() {
        Set<String> allPeople = new HashSet<>();
        allPeople.addAll(expenseRepository.findAllPaidByPersons());
        allPeople.addAll(expenseSplitRepository.findAllPersonNames());

        for (String personName : allPeople) {
            Person person = createOrUpdatePerson(personName);

            BigDecimal totalPaid = expenseRepository.sumAmountByPaidBy(personName);
            BigDecimal totalOwed = expenseSplitRepository.sumAmountByPersonName(personName);

            person.setTotalPaid(totalPaid != null ? totalPaid : BigDecimal.ZERO);
            person.setTotalOwed(totalOwed != null ? totalOwed : BigDecimal.ZERO);

            personRepository.save(person);
        }
    }

    @Override
    public Map<String, Object> getBalances() {
        List<Person> people = getAllPeople();
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> balances = new ArrayList<>();

        for (Person person : people) {
            Map<String, Object> balance = new HashMap<>();
            balance.put("name", person.getName());
            balance.put("totalPaid", person.getTotalPaid());
            balance.put("totalOwed", person.getTotalOwed());
            balance.put("balance", person.getBalance());
            balance.put("status", person.getBalance().compareTo(BigDecimal.ZERO) >= 0 ? "owed" : "owes");
            balances.add(balance);
        }

        response.put("balances", balances);
        return response;
    }

    @Override
    public Map<String, Object> getSettlements() {
        List<Person> people = getAllPeople();
        List<Map<String, Object>> settlements = calculateOptimalSettlements(people);

        Map<String, Object> response = new HashMap<>();
        response.put("settlements", settlements);
        response.put("totalTransactions", settlements.size());

        return response;
    }

    private List<Map<String, Object>> calculateOptimalSettlements(List<Person> people) {
        List<Person> workingCopy = new ArrayList<>();
        for (Person person : people) {
            Person copy = new Person(person.getName());
            copy.setTotalPaid(person.getTotalPaid());
            copy.setTotalOwed(person.getTotalOwed());
            workingCopy.add(copy);
        }
        
        List<Map<String, Object>> settlements = new ArrayList<>();
    
        List<Person> creditors = new ArrayList<>();
        List<Person> debtors = new ArrayList<>();
    
        for (Person person : workingCopy) {
            BigDecimal balance = person.getBalance();
            if (balance.compareTo(BigDecimal.ZERO) > 0) {
                creditors.add(person);
            } else if (balance.compareTo(BigDecimal.ZERO) < 0) {
                debtors.add(person);
            }
        }

        int i = 0, j = 0;
        while (i < creditors.size() && j < debtors.size()) {
            Person creditor = creditors.get(i);
            Person debtor = debtors.get(j);

            BigDecimal creditAmount = creditor.getBalance();
            BigDecimal debtAmount = debtor.getBalance().abs();

            BigDecimal settlementAmount = creditAmount.min(debtAmount);

            if (settlementAmount.compareTo(BigDecimal.ZERO) > 0) {
                Map<String, Object> settlement = new HashMap<>();
                settlement.put("from", debtor.getName());
                settlement.put("to", creditor.getName());
                settlement.put("amount", settlementAmount);
                settlements.add(settlement);

                creditor.setTotalPaid(creditor.getTotalPaid().subtract(settlementAmount));
                debtor.setTotalOwed(debtor.getTotalOwed().subtract(settlementAmount));
            }

            if (creditAmount.compareTo(debtAmount) <= 0) {
                i++;
            }
            if (debtAmount.compareTo(creditAmount) <= 0) {
                j++;
            }
        }

        return settlements;
    }
}