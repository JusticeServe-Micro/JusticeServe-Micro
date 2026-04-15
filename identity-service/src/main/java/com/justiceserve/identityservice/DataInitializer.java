package com.justiceserve.identityservice;

import com.justiceserve.identityservice.entity.User;
import com.justiceserve.identityservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepo;
    private final PasswordEncoder encoder;

    @Override
    public void run(String... args) {
        seedUser("System Admin",     "admin@justiceserve.gov",   "Admin@123",  User.Role.ADMIN);
        seedUser("Justice Sharma",   "judge@justiceserve.gov",   "Judge@123",  User.Role.JUDGE);
        seedUser("Clerk Patel",      "clerk@justiceserve.gov",   "Clerk@123",  User.Role.CLERK);
        seedUser("Adv. Mehta",       "lawyer@justiceserve.gov",  "Lawyer@123", User.Role.LAWYER);
        seedUser("Rahul Citizen",    "citizen@justiceserve.gov", "Citizen@123",User.Role.CITIZEN);
        seedUser("Audit Officer",    "audit@justiceserve.gov",   "Audit@123",  User.Role.AUDITOR);
    }

    private void seedUser(String name, String email, String rawPassword, User.Role role) {
        if (!userRepo.existsByEmail(email)) {
            User u = User.builder()
                    .name(name).email(email)
                    .password(encoder.encode(rawPassword))
                    .role(role).status(User.Status.ACTIVE)
                    .build();
            userRepo.save(u);
            log.info("Seeded user: {} / {} ({})", email, rawPassword, role);
        }
    }
}
