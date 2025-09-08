package com.giftandgo.controller;

import com.giftandgo.model.OutcomeData;
import com.giftandgo.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/file")
public class FileProcessController {

    private final FileService fileService;

    @Autowired
    public FileProcessController(FileService fileService) {
        this.fileService = fileService;
    }


    @PostMapping("/")
    public List<OutcomeData> handleFileUpload(@RequestParam("file") MultipartFile file,
                                              RedirectAttributes redirectAttributes) {

        return fileService.processFile(file);
    }

}
