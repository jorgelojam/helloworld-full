package ec.jtux.services;

import java.sql.Connection;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.jboss.logging.Logger;

import ec.jtux.model.Parametro;

@Path("/parametros")
@OpenAPIDefinition(info = @Info(title = "Parametros Resources", description = "Servicios para informacion de parametros", version = "1.0"))

public class ParametrosRepo {

    @Inject
    private EntityManager em;

    @Inject
    private Logger log;

    @Inject
    private Connection con;

    @GET
    @Counted(description = "Contador parametros", absolute = true)
	@Timed(name = "parametros-time", description = "Tiempo de procesamiento de parametros", unit = MetricUnits.MILLISECONDS, absolute = true)
	@Produces({ MediaType.APPLICATION_JSON })
	@Operation(description = "Lista los parametros del sistema", summary = "Listar parametros")
	@APIResponse(responseCode = "200", description = "Parametros sistema",
	             content = @Content(mediaType = MediaType.APPLICATION_JSON ,
	                                schema = @Schema(implementation = Parametro.class)))
    public List<Parametro> listarParametros(){
        log.info("Consulta JPA");
        TypedQuery<Parametro> q = em.createQuery("SELECT p FROM Parametro p", Parametro.class);
        return q.getResultList();
    }
    
}
