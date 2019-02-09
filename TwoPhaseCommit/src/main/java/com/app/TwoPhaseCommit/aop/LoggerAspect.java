package com.app.TwoPhaseCommit.aop;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class LoggerAspect {

private Log log = LogFactory.getLog(LoggerAspect.class);
	
	@Around("@annotation(com.app.TwoPhaseCommit.aop.MyLog)")
	public Object log(ProceedingJoinPoint joinPoint) throws Throwable {
		String className = joinPoint.getTarget().getClass().getSimpleName();
		String methodName = joinPoint.getSignature().getName();
		String methodSignature = className + "." + methodName + "()";
		
		log.info(methodSignature + " - start");
		StringBuilder sBuilder = new StringBuilder();
		for(Object arg: joinPoint.getArgs()) {
			sBuilder.append("arg=" + arg.toString() + " ,");
		}
		log.debug("method arguments: " + sBuilder.toString());
		
		
		try {
			Object rv = joinPoint.proceed();
			log.info(methodSignature + " - ended successfully");
			return rv;
		} catch (Throwable e) {
			log.error(methodSignature + " - end with error" + e.getClass().getName());
			throw e;
		}
	}


}
