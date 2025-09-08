package com.giftandgo.service;

import com.giftandgo.error.BadRequestException;
import com.giftandgo.model.EntryData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DataServiceImplTest {

    private DataService dataService;

    @BeforeEach
    void setup(){
        dataService = new DataServiceImpl();
    }

    @Test
    void processData() {

        EntryData expectedResult = new EntryData(
                "18148426-89e1-11ee-b9d1-0242ac120002",
                "1X1D14",
                "John Smith",
                "Likes Apricots",
                "Rides A Bike",
                6.2,
                12.1
                );

        String input = "18148426-89e1-11ee-b9d1-0242ac120002|1X1D14|John Smith|Likes Apricots|Rides A Bike|6.2|12.1";
        EntryData result = dataService.processData(input);
        assertEquals(expectedResult, result);
    }

    @Test
    void processData_whenWrongNumberOfFields() {

        String input = "18148426-89e1-11ee-b9d1-0242ac120002|1X1D14|John Smith|Likes Apricots|Rides A Bike";
        BadRequestException ex = assertThrows(BadRequestException.class,
                ()-> dataService.processData(input));

        assertEquals("file with wrong number of fields", ex.getMessage());
    }

    @Test
    void processData_whenWrongAverageSpeed() {

        String input = "18148426-89e1-11ee-b9d1-0242ac120002|1X1D14|John Smith|Likes Apricots|Rides A Bike|xx|12.1";
        BadRequestException ex = assertThrows(BadRequestException.class,
                ()-> dataService.processData(input));

        assertEquals("wrong average speed", ex.getMessage());
    }

    @Test
    void processData_whenWrongTopSpeed() {

        String input = "18148426-89e1-11ee-b9d1-0242ac120002|1X1D14|John Smith|Likes Apricots|Rides A Bike|1.2|xx";
        BadRequestException ex = assertThrows(BadRequestException.class,
                ()-> dataService.processData(input));

        assertEquals("wrong top speed", ex.getMessage());
    }
}