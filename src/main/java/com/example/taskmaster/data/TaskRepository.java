package com.example.taskmaster.data;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.example.taskmaster.domain.Task;




@RepositoryRestResource(collectionResourceRel = "tasks", path = "tasks")
public interface TaskRepository extends MongoRepository<Task, String> {
	
	List<Task> findByDescriptionLike(@Param("desc") String description);
	List<Task> findByUserId(@Param("userId") String userId);


}
