package com.example.SplitApp.service;
import com.example.SplitApp.entity.Expense;
import java.util.List;
import java.util.Optional;

public interface ExpenseService {
    List<Expense> getAllExpenses();
    Optional<Expense> getExpenseById(Long id);
    Expense createExpense(Expense expense);
    Expense updateExpense(Long id, Expense expense);
    void deleteExpense(Long id);
    List<String> getAllPeople();
}

