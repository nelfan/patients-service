package com.softserve.patientsservice.services;

import com.softserve.patientsservice.domain.enities.Patient;
import com.softserve.patientsservice.repositories.PatientRepository;
import com.softserve.patientsservice.services.implementations.PatientServiceImpl;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.ClassRule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext
@ActiveProfiles("test")
class PatientServiceTest {

    @Mock
    PatientRepository patientRepository;

    @InjectMocks
    PatientServiceImpl patientService;

    @Autowired
    StreamBridge streamBridge;

    private final String BINDING_OUT_DEACTIVATE_PATIENT_BY_MPI = "deactivatePatientByMPI-out-0";

    @Value("${spring.cloud.stream.bindings." + BINDING_OUT_DEACTIVATE_PATIENT_BY_MPI + ".destination}")
    private String deactivatePatientByMPITopic;


    @ClassRule
    public static KafkaContainer kafka = new KafkaContainer(DockerImageName
            .parse("confluentinc/cp-kafka:5.4.3"));

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        kafka.start();
        registry.add("spring.cloud.stream.kafka.binder.brokers",
                kafka::getBootstrapServers);
    }

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        patientService = new PatientServiceImpl(patientRepository, streamBridge);
    }

    @AfterAll()
    static void tearDown() {
        kafka.close();
    }

    @Test
    void shouldGetAllPatients() {
        List<Patient> patients = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Patient patient = new Patient();
            patient.setMpi("12345" + i);
            patient.setDateOfBirth(LocalDate.of(2000 + i, 1 + i, 1 + i));
            patient.setActive(true);
            patients.add(patient);
        }

        when(patientRepository.findAll()).thenReturn(patients);

        List<Patient> patientsActual = patientService.getAll();

        assertEquals(3, patientsActual.size());
        assertEquals(patients.get(0), patientsActual.get(0));
        verify(patientRepository, times(1)).findAll();
    }

    @Test
    void shouldGetPatientByMPI() {
        Patient expected = new Patient();
        expected.setMpi("12345");
        expected.setDateOfBirth(LocalDate.of(2001, 11, 11));
        expected.setActive(true);

        when(patientRepository.findById(expected.getMpi())).thenReturn(Optional.of(expected));

        Patient actual = patientService.getPatientByMPI(expected.getMpi());

        assertEquals(actual, expected);
        verify(patientRepository, times(1)).findById(expected.getMpi());
    }

    @Test
    void shouldCreatePatient() {
        Patient patient = new Patient();
        patient.setMpi("12345");
        patient.setDateOfBirth(LocalDate.of(2000, 1, 1));
        patient.setActive(true);

        Patient expected = new Patient();
        expected.setMpi("12345");
        expected.setDateOfBirth(LocalDate.of(2000, 1, 1));
        expected.setActive(true);

        when(patientRepository.save(patient)).thenReturn(expected);

        Patient actual = patientService.create(patient);

        assertEquals(expected, actual);
        verify(patientRepository, times(1)).save(patient);


    }

    @Test
    void shouldUpdatePatient() {
        Patient patient = new Patient();
        patient.setMpi("12345");
        patient.setDateOfBirth(LocalDate.of(2000, 10, 10));
        patient.setActive(true);

        Patient patientSaved = new Patient();
        patientSaved.setMpi("12345");
        patientSaved.setDateOfBirth(LocalDate.of(2000, 10, 10));
        patientSaved.setActive(true);

        Patient patientUpdated = new Patient();
        patientUpdated.setMpi("12345");
        patientUpdated.setDateOfBirth(LocalDate.of(2000, 10, 10));
        patientUpdated.setActive(true);

        when(patientRepository.findById(patient.getMpi())).thenReturn(Optional.of(patientSaved));

        when(patientRepository.save(patientUpdated)).thenReturn(patientUpdated);

        Patient actual = patientService.update(patient);

        assertEquals(patientUpdated, actual);

        verify(patientRepository, times(1)).save(patient);
        verify(patientRepository, times(1)).save(patientUpdated);
    }

    @Test
    void shouldDeletePatientByMPI() {
        Patient patient = new Patient();
        patient.setMpi("12345");
        patient.setDateOfBirth(LocalDate.of(2000, 10, 10));
        patient.setActive(true);

        when(patientRepository.findById(patient.getMpi())).thenReturn(Optional.of(patient));

        Boolean isDeleted = patientService.deletePatientByMPI(patient.getMpi());

        assertEquals(isDeleted, true);
        verify(patientRepository, times(1)).deleteById(patient.getMpi());
    }

    @Test
    void shouldDeactivatePatientByMPI() {
        Patient patient = new Patient();
        patient.setMpi("12345");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(
                ImmutableMap.of(
                        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers(),
                        ConsumerConfig.GROUP_ID_CONFIG, "testGroup",
                        ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"
                ),
                new StringDeserializer(),
                new StringDeserializer()
        );

        consumer.subscribe(Collections.singletonList(deactivatePatientByMPITopic));

        when(patientRepository.customDeactivatePatientByMPI(patient.getMpi())).thenReturn(1);

        streamBridge.send(BINDING_OUT_DEACTIVATE_PATIENT_BY_MPI, patient.getMpi());

        ConsumerRecords<String, String> consumerRecords = consumer.poll(Duration.ofMillis(2000));

        Assertions.assertEquals(1, consumerRecords.count());

        consumerRecords.records(deactivatePatientByMPITopic).forEach(s ->
                Assertions.assertEquals(patient.getMpi(), s.value()));

        consumer.unsubscribe();
    }
}