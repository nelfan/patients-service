package com.softserve.patientsservice.services.implementations;

import com.softserve.patientsservice.domain.enities.Patient;
import com.softserve.patientsservice.repositories.PatientRepository;
import com.softserve.patientsservice.services.PatientService;
import com.softserve.patientsservice.utils.exceptions.CustomEntityNotFoundException;
import com.softserve.patientsservice.utils.exceptions.CustomFailedToDeleteEntityException;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Log
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;

    @Override
    public List<Patient> getAll() {
        return (List<Patient>) patientRepository.findAll();
    }

    @Override
    public Patient getPatientByMPI(String MPI) {
        return patientRepository.findById(MPI)
                .orElseThrow(() -> new CustomEntityNotFoundException(Patient.class));
    }

    @Override
    public Patient create(Patient patient) {
        return patientRepository.save(patient);
    }

    @Override
    public Patient update(Patient patient) {
        Patient existingPatient = getPatientByMPI(patient.getMpi());
        existingPatient.setMpi(patient.getMpi());
        existingPatient.setDateOfBirth(patient.getDateOfBirth());
        existingPatient.setActive(patient.isActive());
        return patientRepository.save(existingPatient);
    }

    @Override
    public Boolean deletePatientByMPI(String MPI) {
        try {
            getPatientByMPI(MPI);
            patientRepository.deleteById(MPI);
            return true;
        } catch (Exception e) {
            log.severe(e.getMessage());
            throw new CustomFailedToDeleteEntityException(Patient.class);
        }
    }

    @Override
    public int deactivatePatientByMPI(String MPI) {
        return patientRepository.customDeactivatePatientByMPI(MPI);
    }
}
