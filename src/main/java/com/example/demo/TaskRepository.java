package com.example.demo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {
	List<Task>  findByAccountCodeAndDoneOrderByCode(Integer accountCode, String done);
	List<Task>  findByAccountCodeAndDoneOrderByDate(Integer accountCode, String done);
	List<Task>  findByAccountCodeAndDoneAndCategoryCodeOrderByCode(Integer accountCode, String done, Integer categoryCode);
	List<Task>  findByAccountCodeAndDoneAndCategoryCodeOrderByDate(Integer accountCode, String done, Integer categoryCode);
	List<Task>  findByShareOrderByCode(String share);
	List<Task>  findByShareOrderByDate(String share);
	
}
