package com.giftandgo.service;

import com.giftandgo.config.FeatureFlagProperties;
import com.giftandgo.error.BadRequestException;
import com.giftandgo.model.EntryData;
import com.giftandgo.model.OutcomeData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataServiceImplTest {

    @Mock
    private FeatureFlagProperties featureFlagProperties;
    private DataService dataService;

    @BeforeEach
    void setup(){
        dataService = new DataServiceImpl(featureFlagProperties);
    }

    @Test
    void processData() {
        when(featureFlagProperties.isEnableValidations()).thenReturn(true);

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
        verify(featureFlagProperties, never()).isEnableValidations();
    }

    @Test
    void processData_whenWrongAverageSpeed_whenFFisON() {
        when(featureFlagProperties.isEnableValidations()).thenReturn(true);

        String input = "18148426-89e1-11ee-b9d1-0242ac120002|1X1D14|John Smith|Likes Apricots|Rides A Bike|xx|12.1";
        BadRequestException ex = assertThrows(BadRequestException.class,
                ()-> dataService.processData(input));

        assertEquals("the field average speed is not a double", ex.getMessage());
    }

    @Test
    void processData_whenWrongAverageSpeed_whenFFisOff() {
        EntryData expectedResult = new EntryData(
                "18148426-89e1-11ee-b9d1-0242ac120002",
                "1X1D14",
                "John Smith",
                "Likes Apricots",
                "Rides A Bike",
                null,
                12.1
        );

        when(featureFlagProperties.isEnableValidations()).thenReturn(false);

        String input = "18148426-89e1-11ee-b9d1-0242ac120002|1X1D14|John Smith|Likes Apricots|Rides A Bike|xx|12.1";
        EntryData result = dataService.processData(input);
        assertEquals(expectedResult, result);
    }

    @Test
    void processData_whenWrongTopSpeed_whenFFisON() {
        when(featureFlagProperties.isEnableValidations()).thenReturn(true);

        String input = "18148426-89e1-11ee-b9d1-0242ac120002|1X1D14|John Smith|Likes Apricots|Rides A Bike|1.2|xx";
        BadRequestException ex = assertThrows(BadRequestException.class,
                ()-> dataService.processData(input));

        assertEquals("the field top speed is not a double", ex.getMessage());
    }

    @Test
    void processData_whenWrongTopSpeed_whenFFisOFF() {
        EntryData expectedResult = new EntryData(
                "18148426-89e1-11ee-b9d1-0242ac120002",
                "1X1D14",
                "John Smith",
                "Likes Apricots",
                "Rides A Bike",
                1.2,
                null
        );
        when(featureFlagProperties.isEnableValidations()).thenReturn(false);

        String input = "18148426-89e1-11ee-b9d1-0242ac120002|1X1D14|John Smith|Likes Apricots|Rides A Bike|1.2|xx";
        EntryData result = dataService.processData(input);
        assertEquals(expectedResult, result);
    }

    @Test
    void processData_whenUUIDIsEmpty_whenFFisON() {

        when(featureFlagProperties.isEnableValidations()).thenReturn(true);

        String input = "|1X1D14|John Smith|Likes Apricots|Rides A Bike|1.2|8.8";
        BadRequestException ex = assertThrows(BadRequestException.class,
                ()-> dataService.processData(input));

        assertEquals("the field uuid is empty", ex.getMessage());
    }

    @Test
    void processData_whenUUIDIsEmpty_whenFFisOFF() {
        EntryData expectedResult = new EntryData(
                "",
                "1X1D14",
                "John Smith",
                "Likes Apricots",
                "Rides A Bike",
                1.2,
                8.8
        );
        when(featureFlagProperties.isEnableValidations()).thenReturn(false);

        String input = "|1X1D14|John Smith|Likes Apricots|Rides A Bike|1.2|8.8";
        EntryData result = dataService.processData(input);
        assertEquals(expectedResult, result);
    }

    @Test
    void convertToOutcomeData(){

        EntryData entryData = new EntryData(
                "18148426-89e1-11ee-b9d1-0242ac120002",
                "1X1D14",
                "John Smith",
                "Likes Apricots",
                "Rides A Bike",
                6.2,
                12.1
        );

        OutcomeData result = dataService.convertToOutcomeData(entryData);

        OutcomeData expectedResult = new OutcomeData("John Smith", "Rides A Bike",  12.1);
        assertEquals(expectedResult, result);
    }

}