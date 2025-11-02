package com.gustavo.springsecurity.controller;

import com.gustavo.springsecurity.dto.UserDTO;
import com.gustavo.springsecurity.entities.Role;
import com.gustavo.springsecurity.entities.User;
import com.gustavo.springsecurity.repository.RoleRepository;
import com.gustavo.springsecurity.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(value = "/users")
public class UserController {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final RoleRepository roleRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository, RoleRepository roleRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_admin')")
    public ResponseEntity<List<User>> findAll() {
        List<User> userList = userRepository.findAll();
        return ResponseEntity.ok().body(userList);
    }

    @PostMapping
    @Transactional
    public ResponseEntity<Void> insert(@RequestBody UserDTO userDTO) {
        var basicRole = roleRepository.findByName(Role.Values.BASIC.name().toLowerCase());

        var isInDataBase = userRepository.findByUsername(userDTO.username());

        if (isInDataBase.isPresent()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        User user = new User();
        user.setUsername(userDTO.username());
        user.setPassword(passwordEncoder.encode(userDTO.password()));
        user.setRoles(Set.of(basicRole));

        userRepository.save(user);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(user.getUserId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

}
