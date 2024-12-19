package com.example.taskapp.controllers;

import com.example.taskapp.repository.TaskRepository;
import com.example.taskapp.models.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    @GetMapping
    @ResponseBody
    public Page<Task> listTasks(@RequestParam(defaultValue = "") String search,
                                @RequestParam(defaultValue = "") String status,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Task> tasks;

        if (status.isEmpty()) {
            tasks = taskRepository.findByTitleContaining(search, pageable);
        } else {
            tasks = taskRepository.findByTitleContainingAndStatus(search, status, pageable);
        }

        return tasks;
    }

    @GetMapping("/add")
    public String showAddTaskPage(Model model) {
        model.addAttribute("task", new Task());
        model.addAttribute("statuses", List.of("PENDING", "IN_PROGRESS", "COMPLETED"));
        return "add-task";
    }

    @PostMapping("/add")
    public String addTask(@ModelAttribute Task task) {
        if (task.getStatus() == null || task.getStatus().isEmpty()) {
            task.setStatus("PENDING");
        }

        taskRepository.save(task);
        return "redirect:/tasks";
    }

    @GetMapping("/edit-task/{id}")
    public String showEditTaskPage(@PathVariable Long id, Model model) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid task ID: " + id));

        model.addAttribute("task", task);
        model.addAttribute("statuses", List.of("PENDING", "IN_PROGRESS", "COMPLETED"));
        return "edit-task";
    }

    @PostMapping("/edit-task/{id}")
    public String updateTask(@PathVariable Long id, @ModelAttribute Task task) {
        task.setId(id);

        if (task.getStatus() == null || task.getStatus().isEmpty()) {
            task.setStatus("PENDING");
        }

        taskRepository.save(task);
        return "redirect:/tasks";
    }

    @DeleteMapping("/delete-task/{id}")
    public String deleteTask(@PathVariable Long id) {
        taskRepository.deleteById(id);
        return "redirect:/tasks";
    }
}