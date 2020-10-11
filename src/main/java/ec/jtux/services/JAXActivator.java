package ec.jtux.services;

import javax.annotation.security.DeclareRoles;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.eclipse.microprofile.auth.LoginConfig;

@ApplicationPath("/api")
@LoginConfig(authMethod = "MP-JWT", realmName = "helloworldfull")
@DeclareRoles({"ADMIN", "SUPER", "MONITOR", "USER"})
public class JAXActivator extends Application {

    public JAXActivator() {
        // CONSTRUCTOR VACIO
    }
}