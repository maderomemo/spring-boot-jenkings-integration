package com.madero.todo.task;

import java.util.Date;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter 
@ToString @EqualsAndHashCode
@NoArgsConstructor @AllArgsConstructor
public class TaskDTO{
	
	private Long id;
	
	@NotNull(message = "name is required.")
	@Size(min = 1, max = 50, message = "name size must be between 1 and 50.")
	private String name;
	
	@NotNull(message = "description is required.")
	@Size(min = 1, max = 100, message = "description size must be between 1 and 100.")
	private String description;
	
	@Future(message = "expirationDate must be a date in the future.")
	@NotNull(message = "expirationDate is required.")
	private Date expirationDate;

}
