package x10.trainup.security.core.jwt;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import x10.trainup.commons.domain.entities.RoleEntity;
import x10.trainup.commons.domain.entities.UserEntity;
import x10.trainup.commons.domain.enums.Role;
import x10.trainup.security.core.principal.UserPrincipal;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @org.springframework.beans.factory.annotation.Value("${app.cookie.access-name:access_token}")
    private String accessCookieName;

    private final JwtServiceImpl jwtService;

    // các endpoint công khai mà filter có thể bỏ qua
    private static final Set<String> PUBLIC_PATHS = new HashSet<>(Arrays.asList(
            "/api/auth/sign-in",
            "/api/auth/sign-up",
            "/api/auth/verify-email",
            "/api/auth/refresh-token",
            "/api/auth/forgot-password"
    ));

    public JwtAuthFilter(JwtServiceImpl jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        // Bỏ qua các endpoint công khai
        if (isPublicPath(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = resolveToken(request);

            // Không có token -> bỏ qua (để chain tiếp tục, endpoint sẽ trả 401 nếu cần)
            if (token == null) {
                filterChain.doFilter(request, response);
                return;
            }

            // Validate token -> trả về userId nếu OK
            String userId = jwtService.validateToken(token);
            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                Claims claims = jwtService.extractAllClaims(token);
                List<String> roleNames = claims.get("roles", List.class);

                // map roles -> authorities
                var authorities = roleNames.stream()
                        .map(r -> "ROLE_" + r)
                        .map(org.springframework.security.core.authority.SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

                // build UserPrincipal từ claims (hoặc có thể load DB nếu cần)
                UserEntity userEntity = new UserEntity();
                userEntity.setId(userId);
                userEntity.setUsername(claims.get("username", String.class));
                userEntity.setEmail(claims.get("email", String.class));
                userEntity.setRoles(roleNames.stream()
                        .map(r -> new RoleEntity(null, Role.valueOf(r), null))
                        .toList());

                UserPrincipal principal = UserPrincipal.from(userEntity);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(principal, null, authorities);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            // Không để lỗi JWT làm hỏng request chain (tránh 500)
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    /** Lấy JWT: ưu tiên Bearer header, nếu không có thì lấy từ cookie access_token */
    private String resolveToken(HttpServletRequest request) {
        // 1) Authorization header
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        // 2) HttpOnly cookie
        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                if (accessCookieName.equals(c.getName())) {
                    String v = c.getValue();
                    if (v != null && !v.isBlank()) return v;
                }
            }
        }
        return null;
    }

    private boolean isPublicPath(String uri) {
        // match chính xác hoặc bắt đầu với (tùy routing của bạn)
        if (PUBLIC_PATHS.contains(uri)) return true;
        // ví dụ: cho phép /api/auth/verify-email?token=...
        return uri.startsWith("/api/auth/verify-email");
    }
}
