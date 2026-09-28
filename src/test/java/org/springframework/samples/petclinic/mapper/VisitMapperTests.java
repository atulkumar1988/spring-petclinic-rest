package org.springframework.samples.petclinic.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.Visit;
import org.springframework.samples.petclinic.rest.dto.VisitDto;
import org.springframework.samples.petclinic.rest.dto.VisitFieldsDto;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link VisitMapper}.
 */
class VisitMapperTests {

    private final VisitMapper visitMapper = new VisitMapperImpl();

    private Pet pet;

    @BeforeEach
    void setup() {
        pet = new Pet();
        pet.setId(7);
    }

    @Test
    void shouldMapNullVisitDtoToNullVisit() {
        assertNull(visitMapper.toVisit((VisitDto) null));
    }

    @Test
    void shouldMapVisitDtoToVisit() {
        VisitDto visitDto = new VisitDto(LocalDate.of(2013, 1, 1), "rabies shot", 1, 7);

        Visit visit = visitMapper.toVisit(visitDto);

        assertNotNull(visit);
        assertEquals(LocalDate.of(2013, 1, 1), visit.getDate());
        assertEquals("rabies shot", visit.getDescription());
        assertEquals(1, visit.getId());
        assertNotNull(visit.getPet());
        assertEquals(7, visit.getPet().getId());
    }

    @Test
    void shouldMapNullVisitFieldsDtoToNullVisit() {
        assertNull(visitMapper.toVisit((VisitFieldsDto) null));
    }

    @Test
    void shouldMapVisitFieldsDtoToVisitIgnoringIdAndPet() {
        VisitFieldsDto visitFieldsDto = new VisitFieldsDto(LocalDate.of(2013, 1, 1), "rabies shot");

        Visit visit = visitMapper.toVisit(visitFieldsDto);

        assertNotNull(visit);
        assertEquals(LocalDate.of(2013, 1, 1), visit.getDate());
        assertEquals("rabies shot", visit.getDescription());
        assertNull(visit.getId());
        assertNull(visit.getPet());
    }

    @Test
    void shouldMapNullVisitToNullVisitDto() {
        assertNull(visitMapper.toVisitDto(null));
    }

    @Test
    void shouldMapVisitToVisitDtoWithPet() {
        Visit visit = new Visit();
        visit.setId(1);
        visit.setDate(LocalDate.of(2013, 1, 1));
        visit.setDescription("rabies shot");
        visit.setPet(pet);

        VisitDto visitDto = visitMapper.toVisitDto(visit);

        assertNotNull(visitDto);
        assertEquals(LocalDate.of(2013, 1, 1), visitDto.date());
        assertEquals("rabies shot", visitDto.description());
        assertEquals(1, visitDto.id());
        assertEquals(7, visitDto.petId());
    }

    @Test
    void shouldRejectVisitWithoutPetSincePetIdIsRequiredOnVisitDto() {
        // A persisted Visit always has a pet (visits.pet_id is NOT NULL in the schema),
        // so mapping a pet-less Visit is an invalid state. VisitDto's compact constructor
        // enforces this contract by failing fast instead of silently emitting a null petId.
        Visit visit = new Visit();
        visit.setId(1);
        visit.setDate(LocalDate.of(2013, 1, 1));
        visit.setDescription("rabies shot");
        visit.setPet(null);

        assertThrows(NullPointerException.class, () -> visitMapper.toVisitDto(visit));
    }

    @Test
    void shouldMapNullVisitsCollectionToNull() {
        assertNull(visitMapper.toVisitsDto(null));
    }

    @Test
    void shouldMapEmptyVisitsCollectionToEmptyCollection() {
        Collection<VisitDto> visitsDto = visitMapper.toVisitsDto(List.of());

        assertNotNull(visitsDto);
        assertTrue(visitsDto.isEmpty());
    }

    @Test
    void shouldMapVisitsCollectionToVisitsDto() {
        Visit firstVisit = new Visit();
        firstVisit.setId(1);
        firstVisit.setDate(LocalDate.of(2013, 1, 1));
        firstVisit.setDescription("rabies shot");
        firstVisit.setPet(pet);

        Visit secondVisit = new Visit();
        secondVisit.setId(2);
        secondVisit.setDate(LocalDate.of(2013, 2, 1));
        secondVisit.setDescription("neutered");
        secondVisit.setPet(pet);

        Collection<VisitDto> visitsDto = visitMapper.toVisitsDto(Arrays.asList(firstVisit, secondVisit));

        assertNotNull(visitsDto);
        assertEquals(2, visitsDto.size());
        assertTrue(visitsDto.stream().anyMatch(v -> v.id().equals(1) && "rabies shot".equals(v.description())));
        assertTrue(visitsDto.stream().anyMatch(v -> v.id().equals(2) && "neutered".equals(v.description())));
    }
}
