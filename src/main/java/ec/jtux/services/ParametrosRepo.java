package ec.jtux.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
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
    @APIResponse(responseCode = "200", description = "Parametros sistema", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Parametro.class)))
    public List<Parametro> listarParametros() {
        log.info("Consulta JPA");
        TypedQuery<Parametro> q = em.createQuery("SELECT p FROM Parametro p", Parametro.class);
        return q.getResultList();
    }

    @GET
    @Path("/{codigo}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Counted(description = "Contador parametro", absolute = true)
    @Timed(name = "parametro-time", description = "Tiempo de procesamiento de parametro", unit = MetricUnits.MILLISECONDS, absolute = true)
    @Operation(description = "Consigue parametro del sistema", summary = "Parametro sistema")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Parametro sistema", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Parametro.class))),
            @APIResponse(responseCode = "404", description = "Parametro no existe", content = @Content(mediaType = MediaType.TEXT_PLAIN, schema = @Schema(implementation = String.class))) })
    public Response consultaParametro(
            @Parameter(description = "Codigo de parametro", required = true) @PathParam("codigo") String codigo) {
        log.info("Consulta JPA");
        TypedQuery<Parametro> q = em.createQuery("SELECT p FROM Parametro p WHERE p.codigo = :codigo", Parametro.class);
        Parametro p = null;
        try {
            p = q.setParameter("codigo", codigo).getSingleResult();
            return Response.ok(p).build();
        } catch (NoResultException e) {
            return Response.status(Response.Status.NOT_FOUND).entity("No hay resultados").build();
        }
    }

    @GET
    @Path("/query/{valor}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Counted(description = "Contador valor", absolute = true)
    @Timed(name = "valor-time", description = "Tiempo de procesamiento de parametro valor", unit = MetricUnits.MILLISECONDS, absolute = true)
    @Operation(description = "Consigue parametro del sistema por el valor", summary = "Parametro sistema por valor")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Parametro sistema", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Parametro.class))),
            @APIResponse(responseCode = "404", description = "Parametro no existe", content = @Content(mediaType = MediaType.TEXT_PLAIN, schema = @Schema(implementation = String.class))) })
    public Response consultaParametroValor(
            @Parameter(description = "Valor de parametro", required = true) @PathParam("valor") String valor) {
        log.info("Consulta SQL");
        List<Parametro> result = new ArrayList<Parametro>();
        try {
            PreparedStatement ps = con.prepareStatement(
                    "select codigo,nombre,tipo,valor,eliminado,estado from parametros where valor = ?");
            ps.setString(1, valor);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Parametro p = new Parametro();
                p.setCodigo(rs.getString(1));
                p.setNombre(rs.getString(2));
                p.setTipo(rs.getString(3));
                p.setValor(rs.getString(4));
                p.setEliminado(rs.getBoolean(5));
                p.setEstado(rs.getBoolean(6));
                result.add(p);
            }
            rs.close();
            ps.close();
        } catch (SQLException es) {
            log.error(es.getMessage());
        }
        if (result.size() == 0) {
            return Response.status(Response.Status.NOT_FOUND).entity("No hay resultados").build();
        } else {
            return Response.ok(result).build();
        }
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Counted(description = "Contador create", absolute = true)
    @Timed(name = "create-parametro-time", description = "Tiempo de procesamiento de crear parametro", unit = MetricUnits.MILLISECONDS, absolute = true)
    @Operation(description = "Crear parametros del sistema", summary = "Nuevo parametro sistema")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Parametro sistema creado", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Parametro.class))),
            @APIResponse(responseCode = "400", description = "Parametro con error", content = @Content(mediaType = MediaType.TEXT_PLAIN, schema = @Schema(implementation = String.class))),
            @APIResponse(responseCode = "401", description = "No autorizado", content = @Content(mediaType = MediaType.TEXT_PLAIN, schema = @Schema(implementation = String.class))),
            @APIResponse(responseCode = "403", description = "Acceso denegado", content = @Content(mediaType = MediaType.TEXT_PLAIN, schema = @Schema(implementation = String.class))) })
    @RolesAllowed({ "ADMIN" })
    @Transactional
    public Response creaParametro(@Parameter(description = "Parametro a crear", required = true) Parametro param) {
        log.info("Crear un nuevo parametro");
        try {
            em.persist(param);
            return Response.ok(param).build();
        } catch (EntityExistsException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Error al crear").build();
        }
    }

    @DELETE
    @Path("/{codigo}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Counted(description = "Contador delete", absolute = true)
    @Timed(name = "delete-parametro-time", description = "Tiempo de procesamiento de eliminar parametro", unit = MetricUnits.MILLISECONDS, absolute = true)
    @Operation(description = "Eliminar parametros del sistema", summary = "Borra parametro sistema")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Parametro sistema eliminado", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Parametro.class))),
            @APIResponse(responseCode = "400", description = "Parametro con error", content = @Content(mediaType = MediaType.TEXT_PLAIN, schema = @Schema(implementation = String.class))),
            @APIResponse(responseCode = "401", description = "No autorizado", content = @Content(mediaType = MediaType.TEXT_PLAIN, schema = @Schema(implementation = String.class))),
            @APIResponse(responseCode = "403", description = "Acceso denegado", content = @Content(mediaType = MediaType.TEXT_PLAIN, schema = @Schema(implementation = String.class))) })
    @RolesAllowed({ "ADMIN" })
    @Transactional
    public Response eliminaParametro(
            @Parameter(description = "Codigo de parametro", required = true) @PathParam("codigo") String codigo) {
        log.info("Elimnina parametro");
        try {
            Parametro p = em.find(Parametro.class, codigo);
            if (p != null) {
                em.remove(p);
                return Response.ok("Eliminado correctamente").build();
            } else
                return Response.status(Response.Status.NOT_FOUND).entity("Error al eliminar").build();
        } catch (IllegalArgumentException ea) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Error al eliminar").build();
        }
    }

    @PUT
    @Path("/{codigo}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Counted(description = "Contador update", absolute = true)
    @Timed(name = "update-parametro-time", description = "Tiempo de procesamiento de actualizar parametro", unit = MetricUnits.MILLISECONDS, absolute = true)
    @Operation(description = "Actualiza parametros del sistema", summary = "Modifica parametro sistema")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Parametro sistema actualizado", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Parametro.class))),
            @APIResponse(responseCode = "400", description = "Parametro con error", content = @Content(mediaType = MediaType.TEXT_PLAIN, schema = @Schema(implementation = String.class))),
            @APIResponse(responseCode = "401", description = "No autorizado", content = @Content(mediaType = MediaType.TEXT_PLAIN, schema = @Schema(implementation = String.class))),
            @APIResponse(responseCode = "403", description = "Acceso denegado", content = @Content(mediaType = MediaType.TEXT_PLAIN, schema = @Schema(implementation = String.class))) })
    @RolesAllowed({ "ADMIN" })
    @Transactional
    public Response actualizaParametro(
            @Parameter(description = "Codigo de parametro", required = true) @PathParam("codigo") String codigo,
            @Parameter(description = "Parametro a modificar", required = true) Parametro param) {
        log.info("Actualiza parametro");
        try {
            Parametro p = em.find(Parametro.class, codigo);
            if (p != null) {
                em.merge(param);
                return Response.ok(param).build();
            } else
                return Response.status(Response.Status.NOT_FOUND).entity("Error al actualizar").build();
        } catch (IllegalArgumentException ea) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Error al actualizar").build();
        }
    }

}
