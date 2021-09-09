package com.softserve.patientsservice.repositories;

import com.google.cloud.spring.data.spanner.repository.SpannerRepository;
import com.google.cloud.spring.data.spanner.repository.query.Query;
import com.softserve.patientsservice.domain.enities.Patient;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepository extends SpannerRepository<Patient, String> {

@Query(value = "UPDATE PATIENTS SET IS_ACTIVE=false WHERE MASTER_PATIENT_IDENTIFIER = @MPI", dmlStatement = true)
int customDeactivatePatientByMPI(@Param("MPI") String MPI);
}
