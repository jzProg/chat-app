package com.jzprog.chatapp.src.advices;

import java.util.logging.Logger;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;


@Aspect
@Component
public class LogAspect {
	
    Logger log = Logger.getLogger(this.getClass().getName());

	
	@Around("@annotation(LogMethodInfo)")
	public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
		log.info("Inside method: " + joinPoint.getSignature().getName());
		Object[] arguments = joinPoint.getArgs();
		for (Object argument : arguments) log.info(argument.toString());
	    return joinPoint.proceed();
	}
	
}

