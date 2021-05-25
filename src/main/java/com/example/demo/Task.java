package com.example.demo;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="task")
public class Task {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="code")
	private Integer code;
	
	@Column(name="account_code")
	private Integer accountCode;
	
	@Column(name="task")
	private String task;
	
	@Column(name="date")
	private java.sql.Timestamp date;
	
	@Column(name="category_code")
	private Integer categoryCode;
	
	@Column(name="share")
	private String share;
	
	@Column(name="done")
	private String done;
	
	@Column(name="memo")
	private String memo;
	
	@Transient
	private String name;

	public Integer getCode() {
		return code;
	}

	public Integer getAccountCode() {
		return accountCode;
	}

	public String getTask() {
		return task;
	}

	public java.sql.Timestamp getDate() {
		return date;
	}

	public Integer getCategoryCode() {
		return categoryCode;
	}

	public String getShare() {
		return share;
	}

	public String getDone() {
		return done;
	}

	public String getMemo() {
		return memo;
	}

	public Task() {
	}

	public Task(Integer accountCode, String task, Timestamp date, Integer categoryCode, String share,
			String done, String memo) {
		this.accountCode = accountCode;
		this.task = task;
		this.date = date;
		this.categoryCode = categoryCode;
		this.share = share;
		this.done = done;
		this.memo = memo;
	}

	public Task(Integer code, Integer accountCode, String task, Timestamp date, Integer categoryCode, String share,
			String done, String memo) {
		this.code = code;
		this.accountCode = accountCode;
		this.task = task;
		this.date = date;
		this.categoryCode = categoryCode;
		this.share = share;
		this.done = done;
		this.memo = memo;
	}

	public String getName() {
		return name;
	}
	
}
