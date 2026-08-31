# Single Responsibility Principle (SRP)

> **"S"** in **SOLID**

---

## Definition

> **"A class should have only ONE reason to change."**

Coined by Robert C. Martin.

In plain English: **Every class should do exactly one thing and own that one thing completely.**

> Note: "reason to change" is key. It does not mean one method or one line.
> It means **one actor, one concern, one responsibility** owns that class.

---

## Why It Matters in Real Projects

SRP is the **most commonly violated** of all SOLID principles -- usually not out of laziness,
but because responsibilities creep in slowly. A service starts handling one thing, then someone
adds logging, then validation, then email sending... and before you know it you have a 1000-line
God class that nobody dares touch.

The cost of that is:
- **Merge conflicts** -- 3 developers editing the same class for 3 unrelated reasons
- **Test explosion** -- one class with 10 responsibilities needs 50 test cases
- **Fragile changes** -- touching the email logic accidentally breaks the DB logic

---

## The Problem Without SRP

**Use Case: User Registration System**

One class doing everything -- user persistence, password hashing, email notification, and audit logging.

```java
// This class has 5 reasons to change -- violates SRP badly
public class UserService {

    public void registerUser(String name, String email, String password) {

        // 1. Validate input
        if (name == null || email == null || password.length() < 8) {
            throw new IllegalArgumentException("Invalid input");
        }

        // 2. Hash the password
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        // 3. Save to database
        String sql = "INSERT INTO users (name, email, password) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/db", "root", "pass");
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, hashedPassword);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // 4. Send welcome email
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        Session session = Session.getInstance(props);
        try {
            Message msg = new MimeMessage(session);
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            msg.setSubject("Welcome!");
            msg.setText("Hi " + name + ", welcome aboard!");
            Transport.send(msg);
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        // 5. Write audit log
        try (FileWriter fw = new FileWriter("audit.log", true)) {
            fw.write(LocalDateTime.now() + " - User registered: " + email + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

### How many reasons to change?

| Responsibility | Who demands the change |
|---|---|
| Input validation rules change | Business team |
| Password hashing algorithm changes | Security team |
| Database / ORM changes | DBA / infrastructure team |
| Email template or SMTP config changes | Marketing team |
| Log format or destination changes | DevOps / observability team |

**5 different teams, 5 different reasons to change -- all in one class.** That is SRP violated 4 times over.

---

## The Fix -- Apply SRP

Split each responsibility into its own focused class.

### Each class owns exactly one concern

```java
// 1. Validation -- owned by business rules team
public class UserValidator {
    public void validate(String name, String email, String password) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Name is required");
        if (email == null || !email.contains("@"))
            throw new IllegalArgumentException("Valid email is required");
        if (password == null || password.length() < 8)
            throw new IllegalArgumentException("Password must be at least 8 characters");
    }
}

// 2. Password encoding -- owned by security team
public class PasswordEncoder {
    public String encode(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
    }
}

// 3. Persistence -- owned by data/infra team
@Repository
public class UserRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void save(User user) {
        jdbcTemplate.update(
            "INSERT INTO users (name, email, password) VALUES (?, ?, ?)",
            user.getName(), user.getEmail(), user.getPassword()
        );
    }
}

// 4. Email -- owned by marketing/comms team
@Service
public class EmailService {
    public void sendWelcome(String name, String email) {
        System.out.println("Sending welcome email to: " + email);
        // JavaMail / SendGrid / SES integration here
    }
}

// 5. Audit logging -- owned by DevOps/observability team
@Component
public class AuditLogger {
    private static final Logger log = LoggerFactory.getLogger(AuditLogger.class);

    public void log(String message) {
        log.info("[AUDIT] {} - {}", LocalDateTime.now(), message);
    }
}
```

### UserService now has ONE job -- orchestrate registration

```java
@Service
public class UserService {

    private final UserValidator   validator;
    private final PasswordEncoder encoder;
    private final UserRepository  userRepository;
    private final EmailService    emailService;
    private final AuditLogger     auditLogger;

    // Constructor injection -- DIP in action
    public UserService(UserValidator validator,
                       PasswordEncoder encoder,
                       UserRepository userRepository,
                       EmailService emailService,
                       AuditLogger auditLogger) {
        this.validator      = validator;
        this.encoder        = encoder;
        this.userRepository = userRepository;
        this.emailService   = emailService;
        this.auditLogger    = auditLogger;
    }

    public void registerUser(String name, String email, String password) {
        validator.validate(name, email, password);                  // delegate
        String hashed = encoder.encode(password);                   // delegate
        userRepository.save(new User(name, email, hashed));         // delegate
        emailService.sendWelcome(name, email);                      // delegate
        auditLogger.log("User registered: " + email);               // delegate
    }
    // ONE reason to change: the registration WORKFLOW changes
}
```

---

## Unit Testing -- Laser Focused

```java
@Test
public void testValidator_throwsOnShortPassword() {
    UserValidator validator = new UserValidator();
    assertThrows(IllegalArgumentException.class,
        () -> validator.validate("John", "john@test.com", "short"));
}

@Test
public void testPasswordEncoder_returnsHashedValue() {
    PasswordEncoder encoder = new PasswordEncoder();
    String hash = encoder.encode("mypassword");
    assertNotEquals("mypassword", hash);
    assertTrue(BCrypt.checkpw("mypassword", hash));
}

@Test
public void testUserService_orchestratesAllSteps() {
    // All collaborators mocked -- UserService has NOTHING to test but orchestration
    UserValidator   mockValidator  = mock(UserValidator.class);
    PasswordEncoder mockEncoder    = mock(PasswordEncoder.class);
    UserRepository  mockRepo       = mock(UserRepository.class);
    EmailService    mockEmail      = mock(EmailService.class);
    AuditLogger     mockLogger     = mock(AuditLogger.class);

    when(mockEncoder.encode("pass1234")).thenReturn("hashed");

    UserService service = new UserService(
        mockValidator, mockEncoder, mockRepo, mockEmail, mockLogger);

    service.registerUser("John", "john@test.com", "pass1234");

    verify(mockValidator).validate("John", "john@test.com", "pass1234");
    verify(mockEncoder).encode("pass1234");
    verify(mockRepo).save(any(User.class));
    verify(mockEmail).sendWelcome("John", "john@test.com");
    verify(mockLogger).log(contains("john@test.com"));
}
```

---

## SRP at the Package Level Too

SRP applies beyond just classes -- apply it at the **package level** too:

```
com.myapp
├── validation/        <- UserValidator, OrderValidator
├── security/          <- PasswordEncoder, TokenService
├── repository/        <- UserRepository, OrderRepository
├── notification/      <- EmailService, SMSService
├── audit/             <- AuditLogger
└── service/           <- UserService (orchestrator only)
```

Each package has one reason to change. When the security team asks for a change, go to `security/`.
When marketing changes the email template, go to `notification/`. Zero cross-cutting noise.

---

## Before vs After

| Before SRP | After SRP |
|---|---|
| 1 class, 5 responsibilities, 200+ lines | 5 classes, each under 30 lines |
| Marketing change risks breaking DB logic | Each concern is fully isolated |
| One 50-case test class | 5 focused test classes, each 5-10 tests |
| 3 developers conflict on same file | Each team owns their class -- no merge conflicts |
| Cannot mock email without real SMTP | Each dependency easily mocked in isolation |

---

## Key Insight

> The real signal for an SRP violation is when you describe a class with the word **"and"**.
>
> *"This class validates users **and** hashes passwords **and** sends emails."*
>
> The moment you say **"and"**, you have at least two responsibilities.
> Stop, split, and make your future self -- and your teammates -- thank you.
