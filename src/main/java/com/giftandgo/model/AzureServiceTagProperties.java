package com.giftandgo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AzureServiceTagProperties(List<String> addressPrefixes) {}
