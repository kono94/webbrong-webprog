package de.hsbhv.gamegateway;

public class SecurityConstants {
    public static final String AUTH_LOGIN_URL = "/api/token";

    // Signing key for HS512 algorithm
    //TODO: put this into property file
    public static final String JWT_SECRET = "C*F-JaNdRgUkXp2s5v8y/A?D(G+KbPeShVmYq3t6w9z$C&E)H@McQfTjWnZr4u7x";

    // JWT token defaults
    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String TOKEN_TYPE = "JWT";
    public static final String TOKEN_ISSUER = "webbrong-orchestrator";
    public static final String TOKEN_AUDIENCE = "webbrong";
}
