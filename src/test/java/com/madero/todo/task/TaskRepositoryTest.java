package com.madero.todo.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.madero.todo.exception.TaskException;

@DataJpaTest
public class TaskRepositoryTest {
	
	@Autowired
	private TaskRepository taskRepository;

	private final static String TASK_NAME = "Install Git";

	private final static String TASK_DESCRIPTION = "Install Git core to control source code repositories.";

	private final static int TIME_INTERVAL = 1000 * 60 * 60 * 2;

	@Test
	public void saveTask() {
		Date now = new Date();
		//Given
		Task task = new Task();
		task.setName(TASK_NAME);
		task.setDescription(TASK_DESCRIPTION);
		task.setExpirationDate(new Date(now.getTime() + TIME_INTERVAL));
		//When
		task = this.taskRepository.saveAndFlush(task);
		//Then
		assertNotNull(task.getId());
		assertNotNull(task.getCrtdOn());
	}

	@Test
	public void updateTask() {
		Date now = new Date();
		//Given
		Task task = new Task();
		task.setName(TASK_NAME);
		task.setDescription(TASK_DESCRIPTION);
		task.setExpirationDate(new Date(now.getTime() + TIME_INTERVAL));
		task = this.taskRepository.saveAndFlush(task);
		//When
		Task taskToModify = this.taskRepository.findById(task.getId()).orElseThrow(()-> new TaskException("Task not found."));
		taskToModify.setEndDate(now);
		taskToModify = this.taskRepository.saveAndFlush(taskToModify);
		//Then
		assertNotNull(taskToModify.getEndDate());
	}

	@Test
	public void findLateTasks() {
		Date now = new Date();
		//Given
		Task taskOnTime = new Task();
		taskOnTime.setName(TASK_NAME);
		taskOnTime.setDescription(TASK_DESCRIPTION);
		taskOnTime.setExpirationDate(new Date(now.getTime() + TIME_INTERVAL));
		taskOnTime = this.taskRepository.saveAndFlush(taskOnTime);

		Task taskLate = new Task();
		taskLate.setName(TASK_NAME);
		taskLate.setDescription(TASK_DESCRIPTION);
		taskLate.setExpirationDate(new Date(now.getTime() - TIME_INTERVAL));
		taskLate = this.taskRepository.saveAndFlush(taskLate);
		//When
		List<Task> tasksLateDB = this.taskRepository.findByExpirationDateBeforeAndEndDateIsNull(now);
		//Then
		List<Task> tasksLate = new ArrayList<Task>();
		tasksLate.add(taskLate);

		assertEquals(tasksLateDB, tasksLate);			
	}

	@Test
	public void findOnTimeTask() {
		Date now = new Date();
		//Given
		Task taskOnTime = new Task();
		taskOnTime.setName(TASK_NAME);
		taskOnTime.setDescription(TASK_DESCRIPTION);
		taskOnTime.setExpirationDate(new Date(now.getTime() + TIME_INTERVAL));
		taskOnTime = this.taskRepository.saveAndFlush(taskOnTime);

		Task taskLate = new Task();
		taskLate.setName(TASK_NAME);
		taskLate.setDescription(TASK_DESCRIPTION);
		taskLate.setExpirationDate(new Date(now.getTime() - TIME_INTERVAL));
		taskLate = this.taskRepository.saveAndFlush(taskLate);
		//When
		List<Task> tasksOnTimeDB = this.taskRepository.findByExpirationDateAfterAndEndDateIsNull(now);
		//Then
		List<Task> tasksOnTime = new ArrayList<Task>();
		tasksOnTime.add(taskOnTime);

		assertEquals(tasksOnTimeDB, tasksOnTime);
	}

	@Test
	public void findDoneTask() {
		Date now = new Date();
		//Given
		Task taskDone = new Task();
		taskDone.setName(TASK_NAME);
		taskDone.setDescription(TASK_DESCRIPTION);
		taskDone.setExpirationDate(new Date(now.getTime() + TIME_INTERVAL));
		taskDone.setEndDate(now);
		taskDone = this.taskRepository.saveAndFlush(taskDone);

		Task taskPending = new Task();
		taskPending.setName(TASK_NAME);
		taskPending.setDescription(TASK_DESCRIPTION);
		taskPending.setExpirationDate(new Date(now.getTime() + TIME_INTERVAL));
		taskPending = this.taskRepository.saveAndFlush(taskPending);
		//When
		List<Task> tasksDoneDB = this.taskRepository.findByEndDateIsNotNull();
		//Then
		List<Task> tasksDone = new ArrayList<Task>();
		tasksDone.add(taskDone);

		assertEquals(tasksDoneDB, tasksDone);	
	}

	@Test
	public void findPendinTask() {
		Date now = new Date();
		//Given
		Task taskDone = new Task();
		taskDone.setName(TASK_NAME);
		taskDone.setDescription(TASK_DESCRIPTION);
		taskDone.setExpirationDate(new Date(now.getTime() + TIME_INTERVAL));
		taskDone.setEndDate(now);
		taskDone = this.taskRepository.saveAndFlush(taskDone);

		Task taskPending = new Task();
		taskPending.setName(TASK_NAME);
		taskPending.setDescription(TASK_DESCRIPTION);
		taskPending.setExpirationDate(new Date(now.getTime() + TIME_INTERVAL));
		taskPending = this.taskRepository.saveAndFlush(taskPending);
		//When
		List<Task> tasksPendingDB = this.taskRepository.findByEndDateIsNull();
		//Then
		List<Task> tasksPending = new ArrayList<Task>();
		tasksPending.add(taskPending);

		assertEquals(tasksPendingDB, tasksPending);	
	}

}