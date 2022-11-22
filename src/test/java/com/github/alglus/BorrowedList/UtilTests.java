package com.github.alglus.BorrowedList;

import com.github.alglus.BorrowedList.models.Role;
import com.github.alglus.BorrowedList.security.CustomUserDetails;
import com.github.alglus.BorrowedList.util.Util;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class UtilTests {

    @Test
    @WithAnonymousUser
    void anonymous_user_is_not_signed_in() {
        assertThat(Util.userIsSignedIn()).isFalse();
        assertThat(Util.userIsNotSignedIn()).isTrue();
    }

    @Test
    @WithUserDetails
    void authenticated_user_is_signed_in() {
        CustomUserDetails userDetails = new CustomUserDetails(
                1, "name@email.com", "password", true, Role.USER);

        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(auth);

        assertThat(Util.userIsSignedIn()).isTrue();
        assertThat(Util.userIsNotSignedIn()).isFalse();
    }
}
