package com.giftandgo.aspect;

import com.giftandgo.model.LogEntry;
import com.giftandgo.repository.LogEntryRepository;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    private final LogEntryRepository logEntryRepository;

    @Autowired
    public LoggingAspect(LogEntryRepository logEntryRepository) {
        this.logEntryRepository = logEntryRepository;
    }

    @Around("@annotation(LogExecutionTime)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        Instant before = Instant.now();

        Object proceed = joinPoint.proceed();
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        Duration duration = Duration.between(before, Instant.now());
        LogEntry logEntry = LocalStore.getLogEntry();
        saveLogEntry(before, duration, logEntry, attributes.getResponse().getStatus());
        logger.info("Executed in " + duration.toMillis() + "ms");
        return proceed;
    }

    private void saveLogEntry(Instant before, Duration duration, LogEntry logEntry, Integer responseCode){
        logEntry.setId(UUID.randomUUID().toString());
        logEntry.setDate(before);
        logEntry.setTimeLapsed(duration.toMillis());
        logEntry.setResponseCode(responseCode);
        logEntryRepository.save(logEntry);
    }
}
