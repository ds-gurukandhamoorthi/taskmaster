package com.example.taskmaster;





import org.springframework.data.annotation.Id;

public class Task {
	@Id
	private String id;
	
	private String description;
	
	private User user;
	
	
	public Task() {
		
	}
	
	public Task(String description) {
		this.description = description;
	}
	
	public String getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return "Task [id=" + id + ", description=" + description + ", user=" + user.getId() + "]";
	}

}
