package com.biblioteca.controladores;

import com.biblioteca.bd.ConexionBaseDatos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Date;

public class GestionProductoController {

    public boolean guardarDocumento(String tipoDocumento, String[] nombresColumnas, String[] datos, String[] tiposColumnas) {
        if (datos.length != nombresColumnas.length) {
            System.err.println("Error: El número de campos de datos proporcionados no coincide con los nombres de las columnas.");
            return false;
        }

        StringBuilder sql = new StringBuilder("INSERT INTO `" + tipoDocumento + "` (");
        for (String columna : nombresColumnas) {
            sql.append("`").append(columna).append("`, ");
        }
        sql.setLength(sql.length() - 2);
        sql.append(") VALUES (");
        for (int i = 0; i < nombresColumnas.length; i++) {
            sql.append("?, ");
        }
        sql.setLength(sql.length() - 2);
        sql.append(")");

        try (Connection conexion = ConexionBaseDatos.getConexion();
             PreparedStatement statement = conexion.prepareStatement(sql.toString())) {

            for (int i = 0; i < datos.length; i++) {
                String tipo = tiposColumnas[i].toLowerCase();
                String valor = datos[i];

                if (tipo.contains("int")) {
                    if (valor == null || valor.isEmpty()) {
                        statement.setNull(i + 1, java.sql.Types.INTEGER);
                    } else {
                        statement.setInt(i + 1, Integer.parseInt(valor));
                    }
                } else if (tipo.contains("date")) {
                    if (valor == null || valor.isEmpty()) {
                        statement.setNull(i + 1, java.sql.Types.DATE);
                    } else {
                        statement.setDate(i + 1, Date.valueOf(valor));
                    }
                } else {
                    statement.setString(i + 1, valor);
                }
            }

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error SQL durante la operación de guardado: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
