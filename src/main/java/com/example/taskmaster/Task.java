package com.example.taskmaster;





import org.springframework.data.annotation.Id;

public class Task {
	@Id
	private String id;
	
	private String description;
	
	
	public Task() {
		
	}
	
	public Task(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
