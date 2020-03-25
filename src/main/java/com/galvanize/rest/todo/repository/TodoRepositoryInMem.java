package com.galvanize.rest.todo.repository;

import com.galvanize.rest.todo.entities.Todo;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class TodoRepositoryInMem  {
	private List<Todo> todos = new ArrayList<>();
	private Long nextId = 0L;

	public List<Todo> findAll() {
		return todos;
	}

	public Todo save(Todo todo) {
		if (todo.getId() == null) {
			todo.setId(nextId++);
			todos.add(todo);
		}
		return todo;
	}

	public void deleteAll() {
		todos.clear();
	}
}
