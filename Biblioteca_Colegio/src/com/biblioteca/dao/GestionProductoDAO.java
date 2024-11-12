// GestionProductoDAO.java
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

    // Método getConexion que retorna la conexión
    public Connection getConexion() {
        return conexion;
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
        sb.append("CREATE TABLE `").append(nombreTabla).append("` (");
        sb.append("`id` VARCHAR(10) PRIMARY KEY");

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

        try (Statement stmt = conexion.createStatement()) {
            stmt.executeUpdate(sb.toString());
            System.out.println("Tabla " + nombreTabla + " creada exitosamente con columnas constantes.");
        } catch (SQLException e) {
            System.err.println("Error al crear la tabla " + nombreTabla + ": " + e.getMessage());
            throw e;
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
            if (resultSet.next()) {
                return resultSet.getInt("total");
            }
        }
        return 0;
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

    public ArrayList<String> obtenerColumnasDeTiposDatos() throws SQLException {
        ArrayList<String> columnas = new ArrayList<>();
        String sql = "SHOW COLUMNS FROM `tiposdatos`";

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

    public void insertarDatosEnTabla(String nombreTabla, ArrayList<String> nombresColumnas, ArrayList<String> valores) throws SQLException {
        if (valores.size() != nombresColumnas.size()) {
            throw new SQLException("El número de valores no coincide con el número de columnas.");
        }

        StringBuilder sql = new StringBuilder("INSERT INTO `" + nombreTabla + "` (");
        for (String columna : nombresColumnas) {
            sql.append("`").append(columna).append("`, ");
        }
        sql.delete(sql.length() - 2, sql.length()); // Eliminar la última coma y espacio
        sql.append(") VALUES (");
        for (int i = 0; i < nombresColumnas.size(); i++) {
            sql.append("?, ");
        }
        sql.delete(sql.length() - 2, sql.length()); // Eliminar la última coma y espacio
        sql.append(")");

        try (PreparedStatement statement = conexion.prepareStatement(sql.toString())) {
            for (int i = 0; i < valores.size(); i++) {
                String valor = valores.get(i);
                String columna = nombresColumnas.get(i);
                if (columna.equalsIgnoreCase("id")) {
                    statement.setString(i + 1, valor);
                } else if (columna.toLowerCase().contains("fecha")) {
                    if (valor != null && !valor.isEmpty()) {
                        statement.setDate(i + 1, java.sql.Date.valueOf(valor));
                    } else {
                        statement.setNull(i + 1, java.sql.Types.DATE);
                    }
                } else {
                    statement.setString(i + 1, valor);
                }
            }
            statement.executeUpdate();
        }
    }

    public String generarNuevoId(String nombreTabla) throws SQLException {
        // Verificar que 'conexion' no es null
        if (conexion == null || conexion.isClosed()) {
            throw new SQLException("La conexión a la base de datos no está establecida.");
        }

        // Obtener el número actual de registros en la tabla
        String query = "SELECT COUNT(*) AS total FROM `" + nombreTabla + "`";
        int totalRegistros = 0;

        try (Statement statement = conexion.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            if (resultSet.next()) {
                totalRegistros = resultSet.getInt("total");
            }
        }

        // Incrementar el contador
        totalRegistros++;

        // Generar el nuevo ID basado en el nombre del formulario y el contador
        String prefijo = obtenerPrefijo(nombreTabla);
        String nuevoId = prefijo + String.format("%05d", totalRegistros); // Por ejemplo, ABC00001

        return nuevoId;
    }

    private String obtenerPrefijo(String nombreTabla) {
        // Obtener las primeras tres letras del nombre del formulario en mayúsculas como prefijo
        String prefijo = nombreTabla.length() >= 3 ? nombreTabla.substring(0, 3).toUpperCase() : nombreTabla.toUpperCase();
        return prefijo;
    }

    // Método para actualizar los datos en una tabla específica basado en el ID
    public void actualizarDatosEnTabla(String nombreTabla, ArrayList<String> nombresColumnas, ArrayList<String> valores, String id) throws SQLException {
        if (valores.size() != nombresColumnas.size()) {
            throw new SQLException("El número de valores no coincide con el número de columnas.");
        }

        StringBuilder sql = new StringBuilder("UPDATE `" + nombreTabla + "` SET ");
        for (String columna : nombresColumnas) {
            if (!columna.equalsIgnoreCase("id")) { // Asumiendo que 'id' es la clave primaria
                sql.append("`").append(columna).append("` = ?, ");
            }
        }
        sql.delete(sql.length() - 2, sql.length()); // Eliminar la última coma y espacio
        sql.append(" WHERE `id` = ?");

        try (PreparedStatement statement = conexion.prepareStatement(sql.toString())) {
            int index = 1;
            for (int i = 0; i < nombresColumnas.size(); i++) {
                String columna = nombresColumnas.get(i);
                String valor = valores.get(i);
                if (!columna.equalsIgnoreCase("id")) {
                    if (columna.toLowerCase().contains("fecha")) {
                        if (valor != null && !valor.isEmpty()) {
                            statement.setDate(index++, java.sql.Date.valueOf(valor));
                        } else {
                            statement.setNull(index++, java.sql.Types.DATE);
                        }
                    } else {
                        statement.setString(index++, valor);
                    }
                }
            }
            statement.setString(index, id);
            statement.executeUpdate();
        }
    }

    // Método para obtener todos los IDs de una tabla específica
    public ArrayList<String> obtenerIdsDeTabla(String nombreTabla) throws SQLException {
        ArrayList<String> ids = new ArrayList<>();
        String query = "SELECT `id` FROM `" + nombreTabla + "`";

        try (Statement statement = conexion.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                ids.add(resultSet.getString("id"));
            }
        }

        return ids;
    }

    // Método para obtener todos los datos de una tabla específica

public ArrayList<ArrayList<String>> obtenerDatosDeTabla(String nombreTabla) throws SQLException {
    ArrayList<ArrayList<String>> datos = new ArrayList<>();
    String query = "SELECT * FROM `" + nombreTabla + "`";

    try (Statement statement = conexion.createStatement();
         ResultSet resultSet = statement.executeQuery(query)) {

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

public void eliminarProductoPorId(String nombreTabla, String idProducto) throws SQLException {
        String query = "DELETE FROM `" + nombreTabla + "` WHERE `id` = ?";
        try (PreparedStatement pstmt = conexion.prepareStatement(query)) {
            pstmt.setString(1, idProducto);
            int filasAfectadas = pstmt.executeUpdate();
            if (filasAfectadas == 0) {
                throw new SQLException("No se encontró ningún producto con el ID: " + idProducto);
            }
        }
    }
    
}
