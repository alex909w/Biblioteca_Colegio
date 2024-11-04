package bd;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GestionUsuarios {

    // Método para validar las credenciales
    public static boolean validarCredenciales(String usuario, String contrasena) {
        boolean valido = false;
        Connection conexion = ConexionBaseDatos.getConexion();

        if (conexion != null) {
            String query = "SELECT * FROM Usuarios WHERE email = ? AND clave = ?";
            try (PreparedStatement statement = conexion.prepareStatement(query)) {
                statement.setString(1, usuario);
                statement.setString(2, contrasena);
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    valido = true;
                }
            } catch (SQLException e) {
                System.err.println("Error al validar las credenciales: " + e.getMessage());
            } finally {
                ConexionBaseDatos.cerrarConexion();
            }
        } else {
            System.err.println("Error: Conexión a la base de datos fallida.");
        }

        return valido;
    }

    // Método para obtener el tipo de usuario
    public static String obtenerTipoUsuario(String usuario) {
        String tipoUsuario = "";
        Connection conexion = ConexionBaseDatos.getConexion();

        if (conexion != null) {
            String query = "SELECT tipo_usuario FROM Usuarios WHERE email = ?";
            try (PreparedStatement statement = conexion.prepareStatement(query)) {
                statement.setString(1, usuario);
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    tipoUsuario = resultSet.getString("tipo_usuario");
                } else {
                    System.out.println("Usuario no encontrado.");
                }
            } catch (SQLException e) {
                System.err.println("Error al obtener el tipo de usuario: " + e.getMessage());
            } finally {
                ConexionBaseDatos.cerrarConexion();
            }
        } else {
            System.err.println("Error: Conexión a la base de datos fallida.");
        }

        return tipoUsuario;
    }

    // Método para agregar un usuario a la base de datos
    public static boolean agregarUsuario(String idUsuario, String nombre, String apellido, String clave, String email, JFrame frame) {
        boolean usuarioAgregado = false;
        Connection conexion = ConexionBaseDatos.getConexion();

        if (conexion != null) {
            String query = "INSERT INTO Usuarios (id_usuario, nombre, apellido, clave, email) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement statement = conexion.prepareStatement(query)) {
                statement.setString(1, idUsuario);
                statement.setString(2, nombre);
                statement.setString(3, apellido);
                statement.setString(4, clave);
                statement.setString(5, email);

                int filasInsertadas = statement.executeUpdate();
                usuarioAgregado = (filasInsertadas > 0);

                if (usuarioAgregado) {
                    JOptionPane.showMessageDialog(frame, "Usuario agregado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(frame, "No se pudo agregar el usuario.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(frame, "Error al agregar el usuario: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                ConexionBaseDatos.cerrarConexion();
            }
        } else {
            System.err.println("Error: Conexión a la base de datos fallida.");
        }

        return usuarioAgregado;
    }
}
