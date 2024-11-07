package com.biblioteca.bd;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBaseDatos {

    private static Connection conexion;

    public static Connection getConexion() {
        try {
            // Si la conexión es nula o está cerrada, crea una nueva
            if (conexion == null || conexion.isClosed()) {
                String url = "jdbc:mysql://localhost:3306/biblioteca_colegio";
                String usuario = "root";
                String contrasena = "";
                conexion = DriverManager.getConnection(url, usuario, contrasena);
                System.out.println("Conexión a la base de datos establecida correctamente.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al conectar con la base de datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return conexion;
    }

    public static void cerrarConexion() {
        if (conexion != null) {
            try {
                conexion.close();
                conexion = null;
                System.out.println("Conexión a la base de datos cerrada correctamente.");
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }
}
