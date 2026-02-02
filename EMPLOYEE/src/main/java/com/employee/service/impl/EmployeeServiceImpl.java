package com.employee.service.impl;

import com.employee.client.AddressClient;
import com.employee.exception.BadRequestException;
import com.employee.exception.ResourceNotFoundException;
import com.employee.model.dto.AddressDto;
import com.employee.model.dto.EmployeeDto;
import com.employee.model.entity.Employee;
import com.employee.repository.EmployeeRepository;
import com.employee.service.EmployeeService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    Logger log = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    private final EmployeeRepository employeeRepository;

    private final ModelMapper modelMapper;

    private final AddressClient addressClient;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, ModelMapper modelMapper,
                               AddressClient addressClient) {
        this.employeeRepository = employeeRepository;
        this.modelMapper = modelMapper;
        this.addressClient = addressClient;
    }

    @Override
    public EmployeeDto saveEmployee(EmployeeDto employeeDto) {
        if (employeeDto.getId() != null) {
            throw new BadRequestException("Please provide employee id");
        }
        Employee entity = modelMapper.map(employeeDto, Employee.class);
        Employee savedEntity = employeeRepository.save(entity);
        return modelMapper.map(savedEntity, EmployeeDto.class);
    }

    @Override
    public EmployeeDto updateEmployee(Long id, EmployeeDto employeeDto) {
        if (id == null || employeeDto.getId() == null) {
            throw new BadRequestException("Please provide employee id");
        }
        if (!Objects.equals(id, employeeDto.getId())) {
            throw new BadRequestException("Employee id mismatch");
        }

        employeeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
        Employee entity = modelMapper.map(employeeDto, Employee.class);
        Employee updatedEntity = employeeRepository.save(entity);
        return modelMapper.map(updatedEntity, EmployeeDto.class);
    }

    @Override
    public void deleteEmployee(Long id) {
        Employee employee = employeeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
        employeeRepository.delete(employee);
    }

    @Override
    public EmployeeDto getSingleEmployee(Long id) {
        Employee employee = employeeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
        List<AddressDto> addressByEmpId = new ArrayList<>();
        EmployeeDto dto = modelMapper.map(employee, EmployeeDto.class);
        try {
            addressByEmpId = addressClient.getAddressByEmpId(id);
            dto.setAddresses(addressByEmpId);
        } catch (Exception ex) {
            log.info("No address found for employee id: {} ", id);
        }
        return dto;
    }

    @Override
    public List<EmployeeDto> getAllEmployees() {
        List<Employee> employees = employeeRepository.findAll();
        if (employees.isEmpty()) {
            throw new ResourceNotFoundException("Employees not found");
        }
        List<EmployeeDto> employeeDtoList = employees.stream().map(emp -> modelMapper.map(emp, EmployeeDto.class)).toList();
        List<EmployeeDto> response = new ArrayList<>();
        for (EmployeeDto employeeDto : employeeDtoList) {
            List<AddressDto> addressByEmpId = new ArrayList<>();
            try {
                addressByEmpId = addressClient.getAddressByEmpId(employeeDto.getId());
            } catch (Exception ex) {
                log.info("No address found for employee id: {} ", employeeDto.getId());
            }
            employeeDto.setAddresses(addressByEmpId);
            response.add(employeeDto);
        }
        return employeeDtoList;
    }

    @Override
    public EmployeeDto getEmployeeByEmpCodeAndCompanyName(String empCode, String companyName) {
        Employee employee = employeeRepository.findByEmpCodeAndCompanyName(empCode, companyName).orElseThrow(() -> new ResourceNotFoundException(
                "Employee not found with empCode: " + empCode + " and companyName: " + companyName));
        return modelMapper.map(employee, EmployeeDto.class);
    }

}
