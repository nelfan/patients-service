package com.softserve.patientsservice.utils;

public interface KafkaMessageConverter<T, U> {

    T deserialize(String message, Class<T> dtoClass);

    String serialize(U object);
}
