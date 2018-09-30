package com.example.taskmaster;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
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
public class UserTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;

	@Before
	public void deleteAllBeforeTests() throws Exception {
		userRepository.deleteAll();
	}

	@Test
	public void shouldReturnRepositoryIndex() throws Exception {

		mockMvc.perform(get("/")).andDo(print()).andExpect(status().isOk())
				.andExpect(jsonPath("$._links.users").exists());
	}

	@Test
	public void shouldCreateEntity() throws Exception {

		mockMvc.perform(post("/users").content("{\"firstName\": \"Samuel\", \"lastName\": \"Beckett\"}"))
				.andExpect(status().isCreated()).andExpect(header().string("Location", containsString("users/")));
	}

	@Test
	public void shouldCreateMultipleEntities() throws Exception {
		// We want to make sure there is no problem with autogeneration of ids

		// FIXME: Can two people have the same firstname + lastname? if not then add
		// another test to that effect and modify this one
		mockMvc.perform(post("/users").content("{\"firstName\": \"Samuel\", \"lastName\": \"Beckett\"}"))
				.andExpect(status().isCreated()).andExpect(header().string("Location", containsString("users/")));
		mockMvc.perform(post("/users").content("{\"firstName\": \"Samuel\", \"lastName\": \"Beckett\"}"))
				.andExpect(status().isCreated()).andExpect(header().string("Location", containsString("users/")));
	}

	@Test
	public void shouldRetrieveEntity() throws Exception {

		MvcResult mvcResult = mockMvc
				.perform(post("/users").content("{\"firstName\": \"Samuel\", \"lastName\": \"Beckett\"}"))
				.andExpect(status().isCreated()).andExpect(status().isCreated()).andReturn();

		String location = mvcResult.getResponse().getHeader("Location");
		mockMvc.perform(get(location)).andExpect(status().isOk()).andExpect(jsonPath("$.firstName").value("Samuel"))
				.andExpect(jsonPath("$.lastName").value("Beckett")).andExpect(jsonPath("$.tasks", hasSize(0)));
	}

	@Test
	public void shouldQueryEntity() throws Exception {
		MvcResult mvcResult = mockMvc
				.perform(post("/users").content("{\"firstName\": \"Samuel\", \"lastName\": \"Beckett\"}"))
				.andExpect(status().isCreated()).andExpect(status().isCreated()).andReturn();

		mockMvc.perform(get("/users/search/findByFirstNameAndLastName?firstName={firstName}&lastName={lastName}",
				"Samuel", "Beckett")).andExpect(status().isOk())
				.andExpect(jsonPath("$._embedded.users[0].firstName").value("Samuel"))
				.andExpect(jsonPath("$._embedded.users[0].lastName").value("Beckett"))
				.andExpect(jsonPath("$._embedded.users[0].tasks", hasSize(0)));
	}

	@Test
	public void shouldUpdateEntity() throws Exception {

		MvcResult mvcResult = mockMvc
				.perform(post("/users").content("{\"firstName\": \"Samuel\", \"lastName\": \"Beckett\"}"))
				.andExpect(status().isCreated()).andExpect(status().isCreated()).andReturn();

		String location = mvcResult.getResponse().getHeader("Location");

		mockMvc.perform(patch(location).content("{\"firstName\": \"Andrew\", \"lastName\": \"Belis\"}"))
				.andExpect(status().isNoContent());

		mockMvc.perform(get(location)).andExpect(status().isOk()).andExpect(jsonPath("$.firstName").value("Andrew"))
				.andExpect(jsonPath("$.lastName").value("Belis")).andExpect(jsonPath("$.tasks", hasSize(0)));
	}

	@Test
	public void shouldPartiallyUpdateEntity() throws Exception {

		MvcResult mvcResult = mockMvc
				.perform(post("/users").content("{\"firstName\": \"Samuel\", \"lastName\": \"Beckett\"}"))
				.andExpect(status().isCreated()).andExpect(status().isCreated()).andReturn();

		String location = mvcResult.getResponse().getHeader("Location");

		mockMvc.perform(patch(location).content("{\"firstName\": \"Samuel Barclay\"}"))
				.andExpect(status().isNoContent());

		mockMvc.perform(get(location)).andExpect(status().isOk())
				.andExpect(jsonPath("$.firstName").value("Samuel Barclay"))
				.andExpect(jsonPath("$.lastName").value("Beckett")).andExpect(jsonPath("$.tasks", hasSize(0)));
	}

	@Test
	public void shouldDeleteEntity() throws Exception {

		MvcResult mvcResult = mockMvc
				.perform(post("/users").content("{\"firstName\": \"Samuel\", \"lastName\": \"Beckett\"}"))
				.andExpect(status().isCreated()).andExpect(status().isCreated()).andReturn();

		String location = mvcResult.getResponse().getHeader("Location");
		mockMvc.perform(delete(location)).andExpect(status().isNoContent());

		mockMvc.perform(get(location)).andExpect(status().isNotFound());
	}

}
