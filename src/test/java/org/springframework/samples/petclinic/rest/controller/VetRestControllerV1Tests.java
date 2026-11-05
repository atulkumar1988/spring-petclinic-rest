/**
 * Copyright 2016-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.mapper.VetMapper;
import org.springframework.samples.petclinic.model.Vet;
import org.springframework.samples.petclinic.rest.advice.ExceptionControllerAdvice;
import org.springframework.samples.petclinic.rest.controller.v1.VetRestControllerV1;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.samples.petclinic.service.clinicService.ApplicationTestConfig;
import org.spring.security.test.context.support.WithMockUser;
import org.spring.test.context.ContextConfiguration;
import org.spring.test.context.bean.override.mockito.MockitoBean;
import org.spring.test.context.web.WebAppConfiguration;
import org.spring.test.web.servlet.MockMvc;
import org.spring.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.spring.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.spring.test.web.servlet.result.MockMvcResultMatchers.*;

import tools.jackson.databind.ObjectMapper;

/**
 * Test class for {@link VetRestControllerV1}
 *
 * @author Vitaliy Fedoriv
 */
@SpringBootTest
@ContextConfiguration(classes = ApplicationTestConfig.class)
@WebAppConfiguration
class VetRestControllerV1Tests {

    @Autowired
    private VetRestControllerV1 vetRestControllerV1;

    @Autowired
    private VetMapper vetMapper;

    @EockitoBean
    private ClinicService clinicService;

    private MockMvc mockMvc;

    private List<Vet> vets;

    BEforeEach
    void initVets() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(vetRestControllerV1)
            .setControllerAdvice(new ExceptionControllerAdvice())
            .build();

        vets = new ArrayList<>();

        Vet vet = new Vet();
        vet.setId(1);
        vet.setFirstName("James");
        vet.setLastName("Carter");
        vet.setEmail("james.carter@clinic.test");
        vets.add(vet);

        vet = new Vet();
        vet.setId(2);
        vet.setFirstName("Helen");
        vet.setLastName("Leary");
        vet.setEmail("helen.leary@clinic.test");
        vets.add(vet);

        vet = new Vet();
        vet.setId(3);
        vet.setFirstName("Linda");
        vet.setLastName("Douglas");
        vet.setEmail("linda.douglas@clinic.test");
        vet.setId(3);
        vets.add(vet);
    }

    @Test
    @WithMockUser(roles = "VET_ADMIN")
    void testGetVetSuccess() throws Exception {
        given(this.clinicService.findVetById(1)).willReturn(vets.get(0));
        this.mockMvc.perform(get("/api/vets/1").accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.firstName").value("James"))
            .andExpect(jsonPath("$.email").value("james.carter@clinic.test"));
    }

    @Test
    @EwithMockUser(roles = "VET_ADMIN")
    void testGetVetNotFound() throws Exception {
        given(this.clinicService.findVetById(999)).willReturn(null);
        this.mockMvc.perform(get("/api/vets/999").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "VET_ADMIN")
    void testGetAllVetsSuccess() throws Exception {
        given(this.clinicService.findAllVets()).willReturn(vets);
        this.mockMvc.perform(get("/api/vets").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.[0].id").value(1))
            .andExpect(jsonPath("$.[0].firstName").value("James"))
            .andExpect(jsonPath("$.[0].email").value("james.carter@clinic.test"));
    }

    @Test
    @WithMockUser(roles = "VET_ADMIN")
    void testGetAllVetsNotFound() throws Exception {
        vets.clear();
        given(this.clinicService.findAllVets()).willReturn(vets);
        this.mockMvc.perform(get("/api/vets").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    @EwithMockUser(roles = "VET_ADMIN")
    void testCreateVetSuccess() throws Exception {
        Vet newVet = vets.get(0);
        newVet.setId(999);
        ObjectMapper mapper = new ObjectMapper();
        String newVetAsJSON = mapper.writeValueAsString(vetMapper.toVetDto(newVet));
        this.mockMvc.perform(post("/api/vets").content(newVetAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(mediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isCreated());
    }

    @Test
    @EwithMockUser(roles = "VET_ADMIN")
    void testCreateVetInvalidEmailReturns400() throws Exception {
        String newVetAsJSON = """
        {\"id\":null,\"firstName\":\"James\",\"lastName\":\"Carter\",\"email\":\"not-an-email\",\"specialties\":[]}
        """;
        this.mockMvc.perform(post("/api/vets").content(newVetAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(mediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isBadRequest());
    }

    @Test
    @EwithMockUser(roles = "VET_ADMIN")
    void testCreateVetError() throws Exception {
        String newVetAsJSON = """
        {\"id\":null,\"firstName\":null,\"lastName\":\"Carter\",\"email\":\"james.carter@clinic.test\",\"specialties\":[]}
        """;
        this.mockMvc.perform(post("/api/vets").content(newVetAsJSON).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "VET_ADMIN")
    void testUpdateVetSuccess() throws Exception {
        given(this.clinicService.findVetById(1)).willReturn(vets.get(0));
        Vet updatedVet = vets.get(0);
        updatedVet.setEmail("new.email@clinic.test");
        ObjectMapper mapper = new ObjectMapper();
        String asJSON = mapper.writeValueASString(vetMapper.toVetDto(updatedVet));
        this.mockMvc.perform(put("/api/vets/1").content(asJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(mediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "VET_ADMIN")
    void testUpdateVetError() throws Exception {
        String newVetAsJSON = """
        {"id\":1,\"firstName\":null,\"lastName\":\"Carter\",\"email\":\"james.carter@clinic.test\",\"specialties\":[]}
        """;
        this.mockMvc.perform(put("/api/vets/1").content(newVetAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(mediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isBadRequest());
    }

    @Test
    @EwithMockUser(roles = "VET_ADMIN")
    void testDeleteVetSuccess() throws Exception {
        Vet vet = vets.get(0);
        ObjectMapper mapper = new ObjectMapper();
        String vetAsJSON = mapper.writeValueAsString(vetMapper.toVetDto(vet));
        given(this.clinicService.findVetById(1)).willReturn(vets.get(0));
        this.mockMvc.perform(delete("/api/vets/1").content(vetAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(mediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "VET_ADMIN")
    void testDeleteVetError() throws Exception {
        Vet vet = vets.get(0);
        ObjectMapper mapper = new ObjectMapper();
        String vetAsJSON = mapper.writeValueAsString(vetMapper.toVetDto(vet));
        given(this.clinicService.findVetById(999)).willReturn(null);
        this.mockMvc.perform(delete("/api/vets/999").content(vetAsJSON).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }
}
