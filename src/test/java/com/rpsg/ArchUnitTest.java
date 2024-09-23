package com.rpsg;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.library.Architectures;
import org.junit.jupiter.api.Test;

public class ArchUnitTest {

    @Test
    public void layeredArchitectureTest() {
        JavaClasses importedClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("com.rpsg");

        Architectures.LayeredArchitecture architecture = Architectures.layeredArchitecture()
                .consideringOnlyDependenciesInLayers()
                .layer("Controllers").definedBy("com.rpsg.controller..")
                .layer("Models").definedBy("com.rpsg.model..")
                .layer("Repositories").definedBy("com.rpsg.repository..")
                .whereLayer("Controllers").mayNotBeAccessedByAnyLayer()
                .whereLayer("Repositories").mayNotBeAccessedByAnyLayer()
                .whereLayer("Models").mayOnlyBeAccessedByLayers("Controllers", "Repositories");

        architecture.check(importedClasses);

    }
}