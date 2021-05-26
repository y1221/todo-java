package com.example.demo;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

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
	
	@Autowired
	TaskController taskController;
	
	@RequestMapping(value="/shareTask", method=RequestMethod.POST)
	public ModelAndView sharePage(
			ModelAndView mv
			) {
		List<ShareTask> list = new ArrayList<ShareTask>();
		list = shareTaskRepository.findShareTaskOrderByCode();
		for (ShareTask t : list) {
			Timestamp date = t.getDate();
			String jisa = taskController.jisa(date);
			t.setDeadline(jisa);
		}
		mv.addObject("tasks", list);
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
				List<ShareTask> list = new ArrayList<ShareTask>();
				list = shareTaskRepository.findShareTaskOrderByDate();
				for (ShareTask t : list) {
					Timestamp date = t.getDate();
					String jisa = taskController.jisa(date);
					t.setDeadline(jisa);
				}
				mv.addObject("tasks", list);
				mv.setViewName("shareTask");
				return mv;
			}
		return mv;
	}

}
