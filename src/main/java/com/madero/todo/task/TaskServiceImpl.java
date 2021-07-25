package com.madero.todo.task;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.madero.todo.enums.TaskStatus;
import com.madero.todo.exception.TaskException;

@Service
public class TaskServiceImpl implements TaskService{
	
	private TaskRepository taskRepository;

	@Autowired
	public TaskServiceImpl(TaskRepository taskRepository) {
		this.taskRepository = taskRepository;
	}

	@Override
	public Task saveTask(Task task) {
		return this.taskRepository.saveAndFlush(task);
	}

	@Override
	public List<Task> findAllByStatus(TaskStatus taskStatus) {
		switch(taskStatus) {
			case late:
				return this.taskRepository.findByExpirationDateBeforeAndEndDateIsNull(new Date());
			case onTime:
				return this.taskRepository.findByExpirationDateAfterAndEndDateIsNull(new Date());
			case done:
				return this.taskRepository.findByEndDateIsNotNull();
			case pending:
				return this.taskRepository.findByEndDateIsNull();
			default:
				return this.taskRepository.findAll();
		}
	}

	@Override
	public Task finalizeTask(Long taskId) {
		Task task = this.taskRepository.findById(taskId).orElseThrow(()-> new TaskException("TaskId not found " + taskId));
		if(task.getEndDate() != null)
			throw new TaskException("Task already finalized.");
		task.setEndDate(new Date());
		return this.taskRepository.saveAndFlush(task);
	}

}
