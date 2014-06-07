package com.p5solutions.core.jpa.orm.oracle;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.transaction.annotation.Transactional;

/**
 * FastConnectionFailover: Fast connection Failover annotation specified on
 * individual methods within a @Transactional proxy
 * 
 * @author Kasra Rasaee
 * @since 2009-12-22
 * @see Transactional for details on spring transactional services.
 * @see org.springframework.batch.retry.interceptor.RetryOperationsInterceptor
 * @see org.springframework.data.jdbc.retry.oracle.RacFailoverRetryPolicy
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
@Inherited
public @interface FastConnectionFailover {
	//
}