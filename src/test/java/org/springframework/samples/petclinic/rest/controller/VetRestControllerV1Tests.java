package org.springframework.samples.petclinic.rest.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.model.Specialty;
import org.springframework.samples.petclinic.model.Vet;
import org.springframework.samples.petclinic.rest.advice.ExceptionControllerAdvice;
import org.springframework.samples.petclinic.rest.controller.v1.VetRestControllerV1;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.samples.petclinic.service.clinicService.ApplicationTestConfig;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ContextConfiguration(classes = ApplicationTestConfig.class)
@WebAppConfiguration
class VetRestControllerV15ests {

    @\Autowired
    private VetRestControllerV1 vetRestControllerV1;

    @MockitoBean
    private ClinicService clinicService;

    private MockMvc mockMvc;

    private List<Vet> vets;

    @BeforeEach
    void init() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(vetRestControllerV1)
            .setControllerAdvice(new ExceptionControllerAdvice())
            .build();

        vets = new ArrayList<>();

        Vet james = new Vet();
        james.setId(1);
        james.setFirstName("James");
        james.setLastName("Carter");
        james.setEmail("james.carter@clinic.test");
        Specialty specialty = new Specialty();
        specialty.setId(1);
        specialty.setName("radiology");
        james.addSpecialty(specialty);
        vets.add(james);
    }

    @Test
    @WithMockUser(roles = "VET_ADMIN")
    void testGetVetIncludesEmail() throws Exception {
        given(this.clinicService.findVetById(1)).willReturn(vets.get(0));

        this.mockMvc.perform(get("/api/vets/1").accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("james.carter@clinic.test"));
    }

    @Test
    @WithMockUser(roles = "VET_ADMIN")
    void testCreateVetInvalidEmailReturnsBadRequest() throws Exception {
        String body = "{\"firstName\":\"James\",\"lastName\":\"Carter\",\"email\":\"not-an-email\",\"specialties\":[],\"id\":1}";

        this.mockMvc.perform(post("/api/vets").contentType(MediaType.APPLICATION_JSON_VALUE).content(body))
            .andExpect(status().isBadRequest());
    }
}
