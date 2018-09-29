package com.example.taskmaster;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TaskTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private TaskRepository taskRepository;

	@Before
	public void deleteAllBeforeTests() throws Exception {
		taskRepository.deleteAll();
	}

	@Test
	public void shouldReturnRepositoryIndex() throws Exception {

		mockMvc.perform(get("/")).andDo(print()).andExpect(status().isOk())
				.andExpect(jsonPath("$._links.tasks").exists());
	}

	@Test
	public void shouldCreateEntity() throws Exception {

		mockMvc.perform(post("/tasks").content("{\"description\": \"Wait For Godot\"}")).andExpect(status().isCreated())
				.andExpect(header().string("Location", containsString("tasks/")));
	}

	@Test
	public void shouldCreateMultipleEntities() throws Exception {
		// We want to make sure there is no problem with autogeneration of ids
		mockMvc.perform(post("/tasks").content("{\"description\": \"Wait For Godot\"}")).andExpect(status().isCreated())
				.andExpect(header().string("Location", containsString("tasks/")));
		mockMvc.perform(post("/tasks").content("{\"description\": \"Return Home\"}")).andExpect(status().isCreated())
				.andExpect(header().string("Location", containsString("tasks/")));
	}

	@Test
	public void shouldRetrieveEntity() throws Exception {

		MvcResult mvcResult = mockMvc.perform(post("/tasks").content("{\"description\": \"Wait For Godot\"}"))
				.andExpect(status().isCreated()).andReturn();

		String location = mvcResult.getResponse().getHeader("Location");
		mockMvc.perform(get(location)).andExpect(status().isOk())
				.andExpect(jsonPath("$.description").value("Wait For Godot"));
	}

	@Test
	public void shouldQueryEntity() throws Exception {

		mockMvc.perform(post("/tasks").content("{\"description\": \"Wait For Godot\"}"))
				.andExpect(status().isCreated());

		mockMvc.perform(get("/tasks/search/findByDescriptionLike?desc={description}", "Godot"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$._embedded.tasks[0].description").value("Wait For Godot"));
	}
	
	@Test
	public void shouldUpdateEntity() throws Exception {

		MvcResult mvcResult = mockMvc.perform(post("/tasks").content("{\"description\": \"Wait For Godot\"}"))
				.andExpect(status().isCreated()).andReturn();

		String location = mvcResult.getResponse().getHeader("Location");

		mockMvc.perform(
				patch(location).content("{\"description\": \"Return Home\"}")).andExpect(
						status().isNoContent());

		mockMvc.perform(get(location)).andExpect(status().isOk()).andExpect(
				jsonPath("$.description").value("Return Home"));
	}
	
	@Test
	public void shouldDeleteEntity() throws Exception {

		MvcResult mvcResult = mockMvc.perform(post("/tasks").content("{\"description\": \"Wait For Godot\"}"))
				.andExpect(status().isCreated()).andReturn();

		String location = mvcResult.getResponse().getHeader("Location");
		mockMvc.perform(delete(location)).andExpect(status().isNoContent());

		mockMvc.perform(get(location)).andExpect(status().isNotFound());
	}

}
