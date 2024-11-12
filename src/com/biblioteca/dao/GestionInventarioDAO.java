package com.biblioteca.dao;

import com.biblioteca.bd.ConexionBaseDatos;
import java.sql.*;
import java.util.ArrayList;

public class GestionInventarioDAO {

    private final Connection conexion;

    public GestionInventarioDAO() throws SQLException {
        // Obtener conexión a la base de datos usando la clase de conexión
        this.conexion = ConexionBaseDatos.getConexion();
    }

    public ArrayList<String> obtenerTiposDocumentos() throws SQLException {
        ArrayList<String> tipos = new ArrayList<>();
        String query = "SELECT nombre FROM tiposdocumentos";
        Statement stmt = conexion.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        while (rs.next()) {
            tipos.add(rs.getString("nombre"));
        }
        return tipos;
    }

    public ArrayList<String> obtenerColumnasDeTabla(String nombreTabla) throws SQLException {
        ArrayList<String> columnas = new ArrayList<>();
        DatabaseMetaData metaData = conexion.getMetaData();
        ResultSet rs = metaData.getColumns(null, null, nombreTabla, null);
        while (rs.next()) {
            columnas.add(rs.getString("COLUMN_NAME"));
        }
        return columnas;
    }

    public String generarNuevoId(String nombreTabla) throws SQLException {
        String nuevoId = null;
        String query = "SELECT MAX(id) + 1 AS nuevoId FROM " + nombreTabla;
        Statement stmt = conexion.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        if (rs.next()) {
            nuevoId = rs.getString("nuevoId") != null ? rs.getString("nuevoId") : "1";
        }
        return nuevoId;
    }

    public void insertarDatosEnTabla(String nombreTabla, ArrayList<String> columnas, ArrayList<String> valores) throws SQLException {
        String query = "INSERT INTO " + nombreTabla + " (" + String.join(", ", columnas) + ") VALUES (" +
                String.join(", ", valores.stream().map(v -> "?").toArray(String[]::new)) + ")";
        PreparedStatement pstmt = conexion.prepareStatement(query);
        for (int i = 0; i < valores.size(); i++) {
            pstmt.setString(i + 1, valores.get(i));
        }
        pstmt.executeUpdate();
    }

    public ArrayList<ArrayList<String>> obtenerDatosDeTabla(String nombreTabla) throws SQLException {
        ArrayList<ArrayList<String>> datos = new ArrayList<>();
        String query = "SELECT * FROM " + nombreTabla;
        Statement stmt = conexion.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        while (rs.next()) {
            ArrayList<String> fila = new ArrayList<>();
            for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                fila.add(rs.getString(i));
            }
            datos.add(fila);
        }
        return datos;
    }

    public ArrayList<String> obtenerDatosPorId(String nombreTabla, String id) throws SQLException {
        ArrayList<String> datos = new ArrayList<>();
        String query = "SELECT * FROM " + nombreTabla + " WHERE id = ?";
        PreparedStatement pstmt = conexion.prepareStatement(query);
        pstmt.setString(1, id);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                datos.add(rs.getString(i));
            }
        }
        return datos;
    }

    public void actualizarDatosEnTabla(String nombreTabla, ArrayList<String> columnas, ArrayList<String> valores, String id) throws SQLException {
        String query = "UPDATE " + nombreTabla + " SET " + String.join(", ", columnas.stream().map(c -> c + " = ?").toArray(String[]::new)) +
                " WHERE id = ?";
        PreparedStatement pstmt = conexion.prepareStatement(query);
        for (int i = 0; i < valores.size(); i++) {
            pstmt.setString(i + 1, valores.get(i));
        }
        pstmt.setString(valores.size() + 1, id);
        pstmt.executeUpdate();
    }

    public void eliminarInventarioPorId(String nombreTabla, String id) throws SQLException {
        String query = "DELETE FROM " + nombreTabla + " WHERE id = ?";
        PreparedStatement pstmt = conexion.prepareStatement(query);
        pstmt.setString(1, id);
        pstmt.executeUpdate();
    }
}
