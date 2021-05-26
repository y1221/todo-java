package com.example.demo;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

@Entity
public class ShareTask {
	@Id
	private Integer code;
	
	private String name;
	
	private String task;
	
	private Timestamp date;
	
	@Transient
	private String deadline;

	public String getDeadline() {
		return deadline;
	}

	public void setDeadline(String deadline) {
		this.deadline = deadline;
	}

	public Integer getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public String getTask() {
		return task;
	}

	public Timestamp getDate() {
		return date;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setTask(String task) {
		this.task = task;
	}

	public void setDate(Timestamp date) {
		this.date = date;
	}

	public ShareTask() {
	}
}
