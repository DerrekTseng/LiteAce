package lite.core.aop;

import org.aspectj.lang.JoinPoint;

public interface AopInterceptor {

	public void before(JoinPoint joinPoint);

	public void after(JoinPoint joinPoint);

	public void afterReturning(JoinPoint joinPoint, Object returnValue);

	public void afterThrowing(JoinPoint joinPoint, Throwable throwable);
}
