package com.example.taskmaster;

import java.util.Collection;
import java.util.Collections;
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

	public String getId() {
		return id;
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
		return Collections.unmodifiableCollection(tasks); // We preclude the possibility of anybody getting the collection and adding a value through the pointer
	}

	public void setTasks(Collection<Task> tasks) {
		this.tasks = tasks;
	}

	public void assign(Task task) { //FIXME: If this loop poses any performance problems, opt for an impure getter for tasks.
		Collection<Task> tempTasks = new LinkedHashSet<>();
		for(Task t: tasks) {
			tempTasks.add(t);
		}
		//FIXME: We choose a strategy of reassigning. Such peremptoriness may be frowned upon.
		User currentlyAssignedPerson = task.getUser();
		if (currentlyAssignedPerson != null) {
			currentlyAssignedPerson.relieveFrom(task);
		}
		task.setUser(this);
		tempTasks.add(task);
		this.setTasks(tempTasks);
	}
	
	public void relieveFrom(Task task) { //FIXME: If this loop poses any performance problems, opt for an impure getter for tasks.
		Collection<Task> tempTasks = new LinkedHashSet<>();
		for(Task t: tasks) {
			if(!t.equals(task)) {
				tempTasks.add(t);
			}
		}
		this.setTasks(tempTasks);
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", tasks=" + tasks + "]";
	}
	

}
