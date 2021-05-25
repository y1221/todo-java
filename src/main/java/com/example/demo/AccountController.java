package com.example.demo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AccountController {
	
	@Autowired
	HttpSession session;
	
	@Autowired
	AccountRepository accountRepository;
	
	@Autowired
	TaskRepository taskRepository;
	
	@Autowired
	TaskCategoryRepository taskCategoryRepository;
	
	@Autowired
	TaskController taskController;
	
	@RequestMapping("/")
	public String loginPage() {
		return "login";
	}
	
	@RequestMapping(value="/login", method=RequestMethod.POST)
		public ModelAndView login(
				@RequestParam("loginId") String loginId,
				@RequestParam("password") String password,
				ModelAndView mv
				) {
		if(loginId == null || loginId.length() == 0 || password == null || password.length() == 0) {
			mv.addObject("message", "入力漏れがあります");
			mv.setViewName("login");
			return mv;
		}
		
		List<Account> list = accountRepository.findByLoginIdAndPassword(loginId, password);
		if (list.size() == 0) {
			mv.addObject("message", "ログインIDまたはパスワードが間違っています");
			mv.setViewName("login");
			return mv;
		}
		Account account = list.get(0);
		session.setAttribute("name", account.getName());
		session.setAttribute("accountCode", account.getCode());
		session.setAttribute("categories", taskCategoryRepository.findByAccountCode(account.getCode()));
		session.setAttribute("sort", "t");
		session.setAttribute("category", 0);
		LocalDateTime ldt = LocalDateTime.now();
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy");
		String year = dtf.format(ldt);
		int y = Integer.parseInt(year);
		int[] years = new int[] {y, y+1, y+2, y+3, y+4};

		session.setAttribute("years", years);
		
		session.setAttribute("tasks", taskRepository.findByAccountCodeAndDoneOrderByCode(account.getCode(), ""));
		session.setAttribute("doneTasks", taskRepository.findByAccountCodeAndDoneOrderByCode(account.getCode(), "1"));
		mv.setViewName("top");
		return mv;
	}

	@RequestMapping("/logout")
	public String logout() {
		session.invalidate();
		return loginPage();
	}
	
	@RequestMapping(value="/entry", method= {RequestMethod.POST, RequestMethod.GET})
	public ModelAndView entryPage(
			@RequestParam(name="name", defaultValue="") String name,
			@RequestParam(name="loginId", defaultValue="") String loginId,
			ModelAndView mv
			) {
		mv.addObject("account", new Account(loginId, name));
		mv.setViewName("entry");
		return mv;
	}
	
	@RequestMapping(value="/confirm", method=RequestMethod.POST)
	public ModelAndView entry(
			@RequestParam(name="name", defaultValue="") String name,
			@RequestParam(name="loginId", defaultValue="") String loginId,
			@RequestParam(name="password", defaultValue="") String password,
			ModelAndView mv
			) {
		if(loginId == null || loginId.length() == 0 || password == null || password.length() == 0 ||
				name == null || name.length() == 0	) {
			mv.addObject("message", "入力漏れがあります");
			mv.addObject("account", new Account(loginId, name));
			mv.setViewName("entry");
			return mv;
		}
		List<Account> list = accountRepository.findByLoginId(loginId);
		if (list.size() != 0) {
			mv.addObject("message", "入力されたログインIDはすでに使用されています");
			loginId = "";
			mv.addObject("account", new Account(loginId, name));
			mv.setViewName("entry");
			return mv;
		}
		mv.addObject("account", new Account(loginId, password, name));
		mv.setViewName("confirm");
		return mv;
	}
	
	@RequestMapping(value="/addAccount", method=RequestMethod.POST)
	public ModelAndView loginPage(
			@RequestParam(name="name", defaultValue="") String name,
			@RequestParam(name="loginId", defaultValue="") String loginId,
			@RequestParam(name="password", defaultValue="") String password,
			ModelAndView mv
			) {
		Account a = new Account(loginId, password,  name);
		accountRepository.saveAndFlush(a);
		mv.addObject("addMessage", "新規登録が完了しました");
		mv.setViewName("login");
		return mv;
	}

}
