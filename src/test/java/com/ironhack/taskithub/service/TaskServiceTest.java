package com.ironhack.taskithub.service;

import com.ironhack.taskithub.dto.TaskDTO;
import com.ironhack.taskithub.enums.Priority;
import com.ironhack.taskithub.enums.Status;
import com.ironhack.taskithub.model.Department;
import com.ironhack.taskithub.model.Task;
import com.ironhack.taskithub.model.User;
import com.ironhack.taskithub.repository.DepartmentRepository;
import com.ironhack.taskithub.repository.TaskRepository;
import com.ironhack.taskithub.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TaskServiceTest
 */
@SpringBootTest
public class TaskServiceTest {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    private Task testTask;
    private User testUser;
    private Department testDepartment;

    @BeforeEach
    void setUp() {
        testDepartment = new Department();
        testDepartment.setName("Test Department");
        testDepartment = departmentRepository.save(testDepartment);

        testUser = new User();
        testUser.setName("Test User");
        testUser.setUsername("testuser");
        testUser.setPassword("password");
        testUser = userRepository.save(testUser);

        testTask = new Task();
        testTask.setTitle("Test Task");
        testTask.setDescription("Test Description");
        testTask.setDepartment(testDepartment);
        testTask.setCreatedBy(testUser);
        testTask.setPriority(Priority.MEDIUM);
        testTask.setStatus(Status.NOT_STARTED);
        testTask.setDueDate(LocalDateTime.now().plusDays(7));
        testTask = taskRepository.save(testTask);
    }

    @AfterEach
    void tearDown() {
        taskRepository.deleteAll();
        userRepository.deleteAll();
        departmentRepository.deleteAll();
    }

    @Test
    @Transactional
    void createTaskFromDTO_validInput_taskCreated() {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setTitle("New Task");
        taskDTO.setDescription("New Description");
        taskDTO.setPriority(Priority.HIGH);
        taskDTO.setStatus(Status.IN_PROGRESS);
        taskDTO.setDueDate(LocalDateTime.now().plusDays(14));
        taskDTO.setDepartmentId(testDepartment.getId());
        taskDTO.setCreatedById(testUser.getId());
        taskDTO.setAssignedUserIds(Arrays.asList(testUser.getId()));

        Task createdTask = taskService.createTaskFromDTO(taskDTO);

        assertNotNull(createdTask);
        assertEquals("New Task", createdTask.getTitle());
        assertEquals("New Description", createdTask.getDescription());
        assertEquals(Priority.HIGH, createdTask.getPriority());
        assertEquals(Status.IN_PROGRESS, createdTask.getStatus());
        assertEquals(testDepartment.getId(), createdTask.getDepartment().getId());
        assertEquals(testUser.getId(), createdTask.getCreatedBy().getId());
        assertTrue(createdTask.getAssignedUsers().contains(testUser));
    }

    @Test
    @Transactional
    void getTasksByDepartment_existingDepartment_returnsTasks() {
        List<Task> tasks = taskService.getTasksByDepartment(testDepartment.getId());
        assertFalse(tasks.isEmpty());
        assertTrue(tasks.contains(testTask));
    }

    @Test
    @Transactional
    void getTasksByDepartment_nonExistingDepartment_throwsException() {
        assertThrows(ResponseStatusException.class, () -> taskService.getTasksByDepartment(999L));
    }

    @Test
    @Transactional
    void getTasksCreatedByUser_existingUser_returnsTasks() {
        List<Task> tasks = taskService.getTasksCreatedByUser(testUser.getId());
        assertFalse(tasks.isEmpty());
        assertTrue(tasks.contains(testTask));
    }

    @Test
    @Transactional
    void getTasksAssignedToUser_existingUser_returnsTasks() {
        testTask.setAssignedUsers(new ArrayList<>(Arrays.asList(testUser)));
        taskRepository.save(testTask);

        List<Task> tasks = taskService.getTasksAssignedToUser(testUser.getId());
        assertFalse(tasks.isEmpty());
        assertTrue(tasks.contains(testTask));
    }

    @Test
    @Transactional
    void getTaskById_existingId_returnsTask() {
        Task foundTask = taskService.getTaskById(testTask.getId());
        assertNotNull(foundTask);
        assertEquals(testTask.getTitle(), foundTask.getTitle());
    }

    @Test
    @Transactional
    void getTaskById_nonExistingId_throwsException() {
        assertThrows(ResponseStatusException.class, () -> taskService.getTaskById(999L));
    }

    @Test
    @Transactional
    void getAllTasks_returnsAllTasks() {
        List<Task> tasks = taskService.getAllTasks();
        assertFalse(tasks.isEmpty());
        assertTrue(tasks.contains(testTask));
    }

    @Test
    @Transactional
    void updateTaskFromDTO_validInput_taskUpdated() {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setTitle("Updated Task");
        taskDTO.setDescription("Updated Description");
        taskDTO.setPriority(Priority.LOW);
        taskDTO.setStatus(Status.COMPLETED);
        taskDTO.setDueDate(LocalDateTime.now().plusDays(21));
        taskDTO.setAssignedUserIds(Arrays.asList(testUser.getId()));

        Task updatedTask = taskService.updateTaskFromDTO(testTask.getId(), taskDTO);

        assertNotNull(updatedTask);
        assertEquals("Updated Task", updatedTask.getTitle());
        assertEquals("Updated Description", updatedTask.getDescription());
        assertEquals(Priority.LOW, updatedTask.getPriority());
        assertEquals(Status.COMPLETED, updatedTask.getStatus());
        assertTrue(updatedTask.getAssignedUsers().contains(testUser));
    }

    @Test
    @Transactional
    void deleteTask_existingId_taskDeleted() {
        taskService.deleteTask(testTask.getId());
        assertFalse(taskRepository.existsById(testTask.getId()));
    }

    @Test
    @Transactional
    void toTaskDTO_validTask_returnsCorrectDTO() {
        TaskDTO taskDTO = taskService.toTaskDTO(testTask);
        assertEquals(testTask.getId(), taskDTO.getId());
        assertEquals(testTask.getTitle(), taskDTO.getTitle());
        assertEquals(testTask.getDescription(), taskDTO.getDescription());
        assertEquals(testTask.getPriority(), taskDTO.getPriority());
        assertEquals(testTask.getStatus(), taskDTO.getStatus());
        assertEquals(testTask.getDepartment().getId(), taskDTO.getDepartmentId());
        assertEquals(testTask.getCreatedBy().getId(), taskDTO.getCreatedById());
    }
}
