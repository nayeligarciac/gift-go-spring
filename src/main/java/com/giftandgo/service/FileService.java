package com.giftandgo.service;

import com.giftandgo.model.OutcomeData;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {

    List<OutcomeData> processFile(MultipartFile file);
}
