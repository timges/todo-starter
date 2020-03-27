package com.galvanize.rest.todo.controller;

import com.galvanize.rest.todo.entities.Todo;
import com.galvanize.rest.todo.repository.TodoRepositoryInMem;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
	
	@GetMapping("/{category}")
	List<Todo> getAllTodosByCategory(@PathVariable String category) {
		return (
			todoRepository.findAll().stream()
			.filter(todo -> todo.getCategory().equals(category))
			.collect(Collectors.toList())
		);
	}

	@GetMapping("/completed")
	List<Todo> getAllCompletedTodos() {
		return (
			todoRepository.findAll().stream()
			.filter(todo -> todo.isCompleted())
			.collect(Collectors.toList())
		);
	}

	@GetMapping("/uncompleted")
	List<Todo> getAllUnompletedTodos() {
		return (
			todoRepository.findAll().stream()
			.filter(todo -> !todo.isCompleted())
			.collect(Collectors.toList())
		);
	}	

	@PostMapping
	ResponseEntity<Todo> addOneTodo(@RequestBody Todo todo) {	
		Todo saved = todoRepository.save(todo);
		return new ResponseEntity<Todo>(saved, HttpStatus.CREATED);  
	}

	@PostMapping("/completed/{id}")
	ResponseEntity<Todo> completeTodo(@PathVariable Long id) {
		return new ResponseEntity<Todo>(todoRepository.completeTodo(id), HttpStatus.CREATED);
	}

	@PostMapping("/update-text/{id}")
	ResponseEntity<Todo> updateText(@PathVariable Long id, @RequestParam String text) {
		return new ResponseEntity<Todo>(todoRepository.updateText(id, text), HttpStatus.CREATED);
	}

	@PostMapping("/add-date/{id}")
	ResponseEntity<Todo> updateDate(@PathVariable Long id, @RequestParam String dueDate) {
		return new ResponseEntity<Todo>(todoRepository.updateDate(id, LocalDate.parse(dueDate)), HttpStatus.ACCEPTED);
	}

	@PostMapping("/categorize/{id}")
	ResponseEntity<Todo> categorizeTodo(@PathVariable Long id, @RequestParam String category) {
		return new ResponseEntity<Todo>(todoRepository.categorize(id, category), HttpStatus.CREATED);
	}


	

}
