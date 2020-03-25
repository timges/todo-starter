package com.galvanize.rest.todo;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.galvanize.rest.todo.entities.Todo;
import com.galvanize.rest.todo.repository.TodoRepositoryInMem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest
final class TodoListControllerTest {
	@Autowired
	TodoRepositoryInMem todoRepository;

	@Autowired
	MockMvc mvc;

	@Autowired
	ObjectMapper objectMapper;

	@BeforeEach
	void beforeEach() {
		todoRepository.deleteAll();
	}

	@AfterEach
	void afterEach() {
		todoRepository.deleteAll();
	}

	@Test
	void returnEmptyTodoListByDefault() throws Exception {
		mvc.perform(get("/api/todo")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().string("[]"));

	}

	@Test
	void returnOneTodoWhenOneInList() throws Exception {
		final Todo todo = new Todo("finish api");
		final List<Todo> expected = Collections.singletonList(todoRepository.save(todo));

		String json = mvc.perform(get("/api/todo")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(expected)))
				// Get response as string to parse below for ObjectMapper Example
				.andReturn().getResponse().getContentAsString();

		// Example using ObjectMapper
		List<Todo> actual = objectMapper.readValue(json, new TypeReference<List<Todo>>() {});
		assertThat(actual.size(), equalTo(1));
    	assertThat(actual, is(expected));
	}

	@Test
	void returnManyTodoWhenManyInList() throws Exception {
		final Todo todo1 = new Todo("finish api1");
		final Todo todo2 = new Todo("finish api2"); 
		final List<Todo> expected = new ArrayList<>();
		Collections.addAll(expected, todoRepository.save(todo1), todoRepository.save(todo2));
		
		String json = mvc.perform(get("/api/todo")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(expected)))
				// Get response as string to parse below for ObjectMapper Example
				.andReturn().getResponse().getContentAsString();

		// Example using ObjectMapper
		List<Todo> actual = objectMapper.readValue(json, new TypeReference<List<Todo>>() {});
		assertThat(actual.size(), equalTo(2));
    	assertThat(actual, is(expected));
	}

	@Test
	void canCreateNewTodo() throws Exception {
		final String payload = "{\"text\": \"add a todo\"}";
		mvc.perform(post("/api/todo")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(payload))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.text").value("add a todo"))
				.andExpect(jsonPath("$.id").isNumber());
	}

	@Test
	void updateCompleteStatus() throws Exception {
		final Todo unfinishedTodo = new Todo("unfinished todo");
		todoRepository.save(unfinishedTodo);

		mvc.perform(post("/api/todo/completed/" + unfinishedTodo.getId()))					
						.andExpect(status().isCreated())
						.andExpect(jsonPath("$.text").value("unfinished todo"))
						.andExpect(jsonPath("$.id").isNumber())
						.andExpect(jsonPath("$.completed").value(true));
	}

	@Test
	void updateTodoText() throws Exception {
		final Todo initialTodo = todoRepository.save(new Todo("initial text"));
		mvc.perform(post("/api/todo/update-text/" + initialTodo.getId())
						.param("text","new text"))					
						.andExpect(status().isCreated())
						.andExpect(jsonPath("$.text").value("new text"))
						.andExpect(jsonPath("$.id").isNumber());
	}

	@Disabled
	@Test
	void updateTodoTextFailsWithWrongID() throws Exception {
		final Todo initialTodo = todoRepository.save(new Todo("initial text"));
		mvc.perform(post("/api/todo/update-text/" + initialTodo.getId())
						.param("text","new text"))					
						.andExpect(status().isCreated())
						.andExpect(jsonPath("$.text").value("new text"))
						.andExpect(jsonPath("$.id").isNumber());
	}

	@Test 
	void categorize() throws Exception {
		final Todo uncategorizedTodo = todoRepository.save(new Todo("text"));
		mvc.perform(post("/api/todo/categorize/" + uncategorizedTodo.getId())
						.param("category","my category"))					
						.andExpect(status().isCreated())
						.andExpect(jsonPath("$.category").value("my category"))
						.andExpect(jsonPath("$.id").isNumber());
	}

	@Test 
	void returnTodoByCategory() throws Exception {
		final String category = "important";
		final Todo todo1 = new Todo("type important");
		final Todo todo2 = new Todo("type important"); 
		final Todo todo3 = new Todo("uncategorized "); 
		
		todo1.setCategory(category);
		todo2.setCategory(category);

		todoRepository.save(todo3);

		final List<Todo> expected = new ArrayList<>();
		Collections.addAll(expected, todoRepository.save(todo1), todoRepository.save(todo2));

		String json = mvc.perform(get("/api/todo/" + category)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(expected)))
				// Get response as string to parse below for ObjectMapper Example
				.andReturn().getResponse().getContentAsString();

		// Example using ObjectMapper
		List<Todo> actual = objectMapper.readValue(json, new TypeReference<List<Todo>>() {});
		assertThat(actual.size(), equalTo(2));
    	assertThat(actual, is(expected));
	}

	@Test
	void shouldIndicateCompletedTask() throws Exception {		
		final Todo todo1 = new Todo("type important");
		final Todo todo2 = new Todo("type important"); 
		final Todo todo3 = new Todo("uncategorized "); 

		todo1.setCompleted(true);
		todo2.setCompleted(true);

		todoRepository.save(todo3);
		
		List<Todo> expected = new ArrayList<>();
		Collections.addAll(expected, todoRepository.save(todo1) , todoRepository.save(todo2));
		
		String json = mvc.perform(get("/api/todo/completed")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(expected)))
				// Get response as string to parse below for ObjectMapper Example
				.andReturn().getResponse().getContentAsString();

		// Example using ObjectMapper
		List<Todo> actual = objectMapper.readValue(json, new TypeReference<List<Todo>>() {});
		assertThat(actual.size(), equalTo(2));
    	assertThat(actual, is(expected));
	}
}



































