package com.giftandgo.aspect;


import com.giftandgo.model.LogEntry;

public class LocalStore {

  private static final ThreadLocal<LogEntry> threadLocalValue = new ThreadLocal<>();

   public static void setLogEntry(LogEntry logEntry) {
       threadLocalValue.set(logEntry);
   }

   public static LogEntry getLogEntry(){
       return threadLocalValue.get();
   }
}
