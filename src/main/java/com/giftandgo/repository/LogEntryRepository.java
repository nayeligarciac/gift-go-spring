package com.giftandgo.repository;

import com.giftandgo.model.LogEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogEntryRepository extends JpaRepository<LogEntry, String> {
}
