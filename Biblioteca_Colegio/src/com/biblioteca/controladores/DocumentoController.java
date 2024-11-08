package com.biblioteca.controladores;

import com.biblioteca.bd.ConexionBaseDatos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DocumentoController {

    public boolean guardarDocumento(String tipoDocumento, String[] datos, String[] tiposColumnas) {
        ArrayList<String> columnas = obtenerColumnasDeTabla(tipoDocumento);
        
        if (columnas == null || columnas.isEmpty()) {
            System.err.println("No se encontraron columnas para la tabla: " + tipoDocumento);
            return false;
        }

        // Construir consulta dinámica
        StringBuilder sql = new StringBuilder("INSERT INTO ").append(tipoDocumento).append(" (");
        for (String columna : columnas) {
            sql.append(columna).append(", ");
        }
        sql.setLength(sql.length() - 2); // Eliminar la última coma
        sql.append(") VALUES (");
        for (int i = 0; i < columnas.size(); i++) {
            sql.append("?, ");
        }
        sql.setLength(sql.length() - 2); // Eliminar la última coma
        sql.append(")");

        try (Connection conexion = ConexionBaseDatos.getConexion();
             PreparedStatement statement = conexion.prepareStatement(sql.toString())) {

            // Asignar valores a los parámetros según el tipo de columna
            for (int i = 0; i < datos.length; i++) {
                String tipoColumna = tiposColumnas[i].toLowerCase(); // Obtener el tipo de columna
                switch (tipoColumna) {
                    case "int":
                        statement.setInt(i + 1, Integer.parseInt(datos[i])); // Parsear a int
                        break;
                    case "date":
                        statement.setDate(i + 1, java.sql.Date.valueOf(datos[i])); // Asignar como fecha
                        break;
                    default:
                        statement.setString(i + 1, datos[i]); // Asignar como String
                }
            }

            int filasInsertadas = statement.executeUpdate();
            return filasInsertadas > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Método para obtener dinámicamente las columnas de la tabla
    private ArrayList<String> obtenerColumnasDeTabla(String tipoDocumento) {
        ArrayList<String> columnas = new ArrayList<>();
        String query = "SHOW COLUMNS FROM " + tipoDocumento;

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
