package site.yuanshen.gtbp.handler;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import site.yuanshen.gtbp.auth.JWTTokenService;

/**
 * 成功身份验证时，已签名的 JWT 对象被序列化并作为持有者令牌添加到授权标头中
 */
@Component
public class BasicAuthenticationSuccessHandler
        implements ServerAuthenticationSuccessHandler {

    /**
     * 我们用于创建 JWT 对象的成功身份验证对象，并添加到当前 WebExchange 的授权标头中
     *
     * @param webFilterExchange
     * @param authentication
     * @return
     */
    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        ServerWebExchange exchange = webFilterExchange.getExchange();
        //TODO refactor this nasty implementation
        exchange.getResponse()
                .getHeaders()
                .add(HttpHeaders.AUTHORIZATION, getHttpAuthHeaderValue(authentication));
        return webFilterExchange.getChain().filter(exchange);
    }

    private static String getHttpAuthHeaderValue(Authentication authentication){
        return String.join(" ","Bearer",tokenFromAuthentication(authentication));
    }

    private static String tokenFromAuthentication(Authentication authentication){
        return JWTTokenService.generateToken(
                                            authentication.getName(),
                                            authentication.getCredentials(),
                                            authentication.getAuthorities());
    }
}
