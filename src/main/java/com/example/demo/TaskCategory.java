package com.example.demo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="task_category")
public class TaskCategory {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="code")
	private Integer code;
	
	@Column(name="category")
	private String category;
	
	@Column(name="account_code")
	private Integer accountCode;

	public Integer getCode() {
		return code;
	}

	public String getCategory() {
		return category;
	}

	public Integer getAccountCode() {
		return accountCode;
	}

	public TaskCategory() {
	}

	public TaskCategory(String category, Integer accountCode) {
		this.category = category;
		this.accountCode = accountCode;
	}



}
