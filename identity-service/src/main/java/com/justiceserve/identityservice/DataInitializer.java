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
        // Existing users with phone numbers
        seedUser("System Admin",     "admin@justiceserve.gov",   "Admin@123",  User.Role.ADMIN,      "9999900001");
        seedUser("Justice Sharma",   "judge@justiceserve.gov",   "Judge@123",  User.Role.JUDGE,      "9999900002");
        seedUser("Clerk Patel",      "clerk@justiceserve.gov",   "Clerk@123",  User.Role.CLERK,      "9999900003");
        seedUser("Adv. Mehta",       "lawyer@justiceserve.gov",  "Lawyer@123", User.Role.LAWYER,     "9999900004");
        seedUser("Vishnu Teja",    "citizen@justiceserve.gov", "Citizen@123",User.Role.CITIZEN,    "9999900005");
        seedUser("Auditor Ravi",    "audit@justiceserve.gov",   "Audit@123",  User.Role.AUDITOR,    "9999900006");
        seedUser("Compliance Officer", "compliance@justiceserve.gov", "Compliance@123", User.Role.COMPLIANCE, "9999900007");
    }

    private void seedUser(String name, String email, String rawPassword, User.Role role, String phone) {
        if (!userRepo.existsByEmail(email)) {
            User u = User.builder()
                    .name(name)
                    .email(email)
                    .phone(phone) // Added phone number mapping here
                    .password(encoder.encode(rawPassword))
                    .role(role)
                    .status(User.Status.ACTIVE)
                    .build();
            userRepo.save(u);
            log.info("Seeded user: {} / {} / {} ({})", email, phone, rawPassword, role);
        }
    }
}