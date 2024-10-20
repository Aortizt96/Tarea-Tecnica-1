package com.project.demo.logic.entity.rol;

import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.user.UserRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UsersSeeder implements ApplicationListener<ContextRefreshedEvent> {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;


    public UsersSeeder(
            RoleRepository roleRepository,
            UserRepository  userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        this.createSuperAdministrator();
        this.createRegularUser();
    }

    private void createSuperAdministrator() {
            createUser("Super", "Admin", "super.admin@gmail.com", "superadmin123", RoleEnum.SUPER_ADMIN);
    }

    private void createRegularUser() {
        createUser("Regular", "user", "regular.user@gmail.com", "user123", RoleEnum.USER);
    }

    private void createUser(String name, String lastname, String email, String password, RoleEnum roleEnum) {
        Optional<Role> optionalRole = roleRepository.findByName(roleEnum);
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalRole.isEmpty() || optionalUser.isPresent()) {
            return;
        }

        User user = new User();
        user.setName(name);
        user.setLastname(lastname);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(optionalRole.get());

        userRepository.save(user);
    }
}
