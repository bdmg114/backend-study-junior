package com.gdgku.study.backend;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final List<User> userList = new ArrayList<>();
    private long nextId = 1L;

    public static class User {
        private Long id;
        private String name;
        private String email;

        public User() {}
        public User(Long id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        user.setId(nextId++);
        userList.add(user);
        return user;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userList;
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        for (User u : userList) {
            if (u.getId().equals(id)) {
                return u;
            }
        }
        return null;
    }

    @GetMapping("/param")
    public String getUserByQueryParam(@RequestParam String name) {
        return "Query Param name: " + name;
    }
}
