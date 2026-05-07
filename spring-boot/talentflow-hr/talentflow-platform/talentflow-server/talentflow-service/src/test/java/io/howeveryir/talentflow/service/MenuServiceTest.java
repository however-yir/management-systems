package io.howeveryir.talentflow.service;

import io.howeveryir.talentflow.mapper.MenuMapper;
import io.howeveryir.talentflow.mapper.MenuRoleMapper;
import io.howeveryir.talentflow.model.Hr;
import io.howeveryir.talentflow.model.Menu;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    @Mock
    private MenuMapper menuMapper;

    @Mock
    private MenuRoleMapper menuRoleMapper;

    @InjectMocks
    private MenuService menuService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getMenusByHrIdShouldReadCurrentPrincipal() {
        Hr hr = new Hr();
        hr.setId(9);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(hr, null, Collections.emptyList())
        );

        Menu menu = new Menu();
        menu.setId(1);
        menu.setName("系统管理");
        when(menuMapper.getMenusByHrId(9)).thenReturn(Collections.singletonList(menu));

        List<Menu> menus = menuService.getMenusByHrId();

        assertEquals(1, menus.size());
        assertSame(menu, menus.get(0));
    }

    @Test
    void updateMenuRoleShouldReturnTrueWhenMidListIsNull() {
        boolean updated = menuService.updateMenuRole(6, null);

        assertTrue(updated);
        verify(menuRoleMapper).deleteByRid(6);
        verify(menuRoleMapper, never()).insertRecord(org.mockito.ArgumentMatchers.anyInt(), org.mockito.ArgumentMatchers.any());
    }

    @Test
    void updateMenuRoleShouldReturnTrueWhenMidListIsEmpty() {
        boolean updated = menuService.updateMenuRole(6, new Integer[0]);

        assertTrue(updated);
        verify(menuRoleMapper).deleteByRid(6);
        verify(menuRoleMapper, never()).insertRecord(org.mockito.ArgumentMatchers.anyInt(), org.mockito.ArgumentMatchers.any());
    }

    @Test
    void updateMenuRoleShouldReturnTrueWhenInsertCountMatches() {
        Integer[] mids = new Integer[]{7, 8};
        when(menuRoleMapper.insertRecord(6, mids)).thenReturn(2);

        boolean updated = menuService.updateMenuRole(6, mids);

        assertTrue(updated);
        verify(menuRoleMapper).deleteByRid(6);
    }

    @Test
    void updateMenuRoleShouldReturnFalseWhenInsertCountMismatch() {
        Integer[] mids = new Integer[]{7, 8, 9};
        when(menuRoleMapper.insertRecord(6, mids)).thenReturn(2);

        boolean updated = menuService.updateMenuRole(6, mids);

        assertFalse(updated);
    }
}
