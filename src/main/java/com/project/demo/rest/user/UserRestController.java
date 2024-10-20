//package com.project.demo.rest.user;
//
//import com.project.demo.logic.entity.user.User;
//import com.project.demo.logic.entity.user.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/users")
//public class UserRestController {
//    @Autowired
//    private UserRepository UserRepository;
//
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @GetMapping
//    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
//    public List<User> getAllUsers() {
//
//        return UserRepository.findAll();
//    }
//
//    @PostMapping
//    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
//    public User addUser(@RequestBody User user) {
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
//        return UserRepository.save(user);
//
//    }
//
//    @GetMapping("/{id}")
//    public User getUserById(@PathVariable Long id) {
//        return UserRepository.findById(id).orElseThrow(RuntimeException::new);
//    }
//
//    @GetMapping("/filterByName/{name}")
//    public List<User> getUserById(@PathVariable String name) {
//        return UserRepository.findUsersWithCharacterInName(name);
//    }
//
//    @PutMapping("/{id}")
//    public User updateUser(@PathVariable Long id, @RequestBody User user) {
//        return UserRepository.findById(id)
//                .map(existingUser -> {
//                    existingUser.setName(user.getName());
//                    existingUser.setLastname(user.getLastname());
//                    existingUser.setEmail(user.getEmail());
//                    return UserRepository.save(existingUser);
//                })
//                .orElseGet(() -> {
//                    user.setId(id);
//                    return UserRepository.save(user);
//                });
//    }
//
//    @DeleteMapping("/{id}")
//    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
//    public void deleteUser(@PathVariable Long id) {
//        UserRepository.deleteById(id);
//    }
//
//    @GetMapping("/me")
//    @PreAuthorize("isAuthenticated()")
//    public User authenticatedUser() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        return (User) authentication.getPrincipal();
//    }
//
//}


package com.project.demo.rest.user;

import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.http.Meta;
import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserRestController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        Pageable pageable = PageRequest.of(page-1, size);
        Page<User> ordersPage = userRepository.findAll(pageable);
        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        meta.setTotalPages(ordersPage.getTotalPages());
        meta.setTotalElements(ordersPage.getTotalElements());
        meta.setPageNumber(ordersPage.getNumber() + 1);
        meta.setPageSize(ordersPage.getSize());
        return new GlobalResponseHandler().handleResponse("Users retrieved successfully",
                ordersPage.getContent(), HttpStatus.OK, meta);
    }


    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> addUser(@RequestBody User user, HttpServletRequest request) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return new GlobalResponseHandler().handleResponse("User updated successfully",
                user, HttpStatus.OK, request);
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> updateUser(@PathVariable Long userId, @RequestBody User user, HttpServletRequest request) {
        Optional<User> foundOrder = userRepository.findById(userId);
        if(foundOrder.isPresent()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
            return new GlobalResponseHandler().handleResponse("User updated successfully",
                    user, HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("User id " + userId + " not found"  ,
                    HttpStatus.NOT_FOUND, request);
        }
    }


    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId, HttpServletRequest request) {
        Optional<User> foundOrder = userRepository.findById(userId);
        if(foundOrder.isPresent()) {
            userRepository.deleteById(userId);
            return new GlobalResponseHandler().handleResponse("User deleted successfully",
                    foundOrder.get(), HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Order id " + userId + " not found"  ,
                    HttpStatus.NOT_FOUND, request);
        }
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public User authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }

}