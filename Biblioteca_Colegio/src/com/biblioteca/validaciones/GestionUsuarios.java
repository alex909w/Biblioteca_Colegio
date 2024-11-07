package com.biblioteca.validaciones;

import com.biblioteca.bd.ConexionBaseDatos;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.mindrot.jbcrypt.BCrypt;
import java.util.regex.Pattern;

public class GestionUsuarios {

    // Regex pattern para validar correos electrónicos
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$");

    /**
     * Valida las credenciales del usuario comparando el correo y la contraseña.
     * Las contraseñas están hasheadas usando BCrypt.
     */
    public static boolean validarCredenciales(String usuario, String contrasena) {
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

    /**
     * Agrega un nuevo usuario a la base de datos con validaciones y hashing de contraseña.
     */
    public static boolean agregarUsuario(String idUsuario, String nombre, String apellido, String clave, String email,
                                         String tipoUsuario, String telefono, String fechaRegistro, int limitePrestamos, JFrame frame) {
        String errorMessage = validarDatosUsuario(nombre, apellido, clave, email, telefono, limitePrestamos);
        if (!errorMessage.isEmpty()) {
            JOptionPane.showMessageDialog(frame, errorMessage, "Error de Validación", JOptionPane.ERROR_MESSAGE);
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

    public static boolean agregarUsuario(String idUsuario, String nombre, String apellido, String clave, String email,
                                         String tipoUsuario, String telefono, String fechaRegistro, JFrame frame) {
        return agregarUsuario(idUsuario, nombre, apellido, clave, email, tipoUsuario, telefono, fechaRegistro, 1, frame);
    }

    /**
     * Actualiza un usuario existente en la base de datos con validaciones y hashing de contraseña.
     */
    public static boolean actualizarUsuario(String idUsuario, String nombre, String apellido, String clave, String email,
                                            String tipoUsuario, String telefono, int limitePrestamos) {
        String errorMessage = validarDatosUsuario(nombre, apellido, clave, email, telefono, limitePrestamos);
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

    public static boolean actualizarUsuario(String idUsuario, String nombre, String apellido, String clave, String email,
                                            String tipoUsuario, String telefono) {
        return actualizarUsuario(idUsuario, nombre, apellido, clave, email, tipoUsuario, telefono, 1);
    }

    /**
     * Busca usuarios en la base de datos según el filtro y el término de búsqueda, y los inserta en el modelo de la tabla.
     */
    public static void buscarUsuarioEnTabla(DefaultTableModel tableModel, String filtro, String termino) {
        Connection conexion = ConexionBaseDatos.getConexion();
        String query = "";

        switch (filtro) {
            case "Nombre":
                query = "SELECT id_usuario, nombre, apellido, tipo_usuario, email, clave, telefono, fecha_registro, limite_prestamos FROM Usuarios WHERE nombre LIKE ?";
                break;
            case "Correo":
                query = "SELECT id_usuario, nombre, apellido, tipo_usuario, email, clave, telefono, fecha_registro, limite_prestamos FROM Usuarios WHERE email LIKE ?";
                break;
            case "Tipo de Usuario":
                query = "SELECT id_usuario, nombre, apellido, tipo_usuario, email, clave, telefono, fecha_registro, limite_prestamos FROM Usuarios WHERE tipo_usuario LIKE ?";
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

    /**
     * Elimina un usuario de la base de datos.
     */
    public static boolean eliminarUsuario(String idUsuario) {
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

    /**
     * Carga todos los usuarios desde la base de datos y los inserta en el modelo de la tabla.
     */
    public static void cargarUsuariosEnTabla(DefaultTableModel tableModel, JFrame frame) {
        Connection conexion = ConexionBaseDatos.getConexion();

        if (conexion != null) {
            String query = "SELECT id_usuario, nombre, apellido, tipo_usuario, email, clave, telefono, fecha_registro, limite_prestamos FROM Usuarios";
            try (PreparedStatement statement = conexion.prepareStatement(query)) {
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
                JOptionPane.showMessageDialog(frame, "Error al cargar usuarios: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                ConexionBaseDatos.cerrarConexion();
            }
        } else {
            System.err.println("Error: Conexión a la base de datos fallida.");
        }
    }

    /**
     * Valida los datos del usuario antes de agregar o actualizar.
     */
    private static String validarDatosUsuario(String nombre, String apellido, String clave, String email, String telefono, int limitePrestamos) {
        StringBuilder errorBuilder = new StringBuilder();

        if (nombre == null || nombre.trim().isEmpty()) {
            errorBuilder.append("El nombre es obligatorio.\n");
        }

        if (apellido == null || apellido.trim().isEmpty()) {
            errorBuilder.append("El apellido es obligatorio.\n");
        }

        if (clave == null || clave.trim().isEmpty()) {
            errorBuilder.append("La contraseña es obligatoria.\n");
        }

        if (email == null || email.trim().isEmpty()) {
            errorBuilder.append("El correo electrónico es obligatorio.\n");
        } else if (!EMAIL_PATTERN.matcher(email).matches()) {
            errorBuilder.append("El correo electrónico no es válido.\n");
        }

        if (telefono == null || telefono.trim().isEmpty()) {
            errorBuilder.append("El teléfono es obligatorio.\n");
        }

        if (limitePrestamos <= 0) {
            errorBuilder.append("El límite de préstamos debe ser un número positivo.\n");
        }

        return errorBuilder.toString();
    }
}
