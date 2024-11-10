package com.biblioteca.validaciones;

import com.biblioteca.bd.ConexionBaseDatos;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TipoDocumentoDAO {
    private Connection connection;

    public TipoDocumentoDAO() throws SQLException {
        this.connection = ConexionBaseDatos.getConexion();
    }

    public boolean existeTabla(String nombreTabla) throws SQLException {
    verificarConexion(); // Verifica y abre la conexión si está cerrada

    String query = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'biblioteca_colegio' AND TABLE_NAME = ?";
    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
        pstmt.setString(1, nombreTabla);
        try (ResultSet rs = pstmt.executeQuery()) {
            return rs.next() && rs.getInt(1) > 0;
        }
    }
}

// Método para verificar y reabrir la conexión si está cerrada
private void verificarConexion() throws SQLException {
    if (connection == null || connection.isClosed()) {
        connection = ConexionBaseDatos.getConexion(); // Reabre la conexión si está cerrada
    }
}


    public ArrayList<String> obtenerTiposDocumentos() throws SQLException {
        ArrayList<String> tipos = new ArrayList<>();
        String query = "SELECT nombre FROM tiposdocumentos";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                tipos.add(rs.getString("nombre"));
            }
        }
        return tipos;
    }

    public void crearTablaParaDocumento(String nombreTabla, ArrayList<String> columnas) throws SQLException {
    StringBuilder sb = new StringBuilder();
    sb.append("CREATE TABLE `").append(nombreTabla).append("` (");
    sb.append("id VARCHAR(10) PRIMARY KEY");

    // Agregar las columnas definidas por el usuario
    for (String columnName : columnas) {
        if (columnName.toLowerCase().contains("fecha")) {
            sb.append(", `").append(columnName).append("` DATE");
        } else {
            sb.append(", `").append(columnName).append("` VARCHAR(255)");
        }
    }

    // Agregar las columnas constantes al final de la tabla
    sb.append(", `Disponibles` INT");
    sb.append(", `Estado` VARCHAR(50)");
    sb.append(", `Palabra clave` VARCHAR(255)");
    sb.append(", `Ubicación Física` VARCHAR(255)");

    sb.append(") CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");

    try (Statement stmt = connection.createStatement()) {
        stmt.executeUpdate(sb.toString());
        System.out.println("Tabla " + nombreTabla + " creada exitosamente con columnas constantes.");
    } catch (SQLException e) {
        System.err.println("Error al crear la tabla " + nombreTabla + ": " + e.getMessage());
        throw e;
    }
}


    public void agregarTipoDocumento(String nombreDocumento) throws SQLException {
    // Convertir el nombre del documento a mayúsculas
    nombreDocumento = nombreDocumento.toUpperCase();

    // Generar las primeras tres letras en mayúsculas para el prefijo del ID
    String prefix = nombreDocumento.substring(0, Math.min(3, nombreDocumento.length()));

    // Obtener el siguiente número secuencial único
    int secuencia = obtenerSecuenciaParaPrefix(prefix);

    // Formatear el ID único con el prefijo y el número
    String id = prefix + String.format("%03d", secuencia);

    // Inserción en la tabla con el ID único y el nombre en mayúsculas
    String query = "INSERT INTO tiposdocumentos (id, nombre) VALUES (?, ?)";
    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
        pstmt.setString(1, id);
        pstmt.setString(2, nombreDocumento);
        pstmt.executeUpdate();
    } catch (SQLException e) {
        System.err.println("Error al agregar tipo de documento: " + e.getMessage());
        throw e;
    }
}

// Método auxiliar para obtener el siguiente número secuencial único para un prefijo dado
private int obtenerSecuenciaParaPrefix(String prefix) throws SQLException {
    String query = "SELECT COUNT(*) FROM tiposdocumentos WHERE id LIKE ?";
    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
        pstmt.setString(1, prefix + "%");
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1) + 1; // La siguiente secuencia es el recuento + 1
            }
        }
    }
    return 1; // Si no hay registros, comienza desde 1
}


    public ArrayList<Map<String, String>> obtenerColumnasInfo(String nombreTabla) throws SQLException {
    ArrayList<Map<String, String>> columnas = new ArrayList<>();
    String consulta = "SHOW COLUMNS FROM " + escapeColumnName(nombreTabla);

    try (Statement stmt = connection.createStatement();
         ResultSet rs = stmt.executeQuery(consulta)) {
        while (rs.next()) {
            Map<String, String> columnInfo = new HashMap<>();
            String nombreColumna = rs.getString("Field");
            columnInfo.put("Field", formatearNombreColumna(nombreColumna)); // Modificado para mantener caracteres especiales
            columnInfo.put("Type", rs.getString("Type"));
            columnas.add(columnInfo);
        }
    }
    return columnas;
}

    public int obtenerNumeroActual(String nombreTabla) throws SQLException {
        String query = "SELECT COUNT(*) AS total FROM `" + nombreTabla + "`";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener el número actual de " + nombreTabla + ": " + e.getMessage());
            throw e;
        }
        return 0;
    }

    public void eliminarTabla(String nombreTabla) throws SQLException {
        String query = "DROP TABLE IF EXISTS " + escapeColumnName(nombreTabla);
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(query);
            System.out.println("Tabla " + nombreTabla + " eliminada exitosamente.");
        } catch (SQLException e) {
            System.err.println("Error al eliminar la tabla " + nombreTabla + ": " + e.getMessage());
            throw e;
        }
    }

    public ArrayList<String> obtenerColumnasDeTabla(String nombreTabla) throws SQLException {
    ArrayList<String> columnas = new ArrayList<>();
    String query = "SHOW COLUMNS FROM `" + nombreTabla + "`";

    try (Statement stmt = connection.createStatement();
         ResultSet rs = stmt.executeQuery(query)) {
        while (rs.next()) {
            String nombreColumna = rs.getString("Field");
            columnas.add(formatearNombreColumna(nombreColumna));
        }
    } catch (SQLException e) {
        System.err.println("Error al obtener columnas de la tabla " + nombreTabla + ": " + e.getMessage());
        throw e;
    }
    return columnas;
}

private String formatearNombreColumna(String nombreColumna) {
    return nombreColumna.replace("_", " ");
}


    private String escapeColumnName(String columnName) {
        return "`" + columnName.replace("`", "``") + "`";
    }


    public void actualizarColumnasTabla(String nombreTabla, ArrayList<String> nuevasColumnas) throws SQLException {
        ArrayList<String> columnasActuales = obtenerColumnasDeTabla(nombreTabla);

        if (nuevasColumnas.size() != columnasActuales.size()) {
            throw new SQLException("El número de nuevas columnas no coincide con el existente.");
        }

        for (int i = 0; i < nuevasColumnas.size(); i++) {
            String columnaActual = columnasActuales.get(i);
            String nuevaColumna = nuevasColumnas.get(i);
            if (!columnaActual.equalsIgnoreCase(nuevaColumna)) {
                String query = "ALTER TABLE " + escapeColumnName(nombreTabla) + " RENAME COLUMN " + escapeColumnName(columnaActual) + " TO " + escapeColumnName(nuevaColumna);
                try (Statement stmt = connection.createStatement()) {
                    stmt.executeUpdate(query);
                    System.out.println("Columna " + columnaActual + " renombrada a " + nuevaColumna + " en la tabla " + nombreTabla);
                } catch (SQLException e) {
                    System.err.println("Error al renombrar columna: " + e.getMessage());
                    throw e;
                }
            }
        }
    }

    public boolean eliminarDatosDeTabla(String nombreTabla) throws SQLException {
    // Verificar si la conexión a la base de datos está abierta antes de proceder
    if (connection.isClosed()) {
        connection = ConexionBaseDatos.getConexion(); // Reabrir la conexión si está cerrada
    }

    try {
        connection.setAutoCommit(false);

        String deleteFromTiposDocumentosQuery = "DELETE FROM tiposdocumentos WHERE nombre = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteFromTiposDocumentosQuery)) {
            pstmt.setString(1, nombreTabla);
            int filasEliminadas = pstmt.executeUpdate();
            if (filasEliminadas == 0) {
                throw new SQLException("No se encontró ningún registro con el nombre " + nombreTabla + " en `tiposdocumentos`.");
            }
        }

        String dropTableQuery = "DROP TABLE IF EXISTS `" + nombreTabla + "`";
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(dropTableQuery);
        }

        connection.commit();
        return true;

    } catch (SQLException e) {
        connection.rollback();
        System.err.println("Error al eliminar los datos de " + nombreTabla + ": " + e.getMessage());
        throw e;
    } finally {
        // No cerramos la conexión aquí para evitar problemas de reutilización
        connection.setAutoCommit(true);
    }
}


    private void crearTablaConColumnasDinamicas(String nombreTabla, ArrayList<String> nombresColumnas) throws SQLException {
        StringBuilder consulta = new StringBuilder("CREATE TABLE `" + nombreTabla + "` (id INT PRIMARY KEY AUTO_INCREMENT");

        for (String columna : nombresColumnas) {
            String columnaEscapada = escapeColumnName(columna);
            consulta.append(", ").append(columnaEscapada).append(" VARCHAR(255)");
        }

        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(consulta.toString());
        }
    }   
}