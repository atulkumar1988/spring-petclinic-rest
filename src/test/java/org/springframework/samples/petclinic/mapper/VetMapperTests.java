package org.springframework.samples.petclinic.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.model.Specialty;
import org.springframework.samples.petclinic.model.Vet;
import org.springframework.samples.petclinic.rest.dto.SpecialtyDto;
import org.springframework.samples.petclinic.rest.dto.VetDto;
import org.springframework.samples.petclinic.rest.dto.VetFieldsDto;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link VetMapper}.
 */
class VetMapperTests {

    private final VetMapper vetMapper = new VetMapperImpl();

    @BeforeEach
    void wireCollaboratingMappers() {
        // VetMapperImpl is generated with componentModel="spring", so its SpecialtyMapper
        // dependency is normally @Autowired by the Spring container. Wire it manually here
        // since these are plain unit tests without a Spring context.
        ReflectionTestUtils.setField(vetMapper, "specialtyMapper", new SpecialtyMapperImpl());
    }

    @Test
    void shouldMapNullVetDtoToNullVet() {
        assertNull(vetMapper.toVet((VetDto) null));
    }

    @Test
    void shouldMapVetDtoToVet() {
        VetDto vetDto = new VetDto("James", "Carter", List.of(new SpecialtyDto(1, "radiology")), 1);

        Vet vet = vetMapper.toVet(vetDto);

        assertNotNull(vet);
        assertEquals(1, vet.getId());
        assertEquals("James", vet.getFirstName());
        assertEquals("Carter", vet.getLastName());
        assertEquals(1, vet.getSpecialties().size());
        assertEquals("radiology", vet.getSpecialties().get(0).getName());
    }

    @Test
    void shouldMapNullVetFieldsDtoToNullVet() {
        assertNull(vetMapper.toVet((VetFieldsDto) null));
    }

    @Test
    void shouldMapVetFieldsDtoToVetIgnoringId() {
        VetFieldsDto vetFieldsDto = new VetFieldsDto("James", "Carter", List.of(new SpecialtyDto(1, "radiology")));

        Vet vet = vetMapper.toVet(vetFieldsDto);

        assertNotNull(vet);
        assertNull(vet.getId());
        assertEquals("James", vet.getFirstName());
        assertEquals("Carter", vet.getLastName());
        assertEquals(1, vet.getSpecialties().size());
    }

    @Test
    void shouldMapNullVetToNullVetDto() {
        assertNull(vetMapper.toVetDto(null));
    }

    @Test
    void shouldMapVetToVetDto() {
        Vet vet = new Vet();
        vet.setId(1);
        vet.setFirstName("James");
        vet.setLastName("Carter");
        Specialty specialty = new Specialty();
        specialty.setId(1);
        specialty.setName("radiology");
        vet.addSpecialty(specialty);

        VetDto vetDto = vetMapper.toVetDto(vet);

        assertNotNull(vetDto);
        assertEquals(1, vetDto.id());
        assertEquals("James", vetDto.firstName());
        assertEquals("Carter", vetDto.lastName());
        assertEquals(1, vetDto.specialties().size());
        assertEquals("radiology", vetDto.specialties().get(0).name());
    }

    @Test
    void shouldMapVetWithNoSpecialtiesToVetDtoWithEmptySpecialties() {
        Vet vet = new Vet();
        vet.setId(1);
        vet.setFirstName("James");
        vet.setLastName("Carter");

        VetDto vetDto = vetMapper.toVetDto(vet);

        assertNotNull(vetDto);
        assertNotNull(vetDto.specialties());
        assertTrue(vetDto.specialties().isEmpty());
    }

    @Test
    void shouldMapNullVetsCollectionToNull() {
        assertNull(vetMapper.toVetDtos(null));
    }

    @Test
    void shouldMapEmptyVetsCollectionToEmptyCollection() {
        Collection<VetDto> vetDtos = vetMapper.toVetDtos(List.of());

        assertNotNull(vetDtos);
        assertTrue(vetDtos.isEmpty());
    }

    @Test
    void shouldMapVetsCollectionToVetDtos() {
        Vet james = new Vet();
        james.setId(1);
        james.setFirstName("James");
        james.setLastName("Carter");

        Vet helen = new Vet();
        helen.setId(2);
        helen.setFirstName("Helen");
        helen.setLastName("Leary");

        Collection<VetDto> vetDtos = vetMapper.toVetDtos(Arrays.asList(james, helen));

        assertNotNull(vetDtos);
        assertEquals(2, vetDtos.size());
        assertTrue(vetDtos.stream().anyMatch(v -> v.id().equals(1) && "James".equals(v.firstName())));
        assertTrue(vetDtos.stream().anyMatch(v -> v.id().equals(2) && "Helen".equals(v.firstName())));
    }
}
