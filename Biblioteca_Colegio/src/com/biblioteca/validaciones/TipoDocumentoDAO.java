package com.biblioteca.validaciones;

import com.biblioteca.bd.ConexionBaseDatos;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class TipoDocumentoDAO {

    public ArrayList<String> obtenerTiposDocumentos() throws SQLException {
        ArrayList<String> tiposDocumentos = new ArrayList<>();
        String consulta = "SELECT nombre FROM tiposdocumentos";
        
        try (Connection conexion = ConexionBaseDatos.getConexion();
             PreparedStatement statement = conexion.prepareStatement(consulta);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                tiposDocumentos.add(resultSet.getString("nombre"));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener los tipos de documentos: " + e.getMessage());
            throw e;
        }

        return tiposDocumentos;
    }

    public void agregarTipoDocumento(String nombreDocumento) throws SQLException {
        String insercion = "INSERT INTO tiposdocumentos (nombre) VALUES (?)";
        try (Connection conexion = ConexionBaseDatos.getConexion();
             PreparedStatement statement = conexion.prepareStatement(insercion)) {
            statement.setString(1, nombreDocumento);
            statement.executeUpdate();
        }
    }

public void crearTablaParaDocumento(String nombreTabla, ArrayList<String> columnas) throws SQLException {
    // Crear la estructura SQL para crear la tabla
    StringBuilder consulta = new StringBuilder("CREATE TABLE `" + nombreTabla + "` (");
    consulta.append("id VARCHAR(10) PRIMARY KEY"); // Usamos VARCHAR para el campo 'id' con longitud 10

    // Añadir las columnas especificadas
    for (String columna : columnas) {
        String nombreColumnaSQL = columna.trim().toUpperCase().replace(" ", "_");
        if (nombreColumnaSQL.toLowerCase().contains("fecha")) {
            consulta.append(", `").append(nombreColumnaSQL).append("` DATE");
        } else {
            consulta.append(", `").append(nombreColumnaSQL).append("` VARCHAR(255)");
        }
    }

    consulta.append(")");

    // Ejecutar la creación de la tabla
    try (Connection conexion = ConexionBaseDatos.getConexion();
         Statement statement = conexion.createStatement()) {

        statement.executeUpdate(consulta.toString());
        System.out.println("Tabla creada exitosamente: " + nombreTabla);

    } catch (SQLException e) {
        System.err.println("Error al crear la tabla para el documento: " + e.getMessage());
        throw e;
    }
}


    
    public boolean existeTabla(String nombreTabla) throws SQLException {
        String consulta = "SHOW TABLES LIKE ?";
        try (Connection conexion = ConexionBaseDatos.getConexion();
             PreparedStatement statement = conexion.prepareStatement(consulta)) {
            statement.setString(1, nombreTabla);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        }
    }

  public void eliminarTabla(String nombreTabla) throws SQLException {
        String eliminacion = "DROP TABLE IF EXISTS " + nombreTabla;
        try (Connection conexion = ConexionBaseDatos.getConexion();
             PreparedStatement statement = conexion.prepareStatement(eliminacion)) {
            statement.executeUpdate();
        }
    }

  public ArrayList<String> obtenerColumnasDeTabla(String nombreTabla) throws SQLException {
        ArrayList<String> columnas = new ArrayList<>();
        String consulta = "SHOW COLUMNS FROM " + nombreTabla;
        try (Connection conexion = ConexionBaseDatos.getConexion();
             PreparedStatement statement = conexion.prepareStatement(consulta);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                columnas.add(resultSet.getString("Field"));
            }
        }
        return columnas;
    }

    public void actualizarColumnasTabla(String nombreTabla, ArrayList<String> nuevasColumnas) throws SQLException {
        ArrayList<String> columnasActuales = obtenerColumnasDeTabla(nombreTabla);

        try (Connection conexion = ConexionBaseDatos.getConexion()) {
            for (int i = 0; i < nuevasColumnas.size(); i++) {
                String columnaActual = columnasActuales.get(i);
                String nuevaColumna = nuevasColumnas.get(i);

                if (!columnaActual.equals(nuevaColumna)) {
                    String consulta = "ALTER TABLE " + nombreTabla + " CHANGE " + columnaActual + " " + nuevaColumna + " VARCHAR(255)";
                    try (PreparedStatement statement = conexion.prepareStatement(consulta)) {
                        statement.executeUpdate();
                    }
                }
            }
        }
    }
    
    // Método para obtener el número de documentos del tipo especificado
    public int obtenerContadorParaDocumento(String tipoDocumento) throws SQLException {
        String consulta = "SELECT COUNT(*) FROM " + tipoDocumento;
        try (Connection conexion = ConexionBaseDatos.getConexion();
             PreparedStatement statement = conexion.prepareStatement(consulta);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        }
        return 0;
    }

   public int obtenerNumeroActual(String tipoDocumento) {
    int numeroActual = 0;
    String consulta = "SELECT MAX(CAST(SUBSTRING(id_documento, 4) AS UNSIGNED)) AS max_id FROM " + tipoDocumento;

    try (Connection conexion = ConexionBaseDatos.getConexion();
         PreparedStatement statement = conexion.prepareStatement(consulta)) {

        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            numeroActual = resultSet.getInt("max_id");
        }

    } catch (SQLException e) {
        System.err.println("Error al obtener el número actual para el tipo de documento " + tipoDocumento + ": " + e.getMessage());
    }

    return numeroActual;
}

    
    
}
