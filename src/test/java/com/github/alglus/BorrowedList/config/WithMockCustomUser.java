package com.github.alglus.BorrowedList.config;

import com.github.alglus.BorrowedList.models.Role;
import com.github.alglus.BorrowedList.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithSecurityContext;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Import(TestsConfig.class)
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUser.WithMockCustomUserSecurityContextFactory.class)
public @interface WithMockCustomUser {

    long id() default 1;

    String email() default "name@example.com";

    String password() default "password";

    boolean isEmailVerified() default true;

    Role role() default Role.USER;


    class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {

        private final PasswordEncoder passwordEncoder;

        @Autowired
        public WithMockCustomUserSecurityContextFactory(PasswordEncoder passwordEncoder) {
            this.passwordEncoder = passwordEncoder;
        }

        @Override
        public SecurityContext createSecurityContext(WithMockCustomUser customUser) {
            SecurityContext context = SecurityContextHolder.createEmptyContext();

            CustomUserDetails principal = new CustomUserDetails(
                    customUser.id(), customUser.email(), passwordEncoder.encode(customUser.password()),
                    customUser.isEmailVerified(), customUser.role());

            Authentication auth = new UsernamePasswordAuthenticationToken(
                    principal, principal.getPassword(), principal.getAuthorities());

            context.setAuthentication(auth);

            return context;
        }
    }
}
