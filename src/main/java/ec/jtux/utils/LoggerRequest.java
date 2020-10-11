package ec.jtux.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

import org.apache.commons.io.IOUtils;
import org.jboss.logging.Logger;

@Logged
@Provider
public class LoggerRequest implements ContainerRequestFilter {

    @Inject
    private Logger log;
    @Context
    private HttpServletRequest httpServletRequest;


    @Override
    public void filter(final ContainerRequestContext requestContext) throws IOException {
        UriInfo uriInfo = requestContext.getUriInfo();
        Request request = requestContext.getRequest();

        String sourceIP = getRemoteIP();
        String reqMethod = request.getMethod();
        String reqUri = uriInfo.getRequestUri().toString();
        String entity = getEntityBody(requestContext);
        String user = getUserPrincipalName(requestContext);

        log.info("Source IP: "+ sourceIP);
        log.info("Requested: "+ reqMethod + " " + reqUri);
        log.info("Entity: " + entity);
        log.info("Username: " + user);

    }

    private String getRemoteIP() {
        String remoteIpAddress = "";
        if (httpServletRequest != null) {
            remoteIpAddress = httpServletRequest.getRemoteAddr();
        }
        return remoteIpAddress;
    }

    private String getUserPrincipalName(ContainerRequestContext context) {
        SecurityContext securityContext = context.getSecurityContext();
        if (securityContext != null) {
            Principal userPrincipal = securityContext.getUserPrincipal();
            if (userPrincipal != null) {
                return userPrincipal.getName();
            } else {
                return "no user principal";
            }
        } else {
            return "no security context";
        }
    }

    private String getEntityBody(ContainerRequestContext requestContext) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        InputStream in = requestContext.getEntityStream();

        final StringBuilder b = new StringBuilder();
        try {
            IOUtils.copy(in, out);

            byte[] requestEntity = out.toByteArray();
            if (requestEntity.length == 0) {
                b.append("\n");
            } else {
                b.append(new String(requestEntity)).append("\n");
            }
            requestContext.setEntityStream(new ByteArrayInputStream(requestEntity));

        } catch (IOException e) {
            log.error("Error logging REST request.");
        }
        return b.toString();
    }
}