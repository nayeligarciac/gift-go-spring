package com.giftandgo.service;

import com.giftandgo.error.BadRequestException;
import com.giftandgo.model.EntryData;
import com.giftandgo.model.OutcomeData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileServiceImpl implements  FileService {


    private final DataService dataService;

    @Autowired
    public FileServiceImpl(DataService dataService) {
        this.dataService = dataService;
    }

    @Override
    public List<OutcomeData> processFile(org.springframework.web.multipart.MultipartFile file) {

        List<OutcomeData> list = new ArrayList<>();
        try {
            if (file.isEmpty()) {
                throw new BadRequestException("File is empty");
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(),
                    StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    EntryData entryData = dataService.processData(line);
                    list.add(dataService.convertToOutcomeData(entryData));
                }
            }
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to store file.", e);
        }
       return list;
    }

}
