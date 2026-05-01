package br.com.marceltanuri.reclameaqui;

import br.com.marceltanuri.reclameaqui.infrasctructure.client.TitleGeneratorClient;
import br.com.marceltanuri.reclameaqui.model.Reclamacao;
import br.com.marceltanuri.reclameaqui.respository.ReclamacaoRepository;
import br.com.marceltanuri.reclameaqui.service.ReclamacaoService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@QuarkusTest
public class ReclamacaoServiceTest {

    @Inject
    ReclamacaoService reclamacaoService;

    @InjectMock
    ReclamacaoRepository reclamacaoRepository;

    @InjectMock
    @RestClient
    TitleGeneratorClient generatorClient;

    //Aqui eu preferi focar em cada method do service separadamente.

    //1 - Listar

    @Test
    @DisplayName("Caminho Feliz: Se o filtro fornulo, tem que retornar todas as reclamacoes.")
    void deveListarTudo() {
        // Arrange
        List<Reclamacao> listaMock = Arrays.asList(new Reclamacao(), new Reclamacao());
        when(reclamacaoRepository.listAll()).thenReturn(listaMock);

        // Act
        List<Reclamacao> resultado = reclamacaoService.listar(null, 0, 10);

        // Assert
        assertEquals(2, resultado.size());
        verify(reclamacaoRepository, times(1)).listAll();
        verify(reclamacaoRepository, never()).listar(any(), anyInt(), anyInt());

    }

    @Test
    @DisplayName("Caminho Feliz: Deve filtrar de acordo com o que a gente coloca no 'filtro'")
    void deveListarComFiltro() {
        // Arrange
        String filtro = "Atraso";
        when(reclamacaoRepository.listar(eq(filtro), anyInt(), anyInt()))
                .thenReturn(Collections.singletonList(new Reclamacao()));

        List<Reclamacao> resultado = reclamacaoService.listar(filtro, 0, 10);

        assertEquals(1, resultado.size());
        verify(reclamacaoRepository, times(1)).listar(eq(filtro), eq(0), eq(10));
    }

    @Test
    @DisplayName("Caminho Não Feliz: Se não tiver nada no filtro, deve-se retornar tudo.")
    void deveIgnorarFiltroVazio() {

        // Act
        reclamacaoService.listar("", 0, 10);

        // Assert
        verify(reclamacaoRepository, times(1)).listAll();
        verify(reclamacaoRepository, never()).listar(anyString(), anyInt(), anyInt());
    }

    // 2 - CRIAR

    @Test
    @DisplayName("Caminho Feliz: Deve só persistir caso o títula já tenha sido feito.")
    void deveCriarComTituloPreenchido() {
        // Arrange
        Reclamacao reclamacao = new Reclamacao();
        reclamacao.setTitle("Meu Título Manual");

        // Act
        reclamacaoService.criar(reclamacao);

        // Assert
        verify(reclamacaoRepository, times(1)).persist(reclamacao);
        verifyNoInteractions(generatorClient);
    }

    @Test
    @DisplayName("Caminho Feliz: Aqui sim a gente utiliza aquela API")
    void deveGerarTituloQuandoVazio() {
        // Arrange
        Reclamacao reclamacao = new Reclamacao();
        reclamacao.setTitle(""); // Título vazio aciona o if

        List<String> titulosMock = Collections.singletonList("Título Gerado pela API");

        when(generatorClient.generate("all-meat", 1, 1)).thenReturn(titulosMock);

        // Act
        reclamacaoService.criar(reclamacao);

        // Assert
        assertEquals("Título Gerado pela API", reclamacao.getTitle());
        verify(generatorClient, times(1)).generate(anyString(), anyInt(), anyInt());
        verify(reclamacaoRepository, times(1)).persist(reclamacao);
    }

    @Test
    @DisplayName("Caminho não feliz: Deve persistir sem a API que gera os títulos")
    void devePersistirMesmoComFalhaNaApi() {
        // Arrange
        Reclamacao reclamacao = new Reclamacao();
        reclamacao.setTitle(null);

        when(generatorClient.generate(anyString(), anyInt(), anyInt()))
                .thenReturn(Collections.emptyList());

        // Act
        reclamacaoService.criar(reclamacao);

        // Assert
        assertNull(reclamacao.getTitle());
        verify(reclamacaoRepository, times(1)).persist(reclamacao);
    }

    // 3 - ATUALIZAR

    @Test
    @DisplayName("Caminho Feliz: Deve atualizar todos os campos quando o ID existe")
    void deveAtualizarComSucesso() {
        // Arrange
        Long idExistente = 1L;

        Reclamacao reclamacaoDoBanco = new Reclamacao();
        reclamacaoDoBanco.setId(idExistente);
        reclamacaoDoBanco.setTitle("Título Antigo que ainda vai mudar");
        reclamacaoDoBanco.setDescription("Descrição para a gente trocar para uma nova.");

        Reclamacao reclamacaoAtualizada = new Reclamacao();
        reclamacaoAtualizada.setTitle("Título Novo");
        reclamacaoAtualizada.setDescription("Nova descrição que, lembrando, precisa ter mais de 20 caracteres.");
        reclamacaoAtualizada.setLocale("Rio de Janeiro");
        reclamacaoAtualizada.setCompany("Empresa X");

        when(reclamacaoRepository.findById(idExistente)).thenReturn(reclamacaoDoBanco);

        // Act
        Reclamacao resultado = reclamacaoService.atualizar(idExistente, reclamacaoAtualizada);

        // Assert
        assertNotNull(resultado);
        assertEquals("Título Novo", resultado.getTitle());
        assertEquals("Rio de Janeiro", resultado.getLocale());
        verify(reclamacaoRepository, times(1)).findById(idExistente);
    }

    @Test
    @DisplayName("Caminho Triste: Deve retornar null caso o id ñ exista.")
    void deveRetornarNullSeNaoExistir() {
        // Arrange
        Long idInexistente = 999L;
        when(reclamacaoRepository.findById(idInexistente)).thenReturn(null);

        // Act
        Reclamacao resultado = reclamacaoService.atualizar(idInexistente, new Reclamacao());

        // Assert
        assertNull(resultado);
        verify(reclamacaoRepository, times(1)).findById(idInexistente);
        verifyNoMoreInteractions(reclamacaoRepository);
    }

    // 4 - DELETAR

    @Test
    @DisplayName("Caminho Feliz: Deve solicitar a exclusão ao repositorio com o Id correto")
    void deveDeletarComSucesso() {
        // Arrange
        Long idParaDeletar = 50L;

        // Act
        reclamacaoService.deletar(idParaDeletar);

        // Assert
        verify(reclamacaoRepository, times(1)).deleteById(idParaDeletar);
    }

    @Test
    @DisplayName("Caminho Triste: Deve lançar uma exceção se o repositório falhar")
    void deveLancarExcecaoQuandoRepositorioFalha() {
        // Arrange
        Long idAlvo = 1L;
        doThrow(new RuntimeException("Erro de conexão")).when(reclamacaoRepository).deleteById(idAlvo);

        // Act
        assertThrows(RuntimeException.class, () -> {
            reclamacaoService.deletar(idAlvo);
        });
    }

}