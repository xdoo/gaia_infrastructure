package de.muenchen.gaia.auth.controller;

import de.muenchen.gaia.auth.dto.AuthorityDto;
import de.muenchen.gaia.auth.dto.UserDto;
import de.muenchen.gaia.auth.entities.Permission;
import de.muenchen.gaia.auth.entities.User;
import de.muenchen.gaia.auth.mapper.UserMapper;
import de.muenchen.gaia.auth.repositories.PermissionRepository;
import de.muenchen.gaia.auth.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.ArrayList;

/**
 * Created by dennis_huning on 08.12.15.
 */
@Controller
public class UserRestController {

    @Autowired
    UserRepository userRepository;

    UserMapper userMapper = UserMapper.INSTANCE;

    @Autowired
    PermissionRepository permissionRepository;

    /**
     * Aktiviert eine konfortable Möglichkeit den token in lesbare Userinformation umzuwandeln. Kann im ResourceServer über die Property "spring.oauth2.resource.userInfoUri" referenziert werden: spring.oauth2.resource.userInfoUri: http://localhost:9999/uaa/user
     */
    @RequestMapping("/user")
    @ResponseBody
    public UserDto user(Principal user) {
        final User savedUser = userRepository.findFirstByUsername(user.getName());
        Iterable<Permission> allPermissions = permissionRepository.findAll();
        UserDto dto = userMapper.userToUserDto(savedUser);
        if (savedUser.isAdmin()) {
            // The authority ADMIN gets automatically all permissions. Hence no service needs to map the permissions manual.
            AuthorityDto all = new AuthorityDto();
            all.setPermissions(new ArrayList<>());
            all.setAuthority("all");
            allPermissions.forEach(permission -> all.getPermissions().add(permission.getPermission()));
            dto.getAuthorities().addAll(all.getPermissions());
        }
        return dto;
    }
}
