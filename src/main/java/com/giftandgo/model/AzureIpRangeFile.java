package com.giftandgo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AzureIpRangeFile(List<AzureServiceTag> values) {}