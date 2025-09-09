package com.giftandgo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AzureServiceTag(String name, AzureServiceTagProperties properties) {}
