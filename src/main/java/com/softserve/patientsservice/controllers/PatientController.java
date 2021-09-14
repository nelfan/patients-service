package com.softserve.patientsservice.controllers;

import com.softserve.patientsservice.domain.dto.PatientDTO;
import com.softserve.patientsservice.domain.enities.Patient;
import com.softserve.patientsservice.domain.mappers.PatientMapper;
import com.softserve.patientsservice.services.PatientService;
import lombok.AllArgsConstructor;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/patients")
public class PatientController {

    private final PatientService patientService;

    private final PatientMapper patientMapper;

    private final StreamBridge streamBridge;

    @GetMapping
    public List<Patient> getAll() {
        return patientService.getAll();
    }

    @GetMapping("/{patientMPI}")
    public Patient getPatientByMPI(@PathVariable String patientMPI) {
        return patientService.getPatientByMPI(patientMPI);
    }

    @PostMapping()
    public ResponseEntity<PatientDTO> createPatient(@RequestBody PatientDTO patientDTO) {
        Patient patient = patientMapper.patientDTOToPatient(patientDTO);

        patientService.create(patient);

        PatientDTO patientDTOResponse = patientMapper.patientToPatientDTO(patient);

        return new ResponseEntity<>(patientDTOResponse, HttpStatus.CREATED);
    }

    @PutMapping()
    public ResponseEntity<PatientDTO> updatePatient(@RequestBody PatientDTO patientDTO) {
        Patient patient = patientMapper.patientDTOToPatient(patientDTO);

        patientService.update(patient);

        PatientDTO patientDTOResponse = patientMapper.patientToPatientDTO(patient);

        return new ResponseEntity<>(patientDTOResponse, HttpStatus.OK);
    }

    @DeleteMapping("/{patientMPI}")
    public ResponseEntity<String> deletePatientByMPI(@PathVariable String patientMPI) {
        patientService.deletePatientByMPI(patientMPI);

        return new ResponseEntity<>("Patient was removed successfully", HttpStatus.OK);
    }

    @PutMapping("/{patientMPI}")
    public ResponseEntity<String> deactivatePatientByMPI(@PathVariable String patientMPI) {
        if (patientService.deactivatePatientByMPI(patientMPI) == 1) {
            streamBridge.send("deactivatePatientByMPI-out-0", patientMPI);
            return new ResponseEntity<>("Patient was deactivated successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Unable to deactivate. Patient not found or already deactivated", HttpStatus.NOT_FOUND);
        }
    }
}
