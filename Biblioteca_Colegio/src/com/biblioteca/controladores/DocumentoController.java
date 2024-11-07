package com.biblioteca.controladores;

import com.biblioteca.bd.ConexionBaseDatos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DocumentoController {

    public boolean guardarDocumento(String tipoDocumento, String[] datos) {
    String sql = "";

    switch (tipoDocumento) {
        case "Libros":
            sql = "INSERT INTO Libros (id_documento, titulo, autor, fecha_publicacion, categoria, ejemplares, isbn, paginas) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            break;
        case "Revistas":
            sql = "INSERT INTO Revistas (id_documento, titulo, autor, fecha_publicacion, categoria, ejemplares, periodicidad) VALUES (?, ?, ?, ?, ?, ?, ?)";
            break;
        case "CDs":
            sql = "INSERT INTO CDs (id_documento, titulo, autor, fecha_publicacion, categoria, ejemplares, artista, genero, duracion_min, numero_canciones) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            break;
        case "Tesis":
            sql = "INSERT INTO Tesis (id_documento, titulo, autor, fecha_publicacion, categoria, ejemplares, universidad) VALUES (?, ?, ?, ?, ?, ?, ?)";
            break;
        default:
            System.err.println("Tipo de documento desconocido: " + tipoDocumento);
            return false;
    }

    try (Connection conexion = ConexionBaseDatos.getConexion();
         PreparedStatement statement = conexion.prepareStatement(sql)) {

        // Inserta los datos en la consulta SQL
        for (int i = 0; i < datos.length; i++) {
            statement.setString(i + 1, datos[i]);
        }

        int filasInsertadas = statement.executeUpdate();
        return filasInsertadas > 0;

    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}


    // Método para contar los parámetros en la consulta
    private int contarParametros(String sql) {
        return (int) sql.chars().filter(ch -> ch == '?').count();
    }
    
    
}
