package com.example.demo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="account")
public class Account {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="code")
	private Integer code;
	
	@Column(name="login_id")
	private String loginId;
	
	@Column(name="password")
	private String password;
	
	@Column(name="name")
	private String name;
	
	public Integer getCode() {
		return code;
	}

	public String getLoginId() {
		return loginId;
	}

	public String getPassword() {
		return password;
	}

	public String getName() {
		return name;
	}

	public Account() {
		
	}

	public Account(String loginId, String password, String name) {
		this.loginId = loginId;
		this.password = password;
		this.name = name;
	}

	public Account(String loginId, String name) {
		this.loginId = loginId;
		this.name = name;
	}

}
