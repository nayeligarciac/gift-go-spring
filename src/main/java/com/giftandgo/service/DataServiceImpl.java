package com.giftandgo.service;

import com.giftandgo.error.BadRequestException;
import com.giftandgo.model.EntryData;
import com.giftandgo.model.OutcomeData;
import org.springframework.stereotype.Service;

@Service
public class DataServiceImpl implements DataService {

    @Override
    public EntryData processData(String rawData) {
        String[] fields = rawData.split("\\|");
        if(fields.length != 7){
            throw new BadRequestException("file with wrong number of fields");
        }
        double averageSpeed;
        try {
            averageSpeed = Double.parseDouble(fields[5]);
        } catch( NumberFormatException ex){
            throw new BadRequestException("wrong average speed");
        }

        double topSpeed;
        try {
            topSpeed = Double.parseDouble(fields[6]);
        } catch( NumberFormatException ex){
            throw new BadRequestException("wrong top speed");
        }

        return new EntryData(
                fields[0],
                fields[1],
                fields[2],
                fields[3],
                fields[4],
                averageSpeed,
                topSpeed
        );
    }

    public OutcomeData convertToOutcomeData(EntryData entryData) {
        return new OutcomeData(entryData.name(), entryData.transport(), entryData.topSpeed());
    }
}
