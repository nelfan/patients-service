package com.softserve.patientsservice.services;

import com.softserve.patientsservice.domain.dto.PatientDTO;
import com.softserve.patientsservice.domain.enities.Patient;
import com.softserve.patientsservice.utils.KafkaMessageConverter;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PatientProducer {

    private KafkaMessageConverter<Patient, PatientDTO> kafkaMessageConverter;

    private KafkaTemplate<String, String> kafkaTemplate;

    public void produce(String topic, PatientDTO patientDTO) {
        String message = kafkaMessageConverter.serialize(patientDTO);
        kafkaTemplate.send(topic, message);
    }

    public void produce(String topic, String message) {
        kafkaTemplate.send(topic, message);
    }
}
