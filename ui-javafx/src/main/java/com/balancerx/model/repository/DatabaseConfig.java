package com.balancerx.model.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.IOException;
import java.io.InputStream;

/**
 * Configuración de la conexión a la base de datos SQL Express.
 */
public class DatabaseConfig {
    
    private static final String CONFIG_FILE = "config.properties";
    private static final String DEFAULT_HOST = "localhost";
    private static final String DEFAULT_PORT = "1433";
    private static final String DEFAULT_DATABASE = "balancerx";
    private static final String DEFAULT_USER = "sa";
    private static final String DEFAULT_PASSWORD = "BalancerX2023";
    
    private static String host;
    private static String port;
    private static String database;
    private static String user;
    private static String password;
    
    static {
        loadConfig();
    }
    
    /**
     * Carga la configuración desde el archivo properties o usa valores por defecto.
     */
    private static void loadConfig() {
        Properties props = new Properties();
        
        try (InputStream input = DatabaseConfig.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input != null) {
                props.load(input);
                
                host = props.getProperty("db.host", DEFAULT_HOST);
                port = props.getProperty("db.port", DEFAULT_PORT);
                database = props.getProperty("db.name", DEFAULT_DATABASE);
                user = props.getProperty("db.user", DEFAULT_USER);
                password = props.getProperty("db.password", DEFAULT_PASSWORD);
            } else {
                System.out.println("Archivo de configuración no encontrado, usando valores por defecto.");
                useDefaultConfig();
            }
        } catch (IOException ex) {
            System.err.println("Error al cargar la configuración: " + ex.getMessage());
            useDefaultConfig();
        }
    }
    
    /**
     * Establece la configuración por defecto.
     */
    private static void useDefaultConfig() {
        host = DEFAULT_HOST;
        port = DEFAULT_PORT;
        database = DEFAULT_DATABASE;
        user = DEFAULT_USER;
        password = DEFAULT_PASSWORD;
    }
    
    /**
     * Obtiene una conexión a la base de datos.
     * @return Conexión a la base de datos
     * @throws SQLException Si ocurre un error al conectar
     */
    public static Connection getConnection() throws SQLException {
        String url = String.format("jdbc:sqlserver://%s:%s;databaseName=%s;encrypt=false", host, port, database);
        return DriverManager.getConnection(url, user, password);
    }
    
    /**
     * Cierra la conexión a la base de datos.
     * @param connection Conexión a cerrar
     */
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }
}