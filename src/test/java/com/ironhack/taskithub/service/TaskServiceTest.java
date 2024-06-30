package com.ironhack.taskithub.service;

import com.ironhack.taskithub.dto.TaskDTO;
import com.ironhack.taskithub.model.Department;
import com.ironhack.taskithub.model.Task;
import com.ironhack.taskithub.model.User;
import com.ironhack.taskithub.repository.DepartmentRepository;
import com.ironhack.taskithub.repository.TaskRepository;
import com.ironhack.taskithub.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * TaskServiceTest
 */
@SpringBootTest
class TaskServiceTest {

    @InjectMocks
    private TaskService taskService;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createTaskFromDTO_validDTO_returnsCreatedTask() {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setTitle("Test Task");
        taskDTO.setDepartmentId(1L);
        taskDTO.setCreatedById(1L);
        taskDTO.setAssignedUserIds(Arrays.asList(1L, 2L));

        Department department = new Department();
        department.setId(1L);
        User createdBy = new User();
        createdBy.setId(1L);
        User assignedUser1 = new User();
        assignedUser1.setId(1L);
        User assignedUser2 = new User();
        assignedUser2.setId(2L);

        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(userRepository.findById(1L)).thenReturn(Optional.of(createdBy));
        when(userRepository.findById(2L)).thenReturn(Optional.of(assignedUser2));
        when(taskRepository.save(any(Task.class))).thenAnswer(i -> i.getArguments()[0]);

        Task createdTask = taskService.createTaskFromDTO(taskDTO);

        assertNotNull(createdTask);
        assertEquals("Test Task", createdTask.getTitle());
        assertEquals(department, createdTask.getDepartment());
        assertEquals(createdBy, createdTask.getCreatedBy());
        assertEquals(2, createdTask.getAssignedUsers().size());
    }

    @Test
    void getTasksByDepartment_validDepartmentId_returnsTaskList() {
        Department department = new Department();
        department.setId(1L);
        Task task1 = new Task();
        task1.setId(1L);
        Task task2 = new Task();
        task2.setId(2L);

        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(taskRepository.findByDepartmentId(1L)).thenReturn(Arrays.asList(task1, task2));

        List<Task> tasks = taskService.getTasksByDepartment(1L);

        assertNotNull(tasks);
        assertEquals(2, tasks.size());
    }

    @Test
    void getTasksByDepartment_invalidDepartmentId_throwsResponseStatusException() {
        when(departmentRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> taskService.getTasksByDepartment(1L));
    }

    @Test
    void getTasksCreatedByUser_validUserId_returnsTaskList() {
        User user = new User();
        user.setId(1L);
        Task task1 = new Task();
        task1.setId(1L);
        Task task2 = new Task();
        task2.setId(2L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(taskRepository.findByCreatedBy_Id(1L)).thenReturn(Arrays.asList(task1, task2));

        List<Task> tasks = taskService.getTasksCreatedByUser(1L);

        assertNotNull(tasks);
        assertEquals(2, tasks.size());
    }

    @Test
    void getTasksAssignedToUser_validUserId_returnsTaskList() {
        User user = new User();
        user.setId(1L);
        Task task1 = new Task();
        task1.setId(1L);
        Task task2 = new Task();
        task2.setId(2L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(taskRepository.findByAssignedUsers_Id(1L)).thenReturn(Arrays.asList(task1, task2));

        List<Task> tasks = taskService.getTasksAssignedToUser(1L);

        assertNotNull(tasks);
        assertEquals(2, tasks.size());
    }

    @Test
    void getTaskById_validId_returnsTask() {
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        Task foundTask = taskService.getTaskById(1L);

        assertNotNull(foundTask);
        assertEquals("Test Task", foundTask.getTitle());
    }

    @Test
    void getTaskById_invalidId_throwsResponseStatusException() {
        when(taskRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> taskService.getTaskById(1L));
    }

    @Test
    void updateTaskFromDTO_validIdAndDTO_returnsUpdatedTask() {
        Task existingTask = new Task();
        existingTask.setId(1L);
        existingTask.setTitle("Old Title");

        TaskDTO updateDTO = new TaskDTO();
        updateDTO.setTitle("New Title");
        updateDTO.setAssignedUserIds(Arrays.asList(1L, 2L));

        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(2L);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(taskRepository.save(any(Task.class))).thenAnswer(i -> i.getArguments()[0]);

        Task updatedTask = taskService.updateTaskFromDTO(1L, updateDTO);

        assertNotNull(updatedTask);
        assertEquals("New Title", updatedTask.getTitle());
        assertEquals(2, updatedTask.getAssignedUsers().size());
    }

    @Test
    void deleteTask_existingId_deletesCalled() {
        doNothing().when(taskRepository).deleteById(1L);

        taskService.deleteTask(1L);

        verify(taskRepository).deleteById(1L);
    }
}
