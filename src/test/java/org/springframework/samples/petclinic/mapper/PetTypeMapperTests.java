package org.springframework.samples.petclinic.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.rest.dto.PetTypeDto;
import org.springframework.samples.petclinic.rest.dto.PetTypeFieldsDto;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link PetTypeMapper}.
 */
class PetTypeMapperTests {

    private final PetTypeMapper petTypeMapper = new PetTypeMapperImpl();

    @Test
    void shouldMapNullPetTypeDtoToNullPetType() {
        assertNull(petTypeMapper.toPetType((PetTypeDto) null));
    }

    @Test
    void shouldMapPetTypeDtoToPetType() {
        PetTypeDto petTypeDto = new PetTypeDto("cat", 1);

        PetType petType = petTypeMapper.toPetType(petTypeDto);

        assertNotNull(petType);
        assertEquals(1, petType.getId());
        assertEquals("cat", petType.getName());
    }

    @Test
    void shouldMapNullPetTypeFieldsDtoToNullPetType() {
        assertNull(petTypeMapper.toPetType((PetTypeFieldsDto) null));
    }

    @Test
    void shouldMapPetTypeFieldsDtoToPetTypeIgnoringId() {
        PetTypeFieldsDto petTypeFieldsDto = new PetTypeFieldsDto("dog");

        PetType petType = petTypeMapper.toPetType(petTypeFieldsDto);

        assertNotNull(petType);
        assertNull(petType.getId());
        assertEquals("dog", petType.getName());
    }

    @Test
    void shouldMapNullPetTypeToNullPetTypeDto() {
        assertNull(petTypeMapper.toPetTypeDto(null));
    }

    @Test
    void shouldMapPetTypeToPetTypeDto() {
        PetType petType = new PetType();
        petType.setId(1);
        petType.setName("cat");

        PetTypeDto petTypeDto = petTypeMapper.toPetTypeDto(petType);

        assertNotNull(petTypeDto);
        assertEquals(1, petTypeDto.id());
        assertEquals("cat", petTypeDto.name());
    }

    @Test
    void shouldMapPetTypeToPetTypeFieldsDto() {
        PetType petType = new PetType();
        petType.setId(1);
        petType.setName("cat");

        PetTypeFieldsDto petTypeFieldsDto = petTypeMapper.toPetTypeFieldsDto(petType);

        assertNotNull(petTypeFieldsDto);
        assertEquals("cat", petTypeFieldsDto.name());
    }

    @Test
    void shouldMapNullPetTypesCollectionToNull() {
        assertNull(petTypeMapper.toPetTypeDtos(null));
    }

    @Test
    void shouldMapEmptyPetTypesCollectionToEmptyList() {
        List<PetTypeDto> petTypeDtos = petTypeMapper.toPetTypeDtos(List.of());

        assertNotNull(petTypeDtos);
        assertTrue(petTypeDtos.isEmpty());
    }

    @Test
    void shouldMapPetTypesCollectionToPetTypeDtos() {
        PetType cat = new PetType();
        cat.setId(1);
        cat.setName("cat");

        PetType dog = new PetType();
        dog.setId(2);
        dog.setName("dog");

        Collection<PetTypeDto> petTypeDtos = petTypeMapper.toPetTypeDtos(Arrays.asList(cat, dog));

        assertNotNull(petTypeDtos);
        assertEquals(2, petTypeDtos.size());
        assertTrue(petTypeDtos.stream().anyMatch(p -> p.id().equals(1) && "cat".equals(p.name())));
        assertTrue(petTypeDtos.stream().anyMatch(p -> p.id().equals(2) && "dog".equals(p.name())));
    }
}
