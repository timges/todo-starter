package com.galvanize.rest.todo.controllers;

import com.galvanize.rest.todo.entities.Todo;
import com.galvanize.rest.todo.repository.TodoRepositoryInMem;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/todo")
public final class TodoListController {

	@Autowired
	TodoRepositoryInMem todoRepository;

	@GetMapping
	List<Todo> getAllTodos() {
		return todoRepository.findAll();
	}


}
