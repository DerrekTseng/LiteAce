package lite.core.websocket;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

/**
 * 註冊 WebSocket 接口
 * 
 * @author DerrekTseng
 *
 */
@Component
@Retention(RUNTIME)
@Target(TYPE)
public @interface WebSocketEndpoint {
	String value();
}
