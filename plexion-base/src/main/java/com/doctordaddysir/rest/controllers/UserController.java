package com.doctordaddysir.rest.controllers;

import com.doctordaddysir.core.model.ResponseEntity;
import com.doctordaddysir.core.reflection.annotations.*;
import com.doctordaddysir.model.User;
import com.doctordaddysir.services.UserService;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class UserController {
    @Inject
    private final UserService userService;

    @Get
    public ResponseEntity<User> getHealth() {
        return ResponseEntity.ok(new User());
    }
}