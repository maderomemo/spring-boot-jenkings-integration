package com.madero.todo.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import com.madero.todo.enums.TaskStatus;
import com.madero.todo.exception.TaskException;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class TaskServiceImplTest {

	@Mock
	private TaskRepository taskRepository;

	@InjectMocks
	private TaskServiceImpl taskServiceImpl;

	private final static Long TASK_ID = 1L;

	private final static String TASK_NAME = "Install Git";

	private final static String TASK_DESCRIPTION = "Install Git core to control source code repositories.";

	private final static int TIME_INTERVAL = 1000 * 60 * 60 * 2;

	@Test
	public void saveTaskTest() {
		Date now = new Date();
		//Given
		Task task = new Task();
		task.setId(TASK_ID);
		task.setName(TASK_NAME);
		task.setDescription(TASK_DESCRIPTION);
		task.setExpirationDate(new Date(now.getTime() + TIME_INTERVAL));
		task.setCrtdOn(now);

		when(this.taskRepository.saveAndFlush(ArgumentMatchers.any(Task.class))).thenReturn(task);
		//When
		Task test = new Task();
		test.setName(TASK_NAME);
		test.setDescription(TASK_DESCRIPTION);
		test.setExpirationDate(new Date(now.getTime() + TIME_INTERVAL));
		test = this.taskServiceImpl.saveTask(test);
		//Then
		assertNotNull(test.getId());
		assertNotNull(test.getCrtdOn());
		verify(this.taskRepository).saveAndFlush(ArgumentMatchers.any(Task.class));
	}

	@Test
	public void finalizeTaskSuccess() {
		Date now = new Date();
		//Given
		Task task = new Task();
		task.setId(TASK_ID);
		task.setName(TASK_NAME);
		task.setDescription(TASK_DESCRIPTION);
		task.setExpirationDate(new Date(now.getTime() + TIME_INTERVAL));
		task.setCrtdOn(now);
		when(this.taskRepository.findById(ArgumentMatchers.any(Long.class))).thenReturn(Optional.of(task));

		Task finalized = new Task();
		finalized.setId(TASK_ID);
		finalized.setName(TASK_NAME);
		finalized.setDescription(TASK_DESCRIPTION);
		finalized.setExpirationDate(new Date(now.getTime() + TIME_INTERVAL));
		finalized.setCrtdOn(now);
		finalized.setEndDate(now);
		when(this.taskRepository.saveAndFlush(ArgumentMatchers.any(Task.class))).thenReturn(finalized);
		//When
		Task test = this.taskServiceImpl.finalizeTask(TASK_ID);
		//Then
		assertNotNull(test.getEndDate());
		verify(this.taskRepository).findById(ArgumentMatchers.any(Long.class));
		verify(this.taskRepository).saveAndFlush(ArgumentMatchers.any(Task.class));
	}

	@Test
	public void  finalizeTaskAlreadyFinalize() {
		Date now = new Date();
		//Given
		Task task = new Task();
		task.setId(TASK_ID);
		task.setName(TASK_NAME);
		task.setDescription(TASK_DESCRIPTION);
		task.setExpirationDate(new Date(now.getTime() + TIME_INTERVAL));
		task.setEndDate(now);
		task.setCrtdOn(now);
		when(this.taskRepository.findById(ArgumentMatchers.any(Long.class))).thenReturn(Optional.of(task));
		//When
		Throwable exception = assertThrows(TaskException.class, () -> this.taskServiceImpl.finalizeTask(TASK_ID));
		//Then
		assertEquals("Task already finalized.", exception.getMessage());
		verify(this.taskRepository).findById(ArgumentMatchers.any(Long.class));		
		verify(this.taskRepository, times(0)).saveAndFlush(ArgumentMatchers.any(Task.class));	
	}

	@Test
	public void finalizeTaskNotFound() {
		//Given
		when(this.taskRepository.findById(ArgumentMatchers.any(Long.class))).thenReturn(Optional.empty());
		//When
		Throwable exception = assertThrows(TaskException.class, () -> this.taskServiceImpl.finalizeTask(TASK_ID));
		//Then
		assertEquals("TaskId not found " + TASK_ID, exception.getMessage());
		verify(this.taskRepository).findById(ArgumentMatchers.any(Long.class));
		verify(this.taskRepository, times(0)).saveAndFlush(ArgumentMatchers.any(Task.class));	
	}

	@Test
	public void findAllLate() {
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

		Task task2 = new Task();
		task2.setId(2L);
		task2.setName(TASK_NAME);
		task2.setDescription(TASK_DESCRIPTION);
		task2.setExpirationDate(new Date(now.getTime() - TIME_INTERVAL));
		task2.setCrtdOn(now);
		tasks.add(task2);

		Task task3 = new Task();
		task3.setId(3L);
		task3.setName(TASK_NAME);
		task3.setDescription(TASK_DESCRIPTION);
		task3.setExpirationDate(new Date(now.getTime() - TIME_INTERVAL));
		task3.setCrtdOn(now);
		tasks.add(task3);

		when(this.taskRepository.findByExpirationDateBeforeAndEndDateIsNull(ArgumentMatchers.any(Date.class))).thenReturn(tasks);
		//When
		List<Task> taskList = this.taskServiceImpl.findAllByStatus(TaskStatus.late);
		//Then
		assertEquals(taskList, tasks);
		verify(this.taskRepository).findByExpirationDateBeforeAndEndDateIsNull(ArgumentMatchers.any(Date.class));
	}

	@Test
	public void findAllOnTime() {
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

		Task task2 = new Task();
		task2.setId(2L);
		task2.setName(TASK_NAME);
		task2.setDescription(TASK_DESCRIPTION);
		task2.setExpirationDate(new Date(now.getTime() + TIME_INTERVAL));
		task2.setCrtdOn(now);
		tasks.add(task2);

		Task task3 = new Task();
		task3.setId(3L);
		task3.setName(TASK_NAME);
		task3.setDescription(TASK_DESCRIPTION);
		task3.setExpirationDate(new Date(now.getTime() + TIME_INTERVAL));
		task3.setCrtdOn(now);
		tasks.add(task3);

		when(this.taskRepository.findByExpirationDateAfterAndEndDateIsNull(ArgumentMatchers.any(Date.class))).thenReturn(tasks);
		//When
		List<Task> taskList = this.taskServiceImpl.findAllByStatus(TaskStatus.onTime);
		//Then
		assertEquals(taskList, tasks);
		verify(this.taskRepository).findByExpirationDateAfterAndEndDateIsNull(ArgumentMatchers.any(Date.class));
	}

	@Test
	public void findAllDone() {
		Date now = new Date();
		List<Task> tasks =  new ArrayList<>();
		//Given
		Task task1 = new Task();
		task1.setId(TASK_ID);
		task1.setName(TASK_NAME);
		task1.setDescription(TASK_DESCRIPTION);
		task1.setExpirationDate(new Date(now.getTime() - TIME_INTERVAL));
		task1.setEndDate(now);
		task1.setCrtdOn(now);
		tasks.add(task1);

		Task task2 = new Task();
		task2.setId(2L);
		task2.setName(TASK_NAME);
		task2.setDescription(TASK_DESCRIPTION);
		task2.setExpirationDate(new Date(now.getTime() + TIME_INTERVAL));
		task2.setEndDate(now);
		task2.setCrtdOn(now);
		tasks.add(task2);

		Task task3 = new Task();
		task3.setId(3L);
		task3.setName(TASK_NAME);
		task3.setDescription(TASK_DESCRIPTION);
		task3.setExpirationDate(new Date(now.getTime() + TIME_INTERVAL));
		task3.setEndDate(now);
		task3.setCrtdOn(now);
		tasks.add(task3);

		when(this.taskRepository.findByEndDateIsNotNull()).thenReturn(tasks);
		//When
		List<Task> taskList = this.taskServiceImpl.findAllByStatus(TaskStatus.done);
		//Then
		assertEquals(taskList, tasks);
		verify(this.taskRepository).findByEndDateIsNotNull();
	}

	@Test
	public void findAllPending() {
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

		Task task2 = new Task();
		task2.setId(2L);
		task2.setName(TASK_NAME);
		task2.setDescription(TASK_DESCRIPTION);
		task2.setExpirationDate(new Date(now.getTime() + TIME_INTERVAL));
		task2.setCrtdOn(now);
		tasks.add(task2);

		Task task3 = new Task();
		task3.setId(3L);
		task3.setName(TASK_NAME);
		task3.setDescription(TASK_DESCRIPTION);
		task3.setExpirationDate(new Date(now.getTime() + TIME_INTERVAL));
		task3.setCrtdOn(now);
		tasks.add(task3);

		when(this.taskRepository.findByEndDateIsNull()).thenReturn(tasks);
		//When
		List<Task> taskList = this.taskServiceImpl.findAllByStatus(TaskStatus.pending);
		//Then
		assertEquals(taskList, tasks);
		verify(this.taskRepository).findByEndDateIsNull();
	}

	@Test
	public void findAll() {
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

		Task task2 = new Task();
		task2.setId(2L);
		task2.setName(TASK_NAME);
		task2.setDescription(TASK_DESCRIPTION);
		task2.setExpirationDate(new Date(now.getTime() - TIME_INTERVAL));
		task2.setCrtdOn(now);
		tasks.add(task2);

		Task task3 = new Task();
		task3.setId(3L);
		task3.setName(TASK_NAME);
		task3.setDescription(TASK_DESCRIPTION);
		task3.setExpirationDate(new Date(now.getTime() + TIME_INTERVAL));
		task3.setCrtdOn(now);
		tasks.add(task3);

		when(this.taskRepository.findAll()).thenReturn(tasks);
		//When
		List<Task> taskList = this.taskServiceImpl.findAllByStatus(TaskStatus.all);
		//Then
		assertEquals(taskList, tasks);
		verify(this.taskRepository).findAll();
	}

	@BeforeEach
	private void resetMockito() {
		reset(this.taskRepository);
	}

}
