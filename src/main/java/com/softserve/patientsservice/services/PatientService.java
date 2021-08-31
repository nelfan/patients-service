package com.softserve.patientsservice.services;

import com.softserve.patientsservice.domain.enities.Patient;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface PatientService {

    List<Patient> getAll();

    Patient getPatientByMPI(String MPI);

    Map<String, LocalDate> getAllPatientsByMPI(List<String> MPI);

    Patient create(Patient patient);

    Patient update(Patient patient);

    Boolean deletePatientByMPI(String MPI);
}
