package site.yuanshen.gtbp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import site.yuanshen.gtbp.ws.AdminWebSocketHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * 配置类集合
 *
 * @author Moment
 */
@Configuration
public class GTBPConfig {

    @Configuration
    public static class WebSocketConfiguration {
        @Bean
        public HandlerMapping webSocketMapping(final AdminWebSocketHandler handler) {
            final Map<String, WebSocketHandler> map = new HashMap<>();
            map.put("/local/ws", handler);
            final SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
            mapping.setOrder(Ordered.HIGHEST_PRECEDENCE);
            mapping.setUrlMap(map);
            return mapping;
        }

        @Bean
        public WebSocketHandlerAdapter handlerAdapter() {
            return new WebSocketHandlerAdapter();
        }
    }
}
