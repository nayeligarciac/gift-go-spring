package com.giftandgo.service;

import com.giftandgo.config.FeatureFlagProperties;
import com.giftandgo.error.BadRequestException;
import com.giftandgo.model.EntryData;
import com.giftandgo.model.OutcomeData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DataServiceImpl implements DataService {

    private final FeatureFlagProperties featureFlagProperties;

    @Autowired
    public DataServiceImpl(FeatureFlagProperties featureFlagProperties) {
        this.featureFlagProperties = featureFlagProperties;
    }

    @Override
    public EntryData processData(String rawData) {
        String[] fields = rawData.split("\\|");
        if(fields.length != 7){
            throw new BadRequestException("file with wrong number of fields");
        }
        Double averageSpeed = getDoubleValue(fields[5], "average speed");
        Double topSpeed = getDoubleValue(fields[6], "top speed");

        return new EntryData(
                validateString(fields[0], "uuid"),
                validateString(fields[1], "id"),
                validateString(fields[2],"name"),
                validateString(fields[3], "likes"),
                validateString(fields[4],"transport"),
                averageSpeed,
                topSpeed
        );
    }

    private Double getDoubleValue(String value, String nameOfField){
        try {
            return Double.parseDouble(value);
        } catch( NumberFormatException ex){
            if(featureFlagProperties.isEnableValidations()) {
                throw new BadRequestException(String.format("the field %s is not a double", nameOfField));
            } else {
                return null;
            }
        }
    }

    private String validateString(String value, String nameOfField) {
        if (featureFlagProperties.isEnableValidations() && (value == null || value.isEmpty())) {
            throw new BadRequestException(String.format("the field %s is empty", nameOfField));
        }
        return value;
    }

    public OutcomeData convertToOutcomeData(EntryData entryData) {
        return new OutcomeData(entryData.name(), entryData.transport(), entryData.topSpeed());
    }
}
