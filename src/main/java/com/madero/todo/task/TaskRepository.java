package com.madero.todo.task;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>{
	List<Task> findByExpirationDateAfterAndEndDateIsNull(Date now); //OnTime
	List<Task> findByExpirationDateBeforeAndEndDateIsNull(Date now); //Late
	List<Task> findByEndDateIsNotNull(); //Done
	List<Task> findByEndDateIsNull(); //Pending
}
