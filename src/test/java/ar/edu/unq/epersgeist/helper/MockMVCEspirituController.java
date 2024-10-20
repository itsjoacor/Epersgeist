package ar.edu.unq.epersgeist.helper;

import ar.edu.unq.epersgeist.controller.dto.espiritu.EspirituBody;
import ar.edu.unq.epersgeist.controller.dto.espiritu.EspirituDTO;
import ar.edu.unq.epersgeist.modelo.espiritu.Espiritu;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collection;
import java.util.List;

@Component
public class MockMVCEspirituController {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    public ResultActions performRequest(MockHttpServletRequestBuilder requestBuilder) throws Throwable {
        try {
            return mockMvc.perform(requestBuilder);
        } catch (ServletException e) {
            throw e.getCause();
        }
    }

    private EspirituDTO guardarEspiritu(Espiritu espiritu, HttpStatus expectedStatus) throws Throwable {
        EspirituBody body = EspirituBody.desdeModelo(espiritu);
        String json = objectMapper.writeValueAsString(body);

        String espirituDTOString = (
                performRequest(MockMvcRequestBuilders.post("/espiritu")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                        .andExpect(MockMvcResultMatchers.status().is(expectedStatus.value()))
                        .andReturn().getResponse().getContentAsString());

        EspirituDTO espirituDTO = objectMapper.readValue(espirituDTOString, EspirituDTO.class);

        espiritu.setId(espirituDTO.getId());

        return EspirituDTO.desdeModelo(espiritu);
    }

    public EspirituDTO guardarEspiritu(Espiritu espiritu) throws Throwable {
        return guardarEspiritu(espiritu, HttpStatus.OK);
    }

    public EspirituDTO recuperarEspiritu(Long espirituId) throws Throwable {
        String json = performRequest(MockMvcRequestBuilders.get("/espiritu/" + espirituId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();

        EspirituDTO dto = objectMapper.readValue(json, EspirituDTO.class);
        return dto;
    }

    public Collection<EspirituDTO> recuperarTodos() throws Throwable {
        String json = performRequest(MockMvcRequestBuilders.get("/espiritu"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();

        Collection<EspirituDTO> dtos = objectMapper.readValue(
                json,
                objectMapper.getTypeFactory().constructCollectionType(List.class, EspirituDTO.class)
        );

        return dtos;
    }

    public void eliminarEspiritu(Long espirituId) throws Throwable {
        performRequest(MockMvcRequestBuilders.delete("/espiritu/" + espirituId))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
