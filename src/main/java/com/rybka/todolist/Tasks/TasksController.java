package com.rybka.todolist.Tasks;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class TasksController {

    @GetMapping("/taskcreation")
    public String showTaskCreation() {
        return "taskcreation.html";
    }


}

