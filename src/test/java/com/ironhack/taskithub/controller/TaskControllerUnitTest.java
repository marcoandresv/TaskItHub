package com.ironhack.taskithub.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ironhack.taskithub.dto.TaskDTO;
import com.ironhack.taskithub.dto.TaskSummaryDTO;
import com.ironhack.taskithub.model.Task;
import com.ironhack.taskithub.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * TaskControllerUnitTest
 */
@SpringBootTest
class TaskControllerUnitTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private TaskService taskService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void createTask_validTaskDTO_taskCreated() throws Exception {
        TaskDTO inputDTO = new TaskDTO();
        inputDTO.setTitle("New Task");
        inputDTO.setDescription("Task description");
        inputDTO.setCreatedById(1L);
        inputDTO.setAssignedUserIds(Arrays.asList(2L));

        TaskSummaryDTO outputDTO = new TaskSummaryDTO();
        outputDTO.setId(1L);
        outputDTO.setTitle("New Task");

        when(taskService.createTaskFromDTO(any(TaskDTO.class))).thenReturn(new Task());
        when(taskService.toTaskSummaryDTO(any(Task.class))).thenReturn(outputDTO);

        mockMvc.perform(post("/tasks")
                .content(objectMapper.writeValueAsString(inputDTO))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("New Task"));
    }

    @Test
    void getTaskById_existingId_taskReturned() throws Exception {
        TaskSummaryDTO taskDTO = new TaskSummaryDTO();
        taskDTO.setId(1L);
        taskDTO.setTitle("Task Title");

        when(taskService.getTaskById(1L)).thenReturn(new Task());
        when(taskService.toTaskSummaryDTO(any(Task.class))).thenReturn(taskDTO);

        mockMvc.perform(get("/tasks/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Task Title"));
    }

    @Test
    void getTasksByDepartment_existingDepartmentId_tasksReturned() throws Exception {
        TaskSummaryDTO task1 = new TaskSummaryDTO();
        task1.setId(1L);
        task1.setTitle("Task 1");

        TaskSummaryDTO task2 = new TaskSummaryDTO();
        task2.setId(2L);
        task2.setTitle("Task 2");

        List<TaskSummaryDTO> tasks = Arrays.asList(task1, task2);

        when(taskService.getTasksByDepartment(1L)).thenReturn(List.of(new Task(), new Task()));
        when(taskService.toTaskSummaryDTO(any(Task.class))).thenReturn(task1, task2);

        mockMvc.perform(get("/tasks/department/{departmentId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("Task 1"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].title").value("Task 2"));
    }

    @Test
    void getTasksCreatedByUser_existingUserId_tasksReturned() throws Exception {
        TaskSummaryDTO task1 = new TaskSummaryDTO();
        task1.setId(1L);
        task1.setTitle("Task 1");

        TaskSummaryDTO task2 = new TaskSummaryDTO();
        task2.setId(2L);
        task2.setTitle("Task 2");

        List<TaskSummaryDTO> tasks = Arrays.asList(task1, task2);

        when(taskService.getTasksCreatedByUser(1L)).thenReturn(List.of(new Task(), new Task()));
        when(taskService.toTaskSummaryDTO(any(Task.class))).thenReturn(task1, task2);

        mockMvc.perform(get("/tasks/created-by/{userId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("Task 1"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].title").value("Task 2"));
    }

    @Test
    void getTasksAssignedToUser_existingUserId_tasksReturned() throws Exception {
        TaskSummaryDTO task1 = new TaskSummaryDTO();
        task1.setId(1L);
        task1.setTitle("Task 1");

        TaskSummaryDTO task2 = new TaskSummaryDTO();
        task2.setId(2L);
        task2.setTitle("Task 2");

        List<TaskSummaryDTO> tasks = Arrays.asList(task1, task2);

        when(taskService.getTasksAssignedToUser(1L)).thenReturn(List.of(new Task(), new Task()));
        when(taskService.toTaskSummaryDTO(any(Task.class))).thenReturn(task1, task2);

        mockMvc.perform(get("/tasks/assigned-to/{userId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("Task 1"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].title").value("Task 2"));
    }

    @Test
    void getAllTasks_tasksExist_listReturned() throws Exception {
        TaskDTO task1 = new TaskDTO();
        task1.setId(1L);
        task1.setTitle("Task 1");

        TaskDTO task2 = new TaskDTO();
        task2.setId(2L);
        task2.setTitle("Task 2");

        List<TaskDTO> tasks = Arrays.asList(task1, task2);

        when(taskService.getAllTasks()).thenReturn(List.of(new Task(), new Task()));
        when(taskService.toTaskDTO(any(Task.class))).thenReturn(task1, task2);

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("Task 1"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].title").value("Task 2"));
    }

    @Test
    void updateTask_existingId_taskUpdated() throws Exception {
        TaskDTO inputDTO = new TaskDTO();
        inputDTO.setTitle("Updated Task");
        inputDTO.setDescription("Updated description");
        inputDTO.setCreatedById(1L);
        inputDTO.setAssignedUserIds(Arrays.asList(2L));

        TaskSummaryDTO outputDTO = new TaskSummaryDTO();
        outputDTO.setId(1L);
        outputDTO.setTitle("Updated Task");

        when(taskService.updateTaskFromDTO(eq(1L), any(TaskDTO.class))).thenReturn(new Task());
        when(taskService.toTaskSummaryDTO(any(Task.class))).thenReturn(outputDTO);

        mockMvc.perform(put("/tasks/{id}", 1L)
                .content(objectMapper.writeValueAsString(inputDTO))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Updated Task"));
    }

    @Test
    void deleteTask_existingId_taskDeleted() throws Exception {
        doNothing().when(taskService).deleteTask(1L);

        mockMvc.perform(delete("/tasks/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(taskService, times(1)).deleteTask(1L);
    }
}
