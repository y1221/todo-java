package com.example.demo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
@Repository
public interface ShareTaskRepository extends JpaRepository<ShareTask, Integer> {
	@Query(value="SELECT task.code AS code, account.name AS name, task.task AS task, task.date AS date "
			+ "FROM task JOIN account ON account.code = task.account_code "
			+ "WHERE task.share = '1' AND task.done = '' ORDER BY task.code",
			nativeQuery = true)
	public List<ShareTask> findShareTaskOrderByCode();
	
	@Query(value="SELECT task.code AS code, account.name AS name, task.task AS task, task.date AS date "
			+ "FROM task JOIN account ON account.code = task.account_code "
			+ "WHERE task.share = '1' AND task.done = '' ORDER BY task.date",
			nativeQuery = true)
	public List<ShareTask> findShareTaskOrderByDate();
}
