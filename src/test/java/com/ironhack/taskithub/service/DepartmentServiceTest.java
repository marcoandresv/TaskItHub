package com.ironhack.taskithub.service;

import com.ironhack.taskithub.dto.DepartmentDTO;
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

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DepartmentServiceTest
 */
@SpringBootTest
public class DepartmentServiceTest {

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    private Department testDepartment;
    private User testUser;
    private Task testTask;

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
    void createDepartmentFromDTO_validInput_departmentCreated() {
        DepartmentDTO dto = new DepartmentDTO();
        dto.setName("New Department");
        dto.setUserIds(Arrays.asList(testUser.getId()));
        dto.setTaskIds(Arrays.asList(testTask.getId()));

        Department created = departmentService.createDepartmentFromDTO(dto);

        assertNotNull(created);
        assertEquals("New Department", created.getName());
        assertTrue(created.getUsers().contains(testUser));
        assertTrue(created.getTasks().contains(testTask));
    }

    @Test
    @Transactional
    void createDepartment_duplicateName_throwsException() {
        Department department = new Department();
        department.setName("Test Department");

        assertThrows(ResponseStatusException.class, () -> departmentService.createDepartment(department, null, null));
    }

    @Test
    @Transactional
    void getAllDepartments_returnsAllDepartments() {
        List<Department> departments = departmentService.getAllDepartments();
        assertFalse(departments.isEmpty());
        assertTrue(departments.contains(testDepartment));
    }

    @Test
    @Transactional
    void getDepartmentById_existingId_returnsDepartment() {
        Department found = departmentService.getDepartmentById(testDepartment.getId());
        assertNotNull(found);
        assertEquals(testDepartment.getName(), found.getName());
    }

    @Test
    @Transactional
    void getDepartmentById_nonExistingId_throwsException() {
        assertThrows(ResponseStatusException.class, () -> departmentService.getDepartmentById(999L));
    }

    @Test
    @Transactional
    void updateDepartmentFromDTO_validInput_departmentUpdated() {
        DepartmentDTO dto = new DepartmentDTO();
        dto.setName("Updated Department");
        dto.setUserIds(Arrays.asList(testUser.getId()));
        dto.setTaskIds(Arrays.asList(testTask.getId()));

        Department updated = departmentService.updateDepartmentFromDTO(testDepartment.getId(), dto);

        assertNotNull(updated);
        assertEquals("Updated Department", updated.getName());
        assertTrue(updated.getUsers().contains(testUser));
        assertTrue(updated.getTasks().contains(testTask));
    }

    @Test
    @Transactional
    void deleteDepartment_existingId_departmentDeleted() {
        departmentService.deleteDepartment(testDepartment.getId());
        assertFalse(departmentRepository.existsById(testDepartment.getId()));
    }

    @Test
    @Transactional
    void deleteDepartment_nonExistingId_throwsException() {
        assertThrows(ResponseStatusException.class, () -> departmentService.deleteDepartment(999L));
    }
}
