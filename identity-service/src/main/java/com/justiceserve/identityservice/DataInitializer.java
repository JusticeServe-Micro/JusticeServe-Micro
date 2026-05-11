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
        seedUser("Rahul Citizen",    "citizen@justiceserve.gov", "Citizen@123",User.Role.CITIZEN,    "9999900005");
        seedUser("Audit Officer",    "audit@justiceserve.gov",   "Audit@123",  User.Role.AUDITOR,    "9999900006");
        seedUser("Compliance Officer", "compliance@justiceserve.gov", "Compliance@123", User.Role.COMPLIANCE, "9999900007");

        // New users with Indian names and varied roles
        seedUser("Justice Kumar",    "judge2@justiceserve.gov",  "Judge@123",  User.Role.JUDGE,      "9876500001");
        seedUser("Justice Singh",    "judge3@justiceserve.gov",  "Judge@123",  User.Role.JUDGE,      "9876500002");
        seedUser("Adv. Gupta",       "lawyer2@justiceserve.gov", "Lawyer@123", User.Role.LAWYER,     "9876500003");
        seedUser("Adv. Reddy",       "lawyer3@justiceserve.gov", "Lawyer@123", User.Role.LAWYER,     "9876500004");
        seedUser("Priya Sharma",     "citizen2@justiceserve.gov","Citizen@123",User.Role.CITIZEN,    "9876500005");
        seedUser("Arjun Kumar",      "citizen3@justiceserve.gov","Citizen@123",User.Role.CITIZEN,    "9876500006");
        seedUser("Meera Singh",      "citizen4@justiceserve.gov","Citizen@123",User.Role.CITIZEN,    "9876500007");
        seedUser("Vikram Patel",     "citizen5@justiceserve.gov","Citizen@123",User.Role.CITIZEN,    "9876500008");
        seedUser("Anjali Gupta",     "citizen6@justiceserve.gov","Citizen@123",User.Role.CITIZEN,    "9876500009");
        seedUser("Clerk Singh",      "clerk2@justiceserve.gov",  "Clerk@123",  User.Role.CLERK,      "9876500010");
        seedUser("Audit Kumar",      "audit2@justiceserve.gov",  "Audit@123",  User.Role.AUDITOR,    "9876500011");
        seedUser("Compliance Singh", "compliance2@justiceserve.gov", "Compliance@123", User.Role.COMPLIANCE, "9876500012");
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