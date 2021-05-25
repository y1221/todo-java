package com.example.demo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface TaskCategoryRepository extends JpaRepository<TaskCategory, Integer> {
	List<TaskCategory>  findByAccountCode(Integer accountCode);
	List<TaskCategory>  findByAccountCodeAndCategoryLike(Integer accountCode, String category);
}
