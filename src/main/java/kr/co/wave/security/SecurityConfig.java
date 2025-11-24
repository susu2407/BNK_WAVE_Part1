package kr.co.wave.security;

import kr.co.wave.jwt.JwtAuthenticationFilter;
import kr.co.wave.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@Configuration
public class    SecurityConfig {

    private final JwtProvider jwtProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.httpBasic(HttpBasicConfigurer::disable)        // 기본 HTTP 인증 비활성
                .formLogin(FormLoginConfigurer::disable)    // 폼 로그인 비활성
                .sessionManagement(config -> config.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 비활성
                .addFilterBefore(new JwtAuthenticationFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class);

        http.formLogin(form -> form
                .loginPage("/member/login")
                .loginProcessingUrl("/member/login")
                //.defaultSuccessUrl("/", true)
                .successHandler((request, response, authentication) -> {
                    // 권한 중 ROLE_ADMIN으로 시작하는 게 있는지 체크
                    boolean isAdmin = authentication.getAuthorities()
                            .stream()
                            .map(a -> a.getAuthority())
                            .anyMatch(role -> role.startsWith("ROLE_ADMIN"));

                    String requestURI = request.getRequestURI();
                    String ctx = request.getContextPath();

                    // member/login에서 ADMIN 권한이면 로그인 불가
                    if(requestURI.equals("/member/login") && isAdmin) {
                        response.sendRedirect(ctx + "/member/login?error=role");
                        return;
                    }

                    // admin/login에서 ADMIN 권한 없으면 로그인 불가
                    if(requestURI.equals("/admin/login") && !isAdmin) {
                        response.sendRedirect(ctx + "/admin/login?error=role");
                        return;
                    }

                    // 정상 로그인: ADMIN이면 /admin, 아니면 /
                    response.sendRedirect(ctx + (isAdmin ? "/admin" : "/"));
                })
                //.failureUrl("/admin/login?error=true")
                .failureHandler((req, res, ex) -> {
                    // 로그인 실패 시 예외 확인
                    System.out.println("로그인 실패: " + ex.getMessage());

                    // 예: 실패 원인 로그 찍기
                    // ex instanceof BadCredentialsException, LockedException, DisabledException 등 체크 가능

                    // 원하는 페이지로 redirect
                    res.sendRedirect("/member/login?error=true");
                })
                .usernameParameter("memId")
                .passwordParameter("password")
        );

        // 인가 설정
        http.authorizeHttpRequests(authorize -> authorize
                /*
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/manager/**").hasAnyRole("ADMIN", "MANAGER")
                .requestMatchers("/member/**").hasAnyRole("ADMIN", "MANAGER", "MEMBER")
                .requestMatchers("/guest/**").permitAll()
                .requestMatchers("/user/**").hasAnyRole("ADMIN", "MANAGER")
                 */
                .anyRequest().permitAll()
        );

        // 기타 설정
        http.csrf(CsrfConfigurer::disable);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}