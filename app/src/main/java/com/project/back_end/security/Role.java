package com.project.back_end.security;

public final class Role {

    private Role() {
    }

    public static final String ADMIN = "ADMIN";
    public static final String PATIENT = "PATIENT";
    public static final String DOCTOR = "DOCTOR";

    public static boolean matchAuthority(String rawAuthority, String authority) {

        return authority.equals("ROLE_" + rawAuthority);
    }
}
