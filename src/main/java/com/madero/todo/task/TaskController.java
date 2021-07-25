package com.madero.todo.task;

import java.util.List;
import javax.validation.Valid;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.madero.todo.enums.TaskAction;
import com.madero.todo.enums.TaskStatus;

@RestController
@RequestMapping("api/v1/tasks")
public class TaskController {
	
	@Autowired
	private TaskService taskService;

	@Autowired
	private ModelMapper modelMapper;

	@PostMapping
	public ResponseEntity<?> saveTask(@Valid @RequestBody TaskDTO task) {
		return new ResponseEntity<TaskDTO>(
				this.modelMapper.map(
						this.taskService.saveTask(
								this.modelMapper.map(task, Task.class)), 
						TaskDTO.class), 
				HttpStatus.CREATED);
	}

	@GetMapping
	public List<TaskDTO> findAllByStatus(@RequestParam(name = "status", required = false, defaultValue = "all") TaskStatus status){
		return this.modelMapper.map(
				this.taskService.findAllByStatus(status), 
				new TypeToken<List<TaskDTO>>() {}.getType());
	}

	@PostMapping("/{taskId}")
	public ResponseEntity<?> finalizeTask(@PathVariable(name = "taskId") Long taskId, @RequestParam(name = "action", required = true) TaskAction action){
		return new ResponseEntity<TaskDTO>(
				this.modelMapper.map(
						this.taskService.finalizeTask(taskId), 
						TaskDTO.class), 
				HttpStatus.OK);
	}

}
