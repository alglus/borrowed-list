package com.github.alglus.BorrowedList.util;


import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class Util {

    public static boolean userIsSignedIn() {
        return !userIsNotSignedIn();
    }

    public static boolean userIsNotSignedIn() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }
}
