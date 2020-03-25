package com.galvanize.rest.todo.entities;

import java.util.Objects;

public final class Todo {
	private String text;
	private Long id;

	public Todo (String text, Long id) {
		this.text = text;
		this.id = id;
	}
	public Todo(String text) {
		this.text = text;
	}

	public Todo() {
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (!(o instanceof Todo)) return false;
		Todo other = (Todo) o;
		if (!this.getId().equals(other.getId())) return false;
		return this.getText().equals(other.getText());
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, text);
	}
}
