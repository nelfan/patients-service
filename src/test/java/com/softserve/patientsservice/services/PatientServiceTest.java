package com.softserve.patientsservice.services;

import com.softserve.patientsservice.domain.enities.Patient;
import com.softserve.patientsservice.repositories.PatientRepository;
import com.softserve.patientsservice.services.implementations.PatientServiceImpl;
import com.softserve.patientsservice.utils.exceptions.CustomEntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PatientServiceTest {

    @Mock
    PatientRepository patientRepository;

    @InjectMocks
    PatientServiceImpl patientService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        patientService = new PatientServiceImpl(patientRepository);
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
}