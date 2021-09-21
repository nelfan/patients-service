package com.softserve.patientsservice.handler;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.ClassRule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.Collections;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext
@ActiveProfiles("test")
class PatientStreamBridgeTest {

    @ClassRule
    public static KafkaContainer kafka = new KafkaContainer(DockerImageName
            .parse("confluentinc/cp-kafka:5.4.3"));

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        kafka.start();
        registry.add("spring.cloud.stream.kafka.binder.brokers",
                kafka::getBootstrapServers);
    }

    @Autowired
    PatientStreamBridge patientStreamBridge;

    private final String BINDING_OUT = "deactivatePatientByMPI-out-0";

    @Value("${spring.cloud.stream.bindings." + BINDING_OUT + ".destination}")
    private String topic;

    @AfterAll()
    static void tearDown() {
        kafka.close();
    }


    @Test
    void shouldSendMPIByBindingName() {

        final String MPI = "test-MPI-value";
        int countOfOutputMessages = 3;

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(
                ImmutableMap.of(
                        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers(),
                        ConsumerConfig.GROUP_ID_CONFIG, "testGroup",
                        ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"
                ),
                new StringDeserializer(),
                new StringDeserializer()
        );

        consumer.subscribe(Collections.singletonList(topic));

        for (int i = 0; i < countOfOutputMessages; i++) {
            patientStreamBridge.send(BINDING_OUT, MPI);
        }

        ConsumerRecords<String, String> consumerRecords = consumer.poll(Duration.ofMillis(2000));

        Assertions.assertEquals(countOfOutputMessages, consumerRecords.count());

        consumerRecords.records(topic).forEach(s -> Assertions.assertEquals(MPI, s.value()));

        consumer.unsubscribe();
    }

}