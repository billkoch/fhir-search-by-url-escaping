package com.example;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.google.common.escape.Escaper;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleType;
import org.hl7.fhir.r4.model.Bundle.HTTPVerb;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@RequiredArgsConstructor
@Slf4j
@SpringBootApplication
public class Application implements CommandLineRunner {

  private final IGenericClient fhirClient;

  private final Escaper hackyEscaper;

  @Override
  public void run(String... args) {
    // Create an artificial identifier with a leading space
    String mrn = " " + UUID.randomUUID().toString();

    // Build a patient and transaction bundle to push into a FHIR server
    Patient patient = new Patient();
    patient.addIdentifier().setSystem("http://acme.org/mrns").setValue(mrn);
    patient.addName().setFamily("Jameson").addGiven("J").addGiven("Jonah");
    patient.setGender(Enumerations.AdministrativeGender.MALE);

    Bundle bundle = new Bundle();
    bundle.setType(BundleType.TRANSACTION);
    bundle
        .addEntry()
        .setResource(patient)
        .getRequest()
        .setMethod(HTTPVerb.POST)
        .setUrl("Patient")
        .getIfNoneExist();
    fhirClient.transaction().withBundle(bundle).execute();

    // Attempt to search for the newly created patient
    Bundle searchResults =
        fhirClient
            .search()
            // The issue arises here, because the leading space character of the MRN isn't URL
            // encoded to "%20"
            .byUrl("/Patient?identifier=http://acme.org/mrns|" + hackyEscaper.escape(mrn))
            .returnBundle(Bundle.class)
            .execute();

    Identifier identifier =
        ((Patient) searchResults.getEntryFirstRep().getResource()).getIdentifierFirstRep();

    log.debug(
        "Found patient with identifier: {}|{}", identifier.getSystem(), identifier.getValue());
  }

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
