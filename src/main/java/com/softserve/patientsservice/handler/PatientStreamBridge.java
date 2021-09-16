package com.softserve.patientsservice.handler;

import lombok.AllArgsConstructor;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class PatientStreamBridge {

    StreamBridge streamBridge;

    public boolean send(String bindingName, Object data) {
        return streamBridge.send(bindingName, data);
    }
}
