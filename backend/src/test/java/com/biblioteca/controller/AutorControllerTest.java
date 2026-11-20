package com.biblioteca.controller;

import com.biblioteca.model.Autor;
import com.biblioteca.repository.AutorRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AutorController.class)
class AutorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AutorRepository autorRepository;

    private Autor novoAutor(Long id, String nome) {
        Autor autor = new Autor();
        autor.setId(id);
        autor.setNome(nome);
        autor.setNacionalidade("Brasileira");
        autor.setDataNascimento(LocalDate.of(1839, 6, 21));
        return autor;
    }

    @Test
    @DisplayName("Deve criar autor com sucesso (201)")
    void deveCriarAutorComSucesso() throws Exception {
        Autor salvo = novoAutor(1L, "Machado de Assis");
        when(autorRepository.save(any(Autor.class))).thenReturn(salvo);

        mockMvc.perform(post("/api/autores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(novoAutor(null, "Machado de Assis"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Machado de Assis"));

        verify(autorRepository, times(1)).save(any(Autor.class));
    }

    @Test
    @DisplayName("Deve retornar 400 ao criar autor sem nome")
    void deveRetornar400SemNome() throws Exception {
        mockMvc.perform(post("/api/autores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\": \"\"}"))
                .andExpect(status().isBadRequest());

        verify(autorRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve retornar 404 para id inexistente")
    void deveRetornar404ParaIdInexistente() throws Exception {
        when(autorRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/autores/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve listar autores ordenados por nome")
    void deveListarAutoresOrdenados() throws Exception {
        when(autorRepository.findAllByOrderByNomeAsc()).thenReturn(List.of(
                novoAutor(2L, "Clarice Lispector"),
                novoAutor(1L, "Machado de Assis")));

        mockMvc.perform(get("/api/autores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nome").value("Clarice Lispector"))
                .andExpect(jsonPath("$[1].nome").value("Machado de Assis"));
    }

    @Test
    @DisplayName("Deve atualizar autor existente (200)")
    void deveAtualizarAutor() throws Exception {
        when(autorRepository.findById(1L)).thenReturn(Optional.of(novoAutor(1L, "Machado")));
        when(autorRepository.save(any(Autor.class))).thenAnswer(inv -> inv.getArgument(0));

        mockMvc.perform(put("/api/autores/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(novoAutor(null, "Machado de Assis"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Machado de Assis"));
    }

    @Test
    @DisplayName("Deve deletar autor existente (200)")
    void deveDeletarAutor() throws Exception {
        when(autorRepository.existsById(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/autores/1"))
                .andExpect(status().isOk());

        verify(autorRepository).deleteById(1L);
    }
}
