package com.devops.releasemgmt.config;

import com.devops.releasemgmt.entity.User;
import com.devops.releasemgmt.entity.enums.Role;
import com.devops.releasemgmt.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!userRepository.existsByUsername("admin")) {
            User admin = User.builder()
                    .username("admin")
                    .email("admin@devops.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .build();
            userRepository.save(admin);
            log.info("Default admin user created (admin / admin123)");
        }

        if (!userRepository.existsByUsername("developer")) {
            User dev = User.builder()
                    .username("developer")
                    .email("dev@devops.com")
                    .password(passwordEncoder.encode("dev123"))
                    .role(Role.DEVELOPER)
                    .build();
            userRepository.save(dev);
            log.info("Default developer user created (developer / dev123)");
        }
    }
}
