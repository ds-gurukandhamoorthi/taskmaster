package com.example.taskmaster;

import java.util.Collection;
import java.util.LinkedHashSet;

import org.springframework.data.annotation.Id;

public class User {
	@Id
	private String id;

	private String firstName;
	private String lastName;

	// If order of tasks has no importance, then HashSet can be used profitably.
	// But we would be surprised if our todo-list's order were to change haphazardly
	// every time we consult it.
	private Collection<Task> tasks = new LinkedHashSet<>();

	public User() {

	}
	
	public User(String firstName, String lastName) {
		this(firstName, lastName, new LinkedHashSet<Task>());
	}

	
	public User(String firstName, String lastName, Collection<Task> tasks) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.tasks = tasks;
	}


	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Collection<Task> getTasks() {
		return tasks;
	}

	public void setTasks(Collection<Task> tasks) {
		this.tasks = tasks;
	}

}
