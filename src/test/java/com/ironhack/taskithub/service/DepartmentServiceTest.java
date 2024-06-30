package com.ironhack.taskithub.service;

import com.ironhack.taskithub.dto.DepartmentDTO;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * DepartmentServiceTest
 */

@SpringBootTest
class DepartmentServiceTest {

    @InjectMocks
    private DepartmentService departmentService;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createDepartmentFromDTO_validDTO_departmentCreated() {
        DepartmentDTO dto = new DepartmentDTO();
        dto.setName("Test Department");
        dto.setTaskIds(Arrays.asList(1L, 2L));
        dto.setUserIds(Arrays.asList(1L, 2L));

        Task task1 = new Task();
        task1.setId(1L);
        Task task2 = new Task();
        task2.setId(2L);

        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(2L);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task1));
        when(taskRepository.findById(2L)).thenReturn(Optional.of(task2));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(departmentRepository.existsByName(anyString())).thenReturn(false);
        when(departmentRepository.save(any(Department.class))).thenAnswer(i -> i.getArguments()[0]);

        Department created = departmentService.createDepartmentFromDTO(dto);

        assertNotNull(created);
        assertEquals("Test Department", created.getName());
        assertEquals(2, created.getTasks().size());
        assertEquals(2, created.getUsers().size());
        verify(departmentRepository, times(1)).save(any(Department.class));
    }

    @Test
    void getAllDepartments_departmentsExist_listReturned() {
        Department dept1 = new Department();
        dept1.setId(1L);
        dept1.setName("Dept 1");
        Department dept2 = new Department();
        dept2.setId(2L);
        dept2.setName("Dept 2");

        when(departmentRepository.findAll()).thenReturn(Arrays.asList(dept1, dept2));

        List<Department> departments = departmentService.getAllDepartments();

        assertFalse(departments.isEmpty());
        assertEquals(2, departments.size());
        assertEquals("Dept 1", departments.get(0).getName());
        assertEquals("Dept 2", departments.get(1).getName());
    }

    @Test
    void getDepartmentById_existingId_departmentReturned() {
        Department dept = new Department();
        dept.setId(1L);
        dept.setName("Test Dept");

        when(departmentRepository.existsById(1L)).thenReturn(true);
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(dept));

        Department found = departmentService.getDepartmentById(1L);

        assertNotNull(found);
        assertEquals("Test Dept", found.getName());
    }

    @Test
    void getDepartmentById_nonExistingId_throwsNotFound() {
        when(departmentRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(ResponseStatusException.class, () -> departmentService.getDepartmentById(1L));
    }

    @Test
    void updateDepartmentFromDTO_existingDepartment_departmentUpdated() {
        Department existingDept = new Department();
        existingDept.setId(1L);
        existingDept.setName("Old Name");

        DepartmentDTO dto = new DepartmentDTO();
        dto.setName("New Name");
        dto.setTaskIds(Arrays.asList(1L));
        dto.setUserIds(Arrays.asList(1L));

        Task task = new Task();
        task.setId(1L);
        User user = new User();
        user.setId(1L);

        when(departmentRepository.findById(1L)).thenReturn(Optional.of(existingDept));
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(departmentRepository.save(any(Department.class))).thenAnswer(i -> i.getArguments()[0]);

        Department updated = departmentService.updateDepartmentFromDTO(1L, dto);

        assertNotNull(updated);
        assertEquals("New Name", updated.getName());
        assertEquals(1, updated.getTasks().size());
        assertEquals(1, updated.getUsers().size());
        verify(departmentRepository, times(1)).save(any(Department.class));
    }

    @Test
    void deleteDepartment_existingId_departmentDeleted() {
        when(departmentRepository.existsById(1L)).thenReturn(true);
        doNothing().when(departmentRepository).deleteById(1L);

        assertDoesNotThrow(() -> departmentService.deleteDepartment(1L));
        verify(departmentRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteDepartment_nonExistingId_throwsNotFound() {
        when(departmentRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(ResponseStatusException.class, () -> departmentService.deleteDepartment(1L));
    }

}
