package com.giftandgo.controller;

import com.giftandgo.aspect.LogExecutionTime;
import com.giftandgo.model.LogEntry;
import com.giftandgo.model.OutcomeData;
import com.giftandgo.repository.LogEntryRepository;
import com.giftandgo.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/file")
public class FileProcessController {

    private final FileService fileService;
    private final LogEntryRepository logEntryRepository;

    @Autowired
    public FileProcessController(FileService fileService,
                                 LogEntryRepository logEntryRepository) {
        this.fileService = fileService;
        this.logEntryRepository = logEntryRepository;
    }

    @LogExecutionTime
    @PostMapping("/")
    public List<OutcomeData> handleFileUpload(@RequestParam("file") MultipartFile file) {
        return fileService.processFile(file);
    }

    @GetMapping("/logs")
    public List<LogEntry> getAllLogEntry(){
        return logEntryRepository.findAll();
    }

}
