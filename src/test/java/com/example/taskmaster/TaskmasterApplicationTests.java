package com.example.taskmaster;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.example.taskmaster.data.TaskRepository;
import com.example.taskmaster.data.UserRepository;
import com.example.taskmaster.domain.Task;
import com.example.taskmaster.domain.User;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TaskmasterApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private TaskRepository taskRepository;

	@Autowired
	private UserRepository userRepository;

	User samuel, charles;
	Task waitFor, returnHome, consultOracular;

	@Before
	public void deleteAllBeforeTests() throws Exception {
		taskRepository.deleteAll();
		userRepository.deleteAll();

		samuel = userRepository.save(new User("Samuel", "Beckett"));
		charles = userRepository.save(new User("Charles", "Dickens"));

		waitFor = taskRepository.save(new Task("Wait for Godot"));
		returnHome = taskRepository.save(new Task("Return home"));
		consultOracular = taskRepository.save(new Task("Consult the oracular oyster and tell time"));

	}

	@Test
	public void shouldReturnRepositoryIndex() throws Exception {

		mockMvc.perform(get("/")).andDo(print()).andExpect(status().isOk())
				.andExpect(jsonPath("$._links.tasks").exists()).andExpect(jsonPath("$._links.users").exists());
	}


	@Test
	public void shouldNotAssignDefaultTasksToAnUser() throws Exception {
		User charles = userRepository.save(new User("Charles", "Dickens"));
		assertTrue(charles.getTasks().isEmpty());
	}

	@Test
	public void shouldAssignATaskToAnUser() throws Exception {
		samuel.assign(waitFor);
		assertTrue(samuel.getTasks().size() == 1);
	}
	

	@Test
	public void shouldAssignATaskToAUserREST() throws Exception {
		String userId = samuel.getId();
		
		MvcResult mvcResult = mockMvc.perform(post("/tasks").content("{\"description\": \"Wait For Godot\"}"))
				.andExpect(status().isCreated()).andReturn();

		String location = mvcResult.getResponse().getHeader("Location");

		mockMvc.perform(
				patch(location).content("{\"user\": {\"id\" : \"+userId+\"}}")).andExpect(
						status().isNoContent());

		mockMvc.perform(get(location)).andExpect(status().isOk()).andExpect(
				jsonPath("$.description").value("Wait For Godot"));
		System.out.println(samuel);
		
		assertTrue(samuel.getTasks().size() == 1);
	}
	
	@Test
	public void shouldQueryTask() throws Exception {

		samuel.assign(waitFor);
		mockMvc.perform(get("/tasks/search/findByUserId?userId={UserId}", samuel.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$._embedded.tasks[0].description").value("Wait For Godot"));
	}
	
	@Test
	public void shouldContainTheAssignedTask() throws Exception {
		samuel.assign(waitFor);
		assertTrue(samuel.getTasks().contains(waitFor));
	}
	

	@Test
	public void shouldAssignMultipleTasksToAUser() throws Exception {
		samuel.assign(waitFor);
		samuel.assign(returnHome);
		assertTrue(samuel.getTasks().size() == 2);
	}
	

	
	@Test
	public void shouldNotContainTheTaskOnceTheUserIsRelievedFromIt() throws Exception {
		samuel.assign(waitFor);
		samuel.relieveFrom(waitFor);
		assertFalse(samuel.getTasks().contains(waitFor));
	}
	
	@Test
	public void shouldAvoidAssigningSameTaskToTwoUsers() throws Exception {
		samuel.assign(returnHome);
		charles.assign(returnHome);
		
		assertTrue(samuel.getTasks().contains(returnHome) ^ charles.getTasks().contains(returnHome));
	}
	
	@Test
	public void shouldAssignTaskToTheLatest() throws Exception {
		samuel.assign(returnHome);
		charles.assign(returnHome);
		
		assertTrue(charles.getTasks().contains(returnHome));
	}
	


}
