package com.softserve.patientsservice.services;

import com.softserve.patientsservice.domain.enities.Patient;

import java.util.List;

public interface PatientService {

    List<Patient> getAll();

    Patient getPatientByMPI(String MPI);

    Patient create(Patient patient);

    Patient update(Patient patient);

    Boolean deletePatientByMPI(String MPI);

    int deactivatePatientByMPI(String MPI);
}
