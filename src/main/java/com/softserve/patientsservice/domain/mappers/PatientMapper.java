package com.softserve.patientsservice.domain.mappers;

import com.softserve.patientsservice.domain.dto.PatientDTO;
import com.softserve.patientsservice.domain.enities.Patient;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PatientMapper {

    Patient patientDTOToPatient(PatientDTO patientDTO);

    PatientDTO patientToPatientDTO(Patient patient);

}
