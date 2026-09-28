/*
 * Copyright 2016-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.samples.petclinic.rest.controller;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.samples.petclinic.rest.controller.v1.OwnerRestControllerV1;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.mapper.OwnerMapper;
import org.springframework.samples.petclinic.mapper.PetMapper;
import org.springframework.samples.petclinic.mapper.VisitMapper;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.rest.advice.ExceptionControllerAdvice;
import org.springframework.samples.petclinic.rest.dto.OwnerDto;
import org.springframework.samples.petclinic.rest.dto.OwnerFieldsDto;
import org.springframework.samples.petclinic.rest.dto.PetDto;
import org.springframework.samples.petclinic.rest.dto.PetFieldsDto;
import org.springframework.samples.petclinic.rest.dto.PetTypeDto;
import org.springframework.samples.petclinic.rest.dto.VisitDto;
import org.springframework.samples.petclinic.rest.dto.VisitFieldsDto;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.samples.petclinic.service.clinicService.ApplicationTestConfig;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.json.JsonMapper;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for {@link OwnerRestControllerV1}
 *
 * @author Vitaliy Fedoriv
 */
@SpringBootTest
@ContextConfiguration(classes = ApplicationTestConfig.class)
@WebAppConfiguration
class OwnerRestControllerV1Tests {

    @Autowired
    private OwnerRestControllerV1 ownerRestControllerV1;

    @Autowired
    private OwnerMapper ownerMapper;

    @Autowired
    private PetMapper petMapper;

    @Autowired
    private VisitMapper visitMapper;

    @MockitoBean
    private ClinicService clinicService;

    private MockMvc mockMvc;

    private List<OwnerDto> owners;

    private List<PetDto> pets;

    private List<VisitDto> visits;

    @BeforeEach
    void initOwners() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(ownerRestControllerV1)
            .setControllerAdvice(new ExceptionControllerAdvice())
            .build();
        owners = new ArrayList<>();

        owners.add(new OwnerDto("George", "Franklin", "110 W. Liberty St.", "Madison", "6085551023", 1,
            List.of(getTestPetWithIdAndName(1, "Rosy"))));
        owners.add(new OwnerDto("Betty", "Davis", "638 Cardinal Ave.", "Sun Prairie", "6085551749", 2, List.of()));
        owners.add(new OwnerDto("Eduardo", "Rodriquez", "2693 Commerce St.", "McFarland", "6085558763", 3, List.of()));
        owners.add(new OwnerDto("Harold", "Davis", "563 Friendly St.", "Windsor", "6085553198", 4, List.of()));

        PetTypeDto petType = new PetTypeDto("dog", 2);

        pets = new ArrayList<>();
        PetDto pet = new PetDto("Rosy", LocalDate.now(), petType, 3, null, List.of());
        pets.add(pet);
        pets.add(new PetDto("Jewel", LocalDate.now(), petType, 4, null, List.of()));

        visits = new ArrayList<>();
        VisitDto visit = new VisitDto(LocalDate.now(), "rabies shot", 2, pet.id());
        visits.add(visit);

        visit = new VisitDto(LocalDate.now(), "neutered", 3, pet.id());
        visits.add(visit);
    }

    private PetDto getTestPetWithIdAndName(final int id, final String name) {
        PetTypeDto petType = new PetTypeDto("dog", 2);
        return new PetDto(name, LocalDate.now(), petType, id, null, List.of(getTestVisitForPet(1, id)));
    }

    private VisitDto getTestVisitForPet(final int id, final int petId) {
        return new VisitDto(LocalDate.now(), "test" + id, id, petId);
    }

    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void testGetOwnerSuccess() throws Exception {
        given(this.clinicService.findOwnerById(1)).willReturn(ownerMapper.toOwner(owners.get(0)));
        this.mockMvc.perform(get("/api/owners/1")
                .accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.firstName").value("George"));
    }

    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void testGetOwnerNotFound() throws Exception {
        given(this.clinicService.findOwnerById(2)).willReturn(null);
        this.mockMvc.perform(get("/api/owners/2")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void testGetOwnersListSuccess() throws Exception {
        owners.remove(0);
        owners.remove(1);
        given(this.clinicService.findOwnerByLastName("Davis")).willReturn(ownerMapper.toOwners(owners));
        this.mockMvc.perform(get("/api/owners?lastName=Davis")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.[0].id").value(2))
            .andExpect(jsonPath("$.[0].firstName").value("Betty"))
            .andExpect(jsonPath("$.[1].id").value(4))
            .andExpect(jsonPath("$.[1].firstName").value("Harold"));
    }

    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void testGetOwnersListNotFound() throws Exception {
        owners.clear();
        given(this.clinicService.findOwnerByLastName("0")).willReturn(ownerMapper.toOwners(owners));
        this.mockMvc.perform(get("/api/owners?lastName=0")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void testGetAllOwnersSuccess() throws Exception {
        owners.remove(0);
        owners.remove(1);
        given(this.clinicService.findAllOwners()).willReturn(ownerMapper.toOwners(owners));
        this.mockMvc.perform(get("/api/owners")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.[0].id").value(2))
            .andExpect(jsonPath("$.[0].firstName").value("Betty"))
            .andExpect(jsonPath("$.[1].id").value(4))
            .andExpect(jsonPath("$.[1].firstName").value("Harold"));
    }

    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void testGetAllOwnersNotFound() throws Exception {
        owners.clear();
        given(this.clinicService.findAllOwners()).willReturn(ownerMapper.toOwners(owners));
        this.mockMvc.perform(get("/api/owners")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void testCreateOwnerSuccess() throws Exception {
        OwnerFieldsDto newOwnerDto = new OwnerFieldsDto("George", "Franklin", "110 W. Liberty St.", "Madison", "6085551023");
        ObjectMapper mapper = new ObjectMapper();
        String newOwnerAsJSON = mapper.writeValueAsString(newOwnerDto);
        this.mockMvc.perform(post("/api/owners")
                .content(newOwnerAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void testCreateOwnerError() throws Exception {
        String newOwnerAsJSON = """
            {"firstName":null,"lastName":"Franklin","address":"110 W. Liberty St.","city":"Madison","telephone":"6085551023"}
            """;
        this.mockMvc.perform(post("/api/owners")
                .content(newOwnerAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void testUpdateOwnerSuccess() throws Exception {
        given(this.clinicService.findOwnerById(1)).willReturn(ownerMapper.toOwner(owners.get(0)));
        int ownerId = owners.get(0).id();
        OwnerFieldsDto updatedOwnerDto = new OwnerFieldsDto("GeorgeI", "Franklin", "110 W. Liberty St.", "Madison", "6085551023");
        ObjectMapper mapper = new ObjectMapper();
        String newOwnerAsJSON = mapper.writeValueAsString(updatedOwnerDto);
        this.mockMvc.perform(put("/api/owners/" + ownerId)
                .content(newOwnerAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().contentType("application/json"))
            .andExpect(status().isNoContent());

        this.mockMvc.perform(get("/api/owners/" + ownerId)
                .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.id").value(ownerId))
            .andExpect(jsonPath("$.firstName").value("GeorgeI"));

    }

    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void testUpdateOwnerSuccessNoBodyId() throws Exception {
        given(this.clinicService.findOwnerById(1)).willReturn(ownerMapper.toOwner(owners.get(0)));
        int ownerId = owners.get(0).id();
        OwnerFieldsDto updatedOwnerDto = new OwnerFieldsDto("GeorgeI", "Franklin", "110 W. Liberty St.", "Madison", "6085551023");
        ObjectMapper mapper = new ObjectMapper();
        String newOwnerAsJSON = mapper.writeValueAsString(updatedOwnerDto);
        this.mockMvc.perform(put("/api/owners/" + ownerId)
                .content(newOwnerAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().contentType("application/json"))
            .andExpect(status().isNoContent());

        this.mockMvc.perform(get("/api/owners/" + ownerId)
                .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.id").value(ownerId))
            .andExpect(jsonPath("$.firstName").value("GeorgeI"));

    }

    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void testUpdateOwnerError() throws Exception {
        String newOwnerAsJSON = """
            {"firstName":"","lastName":"Franklin","address":"110 W. Liberty St.","city":"Madison","telephone":"6085551023"}
            """;
        this.mockMvc.perform(put("/api/owners/1")
                .content(newOwnerAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void testDeleteOwnerSuccess() throws Exception {
        OwnerDto newOwnerDto = owners.get(0);
        ObjectMapper mapper = new ObjectMapper();
        String newOwnerAsJSON = mapper.writeValueAsString(newOwnerDto);
        final Owner owner = ownerMapper.toOwner(owners.get(0));
        given(this.clinicService.findOwnerById(1)).willReturn(owner);
        this.mockMvc.perform(delete("/api/owners/1")
                .content(newOwnerAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void testDeleteOwnerError() throws Exception {
        OwnerDto newOwnerDto = owners.get(0);
        ObjectMapper mapper = new ObjectMapper();
        String newOwnerAsJSON = mapper.writeValueAsString(newOwnerDto);
        given(this.clinicService.findOwnerById(999)).willReturn(null);
        this.mockMvc.perform(delete("/api/owners/999")
                .content(newOwnerAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void testCreatePetSuccess() throws Exception {
        final Owner owner = ownerMapper.toOwner(owners.get(0));
        given(this.clinicService.findOwnerById(1)).willReturn(owner);
        doAnswer(invocation -> {
            var savedPet = invocation.getArgument(0, org.springframework.samples.petclinic.model.Pet.class);
            savedPet.setId(999);
            savedPet.getType().setName("dog");
            return null;
        }).when(this.clinicService).savePet(any());
        PetFieldsDto newPet = new PetFieldsDto("Rosy", pets.get(0).birthDate(), pets.get(0).type());
        ObjectMapper mapper =  JsonMapper.builder()
            .defaultDateFormat(new SimpleDateFormat("dd/MM/yyyy"))
            .build();
        String newPetAsJSON = mapper.writeValueAsString(newPet);
        System.err.println("--> newPetAsJSON=" + newPetAsJSON);
        this.mockMvc.perform(post("/api/owners/1/pets")
                .content(newPetAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void testCreatePetError() throws Exception {
        String newPetAsJSON = """
            {"name":null,"birthDate":"%s","type":{"name":"dog","id":2}}
            """.formatted(pets.get(0).birthDate());
        this.mockMvc.perform(post("/api/owners/1/pets")
                .content(newPetAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isBadRequest()).andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void testCreatePetShouldNotExposeTechnicalDetails() throws Exception {
        PetFieldsDto newPet = new PetFieldsDto("Rosy", pets.get(0).birthDate(), pets.get(0).type());
        ObjectMapper mapper =  JsonMapper.builder()
            .defaultDateFormat(new SimpleDateFormat("dd/MM/yyyy"))
            .build();
        String newPetAsJSON = mapper.writeValueAsString(newPet);
        String technicalMessage = "could not execute statement; SQL [insert into pets ...]; constraint [fk_pet_owner]";
        given(this.clinicService.findOwnerById(1)).willReturn(ownerMapper.toOwner(owners.get(0)));
        doThrow(new DataIntegrityViolationException(technicalMessage)).when(this.clinicService).savePet(any());
        this.mockMvc.perform(post("/api/owners/1/pets")
                .content(newPetAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.detail").value("The requested resource could not be processed due to a data constraint violation"));
    }

    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void testCreatePetWithUnknownOwnerShouldReturnNotFound() throws Exception {
        PetFieldsDto newPet = new PetFieldsDto("Rosy", pets.get(0).birthDate(), pets.get(0).type());
        ObjectMapper mapper = JsonMapper.builder()
            .defaultDateFormat(new SimpleDateFormat("dd/MM/yyyy"))
            .build();
        String newPetAsJSON = mapper.writeValueAsString(newPet);
        given(this.clinicService.findOwnerById(1000000)).willReturn(null);
        this.mockMvc.perform(post("/api/owners/1000000/pets")
                .content(newPetAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void testCreatePetWithNullTypeShouldReturnBadRequestWithGenericDetail() throws Exception {
        String newPetAsJSON = """
            {"name":"Rosy","birthDate":"%s","type":null}
            """.formatted(pets.get(0).birthDate());
        this.mockMvc.perform(post("/api/owners/1/pets")
                .content(newPetAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.detail").value("The request contains invalid or missing parameters"))
            .andExpect(jsonPath("$.title").value("MethodArgumentNotValidException"))
            .andExpect(jsonPath("$.schemaValidationErrors").isArray())
            .andExpect(jsonPath("$.schemaValidationErrors[0].message").exists())
            .andExpect(jsonPath("$.schemaValidationErrors[0].field").exists())
            .andExpect(jsonPath("$.schemaValidationErrors[0].rejectedValue").exists())
            .andExpect(jsonPath("$.schemaValidationErrors[0].defaultMessage").exists());
    }

    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void testCreatePetWithEmptyTypeNameShouldReturnBadRequestWithGenericDetail() throws Exception {
        String newPetAsJSON = """
            {"name":"Rosy","birthDate":"%s","type":{"name":"","id":2}}
            """.formatted(pets.get(0).birthDate());
        this.mockMvc.perform(post("/api/owners/1/pets")
                .content(newPetAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.detail").value("The request contains invalid or missing parameters"))
            .andExpect(jsonPath("$.title").value("MethodArgumentNotValidException"))
            .andExpect(jsonPath("$.schemaValidationErrors").isArray())
            .andExpect(jsonPath("$.schemaValidationErrors[0].message").exists())
            .andExpect(jsonPath("$.schemaValidationErrors[0].field").exists())
            .andExpect(jsonPath("$.schemaValidationErrors[0].rejectedValue").exists())
            .andExpect(jsonPath("$.schemaValidationErrors[0].defaultMessage").exists());
    }

    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void testCreatePetWithNullTypeIdShouldReturnBadRequestWithGenericDetail() throws Exception {
        String newPetAsJSON = """
            {"name":"Rosy","birthDate":"%s","type":{"name":"dog","id":null}}
            """.formatted(pets.get(0).birthDate());
        this.mockMvc.perform(post("/api/owners/1/pets")
                .content(newPetAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.detail").value("The request contains invalid or missing parameters"))
            .andExpect(jsonPath("$.title").value("MethodArgumentNotValidException"))
            .andExpect(jsonPath("$.schemaValidationErrors").isArray())
            .andExpect(jsonPath("$.schemaValidationErrors[0].message").exists())
            .andExpect(jsonPath("$.schemaValidationErrors[0].field").exists())
            .andExpect(jsonPath("$.schemaValidationErrors[0].rejectedValue").exists())
            .andExpect(jsonPath("$.schemaValidationErrors[0].defaultMessage").exists());
    }

    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void testCreatePetWithUnexpectedErrorShouldReturnInternalServerErrorWithGenericDetail() throws Exception {
        PetFieldsDto newPet = new PetFieldsDto("Rosy", pets.get(0).birthDate(), pets.get(0).type());
        ObjectMapper mapper = JsonMapper.builder()
            .defaultDateFormat(new SimpleDateFormat("dd/MM/yyyy"))
            .build();
        String newPetAsJSON = mapper.writeValueAsString(newPet);
        given(this.clinicService.findOwnerById(1)).willReturn(ownerMapper.toOwner(owners.get(0)));
        doThrow(new IllegalStateException("JDBC timeout while executing insert into pets"))
            .when(this.clinicService).savePet(any());
        this.mockMvc.perform(post("/api/owners/1/pets")
                .content(newPetAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.detail").value("An unexpected error occurred while processing your request"))
            .andExpect(jsonPath("$.title").value("IllegalStateException"))
            .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void testCreateOwnerWithUnexpectedErrorShouldReturnInternalServerErrorWithGenericDetail() throws Exception {
        OwnerFieldsDto newOwnerDto = new OwnerFieldsDto("George", "Franklin", "110 W. Liberty St.", "Madison", "6085551023");
        ObjectMapper mapper = new ObjectMapper();
        String newOwnerAsJSON = mapper.writeValueAsString(newOwnerDto);
        doThrow(new RuntimeException("Low-level persistence exception details"))
            .when(this.clinicService).saveOwner(any());
        this.mockMvc.perform(post("/api/owners")
                .content(newOwnerAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.detail").value("An unexpected error occurred while processing your request"))
            .andExpect(jsonPath("$.title").value("RuntimeException"))
            .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void testCreateVisitSuccess() throws Exception {
        VisitFieldsDto newVisit = new VisitFieldsDto(visits.get(0).date(), visits.get(0).description());
        doAnswer(invocation -> {
            var savedVisit = invocation.getArgument(0, org.springframework.samples.petclinic.model.Visit.class);
            savedVisit.setId(999);
            return null;
        }).when(this.clinicService).saveVisit(any());
        ObjectMapper mapper = new ObjectMapper();
        String newVisitAsJSON = mapper.writeValueAsString(newVisit);
        System.out.println("newVisitAsJSON " + newVisitAsJSON);
        this.mockMvc.perform(post("/api/owners/1/pets/1/visits")
                .content(newVisitAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void testGetOwnerPetSuccess() throws Exception {
        var owner = ownerMapper.toOwner(owners.get(0));
        given(this.clinicService.findOwnerById(2)).willReturn(owner);
        var pet = petMapper.toPet(pets.get(0));
        pet.setOwner(owner);
        given(this.clinicService.findPetById(1)).willReturn(pet);
        this.mockMvc.perform(get("/api/owners/2/pets/1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"));
    }

    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void testGetOwnersPetsWithOwnerNotFound() throws Exception {
        owners.clear();
        given(this.clinicService.findAllOwners()).willReturn(ownerMapper.toOwners(owners));
        this.mockMvc.perform(get("/api/owners/1/pets/1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void testGetOwnersPetsWithPetNotFound() throws Exception {
        var owner1 = ownerMapper.toOwner(owners.get(0));
        given(this.clinicService.findOwnerById(1)).willReturn(owner1);
        this.mockMvc.perform(get("/api/owners/1/pets/2")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }


    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void testUpdateOwnersPetSuccess() throws Exception {
        int ownerId = owners.get(0).id();
        int petId = pets.get(0).id();
        given(this.clinicService.findOwnerById(ownerId)).willReturn(ownerMapper.toOwner(owners.get(0)));
        given(this.clinicService.findPetById(petId)).willReturn(petMapper.toPet(pets.get(0)));
        PetFieldsDto updatedPetDto = new PetFieldsDto("Rex", LocalDate.of(2020, 1, 15), pets.get(0).type());
        ObjectMapper mapper =  JsonMapper.builder()
            .defaultDateFormat(new SimpleDateFormat("dd/MM/yyyy"))
            .build();
        String updatedPetAsJSON = mapper.writeValueAsString(updatedPetDto);
        this.mockMvc.perform(put("/api/owners/" + ownerId + "/pets/" + petId)
                .content(updatedPetAsJSON)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void testUpdateOwnersPetOwnerNotFound() throws Exception {
        int ownerId = 0;
        int petId = pets.get(0).id();
        given(this.clinicService.findOwnerById(ownerId)).willReturn(null);
        PetFieldsDto petDto = new PetFieldsDto("Thor", pets.get(0).birthDate(), pets.get(0).type());
        ObjectMapper mapper =  JsonMapper.builder()
            .defaultDateFormat(new SimpleDateFormat("dd/MM/yyyy"))
            .build();
        String updatedPetAsJSON = mapper.writeValueAsString(petDto);
        this.mockMvc.perform(put("/api/owners/" + ownerId + "/pets/" + petId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedPetAsJSON))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void testUpdateOwnersPetPetNotFound() throws Exception {
        int ownerId = owners.get(0).id();
        int petId = 0;
        given(this.clinicService.findOwnerById(ownerId)).willReturn(ownerMapper.toOwner(owners.get(0)));
        given(this.clinicService.findPetById(petId)).willReturn(null);
        PetFieldsDto petDto = new PetFieldsDto("Ghost", LocalDate.of(2020, 1, 1), pets.get(0).type());
        ObjectMapper mapper =  JsonMapper.builder()
            .defaultDateFormat(new SimpleDateFormat("dd/MM/yyyy"))
            .build();
        String updatedPetAsJSON = mapper.writeValueAsString(petDto);
        this.mockMvc.perform(put("/api/owners/" + ownerId + "/pets/" + petId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedPetAsJSON))
            .andExpect(status().isNotFound());
    }

}
