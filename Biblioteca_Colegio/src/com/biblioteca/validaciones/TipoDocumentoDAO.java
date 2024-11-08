package com.biblioteca.validaciones;

import com.biblioteca.bd.ConexionBaseDatos;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TipoDocumentoDAO {

    public static final Map<String, String> abreviaturaToTablaMap = new HashMap<>();

    static {
        abreviaturaToTablaMap.put("LIB", "Libros");
        abreviaturaToTablaMap.put("REV", "Revistas");
        abreviaturaToTablaMap.put("CDS", "CDs");
        abreviaturaToTablaMap.put("TES", "Tesis");
        // Agrega aquí otras categorías según sea necesario
    }

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
    StringBuilder consulta = new StringBuilder("CREATE TABLE `" + nombreTabla + "` (");
    consulta.append("id VARCHAR(10) PRIMARY KEY"); // Campo ID como VARCHAR(10)

    for (String columna : columnas) {
        String nombreColumnaSQL = columna.trim().toUpperCase().replace(" ", "_");

        // Determina el tipo de columna
        String tipoDato = determinarTipoDeDato(columna);

        consulta.append(", `").append(nombreColumnaSQL).append("` ").append(tipoDato);
    }

    consulta.append(")");

    try (Connection conexion = ConexionBaseDatos.getConexion();
         Statement statement = conexion.createStatement()) {
        statement.executeUpdate(consulta.toString());
        System.out.println("Tabla creada exitosamente: " + nombreTabla);
    } catch (SQLException e) {
        System.err.println("Error al crear la tabla para el documento: " + e.getMessage());
        throw e;
    }
}

// Método para determinar el tipo de datos de la columna
private String determinarTipoDeDato(String nombreColumna) {
    nombreColumna = nombreColumna.toLowerCase();

    if (nombreColumna.contains("fecha") || nombreColumna.contains("date")) {
        return "DATE";
    } else if (nombreColumna.contains("num") || nombreColumna.contains("cantidad") || 
               nombreColumna.contains("ejemplares") || nombreColumna.contains("paginas")) {
        return "INT";
    } else {
        return "VARCHAR(255)";
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

    public int obtenerNumeroActual(String tipoDocumento) {
        String nombreTabla = abreviaturaToTablaMap.get(tipoDocumento); // Usa el nombre completo de la tabla
        if (nombreTabla == null) {
            System.err.println("Error: no se encontró la tabla para la abreviatura " + tipoDocumento);
            return 0;
        }
        int numeroActual = 0;
        String consulta = "SELECT MAX(CAST(SUBSTRING(id, 4) AS UNSIGNED)) AS max_id FROM " + nombreTabla;

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

    public ArrayList<String> obtenerIDsPorCategoria(String categoria) throws SQLException {
        String nombreTabla = abreviaturaToTablaMap.get(categoria); // Usa el nombre completo de la tabla
        if (nombreTabla == null) {
            System.err.println("Error: no se encontró la tabla para la categoría " + categoria);
            return new ArrayList<>();
        }
        
        ArrayList<String> ids = new ArrayList<>();
        String sql = "SELECT id FROM " + nombreTabla;

        try (Connection conexion = ConexionBaseDatos.getConexion();
             PreparedStatement statement = conexion.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                ids.add(resultSet.getString("id"));
            }
        }
        return ids;
    }
    
    public ArrayList<Map<String, String>> obtenerColumnasInfo(String nombreTabla) throws SQLException {
    ArrayList<Map<String, String>> columnasInfo = new ArrayList<>();
    String consulta = "SHOW COLUMNS FROM " + nombreTabla;
    
    try (Connection conexion = ConexionBaseDatos.getConexion();
         PreparedStatement statement = conexion.prepareStatement(consulta);
         ResultSet resultSet = statement.executeQuery()) {

        while (resultSet.next()) {
            Map<String, String> columna = new HashMap<>();
            columna.put("Field", resultSet.getString("Field"));
            columna.put("Type", resultSet.getString("Type"));
            columnasInfo.add(columna);
        }
    }
    return columnasInfo;
}

}
