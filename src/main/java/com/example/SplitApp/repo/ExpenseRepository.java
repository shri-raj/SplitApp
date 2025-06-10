package com.example.SplitApp.repo;

import com.example.SplitApp.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByPaidBy(String paidBy);

    @Query("SELECT DISTINCT e.paidBy FROM Expense e")
    List<String> findAllPaidByPersons();

    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.paidBy = :personName")
    BigDecimal sumAmountByPaidBy(String personName);
}
