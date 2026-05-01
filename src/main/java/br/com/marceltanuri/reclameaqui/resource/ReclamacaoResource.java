package br.com.marceltanuri.reclameaqui.resource;

import br.com.marceltanuri.reclameaqui.model.Reclamacao;
import br.com.marceltanuri.reclameaqui.service.ReclamacaoService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

import jakarta.validation.Valid;

@Path("/reclamacoes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReclamacaoResource {

    @Inject
    ReclamacaoService service;

    @GET
    public List<Reclamacao> listar(@QueryParam("filtro") String filtro,
                                   @QueryParam("pagina") @DefaultValue("0") int pagina,
                                   @QueryParam("tamanhoPagina") @DefaultValue("10") int tamanhoPagina) {
        return service.listar(filtro, pagina, tamanhoPagina);
    }

    @GET
    @Path("/{id}")
    public Response buscar(@PathParam("id") Long id) {
        Reclamacao reclamacao = service.buscar(id);
        if (reclamacao == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(reclamacao).build();
    }

    @POST
    public Response criar(@Valid Reclamacao reclamacao) {
        service.criar(reclamacao);
        return Response.status(Response.Status.CREATED).entity(reclamacao).build();
    }

    @PUT
    @Path("/{id}")
    public Response atualizar(@PathParam("id") Long id, Reclamacao reclamacaoAtualizada) {
        Reclamacao atualizada = service.atualizar(id, reclamacaoAtualizada);
        if (atualizada == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(atualizada).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deletar(@PathParam("id") Long id) {
        service.deletar(id);
        return Response.noContent().build();
    }
}
