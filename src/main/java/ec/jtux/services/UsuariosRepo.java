package ec.jtux.services;

import java.sql.Connection;

import javax.annotation.security.DenyAll;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.Path;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.jboss.logging.Logger;

@Path("/")
@OpenAPIDefinition(info = @Info(title = "Usuarios Resource", description = "Servicios para informacion de usuarios", version = "1.0"))
@DenyAll
public class UsuariosRepo {

    @Inject
    private EntityManager em;

    @Inject
    private Logger log;

    @Inject
    private Connection con;

    

    
}
