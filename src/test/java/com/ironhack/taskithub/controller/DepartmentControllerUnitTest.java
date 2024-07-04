package com.ironhack.taskithub.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ironhack.taskithub.dto.DepartmentDTO;
import com.ironhack.taskithub.dto.DepartmentSummaryDTO;
import com.ironhack.taskithub.model.Department;
import com.ironhack.taskithub.service.DepartmentService;
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
 * DepartmentControllerUnitTest
 */
@SpringBootTest
class DepartmentControllerUnitTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private DepartmentService departmentService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void createDepartment_validDepartmentDTO_departmentCreated() throws Exception {
        DepartmentDTO inputDTO = new DepartmentDTO();
        inputDTO.setName("HR");
        inputDTO.setTaskIds(List.of(1L));
        inputDTO.setUserIds(List.of(1L));

        DepartmentSummaryDTO outputDTO = new DepartmentSummaryDTO();
        outputDTO.setId(1L);
        outputDTO.setName("HR");

        when(departmentService.createDepartmentFromDTO(any(DepartmentDTO.class))).thenReturn(new Department());
        when(departmentService.toDepartmentSummaryDTO(any(Department.class))).thenReturn(outputDTO);

        mockMvc.perform(post("/departments")
                .content(objectMapper.writeValueAsString(inputDTO))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("HR"));
    }

    @Test
    void getDepartmentById_existingId_departmentReturned() throws Exception {
        DepartmentSummaryDTO departmentDTO = new DepartmentSummaryDTO();
        departmentDTO.setId(1L);
        departmentDTO.setName("HR");

        when(departmentService.getDepartmentById(1L)).thenReturn(new Department());
        when(departmentService.toDepartmentSummaryDTO(any(Department.class))).thenReturn(departmentDTO);

        mockMvc.perform(get("/departments/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("HR"));
    }

    @Test
    void getAllDepartments_departmentsExist_listReturned() throws Exception {
        DepartmentDTO department1 = new DepartmentDTO();
        department1.setId(1L);
        department1.setName("HR");

        DepartmentDTO department2 = new DepartmentDTO();
        department2.setId(2L);
        department2.setName("IT");

        List<DepartmentDTO> departments = Arrays.asList(department1, department2);

        when(departmentService.getAllDepartments()).thenReturn(List.of(new Department(), new Department()));
        when(departmentService.toDepartmentDTO(any(Department.class))).thenReturn(department1, department2);

        mockMvc.perform(get("/departments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("HR"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("IT"));
    }

    @Test
    void updateDepartment_existingId_departmentUpdated() throws Exception {
        DepartmentDTO inputDTO = new DepartmentDTO();
        inputDTO.setName("HR Updated");
        inputDTO.setTaskIds(List.of(1L));
        inputDTO.setUserIds(List.of(1L));

        DepartmentSummaryDTO outputDTO = new DepartmentSummaryDTO();
        outputDTO.setId(1L);
        outputDTO.setName("HR Updated");

        when(departmentService.updateDepartmentFromDTO(eq(1L), any(DepartmentDTO.class))).thenReturn(new Department());
        when(departmentService.toDepartmentSummaryDTO(any(Department.class))).thenReturn(outputDTO);

        mockMvc.perform(put("/departments/{id}", 1L)
                .content(objectMapper.writeValueAsString(inputDTO))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("HR Updated"));
    }

    @Test
    void deleteDepartment_existingId_departmentDeleted() throws Exception {
        doNothing().when(departmentService).deleteDepartment(1L);

        mockMvc.perform(delete("/departments/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(departmentService, times(1)).deleteDepartment(1L);
    }
}
