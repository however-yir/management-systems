package io.howeveryir.talentflow.service;

import io.howeveryir.talentflow.mapper.HrMapper;
import io.howeveryir.talentflow.mapper.HrRoleMapper;
import io.howeveryir.talentflow.model.Hr;
import io.howeveryir.talentflow.model.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HrServiceTest {

    @Mock
    private HrMapper hrMapper;

    @Mock
    private HrRoleMapper hrRoleMapper;

    @InjectMocks
    private HrService hrService;

    @Test
    void loadUserByUsernameShouldPopulateRoles() {
        Hr hr = new Hr();
        hr.setId(3);
        hr.setUsername("admin");
        hr.setPassword("secret");
        hr.setEnabled(true);

        Role role = new Role();
        role.setId(6);
        role.setName("ROLE_admin");

        when(hrMapper.loadUserByUsername("admin")).thenReturn(hr);
        when(hrMapper.getHrRolesById(3)).thenReturn(Collections.singletonList(role));

        UserDetails details = hrService.loadUserByUsername("admin");

        assertEquals("admin", details.getUsername());
        assertEquals(1, ((Hr) details).getRoles().size());
        assertEquals("ROLE_admin", ((Hr) details).getRoles().get(0).getName());
        verify(hrMapper).getHrRolesById(3);
    }

    @Test
    void loadUserByUsernameShouldThrowWhenUserMissing() {
        when(hrMapper.loadUserByUsername("ghost")).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> hrService.loadUserByUsername("ghost"));
    }

    @Test
    void updateHrRoleShouldReturnTrueWhenInsertedCountMatches() {
        Integer[] rids = new Integer[]{1, 2, 3};
        when(hrRoleMapper.addRole(8, rids)).thenReturn(3);

        boolean updated = hrService.updateHrRole(8, rids);

        assertTrue(updated);
        verify(hrRoleMapper).deleteByHrid(8);
    }

    @Test
    void updateHrRoleShouldReturnFalseWhenInsertedCountNotMatch() {
        Integer[] rids = new Integer[]{1, 2, 3};
        when(hrRoleMapper.addRole(8, rids)).thenReturn(2);

        boolean updated = hrService.updateHrRole(8, rids);

        assertFalse(updated);
    }

    @Test
    void updateHrPasswdShouldReturnTrueWhenOldPasswordMatches() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        Hr hr = new Hr();
        hr.setId(5);
        hr.setPassword(encoder.encode("old-pass"));

        when(hrMapper.selectByPrimaryKey(5)).thenReturn(hr);
        when(hrMapper.updatePasswd(eq(5), anyString())).thenReturn(1);

        boolean updated = hrService.updateHrPasswd("old-pass", "new-pass", 5);

        assertTrue(updated);
        ArgumentCaptor<String> passwordCaptor = ArgumentCaptor.forClass(String.class);
        verify(hrMapper).updatePasswd(eq(5), passwordCaptor.capture());
        assertTrue(encoder.matches("new-pass", passwordCaptor.getValue()));
    }

    @Test
    void updateHrPasswdShouldReturnFalseWhenOldPasswordNotMatch() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        Hr hr = new Hr();
        hr.setId(5);
        hr.setPassword(encoder.encode("old-pass"));

        when(hrMapper.selectByPrimaryKey(5)).thenReturn(hr);

        boolean updated = hrService.updateHrPasswd("wrong-pass", "new-pass", 5);

        assertFalse(updated);
        verify(hrMapper, never()).updatePasswd(eq(5), anyString());
    }

    @Test
    void updateHrPasswdShouldReturnFalseWhenUpdateCountNotOne() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        Hr hr = new Hr();
        hr.setId(5);
        hr.setPassword(encoder.encode("old-pass"));

        when(hrMapper.selectByPrimaryKey(5)).thenReturn(hr);
        when(hrMapper.updatePasswd(eq(5), anyString())).thenReturn(0);

        boolean updated = hrService.updateHrPasswd("old-pass", "new-pass", 5);

        assertFalse(updated);
    }
}
