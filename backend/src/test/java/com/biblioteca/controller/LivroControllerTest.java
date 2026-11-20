package com.biblioteca.controller;

import com.biblioteca.model.Autor;
import com.biblioteca.model.Livro;
import com.biblioteca.repository.AutorRepository;
import com.biblioteca.repository.LivroRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LivroController.class)
class LivroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LivroRepository livroRepository;

    @MockBean
    private AutorRepository autorRepository;

    private Autor autor() {
        Autor autor = new Autor();
        autor.setId(1L);
        autor.setNome("Machado de Assis");
        return autor;
    }

    private Livro novoLivro(Long id, String titulo) {
        Livro livro = new Livro();
        livro.setId(id);
        livro.setTitulo(titulo);
        livro.setAnoPublicacao(1899);
        livro.setGenero("Romance");
        livro.setAutor(autor());
        return livro;
    }

    private static final String JSON_LIVRO = """
            { "titulo": "Dom Casmurro", "isbn": "978-8535910667", "anoPublicacao": 1899,
              "genero": "Romance", "autor": { "id": 1 } }
            """;

    @Test
    @DisplayName("Deve criar livro vinculado a um autor (201)")
    void deveCriarLivroComSucesso() throws Exception {
        when(autorRepository.findById(1L)).thenReturn(Optional.of(autor()));
        when(livroRepository.save(any(Livro.class))).thenReturn(novoLivro(1L, "Dom Casmurro"));

        mockMvc.perform(post("/api/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_LIVRO))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.titulo").value("Dom Casmurro"))
                .andExpect(jsonPath("$.autor.nome").value("Machado de Assis"));
    }

    @Test
    @DisplayName("Deve retornar 400 ao criar livro sem autor")
    void deveRetornar400SemAutor() throws Exception {
        mockMvc.perform(post("/api/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\": \"Sem autor\"}"))
                .andExpect(status().isBadRequest());

        verify(livroRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve retornar 400 quando o autor não existe")
    void deveRetornar400AutorInexistente() throws Exception {
        when(autorRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_LIVRO))
                .andExpect(status().isBadRequest());

        verify(livroRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve retornar 404 para id inexistente")
    void deveRetornar404ParaIdInexistente() throws Exception {
        when(livroRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/livros/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve listar livros ordenados por título")
    void deveListarLivrosOrdenados() throws Exception {
        when(livroRepository.findAllByOrderByTituloAsc()).thenReturn(List.of(
                novoLivro(2L, "Dom Casmurro"),
                novoLivro(1L, "Memórias Póstumas de Brás Cubas")));

        mockMvc.perform(get("/api/livros"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].titulo").value("Dom Casmurro"))
                .andExpect(jsonPath("$[1].titulo").value("Memórias Póstumas de Brás Cubas"));
    }

    @Test
    @DisplayName("Deve atualizar livro e trocar o autor (200)")
    void deveAtualizarLivro() throws Exception {
        Autor outroAutor = new Autor();
        outroAutor.setId(2L);
        outroAutor.setNome("Clarice Lispector");
        when(livroRepository.findById(1L)).thenReturn(Optional.of(novoLivro(1L, "Dom Casmurro")));
        when(autorRepository.findById(2L)).thenReturn(Optional.of(outroAutor));
        when(livroRepository.save(any(Livro.class))).thenAnswer(inv -> inv.getArgument(0));

        mockMvc.perform(put("/api/livros/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\": \"A Hora da Estrela\", \"autor\": {\"id\": 2}}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("A Hora da Estrela"))
                .andExpect(jsonPath("$.autor.nome").value("Clarice Lispector"));
    }

    @Test
    @DisplayName("Deve retornar 404 ao deletar livro inexistente")
    void deveRetornar404AoDeletar() throws Exception {
        when(livroRepository.existsById(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/livros/99"))
                .andExpect(status().isNotFound());

        verify(livroRepository, never()).deleteById(any());
    }
}
