package com.biblioteca.dao;

import com.biblioteca.bd.ConexionBaseDatos;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GestionProductoDAO {

    private final Connection conexion;

    public GestionProductoDAO() throws SQLException {
        conexion = ConexionBaseDatos.getConexion();
        if (conexion == null || conexion.isClosed()) {
            throw new SQLException("No se pudo establecer la conexión con la base de datos.");
        }
    }

    public ArrayList<String> obtenerTiposDocumentos() throws SQLException {
        ArrayList<String> tipos = new ArrayList<>();
        String query = "SELECT nombre FROM `tiposdocumentos`";

        try (Statement statement = conexion.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                tipos.add(resultSet.getString("nombre"));
            }
        }
        return tipos;
    }

    public boolean existeTabla(String nombreTabla) throws SQLException {
        String query = "SELECT TABLE_NAME FROM information_schema.tables WHERE LOWER(TABLE_SCHEMA) = LOWER(DATABASE()) AND LOWER(TABLE_NAME) = LOWER(?)";
        try (PreparedStatement statement = conexion.prepareStatement(query)) {
            statement.setString(1, nombreTabla);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    public ArrayList<Map<String, String>> obtenerColumnasInfo(String nombreTabla) throws SQLException {
        ArrayList<Map<String, String>> columnasInfo = new ArrayList<>();
        String query = "DESCRIBE `" + nombreTabla + "`";

        try (Statement statement = conexion.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                Map<String, String> columnInfo = new HashMap<>();
                columnInfo.put("Field", resultSet.getString("Field"));
                columnInfo.put("Type", resultSet.getString("Type"));
                columnasInfo.add(columnInfo);
            }
        }
        return columnasInfo;
    }

    public void crearTablaParaDocumento(String nombreTabla, ArrayList<String> columnas) throws SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE `").append(nombreTabla).append("` (`id` VARCHAR(10) PRIMARY KEY");

        for (String columnName : columnas) {
            sb.append(columnName.toLowerCase().contains("fecha") ? 
                      ", `" + columnName + "` DATE" : 
                      ", `" + columnName + "` VARCHAR(255)");
        }

        sb.append(", `Disponibles` INT, `Estado` VARCHAR(50), `Palabra clave` VARCHAR(255), `Ubicación Física` VARCHAR(255)");
        sb.append(") CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");

        try (Statement stmt = conexion.createStatement()) {
            stmt.executeUpdate(sb.toString());
        }
    }

    public void agregarTipoDocumento(String nombreDocumento) throws SQLException {
        String query = "INSERT INTO `tiposdocumentos` (`nombre`) VALUES (?)";
        try (PreparedStatement statement = conexion.prepareStatement(query)) {
            statement.setString(1, nombreDocumento);
            statement.executeUpdate();
        }
    }

    public void eliminarTabla(String nombreTabla) throws SQLException {
        String queryDrop = "DROP TABLE IF EXISTS `" + nombreTabla + "`";
        String queryDelete = "DELETE FROM `tiposdocumentos` WHERE LOWER(`nombre`) = LOWER(?)";
        try (Statement statement = conexion.createStatement()) {
            statement.executeUpdate(queryDrop);
        }
        try (PreparedStatement pstmt = conexion.prepareStatement(queryDelete)) {
            pstmt.setString(1, nombreTabla);
            pstmt.executeUpdate();
        }
    }

            public int obtenerNumeroActual(String nombreTabla) throws SQLException {
        String query = "SELECT COUNT(*) AS total FROM `" + nombreTabla + "`";
        try (Statement statement = conexion.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            return resultSet.next() ? resultSet.getInt("total") : 0;
        }
    }
     
              
        

    public ArrayList<String> obtenerColumnasDeTabla(String nombreTabla) throws SQLException {
        ArrayList<String> columnas = new ArrayList<>();
        String query = "SHOW COLUMNS FROM `" + nombreTabla + "`";

        try (Statement statement = conexion.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                columnas.add(resultSet.getString("Field"));
            }
        }
        return columnas;
    }

    public void actualizarColumnasTabla(String nombreTabla, ArrayList<String> nuevasColumnas) throws SQLException {
        ArrayList<String> columnasActuales = obtenerColumnasDeTabla(nombreTabla);
        columnasActuales.remove("id");

        if (nuevasColumnas.size() != columnasActuales.size()) {
            throw new SQLException("No se puede cambiar el número de columnas. La cantidad debe ser la misma.");
        }

        for (int i = 0; i < nuevasColumnas.size(); i++) {
            String columnaActual = columnasActuales.get(i);
            String nuevaColumna = nuevasColumnas.get(i);

            if (!columnaActual.equals(nuevaColumna)) {
                String sql = "ALTER TABLE `" + nombreTabla + "` CHANGE `" + columnaActual + "` `" + nuevaColumna + "` VARCHAR(255)";
                try (Statement statement = conexion.createStatement()) {
                    statement.executeUpdate(sql);
                }
            }
        }
    }

    public boolean eliminarDatosDeTabla(String nombreTabla) throws SQLException {
        String query = "DROP TABLE IF EXISTS `" + nombreTabla + "`";
        try (Statement statement = conexion.createStatement()) {
            statement.executeUpdate(query);
            String queryDelete = "DELETE FROM `tiposdocumentos` WHERE `nombre` = ?";
            try (PreparedStatement pstmt = conexion.prepareStatement(queryDelete)) {
                pstmt.setString(1, nombreTabla);
                pstmt.executeUpdate();
            }
            return true;
        } catch (SQLException e) {
            throw e;
        }
    }

    public ArrayList<String> obtenerColumnasTiposDocumentos() throws SQLException {
        ArrayList<String> columnas = new ArrayList<>();
        String sql = "SHOW COLUMNS FROM `tiposdocumentos`";

        try (Statement statement = conexion.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                columnas.add(resultSet.getString("Field"));
            }
        }
        return columnas;
    }

    public ArrayList<ArrayList<String>> obtenerDatosTiposDocumentos() throws SQLException {
        ArrayList<ArrayList<String>> datos = new ArrayList<>();
        String sql = "SELECT * FROM `tiposdocumentos`";

        try (Statement statement = conexion.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (resultSet.next()) {
                ArrayList<String> fila = new ArrayList<>();
                for (int i = 1; i <= columnCount; i++) {
                    fila.add(resultSet.getString(i));
                }
                datos.add(fila);
            }
        }
        return datos;
    }

    public String generarNuevoId(String nombreTabla) throws SQLException {
        String query = "SELECT COUNT(*) AS total FROM `" + nombreTabla + "`";
        int totalRegistros;

        try (Statement statement = conexion.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            totalRegistros = resultSet.next() ? resultSet.getInt("total") : 0;
        }

        totalRegistros++;
        String prefijo = nombreTabla.substring(0, Math.min(3, nombreTabla.length())).toUpperCase();
        return prefijo + String.format("%05d", totalRegistros);
    }

public void insertarTipoDocumento(String generatedId, String nombreFormulario) throws SQLException {
    // Consulta SQL para insertar un nuevo registro en la tabla 'tiposdocumentos'
    String sql = "INSERT INTO tiposdocumentos (id, nombre) VALUES (?, ?)";

    try (PreparedStatement statement = conexion.prepareStatement(sql)) {
        
        // Asignar valores a los parámetros
        statement.setString(1, generatedId); 
        statement.setString(2, nombreFormulario); 

        // Ejecutar la consulta
        statement.executeUpdate();
    } catch (SQLException e) {
        System.err.println("Error al insertar el tipo de documento: " + e.getMessage());
        throw e;
    }
}




}
