package com.jzprog.chatapp.src.advices;

import java.util.Arrays;
import java.util.logging.Logger;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;


@Aspect
@Component
public class LogAspect {
	
    Logger log = Logger.getLogger(this.getClass().getName());

	
	@Around("@annotation(LogMethodInfo)")
	public Object logInitialInfo(ProceedingJoinPoint joinPoint) throws Throwable {
		Object[] arguments = joinPoint.getArgs();
		log.info("Inside: " + joinPoint.getSignature().getName() +"()" + " params: " + Arrays.toString(arguments));
	    return joinPoint.proceed();
	}
	
	@AfterReturning(pointcut = "@annotation(LogMethodInfo)", returning = "result")
	public void logAfterReturning(JoinPoint joinPoint, Object result) {
	    log.info("Returned: " + result.toString());
	}
	
}

