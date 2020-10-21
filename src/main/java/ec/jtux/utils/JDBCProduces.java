package ec.jtux.utils;

import java.sql.Connection;
import java.sql.SQLException;

import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.eclipse.microprofile.config.inject.ConfigProperty;

public class JDBCProduces {

    @Inject
    @ConfigProperty(name = "ds")
    String dsName;

    @Produces
    private Connection createConnection() throws SQLException, NamingException {
        InitialContext ic = new InitialContext();
        DataSource ds = (DataSource) ic.lookup(dsName);
        return ds.getConnection();
    }

    @SuppressWarnings("unused")
    private void closeConnection(@Disposes Connection conn) throws SQLException {
        conn.close();
    }


}