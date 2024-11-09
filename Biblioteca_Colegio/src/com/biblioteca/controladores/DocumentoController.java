package com.biblioteca.controladores;

import com.biblioteca.bd.ConexionBaseDatos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class DocumentoController {

    public boolean guardarDocumento(String tipoDocumento, String[] datos, String[] tiposColumnas) {
        ArrayList<String> columnas = obtenerColumnasDeTabla(tipoDocumento);

        // Registro para depuración
        System.out.println("Columnas de la tabla: " + columnas);
        System.out.println("Datos a insertar: " + Arrays.toString(datos));

        // Construir consulta dinámica con backticks para escapar nombres de tablas y columnas
        StringBuilder sql = new StringBuilder("INSERT INTO `" + tipoDocumento + "` (");
        for (String columna : columnas) {
            sql.append("`").append(columna).append("`, ");
        }
        sql.setLength(sql.length() - 2); // Eliminar la última coma
        sql.append(") VALUES (");
        for (int i = 0; i < columnas.size(); i++) {
            sql.append("?, ");
        }
        sql.setLength(sql.length() - 2); // Eliminar la última coma
        sql.append(")");

        System.out.println("Consulta SQL: " + sql.toString());

        try (Connection conexion = ConexionBaseDatos.getConexion();
             PreparedStatement statement = conexion.prepareStatement(sql.toString())) {

            // Verificar que el número de datos coincide con el número de columnas
            if (datos.length != columnas.size()) {
                System.err.println("Error: La cantidad de datos (" + datos.length + ") no coincide con la cantidad de columnas (" + columnas.size() + ").");
                return false;
            }

            // Asignar valores a los parámetros según el tipo de columna
            for (int i = 0; i < datos.length; i++) {
                String tipoColumna = tiposColumnas[i].toLowerCase();
                switch (tipoColumna) {
                    case "int":
                        try {
                            statement.setInt(i + 1, Integer.parseInt(datos[i]));
                        } catch (NumberFormatException e) {
                            System.err.println("Error al convertir a int para la columna " + columnas.get(i) + ": " + datos[i]);
                            statement.setNull(i + 1, java.sql.Types.INTEGER);
                        }
                        break;
                    case "date":
                        if (datos[i] != null && !datos[i].isEmpty()) {
                            statement.setDate(i + 1, java.sql.Date.valueOf(datos[i]));
                        } else {
                            statement.setNull(i + 1, java.sql.Types.DATE);
                        }
                        break;
                    default:
                        statement.setString(i + 1, datos[i]);
                }
            }

            int filasInsertadas = statement.executeUpdate();
            return filasInsertadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al ejecutar la consulta SQL: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Método para obtener dinámicamente las columnas de la tabla
    private ArrayList<String> obtenerColumnasDeTabla(String tipoDocumento) {
        ArrayList<String> columnas = new ArrayList<>();
        String query = "SHOW COLUMNS FROM `" + tipoDocumento + "`"; // Escapar el nombre de la tabla

        try (Connection conexion = ConexionBaseDatos.getConexion();
             PreparedStatement statement = conexion.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String nombreColumna = resultSet.getString("Field");
                columnas.add(nombreColumna);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return columnas;
    }
}
