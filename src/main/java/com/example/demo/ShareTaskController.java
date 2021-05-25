package com.example.demo;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ShareTaskController {
	
	@Autowired
	HttpSession session;
	
	@Autowired
	AccountRepository accountRepository;
	
	@Autowired
	TaskRepository taskRepository;
	
	@Autowired
	ShareTaskRepository shareTaskRepository;
	
	@RequestMapping(value="/shareTask", method=RequestMethod.POST)
	public ModelAndView sharePage(
			ModelAndView mv
			) {
		mv.addObject("tasks", shareTaskRepository.findShareTaskOrderByCode());
		mv.setViewName("shareTask");
		return mv;
	}
	
	@RequestMapping(value="/sortShare", method=RequestMethod.POST)
	public ModelAndView sort(
			@RequestParam("sort") String sort,
			ModelAndView mv
			) {
		session.setAttribute("sort", sort);
		
			switch (sort) {
			case "t":
				return sharePage(mv);
			case "s":
				mv.addObject("tasks", shareTaskRepository.findShareTaskOrderByDate());
				mv.setViewName("shareTask");
				return mv;
			}
		return mv;
	}

}
