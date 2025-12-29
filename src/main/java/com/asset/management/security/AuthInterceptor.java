package com.asset.management.security;

import com.asset.management.dto.AuthUser;
import com.asset.management.dto.Result;
import com.asset.management.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 登录与权限拦截器
 */
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private static final Set<String> PUBLIC_PATHS = Set.of(
            "/",
            "/auth/login",
            "/error"
    );
    private static final Pattern STATIC_RESOURCE = Pattern.compile(
            ".*\\.(css|js|html|png|jpg|jpeg|svg|ico|map)$",
            Pattern.CASE_INSENSITIVE
    );

    private final AuthService authService;
    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String path = resolvePath(request);
        if (isPublicPath(path)) {
            return true;
        }

        String token = resolveToken(request);
        if (token == null || token.isBlank()) {
            writeError(response, 401, "未登录");
            return false;
        }

        AuthUser user = authService.getAuthUser(token);
        if (user == null) {
            writeError(response, 401, "登录已失效");
            return false;
        }

        if (!isAllowed(user.getRole(), request.getMethod(), path)) {
            writeError(response, 403, "权限不足");
            return false;
        }

        request.setAttribute("authUser", user);
        request.setAttribute("authToken", token);
        return true;
    }

    private boolean isPublicPath(String path) {
        if (PUBLIC_PATHS.contains(path)) {
            return true;
        }
        return STATIC_RESOURCE.matcher(path).matches();
    }

    private boolean isAllowed(String role, String method, String path) {
        if (role != null && "ADMIN".equalsIgnoreCase(role)) {
            return true;
        }
        if (path.startsWith("/auth/")) {
            return true;
        }
        if (path.startsWith("/users")) {
            return false;
        }
        if ("GET".equalsIgnoreCase(method)) {
            return true;
        }
        if ("POST".equalsIgnoreCase(method) && "/allocations".equals(path)) {
            return true;
        }
        return "POST".equalsIgnoreCase(method) && "/allocations/check-conflict".equals(path);
    }

    private String resolveToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.toLowerCase(Locale.ROOT).startsWith("bearer ")) {
            return header.substring(7).trim();
        }
        String token = request.getHeader("X-Auth-Token");
        if (token != null && !token.isBlank()) {
            return token.trim();
        }
        return null;
    }

    private String resolvePath(HttpServletRequest request) {
        String path = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (contextPath != null && !contextPath.isEmpty() && path.startsWith(contextPath)) {
            path = path.substring(contextPath.length());
        }
        if (path == null || path.isEmpty()) {
            return "/";
        }
        return path;
    }

    private void writeError(HttpServletResponse response, int code, String message) throws IOException {
        response.setStatus(code);
        response.setContentType("application/json;charset=UTF-8");
        Result<Void> result = Result.error(code, message);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
