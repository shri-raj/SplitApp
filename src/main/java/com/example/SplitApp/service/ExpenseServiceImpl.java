package com.example.SplitApp.service;

import com.example.SplitApp.entity.Expense;
import com.example.SplitApp.entity.ExpenseSplit;
import com.example.SplitApp.repo.ExpenseRepository;
import com.example.SplitApp.repo.ExpenseSplitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class ExpenseServiceImpl implements ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private ExpenseSplitRepository expenseSplitRepository;

    @Autowired
    private PersonService personService;

    @Override
    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }

    @Override
    public Optional<Expense> getExpenseById(Long id) {
        return expenseRepository.findById(id);
    }

    @Override
    public Expense createExpense(Expense expense) {
        validateExpense(expense);

        Set<String> peopleInvolved = new HashSet<>();
        peopleInvolved.add(expense.getPaidBy());

        // Store the splits from the request
        List<ExpenseSplit> requestSplits = expense.getSplits();
        // Clear the splits before saving the expense
        expense.setSplits(null);
        
        if (requestSplits != null && !requestSplits.isEmpty()) {
            for (ExpenseSplit split : requestSplits) {
                peopleInvolved.add(split.getPersonName());
            }
        } else {
            peopleInvolved.add(expense.getPaidBy());
        }

        for (String personName : peopleInvolved) {
            personService.createOrUpdatePerson(personName);
        }

        Expense savedExpense = expenseRepository.save(expense);

        if (requestSplits == null || requestSplits.isEmpty()) {
            createEqualSplits(savedExpense, new ArrayList<>(peopleInvolved));
        } else {
            List<ExpenseSplit> splits = new ArrayList<>();
            for (ExpenseSplit split : requestSplits) {
                split.setExpense(savedExpense);
                splits.add(split);
            }
            expenseSplitRepository.saveAll(splits);
            savedExpense.setSplits(splits);
        }

        personService.updatePersonBalances();
        return savedExpense;
    }

    @Override
    public Expense updateExpense(Long id, Expense expense) {
        Optional<Expense> existingExpense = expenseRepository.findById(id);
        if (existingExpense.isEmpty()) {
            throw new RuntimeException("Expense not found with id: " + id);
        }

        validateExpense(expense);

        expenseSplitRepository.deleteByExpenseId(id);

        expense.setId(id);
        expense.setCreatedAt(existingExpense.get().getCreatedAt());

        Expense updatedExpense = expenseRepository.save(expense);

        if (expense.getSplits() == null || expense.getSplits().isEmpty()) {
            Set<String> peopleInvolved = new HashSet<>();
            peopleInvolved.add(expense.getPaidBy());
            createEqualSplits(updatedExpense, new ArrayList<>(peopleInvolved));
        } else {
            List<ExpenseSplit> splits = new ArrayList<>();
            for (ExpenseSplit split : expense.getSplits()) {
                split.setExpense(updatedExpense);
                splits.add(split);
            }
            expenseSplitRepository.saveAll(splits);
        }

        personService.updatePersonBalances();
        return updatedExpense;
    }

    @Override
    public void deleteExpense(Long id) {
        if (!expenseRepository.existsById(id)) {
            throw new RuntimeException("Expense not found with id: " + id);
        }

        expenseSplitRepository.deleteByExpenseId(id);
        expenseRepository.deleteById(id);
        personService.updatePersonBalances();
    }

    @Override
    public List<String> getAllPeople() {
        Set<String> allPeople = new HashSet<>();
        allPeople.addAll(expenseRepository.findAllPaidByPersons());
        allPeople.addAll(expenseSplitRepository.findAllPersonNames());
        return new ArrayList<>(allPeople);
    }

    private void createEqualSplits(Expense expense, List<String> people) {
        if (people.size() == 1 && people.get(0).equals(expense.getPaidBy())) {
            return;
        }
        
        BigDecimal splitAmount = expense.getAmount().divide(
                BigDecimal.valueOf(people.size()), 2, RoundingMode.HALF_UP);
    
        List<ExpenseSplit> splits = new ArrayList<>();
        for (String person : people) {
            ExpenseSplit split = new ExpenseSplit(expense, person, splitAmount);
            splits.add(split);
        }
    
        expense.setSplits(splits);
        expenseSplitRepository.saveAll(splits);
    }

    private void validateExpense(Expense expense) {
        if (expense.getAmount() == null || expense.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (expense.getDescription() == null || expense.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Description is required");
        }
        if (expense.getPaidBy() == null || expense.getPaidBy().trim().isEmpty()) {
            throw new IllegalArgumentException("Paid by is required");
        }
    }
}
