package io.howeveryir.talentflow.service;

import io.howeveryir.talentflow.mapper.EmployeeMapper;
import io.howeveryir.talentflow.model.Employee;
import io.howeveryir.talentflow.model.MailConstants;
import io.howeveryir.talentflow.model.MailSendLog;
import io.howeveryir.talentflow.model.RespPageBean;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeMapper employeeMapper;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private MailSendLogService mailSendLogService;

    @InjectMocks
    private EmployeeService employeeService;

    @Test
    void getEmployeeByPageShouldConvertPageToOffset() {
        Employee condition = new Employee();
        Date[] beginDateScope = null;
        List<Employee> employees = Collections.singletonList(new Employee());

        when(employeeMapper.getEmployeeByPage(20, 20, condition, beginDateScope)).thenReturn(employees);
        when(employeeMapper.getTotal(condition, beginDateScope)).thenReturn(31L);

        RespPageBean result = employeeService.getEmployeeByPage(2, 20, condition, beginDateScope);

        assertEquals(31L, result.getTotal());
        assertEquals(employees, result.getData());
    }

    @Test
    void getEmployeeByPageShouldKeepOriginalWhenPageOrSizeMissing() {
        Employee condition = new Employee();
        List<Employee> employees = Collections.singletonList(new Employee());

        when(employeeMapper.getEmployeeByPage(null, 20, condition, null)).thenReturn(employees);
        when(employeeMapper.getTotal(condition, null)).thenReturn(1L);

        RespPageBean result = employeeService.getEmployeeByPage(null, 20, condition, null);

        assertEquals(1L, result.getTotal());
        assertEquals(employees, result.getData());
    }

    @Test
    void addEmpShouldComputeContractAndSendMailWhenInsertSucceeds() throws ParseException {
        Employee employee = new Employee();
        employee.setId(100);
        employee.setBeginContract(parseDate("2024-01-01"));
        employee.setEndContract(parseDate("2025-01-01"));

        Employee stored = new Employee();
        stored.setId(100);

        when(employeeMapper.insertSelective(employee)).thenReturn(1);
        when(employeeMapper.getEmployeeById(100)).thenReturn(stored);

        int insertResult = employeeService.addEmp(employee);

        assertEquals(1, insertResult);
        assertEquals(1.0, employee.getContractTerm(), 0.0001);

        ArgumentCaptor<MailSendLog> logCaptor = ArgumentCaptor.forClass(MailSendLog.class);
        verify(mailSendLogService).insert(logCaptor.capture());
        MailSendLog sendLog = logCaptor.getValue();
        assertEquals(Integer.valueOf(100), sendLog.getEmpId());
        assertNotNull(sendLog.getMsgId());
        assertTrue(sendLog.getMsgId().length() > 10);

        verify(rabbitTemplate).convertAndSend(
                eq(MailConstants.MAIL_EXCHANGE_NAME),
                eq(MailConstants.MAIL_ROUTING_KEY_NAME),
                eq(stored),
                any(CorrelationData.class)
        );
    }

    @Test
    void addEmpShouldComputeOneAndHalfYearContractTerm() throws ParseException {
        Employee employee = new Employee();
        employee.setId(101);
        employee.setBeginContract(parseDate("2024-01-01"));
        employee.setEndContract(parseDate("2025-07-01"));

        when(employeeMapper.insertSelective(employee)).thenReturn(0);

        int insertResult = employeeService.addEmp(employee);

        assertEquals(0, insertResult);
        assertEquals(1.5, employee.getContractTerm(), 0.0001);
    }

    @Test
    void addEmpShouldNotSendMailWhenInsertFails() throws ParseException {
        Employee employee = new Employee();
        employee.setId(100);
        employee.setBeginContract(parseDate("2024-01-01"));
        employee.setEndContract(parseDate("2025-01-01"));

        when(employeeMapper.insertSelective(employee)).thenReturn(0);

        int insertResult = employeeService.addEmp(employee);

        assertEquals(0, insertResult);
        verify(mailSendLogService, never()).insert(any(MailSendLog.class));
        verify(rabbitTemplate, never()).convertAndSend(
                eq(MailConstants.MAIL_EXCHANGE_NAME),
                eq(MailConstants.MAIL_ROUTING_KEY_NAME),
                any(Employee.class),
                any(CorrelationData.class)
        );
    }

    @Test
    void getEmployeeByPageWithSalaryShouldReturnDataAndTotal() {
        List<Employee> employees = Collections.singletonList(new Employee());
        when(employeeMapper.getEmployeeByPageWithSalary(0, 10)).thenReturn(employees);
        when(employeeMapper.getTotal(null, null)).thenReturn(8L);

        RespPageBean result = employeeService.getEmployeeByPageWithSalary(1, 10);

        assertEquals(8L, result.getTotal());
        assertEquals(employees, result.getData());
    }

    @Test
    void updateEmployeeSalaryByIdShouldDelegateToMapper() {
        when(employeeMapper.updateEmployeeSalaryById(11, 3)).thenReturn(1);

        Integer updated = employeeService.updateEmployeeSalaryById(11, 3);

        assertEquals(1, updated);
        verify(employeeMapper).updateEmployeeSalaryById(11, 3);
    }

    private Date parseDate(String value) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd").parse(value);
    }
}
