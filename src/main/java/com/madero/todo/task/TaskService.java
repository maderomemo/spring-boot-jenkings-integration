package com.madero.todo.task;

import java.util.List;

import com.madero.todo.enums.TaskStatus;

public interface TaskService {
	Task saveTask(Task task);
	List<Task> findAllByStatus(TaskStatus taskStatus);
	Task finalizeTask(Long taskId);
}
