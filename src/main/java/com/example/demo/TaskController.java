package com.example.demo;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class TaskController {
		
	@Autowired
	HttpSession session;
	
	@Autowired
	TaskRepository taskRepository;
	
	@Autowired
	TaskCategoryRepository taskCategoryRepository;
	
	public int accountCode() {
		int accountCode = (int) session.getAttribute("accountCode");
		return accountCode;
	}
	
	public ModelAndView error(
			ModelAndView mv
			) {
		mv.addObject("message", "無効な操作が行われました");
		mv.setViewName("login");
		return mv;
	}
	
	@RequestMapping("/back")
	public ModelAndView topPage(
			ModelAndView mv
			) {
		int accountCode = accountCode();
		String sort = (String)session.getAttribute("sort");
		int categoryCode = (int)session.getAttribute("category");

		if (categoryCode == 0) {
			switch (sort) {
			case "t":
				session.setAttribute("tasks", taskRepository.findByAccountCodeAndDoneOrderByCode(accountCode, ""));
				session.setAttribute("doneTasks", taskRepository.findByAccountCodeAndDoneOrderByCode(accountCode, "1"));
				mv.setViewName("top");
				return mv;
			case "s":
				session.setAttribute("tasks", taskRepository.findByAccountCodeAndDoneOrderByDate(accountCode, ""));
				session.setAttribute("doneTasks", taskRepository.findByAccountCodeAndDoneOrderByCode(accountCode, "1"));
				mv.setViewName("top");
				return mv;
			}
		} else {
			switch (sort) {
			case "t":
				session.setAttribute("tasks", taskRepository.findByAccountCodeAndDoneAndCategoryCodeOrderByCode(accountCode, "", categoryCode));
				session.setAttribute("doneTasks", taskRepository.findByAccountCodeAndDoneOrderByCode(accountCode, "1"));
				mv.setViewName("top");
				return mv;
			case "s":
				session.setAttribute("tasks", taskRepository.findByAccountCodeAndDoneAndCategoryCodeOrderByDate(accountCode, "", categoryCode));
				session.setAttribute("doneTasks", taskRepository.findByAccountCodeAndDoneOrderByCode(accountCode, "1"));
				mv.setViewName("top");
				return mv;
			}
		}
		return mv;
	}
	
	@RequestMapping("/add")
	public ModelAndView addPage(
			ModelAndView mv
			) {
		String task = "";
		int year = 0;
		int month = 0;
		int day = 0;
		int hour = 0;
		int categoryCode = 0;
		String share = "";
		mv.addObject("task", task);
		mv.addObject("year", year);
		mv.addObject("month", month);
		mv.addObject("day", day);
		mv.addObject("hour", hour);
		mv.addObject("categoryCode", categoryCode);
		mv.addObject("share", share);
		mv.setViewName("add");
		return mv;
	}
	
	@RequestMapping(value="/doAdd", method=RequestMethod.POST)
	public ModelAndView add(
			@RequestParam(name="task", defaultValue="") String task,
			@RequestParam("year") String year,
			@RequestParam("month") String month,
			@RequestParam("day") String day,
			@RequestParam(name="hour", defaultValue="") String hour,
			@RequestParam("category") int categoryCode,
			@RequestParam(name="share", defaultValue="") String share,
			ModelAndView mv
			) {
		if (task.length() == 0) {
			mv.addObject("message", "タスクを入力してください");
			mv.setViewName("add");
			return addPage(mv);
		}
		int accountCode = accountCode();
		
		String done = "";
		String memo = "";
		
		StringBuilder sb = new StringBuilder();
		sb.append(year);
		sb.append("-");
		sb.append(month);
		sb.append("-");
		sb.append(day);
		
		if (hour.equals("時")) {
			String str = sb.toString();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			java.util.Date parsedDate = null;
			try {
				parsedDate = sdf.parse(str);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			Timestamp date = new java.sql.Timestamp(parsedDate.getTime());

			Task t = new Task(accountCode, task, date, 
					categoryCode, share, done, memo);
			
			taskRepository.saveAndFlush(t);
			return topPage(mv);
		} else {
			sb.append(" ");
			sb.append(hour);
			String str = sb.toString();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH");
			java.util.Date parsedDate = null;
			try {
				parsedDate = sdf.parse(str);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			Timestamp date = new java.sql.Timestamp(parsedDate.getTime());

			Task t = new Task(accountCode, task, date, 
					categoryCode, share, done, memo);
			
			taskRepository.saveAndFlush(t);
			return topPage(mv);
		}
	}
	
	@RequestMapping(value="/addCategory", method=RequestMethod.POST)
	public ModelAndView addCategoryPage(
			@RequestParam(name="task", defaultValue="") String task,
			@RequestParam("year") int year,
			@RequestParam("month") int month,
			@RequestParam("day") int day,
			@RequestParam(name="hour", defaultValue="") String strHour,
			@RequestParam("category") int categoryCode,
			@RequestParam(name="share", defaultValue="") String share,
			@RequestParam(name="memo", defaultValue="") String memo,
			@RequestParam("before") String before,
			ModelAndView mv
			) {
		session.setAttribute("reTask", task);
		session.setAttribute("reYear", year);
		session.setAttribute("reMonth", month);
		session.setAttribute("reDay", day);
		int hour = 0;
		if (strHour.equals("時")) {
			hour = 0;
		} else {
			hour = Integer.parseInt(strHour);
		}
		session.setAttribute("reHour", hour);
		session.setAttribute("reCategoryCode", categoryCode);
		session.setAttribute("reShare", share);
		session.setAttribute("reMemo", memo);
		session.setAttribute("before", before);

		mv.setViewName("category");
		return mv;
	}
	
	@RequestMapping(value="/doAddCategory", method=RequestMethod.POST)
	public ModelAndView addCategory(
			@RequestParam("category") String category,
			ModelAndView mv
			) {
		if (category == null || category.length() == 0) {
			mv.addObject("message", "カテゴリーを入力してください");
			mv.setViewName("category");
			return mv;
		}
		int accountCode = accountCode();
		List<TaskCategory>  list = taskCategoryRepository.findByAccountCodeAndCategoryLike(accountCode, category);
		if (list.size() != 0) {
			mv.addObject("message", "入力されたカテゴリーはすでに使用されています");
			mv.setViewName("category");
			return mv;
		}
		
		TaskCategory t = new TaskCategory(category, accountCode);
		taskCategoryRepository.saveAndFlush(t);
		session.setAttribute("categories", taskCategoryRepository.findByAccountCode(accountCode));
		List<TaskCategory>  l = taskCategoryRepository.findByAccountCodeAndCategoryLike(accountCode, category);
		TaskCategory task = l.get(0);
		session.setAttribute("reCategoryCode", task.getCode());
		return backCategoryPage(mv);
	}
	
	@RequestMapping(value="/backCategoryPage", method=RequestMethod.POST)
	public ModelAndView backCategoryPage(
			ModelAndView mv
			) {
		mv.addObject("task", (String)session.getAttribute("reTask"));
		mv.addObject("year", (int)session.getAttribute("reYear"));
		mv.addObject("month", (int)session.getAttribute("reMonth"));
		mv.addObject("day", (int)session.getAttribute("reDay"));
		mv.addObject("hour", (int)session.getAttribute("reHour"));
		mv.addObject("categoryCode", (int)session.getAttribute("reCategoryCode"));
		mv.addObject("share", (String)session.getAttribute("reShare"));
		mv.addObject("memo", (String)session.getAttribute("reMemo"));
		String before = (String)session.getAttribute("before");
		if (before.equals("add")) {
			mv.setViewName("add");
			return mv;
		} else {
			mv.setViewName("edit");
			return mv;
		}
	}
	
//	@RequestMapping(value="/category", method=RequestMethod.POST)
//	public ModelAndView category(
//			@RequestParam("category") int categoryCode,
//			ModelAndView mv
//			) {
//		int accountCode = accountCode();
//		session.setAttribute("category", categoryCode);
//
//		if (categoryCode == 0) {
//			session.setAttribute("tasks", taskRepository.findByAccountCodeAndDoneOrderByCode(accountCode, ""));
//			session.setAttribute("doneTasks", taskRepository.findByAccountCodeAndDoneOrderByCode(accountCode, "1"));
//			mv.setViewName("top");
//			return mv;
//		} else {
//			session.setAttribute("tasks", taskRepository.findByAccountCodeAndDoneAndCategoryCodeOrderByCode(accountCode, "", categoryCode));
//			mv.addObject("selectCode", categoryCode);
//			session.setAttribute("doneTasks", taskRepository.findByAccountCodeAndDoneOrderByCode(accountCode, "1"));
//			mv.setViewName("top");
//			return mv;
//		}
//	}
	
	@RequestMapping(value="/category/{code}", method=RequestMethod.GET)
	public ModelAndView category(
			@PathVariable("code") int categoryCode,
			ModelAndView mv
			) {
//		int accountCode = accountCode();
		session.setAttribute("category", categoryCode);
		if (categoryCode != 0) {
			Optional<TaskCategory> task = taskCategoryRepository.findById(categoryCode);
			if (task.isPresent()) {
				TaskCategory t = task.get();
				session.setAttribute("selectCategory", t.getCategory());
			} else {
				return error(mv);
			}
		}
		return topPage(mv);

//		if (categoryCode == 0) {
//			session.setAttribute("tasks", taskRepository.findByAccountCodeAndDoneOrderByCode(accountCode, ""));
//			session.setAttribute("doneTasks", taskRepository.findByAccountCodeAndDoneOrderByCode(accountCode, "1"));
//			mv.setViewName("top");
//			return mv;
//		} else {
//			session.setAttribute("tasks", taskRepository.findByAccountCodeAndDoneAndCategoryCodeOrderByCode(accountCode, "", categoryCode));
//			Optional<TaskCategory> task = taskCategoryRepository.findById(categoryCode);
//			if (task.isPresent()) {
//				TaskCategory t = task.get();
//				session.setAttribute("selectCategory", t.getCategory());
//			}
//			session.setAttribute("doneTasks", taskRepository.findByAccountCodeAndDoneOrderByCode(accountCode, "1"));
//			mv.setViewName("top");
//			return mv;
//		}
	}
	
	@RequestMapping(value="/sort", method=RequestMethod.POST)
	public ModelAndView sort(
			@RequestParam("sort") String sort,
			ModelAndView mv
			) {
//		int categoryCode = (int) session.getAttribute("category");
//		int accountCode = accountCode();
		session.setAttribute("sort", sort);
		return topPage(mv);

//		if (categoryCode == 0) {
//			switch (sort) {
//			case "t":
//				return topPage(mv);
//			case "s":
//				session.setAttribute("tasks", taskRepository.findByAccountCodeAndDoneOrderByDate(accountCode, ""));
//				session.setAttribute("doneTasks", taskRepository.findByAccountCodeAndDoneOrderByCode(accountCode, "1"));
//				mv.setViewName("top");
//				return mv;
//			}
//		} else {
//			switch (sort) {
//			case "t":
//				session.setAttribute("tasks", taskRepository.findByAccountCodeAndDoneAndCategoryCodeOrderByCode(accountCode, "", categoryCode));
//				session.setAttribute("doneTasks", taskRepository.findByAccountCodeAndDoneOrderByCode(accountCode, "1"));
//				mv.setViewName("top");
//				return mv;
//			case "s":
//				session.setAttribute("tasks", taskRepository.findByAccountCodeAndDoneAndCategoryCodeOrderByDate(accountCode, "", categoryCode));
//				session.setAttribute("doneTasks", taskRepository.findByAccountCodeAndDoneOrderByCode(accountCode, "1"));
//				mv.setViewName("top");
//				return mv;
//			}
//		}
	}
	
	@RequestMapping(value="/edit/{code}", method=RequestMethod.GET)
	public ModelAndView editPage(
			@PathVariable("code") int code,
			ModelAndView mv
			) {
		Optional<Task> task = taskRepository.findById(code);
		if (task.isPresent()) {
			Task editTask = task.get();
			mv.addObject("code", editTask.getCode());
			mv.addObject("task", editTask.getTask());
			mv.addObject("categoryCode", editTask.getCategoryCode());
			mv.addObject("share", editTask.getShare());
			mv.addObject("memo", editTask.getMemo());
			Timestamp date = editTask.getDate();
			SimpleDateFormat y = new SimpleDateFormat("yyyy");
			SimpleDateFormat M = new SimpleDateFormat("MM");
			SimpleDateFormat d = new SimpleDateFormat("dd");
			SimpleDateFormat H = new SimpleDateFormat("HH");
			String strYear = y.format(date);
			String strMonth = M.format(date);
			String strDay = d.format(date);
			String strHour = H.format(date);
			int year = Integer.parseInt(strYear);
			int month = Integer.parseInt(strMonth);
			int day = Integer.parseInt(strDay);
			int hour = Integer.parseInt(strHour);
			mv.addObject("year", year);
			mv.addObject("month", month);
			mv.addObject("day", day);
			mv.addObject("hour", hour);
			mv.setViewName("edit");
			return mv;
		} else {
			return error(mv);
		}
		
	}
	
	@RequestMapping(value="/update/{code}", method=RequestMethod.POST)
	public ModelAndView update(
			@PathVariable("code") int code,
			@RequestParam(name="task", defaultValue="") String task,
			@RequestParam("year") String year,
			@RequestParam("month") String month,
			@RequestParam("day") String day,
			@RequestParam(name="hour", defaultValue="") String hour,
			@RequestParam("category") int categoryCode,
			@RequestParam(name="share", defaultValue="") String share,
			@RequestParam(name="memo", defaultValue="") String memo,
			ModelAndView mv
			) {
		if (task.length() == 0) {
			mv.addObject("message", "タスクを入力してください");
			mv.setViewName("add");
			return addPage(mv);
		}
		int accountCode = accountCode();
		
		String done = "";
		
		StringBuilder sb = new StringBuilder();
		sb.append(year);
		sb.append("-");
		sb.append(month);
		sb.append("-");
		sb.append(day);
		
		if (hour.equals("時")) {
			String str = sb.toString();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			java.util.Date parsedDate = null;
			try {
				parsedDate = sdf.parse(str);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			Timestamp date = new java.sql.Timestamp(parsedDate.getTime());

			Task t = new Task(code, accountCode, task, date, 
					categoryCode, share, done, memo);
			
			taskRepository.saveAndFlush(t);
			return topPage(mv);
		} else {
			sb.append(" ");
			sb.append(hour);
			String str = sb.toString();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH");
			java.util.Date parsedDate = null;
			try {
				parsedDate = sdf.parse(str);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			Timestamp date = new java.sql.Timestamp(parsedDate.getTime());

			Task t = new Task(code, accountCode, task, date, 
					categoryCode, share, done, memo);
			
			taskRepository.saveAndFlush(t);
			return topPage(mv);
		}
	}
	
	@RequestMapping(value="/delete/{code}", method=RequestMethod.POST)
	public ModelAndView delete(
			@PathVariable("code") int code,
			ModelAndView mv
			) {
		taskRepository.deleteById(code);
		return topPage(mv);
	}
	
	@RequestMapping(value="/done/{code}", method=RequestMethod.POST)
	public ModelAndView done(
			@PathVariable("code") int code,
			ModelAndView mv
			) {
		Optional<Task> task = taskRepository.findById(code);
		if (task.isPresent()) {
			Task doneTask = task.get();
			String done = doneTask.getDone();
			if (done.length() == 0) {
				done = "1";
			} else {
				done = "";
			}
			Task t = new Task(code, doneTask.getAccountCode(), doneTask.getTask(), doneTask.getDate(), 
					doneTask.getCategoryCode(), doneTask.getShare(), done, doneTask.getMemo());
			
			taskRepository.saveAndFlush(t);
			return topPage(mv);
		} else {
			return error(mv);
		}
	}
}
