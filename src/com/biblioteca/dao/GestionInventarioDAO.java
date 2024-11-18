// Archivo: GestionInventarioDAO.java
// Paquete: com.biblioteca.dao

package com.biblioteca.dao;

import com.biblioteca.bd.ConexionBaseDatos;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public class GestionInventarioDAO {

    private final Connection conexion;
    


    public GestionInventarioDAO() throws SQLException {
        // Obtener conexión a la base de datos usando la clase de conexión
        this.conexion = ConexionBaseDatos.getConexion();
    }

    public ArrayList<String> obtenerTiposDocumentos() throws SQLException {
        ArrayList<String> tipos = new ArrayList<>();
        String query = "SELECT nombre FROM tiposdocumentos";
        try (Statement stmt = conexion.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                tipos.add(rs.getString("nombre"));
            }
        }
        return tipos;
    }

    // Nuevo método para consultar un documento específico por nombre
    public ArrayList<String> consultarTipoDocumentoPorNombre(String nombreDocumento) throws SQLException {
        ArrayList<String> datos = new ArrayList<>();
        String query = "SELECT * FROM tiposdocumentos WHERE nombre = ?";
        try (PreparedStatement pstmt = conexion.prepareStatement(query)) {
            pstmt.setString(1, nombreDocumento);
            try (ResultSet rs = pstmt.executeQuery()) {
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                if (rs.next()) {
                    for (int i = 1; i <= columnCount; i++) {
                        datos.add(rs.getString(i));
                    }
                }
            }
        }
        return datos;
    }

    public ArrayList<String> obtenerColumnasDeTabla(String nombreTabla) throws SQLException {
        ArrayList<String> columnas = new ArrayList<>();
        DatabaseMetaData metaData = conexion.getMetaData();
        try (ResultSet rs = metaData.getColumns(null, null, nombreTabla, null)) {
            while (rs.next()) {
                columnas.add(rs.getString("COLUMN_NAME"));
            }
        }
        return columnas;
    }

    public String generarNuevoId(String nombreTabla) throws SQLException {
        String nuevoId = null;
        String query = "SELECT MAX(id) + 1 AS nuevoId FROM " + nombreTabla;
        try (Statement stmt = conexion.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                nuevoId = rs.getString("nuevoId") != null ? rs.getString("nuevoId") : "1";
            }
        }
        return nuevoId;
    }

    public void insertarDatosEnTabla(String nombreTabla, ArrayList<String> columnas, ArrayList<String> valores) throws SQLException {
        String columnasConComillas = columnas.stream()
                                             .map(columna -> "`" + columna + "`")
                                             .collect(Collectors.joining(", "));
        
        String query = "INSERT INTO " + nombreTabla + " (" + columnasConComillas + ") VALUES (" +
                       valores.stream().map(v -> "?").collect(Collectors.joining(", ")) + ")";
        
        System.out.println("Consulta SQL: " + query);
        System.out.println("Valores: " + valores);
        
        try (PreparedStatement pstmt = conexion.prepareStatement(query)) {
            for (int i = 0; i < valores.size(); i++) {
                pstmt.setString(i + 1, valores.get(i));
            }
            pstmt.executeUpdate();
        }
    }

    public ArrayList<ArrayList<String>> obtenerDatosDeTabla(String nombreTabla) throws SQLException {
        ArrayList<ArrayList<String>> datos = new ArrayList<>();
        String query = "SELECT * FROM " + nombreTabla;
        try (Statement stmt = conexion.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                ArrayList<String> fila = new ArrayList<>();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    fila.add(rs.getString(i));
                }
                datos.add(fila);
            }
        }
        return datos;
    }

    public ArrayList<String> obtenerDatosPorId(String nombreTabla, String id) throws SQLException {
        ArrayList<String> datos = new ArrayList<>();
        String query = "SELECT * FROM " + nombreTabla + " WHERE id = ?";
        try (PreparedStatement pstmt = conexion.prepareStatement(query)) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                        datos.add(rs.getString(i));
                    }
                }
            }
        }
        return datos;
    }

    public void actualizarDatosEnTabla(String nombreTabla, ArrayList<String> columnas, ArrayList<String> valores, String id) throws SQLException {
        String query = "UPDATE " + nombreTabla + " SET " + String.join(", ", columnas.stream().map(c -> c + " = ?").toArray(String[]::new)) +
                " WHERE id = ?";
        try (PreparedStatement pstmt = conexion.prepareStatement(query)) {
            for (int i = 0; i < valores.size(); i++) {
                pstmt.setString(i + 1, valores.get(i));
            }
            pstmt.setString(valores.size() + 1, id);
            pstmt.executeUpdate();
        }
    }

    public void eliminarInventarioPorId(String nombreTabla, String id) throws SQLException {
        String query = "DELETE FROM " + nombreTabla + " WHERE id = ?";
        try (PreparedStatement pstmt = conexion.prepareStatement(query)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        }
    }
    
    public String generarNuevoIdConPrefijo(String nombreTabla) throws SQLException {
        String prefijo = nombreTabla.substring(0, Math.min(3, nombreTabla.length())).toUpperCase();
        String nuevoId = prefijo + "00001";

        String query = "SELECT id FROM " + nombreTabla + " WHERE id LIKE ? ORDER BY id DESC LIMIT 1";
        try (PreparedStatement stmt = conexion.prepareStatement(query)) {
            stmt.setString(1, prefijo + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String ultimoId = rs.getString("id");
                    int numero = Integer.parseInt(ultimoId.substring(3)) + 1;
                    nuevoId = prefijo + String.format("%05d", numero);
                }
            }
        }
        return nuevoId;
    }

    public void modificarCantidadInventario(String nombreTabla, String id, int cantidadModificacion) throws SQLException {
        String query = "UPDATE " + nombreTabla + " SET Disponibles = Disponibles + ? WHERE id = ?";
        try (PreparedStatement pstmt = conexion.prepareStatement(query)) {
            pstmt.setInt(1, cantidadModificacion);
            pstmt.setString(2, id);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("No se encontró el artículo con ID: " + id + " en la tabla " + nombreTabla);
            }
        }
    }
    
    public boolean existeTabla(String nombreTabla) throws SQLException {
        DatabaseMetaData metaData = conexion.getMetaData();
        try (ResultSet rs = metaData.getTables(null, null, nombreTabla, null)) {
            return rs.next(); // Retorna true si la tabla existe
        }
    }
    
    public ArrayList<String> obtenerListaTablas() throws SQLException {
        ArrayList<String> listaTablas = new ArrayList<>();
        String query = "SELECT nombre FROM tiposdocumentos"; // Asume que 'nombre' es la columna que guarda los nombres de las tablas

        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                listaTablas.add(rs.getString("nombre"));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener la lista de tablas: " + e.getMessage());
            throw e;
        }
        
        return listaTablas;
    }

    public Map<String, String> obtenerDatosDeInventario(String nombreTabla, String id) throws SQLException {
    if (nombreTabla == null || id == null) {
        throw new IllegalArgumentException("El nombre de la tabla o el ID no pueden ser nulos.");
    }

    Map<String, String> datos = new HashMap<>();
    String query = "SELECT * FROM " + nombreTabla + " WHERE id = ?";
    try (PreparedStatement pstmt = conexion.prepareStatement(query)) {
        pstmt.setString(1, id);
        try (ResultSet rs = pstmt.executeQuery()) {
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            if (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    String nombreColumna = metaData.getColumnName(i);
                    String valor = rs.getString(i);
                    datos.put(nombreColumna, valor);
                }
            } else {
                System.err.println("No se encontraron datos para el inventario con ID: " + id);
            }
        }
    }
    return datos;
}


   public void actualizarInventario(String nombreTabla, String id, Map<String, String> nuevosDatos) throws SQLException {
    if (nuevosDatos == null || nuevosDatos.isEmpty()) {
        throw new IllegalArgumentException("Los nuevos datos no pueden estar vacíos.");
    }

    // Construir la consulta de actualización con nombres de columnas entre comillas invertidas
    StringBuilder queryBuilder = new StringBuilder("UPDATE `").append(nombreTabla).append("` SET ");
    int i = 0;
    for (Map.Entry<String, String> entry : nuevosDatos.entrySet()) {
        queryBuilder.append("`").append(entry.getKey()).append("` = ?");
        if (i < nuevosDatos.size() - 1) {
            queryBuilder.append(", ");
        }
        i++;
    }
    queryBuilder.append(" WHERE `id` = ?");

    String query = queryBuilder.toString();
    System.out.println("Consulta generada: " + query);  // Imprimir la consulta para depuración

    try (PreparedStatement pstmt = conexion.prepareStatement(query)) {
        int paramIndex = 1;
        for (String value : nuevosDatos.values()) {
            pstmt.setString(paramIndex++, value);
        }
        pstmt.setString(paramIndex, id);

        // Ejecutar la actualización
        int filasAfectadas = pstmt.executeUpdate();
        if (filasAfectadas == 0) {
            throw new SQLException("No se encontró el inventario con ID: " + id + " en la tabla " + nombreTabla);
        }
    }
}


    
    public String obtenerNombreTablaPorTipo(String nombreDocumento) throws SQLException {
    String nombreTabla = null;
    String query = "SELECT nombre_tabla FROM tiposdocumentos WHERE nombre = ?";
    try (PreparedStatement pstmt = conexion.prepareStatement(query)) {
        pstmt.setString(1, nombreDocumento);
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                nombreTabla = rs.getString("nombre_tabla");
            }
        }
    }
    return nombreTabla;
}


private String derivarNombreTabla(String nombreDocumento) {
    if (nombreDocumento == null || nombreDocumento.trim().isEmpty()) {
        return null;
    }
    return nombreDocumento.toLowerCase() + "s";
}

private Properties cargarMapaTipoDocumentoTabla() {
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("tipoDocumentoTabla.properties")) {
            if (input == null) {
                System.out.println("Lo siento, no se pudo encontrar tipoDocumentoTabla.properties");
                return properties;
            }
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return properties;
    }

public void insertarTipoDocumento(String nombre, String nombreTabla) throws SQLException {
    String query = "INSERT INTO tiposdocumentos (nombre, nombre_tabla) VALUES (?, ?)";
    try (PreparedStatement pstmt = conexion.prepareStatement(query)) {
        pstmt.setString(1, nombre);
        pstmt.setString(2, nombreTabla);
        pstmt.executeUpdate();
    }
}

}
