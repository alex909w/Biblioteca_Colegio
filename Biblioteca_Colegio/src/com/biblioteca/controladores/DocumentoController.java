package com.biblioteca.controladores;

import com.biblioteca.bd.ConexionBaseDatos;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DocumentoController {

    public boolean guardarDocumento(String tipoDocumento, String[] datos, String[] tiposColumnas) {
    ArrayList<String> columnas = obtenerColumnasDeTabla(tipoDocumento);

    if (datos.length != columnas.size()) {
        System.err.println("Error: The number of provided data fields does not match the table columns.");
        return false;
    }

    StringBuilder sql = new StringBuilder("INSERT INTO `" + tipoDocumento + "` (");
    for (String columna : columnas) {
        sql.append("`").append(columna).append("`, ");
    }
    sql.setLength(sql.length() - 2); // Remove last comma
    sql.append(") VALUES (");
    for (int i = 0; i < columnas.size(); i++) {
        sql.append("?, ");
    }
    sql.setLength(sql.length() - 2); // Remove last comma
    sql.append(")");

    Connection conexion = null;
    try {
        conexion = ConexionBaseDatos.getConexion();
        PreparedStatement statement = conexion.prepareStatement(sql.toString());

        for (int i = 0; i < datos.length; i++) {
            statement.setString(i + 1, datos[i]);
        }

        int rowsAffected = statement.executeUpdate();
        System.out.println("Rows affected: " + rowsAffected);
        return rowsAffected > 0;

    } catch (SQLException e) {
        System.err.println("SQL Error during save operation: " + e.getMessage());
        return false;
    } finally {
        
    }
}



    private ArrayList<String> obtenerColumnasDeTabla(String tipoDocumento) {
        ArrayList<String> columnas = new ArrayList<>();
        String query = "SHOW COLUMNS FROM `" + tipoDocumento + "`";

        try (Connection conexion = ConexionBaseDatos.getConexion();
             Statement statement = conexion.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                String nombreColumna = resultSet.getString("Field");
                columnas.add(formatearNombreColumna(nombreColumna));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener columnas de la tabla " + tipoDocumento + ": " + e.getMessage());
        }

        return columnas;
    }

    private String formatearNombreColumna(String nombreColumna) {
        return nombreColumna.replace("_", " ");
    }
}
