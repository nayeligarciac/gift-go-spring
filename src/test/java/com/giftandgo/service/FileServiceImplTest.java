package com.giftandgo.service;


import com.giftandgo.error.BadRequestException;
import com.giftandgo.model.EntryData;
import com.giftandgo.model.OutcomeData;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileServiceImplTest {

    FileService fileService;
    @Mock
    DataService dataService;

    @BeforeEach
    void setup(){
        fileService = new FileServiceImpl(dataService);
    }

    @Test
    void processFile() throws IOException {
        MultipartFile multipartFile = mock(MultipartFile.class);
        when(multipartFile.isEmpty()).thenReturn(false);

        InputStream stubInputStream =
                IOUtils.toInputStream("test data", "UTF-8");
        when(multipartFile.getInputStream()).thenReturn(stubInputStream);

        EntryData entryData = mock(EntryData.class);
        when(dataService.processData("test data"))
                .thenReturn(entryData);
        OutcomeData outcomeData = mock(OutcomeData.class);
        when(dataService.convertToOutcomeData(entryData))
                .thenReturn(outcomeData);

        List<OutcomeData> result = fileService.processFile(multipartFile);
        assertEquals(Collections.singletonList(outcomeData), result);
    }


    @Test
    void processFile_whenFileIsEmpty() throws IOException {
        MultipartFile multipartFile = mock(MultipartFile.class);
        when(multipartFile.isEmpty()).thenReturn(true);

        BadRequestException ex = assertThrows(BadRequestException.class,
                ()-> fileService.processFile(multipartFile));

        verify(multipartFile, never()).getInputStream();
        assertEquals("File is empty", ex.getMessage());
    }

}