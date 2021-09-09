package com.softserve.patientsservice.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.softserve.patientsservice.domain.dto.PatientDTO;
import com.softserve.patientsservice.domain.enities.Patient;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PatientKafkaMessageConverterJSONImpl implements KafkaMessageConverter<Patient, PatientDTO> {

    private ObjectMapper objectMapper;

    @SneakyThrows
    @Override
    public Patient deserialize(String message, Class<Patient> dtoClass) {
        return objectMapper.readValue(message, dtoClass);
    }

    @SneakyThrows
    @Override
    public String serialize(PatientDTO object) {
        return objectMapper.writeValueAsString(object);
    }
}
