package com.escooter.security;


import com.escooter.entity.Role;
import com.escooter.entity.User;
import com.escooter.repository.RoleRepository;
import com.escooter.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final SecurityUserDetailsService userDetailsService;
    private final JwtTokenService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Transactional
    public JwtAuthenticationResponse signUp(SignUpRequest request) {
        Role role = roleRepository.findByName("USER")
                .orElseThrow(() -> new NoSuchElementException("Role not found"));
        var user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .balance(new BigDecimal(0))
                .build();

        User savedUser = userRepository.save(user);

        UserDetails userDetails = new SecurityUser(savedUser);

        var jwt = jwtService.generateToken(userDetails);

        return new JwtAuthenticationResponse(jwt);
    }

    public JwtAuthenticationResponse signIn(SignInRequest request) {

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
        ));

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());

        var jwt = jwtService.generateToken(userDetails);

        return new JwtAuthenticationResponse(jwt);
    }
}
