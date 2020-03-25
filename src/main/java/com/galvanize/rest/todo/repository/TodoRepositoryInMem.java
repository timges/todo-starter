package com.galvanize.rest.todo.repository;

import com.galvanize.rest.todo.entities.Todo;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

	public Todo completeTodo(long id) {
		Todo todoToUpdate = getTodoById(id);
		todoToUpdate.setCompleted(true);
		
		return todoToUpdate;
	}

	public Todo updateText(long id, String text) {
		Todo todoToUpdate = getTodoById(id);
		todoToUpdate.setText(text);
		
		return todoToUpdate;
	}

	private Todo getTodoById(long id) {
		Todo itemSearched = todos.stream()
			.filter(todo -> todo.getId() == id)
			.collect(Collectors.toList())
			.get(0);

		return(this.todos.get(this.todos.indexOf(itemSearched)));
	}

	public Todo categorize(Long id, String category) {
		Todo todoToCategorize = getTodoById(id);
		todoToCategorize.setCategory(category);
		
		return todoToCategorize;
	}
}
