package com.payment.paymentapp.controller;

import com.payment.paymentapp.auth.AuthenticationResponse;
import com.payment.paymentapp.auth.JwtUtil;
import com.payment.paymentapp.domain.Role;
import com.payment.paymentapp.domain.User;
import com.payment.paymentapp.domain.VerificationToken;
import com.payment.paymentapp.dto.LogInRequest;
import com.payment.paymentapp.dto.UserDTO;
import com.payment.paymentapp.mapper.UserDTOMapper;
import com.payment.paymentapp.service.EmailService;
import com.payment.paymentapp.service.UserService;
import com.payment.paymentapp.service.VerificationTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@Validated
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private VerificationTokenService verificationTokenService;

    @Autowired
    private UserDTOMapper userDTOMapper;


    @PostMapping(value="/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody UserDTO userRegistrationDTO, HttpServletRequest request)
    {

        if(userService.isEmailExist(userRegistrationDTO.email)) {
            return new ResponseEntity<>("Email exits", HttpStatus.BAD_REQUEST);
        }

        User savedUser = new User();
        savedUser.setEmail(userRegistrationDTO.email);
        savedUser.setName(userRegistrationDTO.name);
        savedUser.setSurname(userRegistrationDTO.surname);
        savedUser.setRole(Role.USER);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = encoder.encode(userRegistrationDTO.password);
        savedUser.setPassword(hash);
        savedUser.setVerified(false);
        UserDTO userDTO = new UserDTO(savedUser);
        System.out.println(userDTO.name + userDTO.email);
        try{
            userService.save(savedUser);
            String token = UUID.randomUUID().toString();
            VerificationToken verificationToken = new VerificationToken();
            verificationToken.setToken(token);
            verificationToken.setUser(savedUser);
            verificationToken.setExpiryDate(LocalDateTime.now().plusHours(24));
            verificationToken.setUsed(false);

            verificationTokenService.save(verificationToken);

            String activationLink = "http://localhost:8080/api/users/verify?token=" + token;
            emailService.sendVerificationEmail(userDTO, activationLink);
            return new ResponseEntity<>("User successefully created", HttpStatus.CREATED);

        }catch (Exception e){
            return new ResponseEntity<>("Cannot send verification email",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value="/login")
    public ResponseEntity<?> login(@Valid @RequestBody LogInRequest logInRequest, HttpServletRequest request){
        String ipAddress = getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");

        User user = userService.findByEmail(logInRequest.getEmail());
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        if(user==null) {
            return new ResponseEntity<>("Unsuccessful login - user doesn't exist",HttpStatus.NOT_FOUND);
        }

        if (!encoder.matches(logInRequest.getPassword(), user.getPassword())) {
            return new ResponseEntity<>("Unsuccessful login - wrong password", HttpStatus.UNAUTHORIZED);
        }

        if (!user.isVerified()) {
            return new ResponseEntity<>("Unsuccessful login - mail not verified", HttpStatus.NOT_ACCEPTABLE);
        }
        String token = jwtUtil.generateToken(user.getId(), user.getRole());
        AuthenticationResponse response = new AuthenticationResponse(token);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }
}
