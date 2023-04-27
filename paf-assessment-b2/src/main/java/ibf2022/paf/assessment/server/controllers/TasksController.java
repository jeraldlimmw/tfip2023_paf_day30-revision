package ibf2022.paf.assessment.server.controllers;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.ModelAndView;

import ibf2022.paf.assessment.server.models.Task;
import ibf2022.paf.assessment.server.models.User;
import ibf2022.paf.assessment.server.services.TodoService;
import jakarta.servlet.http.HttpSession;

// TODO: Task 4, Task 8
@Controller
public class TasksController {

    @Autowired
    private TodoService svc;

    @PostMapping(path="/task", consumes=MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ModelAndView saveTasks(Model m, @RequestBody MultiValueMap<String, String> input) {

        String username = input.getFirst("username");
        System.out.println(">>>> USERNAME: " + username);
        // User user = (User) ss.getAttribute("user");

        // if(null == user) {
        //     user = new User();
        //     user.setUsername(username);
        // }
        User user = new User();
        user.setUsername(username);
        List<Task> taskList = new ArrayList<>();
        int numOfTasks = (input.keySet().size() - 1)/3;
        
        for (int i = 0; i < numOfTasks; i++) {
            Task t = new Task();
            t.setDescription(input.getFirst("description-" + i));
            t.setPriority(Integer.parseInt(input.getFirst("priority-" + i)));
            DateTime d = new DateTime(DateTimeFormat
                        .forPattern("yyyy-MM-dd")
                        .parseDateTime(input.getFirst("dueDate-" + i)));
            t.setDate(d);
            System.out.println(">>>>>>>> TASK: " + t);
            taskList.add(t);
        }
        
        user.setTasks(taskList);
        System.out.println(">>>> USER" + user);
    //     ss.setAttribute("user", user);

    //     return "redirect:/task/save";
    // }

    // @GetMapping(path="/task/save")
    // public ModelAndView upsertTask(Model m, HttpSession ss) {
        
        System.out.println(">>>> ATTEMPT UPSERT");
        // User user = (User) ss.getAttribute("user");
        int rowsAdded = 0;
        
        ModelAndView mv = new ModelAndView();
        try {
            for(Task t : user.getTasks()) {
                int add = svc.upsertTask(t, user.getUsername());
                rowsAdded += add;
            }
            System.out.println(">>>> " + rowsAdded + " tasks added");
        } catch (Exception e) {
            mv.setStatus(HttpStatusCode.valueOf(500));
            mv.setViewName("error");
            return mv;
        }
        
        m.addAttribute("username", user.getUsername());
        m.addAttribute("taskCount", user.getTasks().size());
        System.out.println(">>>> MODEL ATTRIBUTES ADDED");
        // ss.invalidate();
        // System.out.println(">>>> SESSION INVALIDATED");
        mv.setStatus(HttpStatusCode.valueOf(200));
        mv.setViewName("result");
        return mv;
    }
}
