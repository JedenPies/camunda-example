package net.patrykdobrowolski.camunda_connector.config;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectMapperConfiguration {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER.copy();
    }
}
