package org.springframework.samples.petclinic.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.model.Role;
import org.springframework.samples.petclinic.model.User;
import org.springframework.samples.petclinic.rest.dto.RoleDto;
import org.springframework.samples.petclinic.rest.dto.UserDto;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link UserMapper}.
 */
class UserMapperTests {

    private final UserMapper userMapper = new UserMapperImpl();

    @Test
    void shouldMapNullRoleDtoToNullRole() {
        assertNull(userMapper.toRole(null));
    }

    @Test
    void shouldMapRoleDtoToRoleIgnoringIdAndUser() {
        RoleDto roleDto = new RoleDto("admin");

        Role role = userMapper.toRole(roleDto);

        assertNotNull(role);
        assertNull(role.getId());
        assertNull(role.getUser());
        assertEquals("admin", role.getName());
    }

    @Test
    void shouldMapNullRoleToNullRoleDto() {
        assertNull(userMapper.toRoleDto(null));
    }

    @Test
    void shouldMapRoleToRoleDto() {
        Role role = new Role();
        role.setId(1);
        role.setName("admin");

        RoleDto roleDto = userMapper.toRoleDto(role);

        assertNotNull(roleDto);
        assertEquals("admin", roleDto.name());
    }

    @Test
    void shouldMapNullRolesCollectionToNull() {
        assertNull(userMapper.toRoleDtos(null));
    }

    @Test
    void shouldMapEmptyRolesCollectionToEmptyCollection() {
        Collection<RoleDto> roleDtos = userMapper.toRoleDtos(List.of());

        assertNotNull(roleDtos);
        assertTrue(roleDtos.isEmpty());
    }

    @Test
    void shouldMapRolesCollectionToRoleDtos() {
        Role admin = new Role();
        admin.setId(1);
        admin.setName("admin");

        Role vet = new Role();
        vet.setId(2);
        vet.setName("vet");

        Collection<RoleDto> roleDtos = userMapper.toRoleDtos(Arrays.asList(admin, vet));

        assertNotNull(roleDtos);
        assertEquals(2, roleDtos.size());
        assertTrue(roleDtos.stream().anyMatch(r -> "admin".equals(r.name())));
        assertTrue(roleDtos.stream().anyMatch(r -> "vet".equals(r.name())));
    }

    @Test
    void shouldMapNullRoleDtosToNullRoles() {
        assertNull(userMapper.toRoles(null));
    }

    @Test
    void shouldMapEmptyRoleDtosToEmptyRoles() {
        Collection<Role> roles = userMapper.toRoles(List.of());

        assertNotNull(roles);
        assertTrue(roles.isEmpty());
    }

    @Test
    void shouldMapRoleDtosCollectionToRoles() {
        RoleDto admin = new RoleDto("admin");
        RoleDto vet = new RoleDto("vet");

        Collection<Role> roles = userMapper.toRoles(Arrays.asList(admin, vet));

        assertNotNull(roles);
        assertEquals(2, roles.size());
        assertTrue(roles.stream().anyMatch(r -> "admin".equals(r.getName())));
        assertTrue(roles.stream().anyMatch(r -> "vet".equals(r.getName())));
    }

    @Test
    void shouldMapNullUserDtoToNullUser() {
        assertNull(userMapper.toUser(null));
    }

    @Test
    void shouldMapUserDtoToUser() {
        UserDto userDto = new UserDto("john.doe", "1234abc", true, List.of(new RoleDto("admin")));

        User user = userMapper.toUser(userDto);

        assertNotNull(user);
        assertEquals("john.doe", user.getUsername());
        assertEquals("1234abc", user.getPassword());
        assertTrue(user.getEnabled());
        assertNotNull(user.getRoles());
        assertEquals(1, user.getRoles().size());
    }

    @Test
    void shouldMapUserDtoWithNullRolesToUserWithEmptyRoles() {
        UserDto userDto = new UserDto("john.doe", "1234abc", true, null);

        User user = userMapper.toUser(userDto);

        assertNotNull(user);
        assertNotNull(user.getRoles());
        assertTrue(user.getRoles().isEmpty());
    }

    @Test
    void shouldMapNullUserToNullUserDto() {
        assertNull(userMapper.toUserDto(null));
    }

    @Test
    void shouldMapUserToUserDto() {
        User user = new User();
        user.setUsername("john.doe");
        user.setPassword("1234abc");
        user.setEnabled(true);
        user.addRole("admin");

        UserDto userDto = userMapper.toUserDto(user);

        assertNotNull(userDto);
        assertEquals("john.doe", userDto.username());
        assertEquals("1234abc", userDto.password());
        assertTrue(userDto.enabled());
        assertNotNull(userDto.roles());
        assertEquals(1, userDto.roles().size());
        assertEquals("admin", userDto.roles().get(0).name());
    }

    @Test
    void shouldMapUserWithNullRolesToUserDtoWithEmptyRoles() {
        User user = new User();
        user.setUsername("john.doe");

        UserDto userDto = userMapper.toUserDto(user);

        assertNotNull(userDto);
        assertNotNull(userDto.roles());
        assertTrue(userDto.roles().isEmpty());
    }
}
