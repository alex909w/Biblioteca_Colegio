package com.biblioteca.dao;

import com.biblioteca.bd.ConexionBaseDatos;
import com.biblioteca.utilidades.Validaciones;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;
import java.util.regex.Pattern;

public class GestionUsuariosDAO {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$");

    public boolean agregarUsuario(String idUsuario, String nombre, String apellido, String clave, String email,
                                  String tipoUsuario, String telefono, String fechaRegistro, int limitePrestamos) {
        String errorMessage = Validaciones.validarDatosUsuario(nombre, apellido, clave, email, telefono, limitePrestamos);
        if (!errorMessage.isEmpty()) {
            JOptionPane.showMessageDialog(null, errorMessage, "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        boolean usuarioAgregado = false;
        Connection conexion = ConexionBaseDatos.getConexion();

        if (conexion != null) {
            String query = "INSERT INTO Usuarios (id_usuario, nombre, apellido, clave, email, tipo_usuario, telefono, fecha_registro, limite_prestamos) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement statement = conexion.prepareStatement(query)) {
                String hashedClave = BCrypt.hashpw(clave, BCrypt.gensalt());

                statement.setString(1, idUsuario);
                statement.setString(2, nombre);
                statement.setString(3, apellido);
                statement.setString(4, hashedClave);
                statement.setString(5, email);
                statement.setString(6, tipoUsuario);
                statement.setString(7, telefono);
                statement.setString(8, fechaRegistro);
                statement.setInt(9, limitePrestamos);

                int filasInsertadas = statement.executeUpdate();
                usuarioAgregado = (filasInsertadas > 0);

                if (usuarioAgregado) {
                    JOptionPane.showMessageDialog(null, "Usuario agregado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "No se pudo agregar el usuario.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error al agregar el usuario: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                ConexionBaseDatos.cerrarConexion();
            }
        } else {
            System.err.println("Error: Conexión a la base de datos fallida.");
        }

        return usuarioAgregado;
    }

    public boolean actualizarUsuario(String idUsuario, String nombre, String apellido, String clave, String email,
                                     String tipoUsuario, String telefono, int limitePrestamos) {
        String errorMessage = Validaciones.validarDatosUsuario(nombre, apellido, clave, email, telefono, limitePrestamos);
        if (!errorMessage.isEmpty()) {
            JOptionPane.showMessageDialog(null, errorMessage, "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        boolean usuarioActualizado = false;
        Connection conexion = ConexionBaseDatos.getConexion();

        if (conexion != null) {
            String query = "UPDATE Usuarios SET nombre = ?, apellido = ?, clave = ?, email = ?, tipo_usuario = ?, telefono = ?, limite_prestamos = ? " +
                    "WHERE id_usuario = ?";
            try (PreparedStatement statement = conexion.prepareStatement(query)) {
                String hashedClave = BCrypt.hashpw(clave, BCrypt.gensalt());

                statement.setString(1, nombre);
                statement.setString(2, apellido);
                statement.setString(3, hashedClave);
                statement.setString(4, email);
                statement.setString(5, tipoUsuario);
                statement.setString(6, telefono);
                statement.setInt(7, limitePrestamos);
                statement.setString(8, idUsuario);

                int filasActualizadas = statement.executeUpdate();
                usuarioActualizado = (filasActualizadas > 0);

                if (!usuarioActualizado) {
                    System.out.println("No se pudo actualizar el usuario. Verifica que el ID exista en la base de datos.");
                }
            } catch (SQLException e) {
                System.err.println("Error al actualizar el usuario: " + e.getMessage());
            } finally {
                ConexionBaseDatos.cerrarConexion();
            }
        } else {
            System.err.println("Error: Conexión a la base de datos fallida.");
        }

        return usuarioActualizado;
    }

    public void buscarUsuarioEnTabla(DefaultTableModel tableModel, String filtro, String termino) {
        Connection conexion = ConexionBaseDatos.getConexion();
        String query = "";

        switch (filtro) {
            case "Nombre":
                query = "SELECT * FROM Usuarios WHERE nombre LIKE ?";
                break;
            case "Correo":
                query = "SELECT * FROM Usuarios WHERE email LIKE ?";
                break;
            case "Tipo de Usuario":
                query = "SELECT * FROM Usuarios WHERE tipo_usuario LIKE ?";
                break;
            default:
                JOptionPane.showMessageDialog(null, "Filtro de búsqueda no reconocido.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
        }

        if (conexion != null) {
            try (PreparedStatement statement = conexion.prepareStatement(query)) {
                statement.setString(1, "%" + termino + "%");
                ResultSet resultSet = statement.executeQuery();
                tableModel.setRowCount(0);

                while (resultSet.next()) {
                    tableModel.addRow(new Object[]{
                            resultSet.getString("id_usuario"),
                            resultSet.getString("nombre"),
                            resultSet.getString("apellido"),
                            resultSet.getString("tipo_usuario"),
                            resultSet.getString("email"),
                            resultSet.getString("clave"),
                            resultSet.getString("telefono"),
                            resultSet.getString("fecha_registro"),
                            resultSet.getInt("limite_prestamos")
                    });
                }
            } catch (SQLException e) {
                System.err.println("Error al buscar usuarios: " + e.getMessage());
            } finally {
                ConexionBaseDatos.cerrarConexion();
            }
        } else {
            System.err.println("Error: Conexión a la base de datos fallida.");
        }
    }

    public boolean eliminarUsuario(String idUsuario) {
        boolean usuarioEliminado = false;
        Connection conexion = ConexionBaseDatos.getConexion();

        if (conexion != null) {
            String query = "DELETE FROM Usuarios WHERE id_usuario = ?";
            try (PreparedStatement statement = conexion.prepareStatement(query)) {
                statement.setString(1, idUsuario);
                int filasEliminadas = statement.executeUpdate();
                usuarioEliminado = (filasEliminadas > 0);
            } catch (SQLException e) {
                System.err.println("Error al eliminar el usuario: " + e.getMessage());
            } finally {
                ConexionBaseDatos.cerrarConexion();
            }
        } else {
            System.err.println("Error: Conexión a la base de datos fallida.");
        }

        return usuarioEliminado;
    }

    public void cargarUsuariosEnTabla(DefaultTableModel tableModel, JPanel panel) {
    Connection conexion = ConexionBaseDatos.getConexion();

    if (conexion != null) {
        String query = "SELECT * FROM Usuarios";
        try (PreparedStatement statement = conexion.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            tableModel.setRowCount(0); // Limpiar tabla

            while (resultSet.next()) {
                tableModel.addRow(new Object[]{
                        resultSet.getString("id_usuario"),
                        resultSet.getString("nombre"),
                        resultSet.getString("apellido"),
                        resultSet.getString("tipo_usuario"),
                        resultSet.getString("email"),
                        resultSet.getString("clave"),
                        resultSet.getString("telefono"),
                        resultSet.getString("fecha_registro"),
                        resultSet.getInt("limite_prestamos")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(panel, "Error al cargar usuarios: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            ConexionBaseDatos.cerrarConexion();
        }
    } else {
        System.err.println("Error: Conexión a la base de datos fallida.");
    }
}


    public int generarSiguienteId(String tipoUsuario) {
        Connection conexion = ConexionBaseDatos.getConexion();
        int siguienteId = 1;
        String inicial = "";

        switch (tipoUsuario) {
            case "Administrador":
                inicial = "AD";
                break;
            case "Profesor":
                inicial = "PR";
                break;
            case "Alumno":
                inicial = "AL";
                break;
        }

        if (conexion != null && !inicial.isEmpty()) {
            String query = "SELECT MAX(CAST(SUBSTRING(id_usuario, 3) AS UNSIGNED)) AS max_id " +
                    "FROM Usuarios WHERE id_usuario LIKE ?";
            try (PreparedStatement statement = conexion.prepareStatement(query)) {
                statement.setString(1, inicial + "%");
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    int maxId = resultSet.getInt("max_id");
                    siguienteId = maxId + 1;
                }
            } catch (SQLException e) {
                System.err.println("Error al generar el siguiente ID: " + e.getMessage());
            }
        }

        return siguienteId;
    }

    public boolean validarCredenciales(String usuario, String contrasena) {
        boolean valido = false;
        Connection conexion = ConexionBaseDatos.getConexion();

        if (conexion != null) {
            String query = "SELECT clave FROM Usuarios WHERE email = ?";
            try (PreparedStatement statement = conexion.prepareStatement(query)) {
                statement.setString(1, usuario);
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    String hashedPassword = resultSet.getString("clave");
                    if (BCrypt.checkpw(contrasena, hashedPassword)) {
                        valido = true;
                    }
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

    public String obtenerTipoUsuario(String usuario) {
        String tipoUsuario = "";
        Connection conexion = ConexionBaseDatos.getConexion();

        if (conexion != null) {
            String query = "SELECT tipo_usuario FROM Usuarios WHERE email = ?";
            try (PreparedStatement statement = conexion.prepareStatement(query)) {
                statement.setString(1, usuario);
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    tipoUsuario = resultSet.getString("tipo_usuario");
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
}
