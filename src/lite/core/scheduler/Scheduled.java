package lite.core.scheduler;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

/**
 * 自動註冊排程
 * 
 * @author DerrekTseng
 *
 */
@Retention(RUNTIME)
@Target(TYPE)
@Component
public @interface Scheduled {

	String id();

	String scheduled();

	boolean enabled() default true;
}
