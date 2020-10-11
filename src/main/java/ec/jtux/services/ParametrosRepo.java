package ec.jtux.services;

import java.sql.Connection;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
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

    @GET
    @Path("/{codigo}")
    @Produces({MediaType.APPLICATION_JSON})
    @Counted(description = "Contador parametro", absolute = true)
	@Timed(name = "parametro-time", description = "Tiempo de procesamiento de parametro", unit = MetricUnits.MILLISECONDS, absolute = true)
    @Operation(description = "Consigue parametro del sistema", summary = "Parametro sistema")
	@APIResponses(value={
	@APIResponse(responseCode = "200", description = "Parametro sistema",
	             content = @Content(mediaType = MediaType.APPLICATION_JSON ,
	                                schema = @Schema(implementation = Parametro.class))),
    @APIResponse(responseCode = "400", description = "Parametro no existe",
                content = @Content(mediaType = MediaType.TEXT_PLAIN ,
                                    schema = @Schema(implementation = String.class)))}
	)
    public Response consultaParametro(@Parameter(description = "Codigo de parametro", required = true)
                                     @PathParam("codigo") String codigo){
        log.info("Consulta JPA");
        TypedQuery<Parametro> q = em.createQuery("SELECT p FROM Parametro p WHERE p.codigo = :codigo", Parametro.class);
        Parametro p =q.setParameter("codigo",codigo).getSingleResult();
        if (p == null)
            return Response.status(Response.Status.NOT_FOUND)
                .entity(p)
                .build();
        else
            return Response.ok(p).build();
    }
    
}
