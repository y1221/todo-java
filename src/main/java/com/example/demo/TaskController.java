package com.example.demo;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

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
	
	public int[] timestampToInt(Timestamp date) {
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
		int[] dates = new int[] {year, month, day, hour};
		return dates;
	}
	
	public String jisa(Timestamp date) {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime deadline = date.toLocalDateTime();
		long jisaDay = ChronoUnit.DAYS.between(now, deadline);
		long jisaHour = ChronoUnit.HOURS.between(now, deadline);
		long hour = jisaHour - jisaDay * 24;
		StringBuilder sb = new StringBuilder();
		sb.append(jisaDay);
		sb.append("日 ");
		sb.append(hour);
		sb.append("時間");
		String jisa = sb.toString();
		return jisa;
	}
	
	public void randomTalk() {
		Random random = new Random();
		int r = random.nextInt(5);
		switch (r) {
		case 0:
			session.setAttribute("talk", "今日も頑張ろう！");
			break;
		case 1:
			session.setAttribute("talk", "締め切りを守ることが大事！");
			break;
		case 2:
			session.setAttribute("talk", "余裕を持って取り組もう！");
			break;
		case 3:
			session.setAttribute("talk", "タスクを共有して責任感を持とう！");
			break;
		case 4:
			session.setAttribute("talk", "みんなのタスクを確認してモチベーションを上げよう！");
			break;
		}
	}
	
	public void addTaskTalk(String task, String deadline) {
		Random random = new Random();
		int r = random.nextInt(2);
		switch (r) {
		case 0:
			session.setAttribute("talk", task + "を登録したよ！" + deadline + "までだね！頑張ろう！");
			break;
		case 1:
			session.setAttribute("talk", task + "だね！" + deadline + "より早く終わらせることを意識しよう！");
			break;
		}
	}
	
	public void doneTaskTalk(String task) {
		Random random = new Random();
		int r = random.nextInt(3);
		switch (r) {
		case 0:
			session.setAttribute("talk", "すごい！" + task + "を終わらせたんだね！");
			break;
		case 1:
			session.setAttribute("talk", task + "が終わったね！おめでとう！");
			break;
		case 2:
			session.setAttribute("talk", task + "が終わったんだ！よく頑張ったね！");
			break;
		}
	}
	
	@RequestMapping("/back")
	public ModelAndView back(
			ModelAndView mv
			) {
		randomTalk();
		return topPage(mv);
	}
	
	public ModelAndView topPage(
			ModelAndView mv
			) {
		int accountCode = accountCode();
		String sort = (String)session.getAttribute("sort");
		int categoryCode = (int)session.getAttribute("category");
		
		List<Task> list = new ArrayList<Task>();
		if (categoryCode == 0) {
			switch (sort) {
			case "t":
				list = taskRepository.findByAccountCodeAndDoneOrderByCode(accountCode, "");
				for (Task t : list) {
					Timestamp date = t.getDate();
					String jisa = jisa(date);
					t.setMemo(jisa);
				}
				session.setAttribute("tasks", list);
				session.setAttribute("doneTasks", taskRepository.findByAccountCodeAndDoneOrderByCode(accountCode, "1"));
				mv.setViewName("top");
				return mv;
			case "s":
				list = taskRepository.findByAccountCodeAndDoneOrderByDate(accountCode, "");
				for (Task t : list) {
					Timestamp date = t.getDate();
					String jisa = jisa(date);
					t.setMemo(jisa);
				}
				session.setAttribute("tasks", list);
				session.setAttribute("doneTasks", taskRepository.findByAccountCodeAndDoneOrderByDate(accountCode, "1"));
				mv.setViewName("top");
				return mv;
			}
		} else {
			switch (sort) {
			case "t":
				list = taskRepository.findByAccountCodeAndDoneAndCategoryCodeOrderByCode(accountCode, "", categoryCode);
				for (Task t : list) {
					Timestamp date = t.getDate();
					String jisa = jisa(date);
					t.setMemo(jisa);
				}
				session.setAttribute("tasks", list);
				session.setAttribute("doneTasks", taskRepository.findByAccountCodeAndDoneAndCategoryCodeOrderByCode(accountCode, "1", categoryCode));
				mv.setViewName("top");
				return mv;
			case "s":
				list = taskRepository.findByAccountCodeAndDoneAndCategoryCodeOrderByDate(accountCode, "", categoryCode);
				for (Task t : list) {
					Timestamp date = t.getDate();
					String jisa = jisa(date);
					t.setMemo(jisa);
				}
				session.setAttribute("tasks", list);
				session.setAttribute("doneTasks", taskRepository.findByAccountCodeAndDoneAndCategoryCodeOrderByDate(accountCode, "1", categoryCode));
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
		LocalDateTime ldt = LocalDateTime.now();
		DateTimeFormatter y = DateTimeFormatter.ofPattern("yyyy");
		DateTimeFormatter m = DateTimeFormatter.ofPattern("MM");
		DateTimeFormatter d = DateTimeFormatter.ofPattern("dd");
		DateTimeFormatter h = DateTimeFormatter.ofPattern("HH");
		String strYear = y.format(ldt);
		String strMonth = m.format(ldt);
		String strDay = d.format(ldt);
		String strHour = h.format(ldt);
		int year = Integer.parseInt(strYear);
		int month = Integer.parseInt(strMonth);
		int day = Integer.parseInt(strDay);
		int hour = Integer.parseInt(strHour);
		int categoryCode = 0;
		String share = "";
		String memo = "";
		mv.addObject("task", task);
		mv.addObject("year", year);
		mv.addObject("month", month);
		mv.addObject("day", day);
		mv.addObject("hour", hour);
		mv.addObject("categoryCode", categoryCode);
		mv.addObject("share", share);
		mv.addObject("memo", memo);
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
			@RequestParam(name="category", defaultValue="") String strCategoryCode,
			@RequestParam(name="share", defaultValue="") String share,
			@RequestParam(name="memo", defaultValue="") String memo,
			ModelAndView mv
			) {
		int categoryCode = 0;
		if (strCategoryCode.length() == 0) {
			categoryCode = 0;
		} else {
			categoryCode = Integer.parseInt(strCategoryCode);
		}
		if (task.length() == 0) {
			int y = Integer.parseInt(year);
			int m = Integer.parseInt(month);
			int d = Integer.parseInt(day);
			int h = 0;
			if (hour.length() != 0) {
				h = Integer.parseInt(hour);
			}
			mv.addObject("task", task);
			mv.addObject("categoryCode", categoryCode);
			mv.addObject("share", share);
			mv.addObject("memo", memo);
			mv.addObject("year", y);
			mv.addObject("month", m);
			mv.addObject("day", d);
			mv.addObject("hour", h);
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
			
			LocalDateTime now = LocalDateTime.now();
			LocalDateTime deadline = date.toLocalDateTime();
			long jisa = ChronoUnit.DAYS.between(now, deadline);
			if(jisa < 0) {
				int y = Integer.parseInt(year);
				int m = Integer.parseInt(month);
				int d = Integer.parseInt(day);
				int h = 0;
				if (hour.length() != 0) {
					h = Integer.parseInt(hour);
				}
				mv.addObject("task", task);
				mv.addObject("categoryCode", categoryCode);
				mv.addObject("share", share);
				mv.addObject("memo", memo);
				mv.addObject("year", y);
				mv.addObject("month", m);
				mv.addObject("day", d);
				mv.addObject("hour", h);
				mv.addObject("message", "過去の日時は締め切りに設定できません");
				mv.setViewName("add");
				return addPage(mv);
			}
			
			Task t = new Task(accountCode, task, date, 
					categoryCode, share, done, memo);
			taskRepository.saveAndFlush(t);
//			session.setAttribute("category", categoryCode);
			SimpleDateFormat s = new SimpleDateFormat("MM/dd HH");
			String a = s.format(date);
			String dead = a + "時";
			addTaskTalk(task, dead);
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
			
			LocalDateTime now = LocalDateTime.now();
			LocalDateTime deadline = date.toLocalDateTime();
			long jisa = ChronoUnit.HOURS.between(now, deadline);
			if(jisa < 0) {
				int y = Integer.parseInt(year);
				int m = Integer.parseInt(month);
				int d = Integer.parseInt(day);
				int h = 0;
				if (hour.length() != 0) {
					h = Integer.parseInt(hour);
				}
				mv.addObject("task", task);
				mv.addObject("categoryCode", categoryCode);
				mv.addObject("share", share);
				mv.addObject("memo", memo);
				mv.addObject("year", y);
				mv.addObject("month", m);
				mv.addObject("day", d);
				mv.addObject("hour", h);
				mv.addObject("message", "過去の日時は締め切りに設定できません");
				mv.setViewName("add");
				return addPage(mv);
			}
			
			Task t = new Task(accountCode, task, date, 
					categoryCode, share, done, memo);
			
			taskRepository.saveAndFlush(t);
//			session.setAttribute("category", categoryCode);
			SimpleDateFormat s = new SimpleDateFormat("MM/dd HH");
			String a = s.format(date);
			String dead = a + "時";
			addTaskTalk(task, dead);
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
			@RequestParam(name="category", defaultValue="") String strCategoryCode,
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
		int categoryCode = 0;
		if (strCategoryCode.length() == 0) {
			categoryCode = 0;
		} else {
			categoryCode = Integer.parseInt(strCategoryCode);
		}
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
	
	@RequestMapping(value="/category/{code}", method=RequestMethod.GET)
	public ModelAndView category(
			@PathVariable("code") int categoryCode,
			ModelAndView mv
			) {
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
	}
	
	@RequestMapping(value="/sort", method=RequestMethod.POST)
	public ModelAndView sort(
			@RequestParam("sort") String sort,
			ModelAndView mv
			) {
		session.setAttribute("sort", sort);
		return topPage(mv);
	}
	
	@RequestMapping(value="/edit/{code}", method=RequestMethod.GET)
	public ModelAndView editPage(
			@PathVariable("code") int code,
			ModelAndView mv
			) {
		Optional<Task> task = taskRepository.findById(code);
		if (task.isPresent()) {
			Task editTask = task.get();
			session.setAttribute("editCode", editTask.getCode());
			mv.addObject("task", editTask.getTask());
			mv.addObject("categoryCode", editTask.getCategoryCode());
			mv.addObject("share", editTask.getShare());
			mv.addObject("memo", editTask.getMemo());
			session.setAttribute("editDone", editTask.getDone());
			Timestamp date = editTask.getDate();
			int[] dates = timestampToInt(date);
			mv.addObject("year", dates[0]);
			mv.addObject("month", dates[1]);
			mv.addObject("day", dates[2]);
			mv.addObject("hour", dates[3]);
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
			int y = Integer.parseInt(year);
			int m = Integer.parseInt(month);
			int d = Integer.parseInt(day);
			int h = 0;
			if (hour.length() != 0) {
				h = Integer.parseInt(hour);
			}
			mv.addObject("message", "タスクを入力してください");
			mv.addObject("task", task);
			mv.addObject("categoryCode", categoryCode);
			mv.addObject("share", share);
			mv.addObject("memo", memo);
			mv.addObject("year", y);
			mv.addObject("month", m);
			mv.addObject("day", d);
			mv.addObject("hour", h);
			mv.setViewName("edit");
			return mv;
		}
		int accountCode = accountCode();
		
		StringBuilder sb = new StringBuilder();
		sb.append(year);
		sb.append("-");
		sb.append(month);
		sb.append("-");
		sb.append(day);
		
		String done = (String)session.getAttribute("editDone");
		
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
//			session.setAttribute("category", categoryCode);
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
//			session.setAttribute("category", categoryCode);
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
				doneTaskTalk(doneTask.getTask());
			} else {
				done = "";
				randomTalk();
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
