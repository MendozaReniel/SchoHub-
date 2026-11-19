package com.schohub.service;

import com.schohub.model.SessionToken;
import com.schohub.model.Student;
import com.schohub.repository.SessionTokenRepository;
import com.schohub.repository.StudentRepository;
import com.schohub.web.ApiException;
import com.schohub.web.dto.AuthDtos.AuthResponse;
import com.schohub.web.dto.AuthDtos.LoginRequest;
import com.schohub.web.dto.AuthDtos.SignupRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final StudentRepository students;
    private final SessionTokenRepository sessions;
    private final PasswordEncoder passwordEncoder;
    private final StudentMapper mapper;
    private final long ttlDays;

    public AuthService(StudentRepository students,
                       SessionTokenRepository sessions,
                       PasswordEncoder passwordEncoder,
                       StudentMapper mapper,
                       @Value("${app.session.ttl-days:30}") long ttlDays) {
        this.students = students;
        this.sessions = sessions;
        this.passwordEncoder = passwordEncoder;
        this.mapper = mapper;
        this.ttlDays = ttlDays;
    }

    @Transactional
    public AuthResponse signup(SignupRequest request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        String username = request.username().trim();

        if (students.existsByEmailIgnoreCase(email)) {
            throw ApiException.conflict("An account with this email already exists.");
        }
        if (students.existsByUsernameIgnoreCase(username)) {
            throw ApiException.conflict("This username is already taken.");
        }

        Student student = new Student();
        student.setId(UUID.randomUUID().toString());
        student.setEmail(email);
        student.setUsername(username);
        student.setPasswordHash(passwordEncoder.encode(request.password()));
        student.setCreatedAt(Instant.now());
        students.save(student);

        return issueSession(student);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        Student student = students.findByEmailIgnoreCase(request.email().trim())
                .orElseThrow(() -> ApiException.unauthorized("Invalid email or password."));

        if (!passwordEncoder.matches(request.password(), student.getPasswordHash())) {
            throw ApiException.unauthorized("Invalid email or password.");
        }
        return issueSession(student);
    }

    @Transactional
    public void logout(String token) {
        if (token != null) {
            sessions.deleteById(token);
        }
    }

    /** Resolves a bearer token to its student, honouring the expiry date. */
    @Transactional(readOnly = true)
    public Optional<Student> authenticate(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }
        return sessions.findById(token)
                .filter(session -> session.getExpiresAt().isAfter(Instant.now()))
                .map(SessionToken::getStudent);
    }

    @Transactional
    public AuthResponse issueSession(Student student) {
        sessions.deleteExpired(Instant.now());

        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        Instant expiresAt = Instant.now().plus(ttlDays, ChronoUnit.DAYS);

        SessionToken session = new SessionToken();
        session.setToken(token);
        session.setStudent(student);
        session.setCreatedAt(Instant.now());
        session.setExpiresAt(expiresAt);
        session.setLastUsedAt(Instant.now());
        sessions.save(session);

        return new AuthResponse(token, expiresAt, mapper.toResponse(student));
    }
}
