package site.yuanshen.gtbp.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import reactor.core.publisher.Mono;
import site.yuanshen.gtbp.handler.BasicAuthenticationSuccessHandler;

/**
 * Security 配置
 *
 * @author Moment
 */
@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    private final UserDetailService userDetailService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    /**
     * 用户鉴权设置，加入jwt过滤器
     *
     * @param http http过滤器链的构造对象
     * @return 过滤器链
     **/
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .cors().disable()
                .csrf().disable()
                .authorizeExchange()
                .pathMatchers("/", "/user/register").permitAll()
                .pathMatchers("/user/login")
                .authenticated()
                .and()
                .addFilterAt(basicAuthenticationFilter(), SecurityWebFiltersOrder.HTTP_BASIC)
                .authorizeExchange()
                .pathMatchers("/auth/test", "/local/ws")
                .authenticated()
                .and()
                .addFilterAt(bearerAuthenticationFilter(), SecurityWebFiltersOrder.AUTHENTICATION);

        return http.build();
    }


    /**
     * 使用已在 AuthenticationWebFilter 中实现的逻辑，并设置一个自定义 SuccessHandler，
     * 该处理程序将在使用用户密码进行验证时，返回 JWT 使用上面定义的 UserDetailsService 创建身份验证管理器
     *
     * @return AuthenticationWebFilter
     */
    private AuthenticationWebFilter basicAuthenticationFilter() {
        UserDetailsRepositoryReactiveAuthenticationManager authManager;
        AuthenticationWebFilter basicAuthenticationFilter;
        ServerAuthenticationSuccessHandler successHandler;

        authManager = new UserDetailsRepositoryReactiveAuthenticationManager(userDetailService);
        successHandler = new BasicAuthenticationSuccessHandler();

        basicAuthenticationFilter = new AuthenticationWebFilter(authManager);
        basicAuthenticationFilter.setAuthenticationSuccessHandler(successHandler);

        return basicAuthenticationFilter;

    }

    /**
     * 使用 AuthenticationWebFilter 已实现的逻辑，并设置一个自定义转换器，
     * 该转换器将处理在 HTTP 授权标头中包含持有者令牌的请求。
     * <p>
     * 为此过滤器设置虚拟身份验证管理器，它不是必需的，因为转换器会处理此问题。
     *
     * @return bearerAuthenticationFilter 将对包含 JWT 的请求进行验证
     */
    private AuthenticationWebFilter bearerAuthenticationFilter() {
        AuthenticationWebFilter bearerAuthenticationFilter;
        bearerAuthenticationFilter = new AuthenticationWebFilter((ReactiveAuthenticationManager) Mono::just);

        bearerAuthenticationFilter.setServerAuthenticationConverter(new ServerHttpBearerAuthenticationConverter());
        bearerAuthenticationFilter.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers("/auth/test", "/local/ws"));

        return bearerAuthenticationFilter;
    }

}
