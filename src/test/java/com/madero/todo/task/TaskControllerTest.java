package com.madero.todo.task;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.*;
 
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.ObjectContent;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import com.madero.todo.enums.TaskAction;
import com.madero.todo.enums.TaskStatus;
 
@AutoConfigureJsonTesters
@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private TaskServiceImpl taskService;
	
	@Autowired
	private JacksonTester<TaskDTO> jacksonTester;
	
	private final static Long TASK_ID = 1L;

	private final static String TASK_NAME = "Install Git";

	private final static String TASK_DESCRIPTION = "Install Git core to control source code repositories.";

	private final static int TIME_INTERVAL = 1000 * 60 * 60 * 2;
	
	@Test
	public void saveTaskTest() throws Exception{
		Date now = new Date();
		//Given
		Task task = new Task();
		task.setId(TASK_ID);
		task.setName(TASK_NAME);
		task.setDescription(TASK_DESCRIPTION);
		task.setExpirationDate(new Date(now.getTime() + TIME_INTERVAL));
		task.setCrtdOn(now);
		
		when(this.taskService.saveTask(ArgumentMatchers.any(Task.class))).thenReturn(task);
		
		//When
		TaskDTO taskDTO = new TaskDTO();
		taskDTO.setName(TASK_NAME);
		taskDTO.setDescription(TASK_DESCRIPTION);
		taskDTO.setExpirationDate(new Date(now.getTime() + TIME_INTERVAL));
		
		MockHttpServletResponse response = 
				this.mockMvc.perform(
						post("/api/v1/tasks")
						.contentType(MediaType.APPLICATION_JSON)
						.content(this.jacksonTester.write(taskDTO).getJson()))
				.andDo(print()).andReturn().getResponse();
		
		//Then
		assertEquals(response.getStatus(), HttpStatus.CREATED.value());
		
		ObjectContent<TaskDTO> body = jacksonTester.parse(response.getContentAsString());
		body.assertThat().isNotNull();
		body.assertThat().hasNoNullFieldsOrProperties();
		
		verify(this.taskService).saveTask(ArgumentMatchers.any(Task.class));
	}
	
	@Test
	public void saveTaskArgumentNotValidTest() throws Exception{
		//Given		
		when(this.taskService.saveTask(ArgumentMatchers.any(Task.class))).thenReturn(new Task());
		
		//When
		TaskDTO taskDTO = new TaskDTO();
		taskDTO.setName(TASK_NAME);
		taskDTO.setDescription(TASK_DESCRIPTION);
		
		MockHttpServletResponse response = 
				this.mockMvc.perform(
						post("/api/v1/tasks")
						.contentType(MediaType.APPLICATION_JSON)
						.content(this.jacksonTester.write(taskDTO).getJson()))
				.andDo(print()).andReturn().getResponse();
		
		//Then
		assertEquals(response.getStatus(), HttpStatus.BAD_REQUEST.value());
		verify(this.taskService, times(0)).saveTask(ArgumentMatchers.any(Task.class));
	}
	
	@Test
	public void finalizeTaskTest() throws Exception{
		Date now = new Date();
		//Given
		Task task = new Task();
		task.setId(TASK_ID);
		task.setName(TASK_NAME);
		task.setDescription(TASK_DESCRIPTION);
		task.setExpirationDate(new Date(now.getTime() + TIME_INTERVAL));
		task.setEndDate(now);
		task.setCrtdOn(now);
		
		when(this.taskService.finalizeTask(ArgumentMatchers.any(Long.class))).thenReturn(task);
		
		//When
		MockHttpServletResponse response = 
				this.mockMvc.perform(
						post("/api/v1/tasks/{taskId}", TASK_ID.toString()).queryParam("action", TaskAction.finalize.toString())
						.contentType(MediaType.APPLICATION_JSON))
				.andDo(print()).andReturn().getResponse();
		
		//Then
		assertEquals(response.getStatus(), HttpStatus.OK.value());
		verify(this.taskService).finalizeTask(TASK_ID);
	}
	
	@Test
	public void findAllTaskTest() throws Exception {
		Date now = new Date();
		List<Task> tasks =  new ArrayList<>();
		//Given		
		Task task1 = new Task();
		task1.setId(TASK_ID);
		task1.setName(TASK_NAME);
		task1.setDescription(TASK_DESCRIPTION);
		task1.setExpirationDate(new Date(now.getTime() - TIME_INTERVAL));
		task1.setCrtdOn(now);
		tasks.add(task1);

		when(this.taskService.findAllByStatus((ArgumentMatchers.any(TaskStatus.class)))).thenReturn(tasks);
		
		//When
		MockHttpServletResponse response = this.mockMvc.perform(
				get("/api/v1/tasks")
						.queryParam("status", TaskStatus.all.toString())
						.contentType(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();		
		
		//Then
		assertEquals(response.getStatus(), HttpStatus.OK.value());
		verify(this.taskService).findAllByStatus(TaskStatus.all);
	}
	
	@Test
	public void findAllOnTimeTaskTest() throws Exception {
		Date now = new Date();
		List<Task> tasks =  new ArrayList<>();
		//Given		
		Task task1 = new Task();
		task1.setId(TASK_ID);
		task1.setName(TASK_NAME);
		task1.setDescription(TASK_DESCRIPTION);
		task1.setExpirationDate(new Date(now.getTime() + TIME_INTERVAL));
		task1.setCrtdOn(now);
		tasks.add(task1);

		when(this.taskService.findAllByStatus((ArgumentMatchers.any(TaskStatus.class)))).thenReturn(tasks);
		
		//When
		MockHttpServletResponse response = this.mockMvc.perform(
				get("/api/v1/tasks")
						.queryParam("status", TaskStatus.onTime.toString())
						.contentType(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();		
		
		//Then
		assertEquals(response.getStatus(), HttpStatus.OK.value());
		verify(this.taskService).findAllByStatus(TaskStatus.onTime);
	}
	
	@Test
	public void findAllLateTaskTest() throws Exception {
		Date now = new Date();
		List<Task> tasks =  new ArrayList<>();
		//Given		
		Task task1 = new Task();
		task1.setId(TASK_ID);
		task1.setName(TASK_NAME);
		task1.setDescription(TASK_DESCRIPTION);
		task1.setExpirationDate(new Date(now.getTime() + TIME_INTERVAL));
		task1.setCrtdOn(now);
		tasks.add(task1);

		when(this.taskService.findAllByStatus((ArgumentMatchers.any(TaskStatus.class)))).thenReturn(tasks);
		
		//When
		MockHttpServletResponse response = this.mockMvc.perform(
				get("/api/v1/tasks")
						.queryParam("status", TaskStatus.late.toString())
						.contentType(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();		
		
		//Then
		assertEquals(response.getStatus(), HttpStatus.OK.value());
		verify(this.taskService).findAllByStatus(TaskStatus.late);
	}
	
	@Test
	public void findAllDoneTaskTest() throws Exception {
		Date now = new Date();
		List<Task> tasks =  new ArrayList<>();
		//Given		
		Task task1 = new Task();
		task1.setId(TASK_ID);
		task1.setName(TASK_NAME);
		task1.setDescription(TASK_DESCRIPTION);
		task1.setExpirationDate(new Date(now.getTime() + TIME_INTERVAL));
		task1.setEndDate(now);
		task1.setCrtdOn(now);
		tasks.add(task1);

		when(this.taskService.findAllByStatus((ArgumentMatchers.any(TaskStatus.class)))).thenReturn(tasks);
		
		//When
		MockHttpServletResponse response = this.mockMvc.perform(
				get("/api/v1/tasks")
						.queryParam("status", TaskStatus.done.toString())
						.contentType(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();		
		
		//Then
		assertEquals(response.getStatus(), HttpStatus.OK.value());
		verify(this.taskService).findAllByStatus(TaskStatus.done);
	}
	
	@Test
	public void findAllPendingTaskTest() throws Exception {
		Date now = new Date();
		List<Task> tasks =  new ArrayList<>();
		//Given		
		Task task1 = new Task();
		task1.setId(TASK_ID);
		task1.setName(TASK_NAME);
		task1.setDescription(TASK_DESCRIPTION);
		task1.setExpirationDate(new Date(now.getTime() + TIME_INTERVAL));
		task1.setEndDate(now);
		task1.setCrtdOn(now);
		tasks.add(task1);

		when(this.taskService.findAllByStatus((ArgumentMatchers.any(TaskStatus.class)))).thenReturn(tasks);
		
		//When
		MockHttpServletResponse response = this.mockMvc.perform(
				get("/api/v1/tasks")
						.queryParam("status", TaskStatus.pending.toString())
						.contentType(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();		
		
		//Then
		assertEquals(response.getStatus(), HttpStatus.OK.value());
		verify(this.taskService).findAllByStatus(TaskStatus.pending);
	}
    	
}
