package org.springframework.samples.petclinic.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.model.Specialty;
import org.springframework.samples.petclinic.rest.dto.SpecialtyDto;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SpecialtyMapper}.
 */
class SpecialtyMapperTests {

    private final SpecialtyMapper specialtyMapper = new SpecialtyMapperImpl();

    @Test
    void shouldMapNullSpecialtyDtoToNullSpecialty() {
        assertNull(specialtyMapper.toSpecialty(null));
    }

    @Test
    void shouldMapSpecialtyDtoToSpecialty() {
        SpecialtyDto specialtyDto = new SpecialtyDto(1, "radiology");

        Specialty specialty = specialtyMapper.toSpecialty(specialtyDto);

        assertNotNull(specialty);
        assertEquals(1, specialty.getId());
        assertEquals("radiology", specialty.getName());
    }

    @Test
    void shouldMapNullSpecialtyToNullSpecialtyDto() {
        assertNull(specialtyMapper.toSpecialtyDto(null));
    }

    @Test
    void shouldMapSpecialtyToSpecialtyDto() {
        Specialty specialty = new Specialty();
        specialty.setId(1);
        specialty.setName("radiology");

        SpecialtyDto specialtyDto = specialtyMapper.toSpecialtyDto(specialty);

        assertNotNull(specialtyDto);
        assertEquals(1, specialtyDto.id());
        assertEquals("radiology", specialtyDto.name());
    }

    @Test
    void shouldMapNullSpecialtyDtosCollectionToNull() {
        assertNull(specialtyMapper.toSpecialtyDtos(null));
    }

    @Test
    void shouldMapEmptySpecialtyDtosCollectionToEmptyCollection() {
        Collection<SpecialtyDto> specialtyDtos = specialtyMapper.toSpecialtyDtos(List.of());

        assertNotNull(specialtyDtos);
        assertTrue(specialtyDtos.isEmpty());
    }

    @Test
    void shouldMapSpecialtiesCollectionToSpecialtyDtos() {
        Specialty radiology = new Specialty();
        radiology.setId(1);
        radiology.setName("radiology");

        Specialty surgery = new Specialty();
        surgery.setId(2);
        surgery.setName("surgery");

        Collection<SpecialtyDto> specialtyDtos = specialtyMapper.toSpecialtyDtos(Arrays.asList(radiology, surgery));

        assertNotNull(specialtyDtos);
        assertEquals(2, specialtyDtos.size());
        assertTrue(specialtyDtos.stream().anyMatch(s -> s.id().equals(1) && "radiology".equals(s.name())));
        assertTrue(specialtyDtos.stream().anyMatch(s -> s.id().equals(2) && "surgery".equals(s.name())));
    }

    @Test
    void shouldMapNullSpecialtyDtosToNullSpecialties() {
        assertNull(specialtyMapper.toSpecialtys(null));
    }

    @Test
    void shouldMapEmptySpecialtyDtosToEmptySpecialties() {
        Collection<Specialty> specialties = specialtyMapper.toSpecialtys(List.of());

        assertNotNull(specialties);
        assertTrue(specialties.isEmpty());
    }

    @Test
    void shouldMapSpecialtyDtosCollectionToSpecialties() {
        SpecialtyDto radiology = new SpecialtyDto(1, "radiology");
        SpecialtyDto surgery = new SpecialtyDto(2, "surgery");

        Collection<Specialty> specialties = specialtyMapper.toSpecialtys(Arrays.asList(radiology, surgery));

        assertNotNull(specialties);
        assertEquals(2, specialties.size());
        assertTrue(specialties.stream().anyMatch(s -> s.getId().equals(1) && "radiology".equals(s.getName())));
        assertTrue(specialties.stream().anyMatch(s -> s.getId().equals(2) && "surgery".equals(s.getName())));
    }
}
