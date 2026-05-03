package com.gamevault.http.igdb.annotation;

import io.github.bucket4j.Bucket;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class IgdbRateLimitAspect {

    private final Bucket igdbBucket;

    @Around("@annotation(IgdbRateLimited)")
    public Object rateLimit(ProceedingJoinPoint joinPoint) throws Throwable {
        igdbBucket.asBlocking().consume(1);
        return joinPoint.proceed();
    }
}
