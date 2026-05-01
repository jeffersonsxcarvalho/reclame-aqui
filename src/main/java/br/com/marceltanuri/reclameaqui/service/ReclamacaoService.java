
package br.com.marceltanuri.reclameaqui.service;

import br.com.marceltanuri.reclameaqui.model.Reclamacao;
import br.com.marceltanuri.reclameaqui.infrasctructure.client.TitleGeneratorClient;
import br.com.marceltanuri.reclameaqui.respository.ReclamacaoRepository;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;


@ApplicationScoped
public class ReclamacaoService {

    @Inject
    ReclamacaoRepository reclamacaoRepository;

    @Inject
    @RestClient
    TitleGeneratorClient titleGeneratorClient;

    public List<Reclamacao> listar(String filtro, int pagina, int tamanhoPagina) {
        if (filtro != null && !filtro.isEmpty()) {
            return reclamacaoRepository.listar(filtro, pagina, tamanhoPagina);
        }
        return reclamacaoRepository.listAll();
    }

    public Reclamacao buscar(Long id) {
        return reclamacaoRepository.findById(id);
    }

    @Transactional
    public void criar(Reclamacao reclamacao) {
        if (reclamacao.getTitle() == null || reclamacao.getTitle().isBlank()) {
            List<String> response = titleGeneratorClient.generate("all-meat", 1, 1);
            if (response != null && !response.isEmpty()) {
                reclamacao.setTitle(response.get(0));
            }
        }
        reclamacaoRepository.persist(reclamacao);
    }

    @Transactional
    public Reclamacao atualizar(Long id, Reclamacao reclamacaoAtualizada) {
        Reclamacao existente = buscar(id);
        if (existente != null) {
            existente.setTitle(reclamacaoAtualizada.getTitle());
            existente.setDescription(reclamacaoAtualizada.getDescription());
            existente.setLocale(reclamacaoAtualizada.getLocale());
            existente.setCompany(reclamacaoAtualizada.getCompany());
        }
        return existente;
    }

    @Transactional
    public void deletar(Long id) {
        reclamacaoRepository.deleteById(id);
    }
}

