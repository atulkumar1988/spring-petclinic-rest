package org.springframework.samples.petclinic.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.rest.dto.OwnerDto;
import org.springframework.samples.petclinic.rest.dto.OwnerFieldsDto;
import org.springframework.samples.petclinic.rest.dto.OwnerPageDto;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link OwnerMapper}.
 */
class OwnerMapperTests {

    private final OwnerMapper ownerMapper = new OwnerMapperImpl();

    @BeforeEach
    void wireCollaboratingMappers() {
        // OwnerMapperImpl is generated with componentModel="spring", so its PetMapper
        // dependency is normally @Autowired by the Spring container. Wire it manually here
        // since these are plain unit tests without a Spring context.
        PetMapperImpl petMapper = new PetMapperImpl();
        ReflectionTestUtils.setField(petMapper, "visitMapper", new VisitMapperImpl());
        ReflectionTestUtils.setField(ownerMapper, "petMapper", petMapper);
    }

    private Owner newOwner(int id, String firstName) {
        Owner owner = new Owner();
        owner.setId(id);
        owner.setFirstName(firstName);
        owner.setLastName("Franklin");
        owner.setAddress("110 W. Liberty St.");
        owner.setCity("Madison");
        owner.setTelephone("6085551023");
        return owner;
    }

    @Test
    void shouldMapNullOwnerToNullOwnerDto() {
        assertNull(ownerMapper.toOwnerDto(null));
    }

    @Test
    void shouldMapOwnerToOwnerDto() {
        Owner owner = newOwner(1, "George");

        OwnerDto ownerDto = ownerMapper.toOwnerDto(owner);

        assertNotNull(ownerDto);
        assertEquals(1, ownerDto.id());
        assertEquals("George", ownerDto.firstName());
        assertEquals("Franklin", ownerDto.lastName());
        assertNotNull(ownerDto.pets());
        assertTrue(ownerDto.pets().isEmpty());
    }

    @Test
    void shouldMapOwnerWithPetsToOwnerDtoWithPets() {
        Owner owner = newOwner(1, "George");
        Pet pet = new Pet();
        pet.setId(1);
        pet.setName("Leo");
        pet.setBirthDate(java.time.LocalDate.of(2010, 9, 7));
        org.springframework.samples.petclinic.model.PetType type = new org.springframework.samples.petclinic.model.PetType();
        type.setId(1);
        type.setName("cat");
        pet.setType(type);
        owner.addPet(pet);

        OwnerDto ownerDto = ownerMapper.toOwnerDto(owner);

        assertNotNull(ownerDto);
        assertEquals(1, ownerDto.pets().size());
        assertEquals("Leo", ownerDto.pets().get(0).name());
    }

    @Test
    void shouldMapNullOwnerDtoToNullOwner() {
        assertNull(ownerMapper.toOwner((OwnerDto) null));
    }

    @Test
    void shouldMapOwnerDtoToOwner() {
        OwnerDto ownerDto = new OwnerDto("George", "Franklin", "110 W. Liberty St.", "Madison", "6085551023", 1, List.of());

        Owner owner = ownerMapper.toOwner(ownerDto);

        assertNotNull(owner);
        assertEquals(1, owner.getId());
        assertEquals("George", owner.getFirstName());
    }

    @Test
    void shouldMapNullOwnerFieldsDtoToNullOwner() {
        assertNull(ownerMapper.toOwner((OwnerFieldsDto) null));
    }

    @Test
    void shouldMapOwnerFieldsDtoToOwnerIgnoringIdAndPets() {
        OwnerFieldsDto ownerFieldsDto = new OwnerFieldsDto("George", "Franklin", "110 W. Liberty St.", "Madison", "6085551023");

        Owner owner = ownerMapper.toOwner(ownerFieldsDto);

        assertNotNull(owner);
        assertNull(owner.getId());
        assertTrue(owner.getPets().isEmpty());
        assertEquals("George", owner.getFirstName());
    }

    @Test
    void shouldMapNullOwnersCollectionToNull() {
        assertNull(ownerMapper.toOwnerDtoCollection(null));
    }

    @Test
    void shouldMapEmptyOwnersCollectionToEmptyList() {
        List<OwnerDto> ownerDtos = ownerMapper.toOwnerDtoCollection(List.of());

        assertNotNull(ownerDtos);
        assertTrue(ownerDtos.isEmpty());
    }

    @Test
    void shouldMapOwnersCollectionToOwnerDtos() {
        List<OwnerDto> ownerDtos = ownerMapper.toOwnerDtoCollection(Arrays.asList(newOwner(1, "George"), newOwner(2, "Betty")));

        assertNotNull(ownerDtos);
        assertEquals(2, ownerDtos.size());
    }

    @Test
    void shouldMapNullOwnerDtosCollectionToNull() {
        assertNull(ownerMapper.toOwners(null));
    }

    @Test
    void shouldMapOwnerDtosCollectionToOwners() {
        OwnerDto ownerDto = new OwnerDto("George", "Franklin", "110 W. Liberty St.", "Madison", "6085551023", 1, List.of());

        Collection<Owner> owners = ownerMapper.toOwners(List.of(ownerDto));

        assertNotNull(owners);
        assertEquals(1, owners.size());
    }

    @Test
    void shouldMapOwnerPageToOwnerPageDto() {
        Owner owner = newOwner(1, "George");
        PageImpl<Owner> page = new PageImpl<>(List.of(owner), PageRequest.of(0, 5), 1);

        OwnerPageDto ownerPageDto = ownerMapper.toOwnerPageDto(page);

        assertNotNull(ownerPageDto);
        assertEquals(1, ownerPageDto.content().size());
        assertEquals(0, ownerPageDto.page());
        assertEquals(5, ownerPageDto.size());
        assertEquals(1L, ownerPageDto.totalElements());
        assertEquals(1, ownerPageDto.totalPages());
    }
}
