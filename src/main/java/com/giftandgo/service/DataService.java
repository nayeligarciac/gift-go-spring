package com.giftandgo.service;

import com.giftandgo.model.EntryData;
import com.giftandgo.model.OutcomeData;

public interface DataService {
    EntryData processData(String rawData);

    OutcomeData convertToOutcomeData(EntryData entryData);
}
