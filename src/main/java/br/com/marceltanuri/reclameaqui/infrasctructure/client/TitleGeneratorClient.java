package br.com.marceltanuri.reclameaqui.infrasctructure.client;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

@RegisterRestClient(configKey = "title-generator-api")
public interface TitleGeneratorClient {

    @GET
    List<String> generate(@QueryParam("type") String type,
                          @QueryParam("sentences") int sentences,
                          @QueryParam("start-with-lorem") int startWithLorem);

}
