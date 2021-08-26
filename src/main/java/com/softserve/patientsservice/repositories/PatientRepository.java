package com.softserve.patientsservice.repositories;

import com.google.cloud.spring.data.spanner.repository.SpannerRepository;
import com.softserve.patientsservice.domain.enities.Patient;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepository extends SpannerRepository<Patient, String> {
}
