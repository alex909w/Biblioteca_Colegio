package com.biblioteca.dao;

import com.biblioteca.bd.ConexionBaseDatos;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class GestionFormularioDAO {

    private final Connection conexion;

    public GestionFormularioDAO() throws SQLException {
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
    // Reemplazar espacios por guiones bajos en el nombre de la tabla
    String nombreTablaAjustado = nombreTabla.replace(" ", "_");

    StringBuilder sb = new StringBuilder();
    sb.append("CREATE TABLE `").append(nombreTablaAjustado).append("` (`id` VARCHAR(10) PRIMARY KEY");

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
    String nombreTablaAjustado = nombreTabla.replace(" ", "_"); // Reemplazar espacios por guiones bajos
    ArrayList<String> columnas = new ArrayList<>();
    DatabaseMetaData metaData = conexion.getMetaData();
    ResultSet rs = metaData.getColumns(null, null, nombreTablaAjustado, null);
    while (rs.next()) {
        columnas.add(rs.getString("COLUMN_NAME"));
    }
    return columnas;
}



   public void actualizarColumnasTabla(String nombreTabla, ArrayList<String> nuevasColumnas) throws SQLException {
    try {
        ArrayList<String> columnasActuales = obtenerColumnasDeTabla(nombreTabla);
        columnasActuales.remove("id"); // No modificar la columna ID

        // Lista de columnas predeterminadas que no deben ser renombradas ni duplicadas
        ArrayList<String> columnasConstantes = new ArrayList<>(Arrays.asList("Disponibles", "Estado", "Palabra clave", "Ubicación Física"));

        // Filtrar columnas actuales que no son constantes
        ArrayList<String> columnasActualizables = new ArrayList<>();
        for (String columnaActual : columnasActuales) {
            if (!columnasConstantes.contains(columnaActual)) {
                columnasActualizables.add(columnaActual);
            }
        }

        // Filtrar nuevas columnas que no son constantes
        ArrayList<String> nuevasColumnasActualizables = new ArrayList<>();
        for (String nuevaColumna : nuevasColumnas) {
            if (!columnasConstantes.contains(nuevaColumna)) {
                nuevasColumnasActualizables.add(nuevaColumna);
            }
        }

        // Agregar nuevas columnas si hay más nuevas columnas que actuales
        if (nuevasColumnasActualizables.size() > columnasActualizables.size()) {
            for (int i = columnasActualizables.size(); i < nuevasColumnasActualizables.size(); i++) {
                String nuevaColumna = nuevasColumnasActualizables.get(i);
                String sql = "ALTER TABLE `" + nombreTabla + "` ADD COLUMN `" + nuevaColumna + "` VARCHAR(255)";
                System.out.println("Ejecutando SQL para agregar columna: " + sql); // Depuración
                try (Statement statement = conexion.createStatement()) {
                    statement.executeUpdate(sql);
                    System.out.println("Columna añadida: " + nuevaColumna);
                }
            }
        }

        // Eliminar columnas si hay menos nuevas columnas que actuales
        if (nuevasColumnasActualizables.size() < columnasActualizables.size()) {
            for (int i = nuevasColumnasActualizables.size(); i < columnasActualizables.size(); i++) {
                String columnaActual = columnasActualizables.get(i);
                String sql = "ALTER TABLE `" + nombreTabla + "` DROP COLUMN `" + columnaActual + "`";
                System.out.println("Ejecutando SQL para eliminar columna: " + sql); // Depuración
                try (Statement statement = conexion.createStatement()) {
                    statement.executeUpdate(sql);
                    System.out.println("Columna eliminada: " + columnaActual);
                }
            }
        }

        // Renombrar columnas existentes
        int minSize = Math.min(nuevasColumnasActualizables.size(), columnasActualizables.size());
        for (int i = 0; i < minSize; i++) {
            String columnaActual = columnasActualizables.get(i);
            String nuevaColumna = nuevasColumnasActualizables.get(i);
            if (!columnaActual.equals(nuevaColumna)) {
                String sql = "ALTER TABLE `" + nombreTabla + "` CHANGE `" + columnaActual + "` `" + nuevaColumna + "` VARCHAR(255)";
                System.out.println("Ejecutando SQL para renombrar columna: " + sql); // Depuración
                try (Statement statement = conexion.createStatement()) {
                    statement.executeUpdate(sql);
                    System.out.println("Columna renombrada de " + columnaActual + " a " + nuevaColumna);
                }
            }
        }

        // Confirmar la transacción
        if (!conexion.getAutoCommit()) {
            conexion.commit();
            System.out.println("Transacción comprometida exitosamente.");
        }

        System.out.println("Actualización de columnas completada.");
    } catch (SQLException e) {
        if (!conexion.getAutoCommit()) {
            conexion.rollback();
            System.out.println("Transacción revertida debido a un error.");
        }
        throw e;
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
    String prefijo = nombreTabla.length() >= 3 ? nombreTabla.substring(0, 3).toUpperCase() : nombreTabla.toUpperCase();
    int numero = 1; // Empezar desde 1
    String nuevoId;

    while (true) {
        // Genera el ID con el prefijo y el número actual
        nuevoId = prefijo + String.format("%05d", numero);

        // Verificar si el ID ya existe en la base de datos
        String query = "SELECT COUNT(*) FROM `" + nombreTabla + "` WHERE id = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(query)) {
            stmt.setString(1, nuevoId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                // Si el ID no existe, lo devolvemos
                break;
            }
        }
        // Si el ID ya existe, incrementa el número y vuelve a intentar
        numero++;
    }

    return nuevoId;
}


    public void insertarTipoDocumento(String generatedId, String nombreFormulario, String nombreTabla) throws SQLException {
    // Consulta SQL para insertar un nuevo registro en la tabla 'tiposdocumentos'
    String sql = "INSERT INTO tiposdocumentos (id, nombre, nombre_tabla) VALUES (?, ?, ?)";

    try (PreparedStatement statement = conexion.prepareStatement(sql)) {
        
        // Asignar valores a los parámetros
        statement.setString(1, generatedId); 
        statement.setString(2, nombreFormulario); 
        statement.setString(3, nombreTabla);  // Guardar el nombre de la tabla generada

        // Ejecutar la consulta
        statement.executeUpdate();
    } catch (SQLException e) {
        System.err.println("Error al insertar el tipo de documento: " + e.getMessage());
        throw e;
    }
}

    
 

}
