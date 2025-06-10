package com.example.SplitApp.repo;

import com.example.SplitApp.entity.ExpenseSplit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ExpenseSplitRepository extends JpaRepository<ExpenseSplit, Long> {
    List<ExpenseSplit> findByPersonName(String personName);

    @Query("SELECT DISTINCT es.personName FROM ExpenseSplit es")
    List<String> findAllPersonNames();

    @Query("SELECT SUM(es.amount) FROM ExpenseSplit es WHERE es.personName = :personName")
    BigDecimal sumAmountByPersonName(String personName);

    void deleteByExpenseId(Long expenseId);
}
