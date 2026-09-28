package org.springframework.samples.petclinic.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.model.Visit;
import org.springframework.samples.petclinic.rest.dto.PetDto;
import org.springframework.samples.petclinic.rest.dto.PetFieldsDto;
import org.springframework.samples.petclinic.rest.dto.PetPageDto;
import org.springframework.samples.petclinic.rest.dto.PetTypeDto;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link PetMapper}.
 */
class PetMapperTests {

    private final PetMapper petMapper = new PetMapperImpl();

    @BeforeEach
    void wireCollaboratingMappers() {
        // PetMapperImpl is generated with componentModel="spring", so its VisitMapper
        // dependency is normally @Autowired by the Spring container. Wire it manually here
        // since these are plain unit tests without a Spring context.
        ReflectionTestUtils.setField(petMapper, "visitMapper", new VisitMapperImpl());
    }

    private Pet newPet(int id, String name, Integer ownerId) {
        Pet pet = new Pet();
        pet.setId(id);
        pet.setName(name);
        pet.setBirthDate(LocalDate.of(2010, 9, 7));
        PetType type = new PetType();
        type.setId(1);
        type.setName("cat");
        pet.setType(type);
        if (ownerId != null) {
            Owner owner = new Owner();
            owner.setId(ownerId);
            pet.setOwner(owner);
        }
        return pet;
    }

    @Test
    void shouldMapNullPetToNullPetDto() {
        assertNull(petMapper.toPetDto(null));
    }

    @Test
    void shouldMapPetToPetDtoWithOwnerId() {
        Pet pet = newPet(1, "Leo", 5);

        PetDto petDto = petMapper.toPetDto(pet);

        assertNotNull(petDto);
        assertEquals(1, petDto.id());
        assertEquals("Leo", petDto.name());
        assertEquals(5, petDto.ownerId());
        assertEquals("cat", petDto.type().name());
        assertNotNull(petDto.visits());
        assertTrue(petDto.visits().isEmpty());
    }

    @Test
    void shouldMapPetWithoutOwnerToPetDtoWithNullOwnerId() {
        Pet pet = newPet(1, "Leo", null);

        PetDto petDto = petMapper.toPetDto(pet);

        assertNotNull(petDto);
        assertNull(petDto.ownerId());
    }

    @Test
    void shouldMapPetWithVisitsToPetDtoWithVisits() {
        Pet pet = newPet(1, "Leo", 5);
        Visit visit = new Visit();
        visit.setId(10);
        visit.setDate(LocalDate.of(2013, 1, 1));
        visit.setDescription("rabies shot");
        pet.addVisit(visit);

        PetDto petDto = petMapper.toPetDto(pet);

        assertNotNull(petDto);
        assertEquals(1, petDto.visits().size());
        assertEquals("rabies shot", petDto.visits().get(0).description());
    }

    @Test
    void shouldMapNullPetsCollectionToNull() {
        assertNull(petMapper.toPetsDto(null));
    }

    @Test
    void shouldMapPetsCollectionToPetDtos() {
        Collection<PetDto> petDtos = petMapper.toPetsDto(Arrays.asList(newPet(1, "Leo", 5), newPet(2, "Basil", 5)));

        assertNotNull(petDtos);
        assertEquals(2, petDtos.size());
    }

    @Test
    void shouldMapNullPetDtosCollectionToNull() {
        assertNull(petMapper.toPets(null));
    }

    @Test
    void shouldMapPetDtosCollectionToPets() {
        PetTypeDto typeDto = new PetTypeDto("cat", 1);
        PetDto petDto = new PetDto("Leo", LocalDate.of(2010, 9, 7), typeDto, 1, 5, List.of());

        Collection<Pet> pets = petMapper.toPets(List.of(petDto));

        assertNotNull(pets);
        assertEquals(1, pets.size());
    }

    @Test
    void shouldMapNullPetDtoToNullPet() {
        assertNull(petMapper.toPet((PetDto) null));
    }

    @Test
    void shouldMapPetDtoToPetWithOwner() {
        PetTypeDto typeDto = new PetTypeDto("cat", 1);
        PetDto petDto = new PetDto("Leo", LocalDate.of(2010, 9, 7), typeDto, 1, 5, List.of());

        Pet pet = petMapper.toPet(petDto);

        assertNotNull(pet);
        assertEquals(1, pet.getId());
        assertEquals("Leo", pet.getName());
        assertNotNull(pet.getOwner());
        assertEquals(5, pet.getOwner().getId());
    }

    @Test
    void shouldMapNullPetFieldsDtoToNullPet() {
        assertNull(petMapper.toPet((PetFieldsDto) null));
    }

    @Test
    void shouldMapPetFieldsDtoToPetIgnoringIdOwnerAndVisits() {
        PetTypeDto typeDto = new PetTypeDto("cat", 1);
        PetFieldsDto petFieldsDto = new PetFieldsDto("Leo", LocalDate.of(2010, 9, 7), typeDto);

        Pet pet = petMapper.toPet(petFieldsDto);

        assertNotNull(pet);
        assertNull(pet.getId());
        assertNull(pet.getOwner());
        assertEquals("Leo", pet.getName());
        assertTrue(pet.getVisits().isEmpty());
    }

    @Test
    void shouldMapNullPetTypeToNullPetTypeDto() {
        assertNull(petMapper.toPetTypeDto(null));
    }

    @Test
    void shouldMapPetTypeToPetTypeDto() {
        PetType petType = new PetType();
        petType.setId(1);
        petType.setName("cat");

        PetTypeDto petTypeDto = petMapper.toPetTypeDto(petType);

        assertNotNull(petTypeDto);
        assertEquals("cat", petTypeDto.name());
    }

    @Test
    void shouldMapNullPetTypeDtoToNullPetType() {
        assertNull(petMapper.toPetType(null));
    }

    @Test
    void shouldMapPetTypeDtoToPetType() {
        PetTypeDto petTypeDto = new PetTypeDto("cat", 1);

        PetType petType = petMapper.toPetType(petTypeDto);

        assertNotNull(petType);
        assertEquals("cat", petType.getName());
    }

    @Test
    void shouldMapNullPetTypesCollectionToNull() {
        assertNull(petMapper.toPetTypeDtos(null));
    }

    @Test
    void shouldMapPetTypesCollectionToPetTypeDtos() {
        PetType cat = new PetType();
        cat.setId(1);
        cat.setName("cat");

        Collection<PetTypeDto> petTypeDtos = petMapper.toPetTypeDtos(List.of(cat));

        assertNotNull(petTypeDtos);
        assertEquals(1, petTypeDtos.size());
    }

    @Test
    void shouldMapPetPageToPetPageDto() {
        Pet pet = newPet(1, "Leo", 5);
        PageImpl<Pet> page = new PageImpl<>(List.of(pet), PageRequest.of(0, 5), 1);

        PetPageDto petPageDto = petMapper.toPetPageDto(page);

        assertNotNull(petPageDto);
        assertEquals(1, petPageDto.content().size());
        assertEquals(0, petPageDto.page());
        assertEquals(5, petPageDto.size());
        assertEquals(1L, petPageDto.totalElements());
        assertEquals(1, petPageDto.totalPages());
    }
}
