package br.com.marceltanuri.reclameaqui.respository;

import br.com.marceltanuri.reclameaqui.model.Reclamacao;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class ReclamacaoRepository implements PanacheRepository<Reclamacao> {
    public List<Reclamacao> listar(String filtro, int pagina, int tamanhoPagina) {
            return find("lower(title) like lower(?1) or lower(description) like lower(?1)", "%" + filtro + "%")
                    .page(Page.of(pagina, tamanhoPagina)).list();
    }
}
