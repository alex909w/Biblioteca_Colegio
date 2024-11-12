package com.biblioteca.utilidades;

import com.biblioteca.bd.ConexionBaseDatos;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class Validaciones {

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
                }
            } catch (SQLException e) {
                System.err.println("Error al obtener el tipo de usuario: " + e.getMessage());
            }
        }

        return tipoUsuario;
    }

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
            }
        }

        return valido;
    }

    // Método para generar el siguiente ID de usuario basado en la categoría
    public static int generarSiguienteId(String tipoUsuario) {
        Connection conexion = ConexionBaseDatos.getConexion();
        int siguienteId = 1; // Comenzar con 1 si no hay registros
        String inicial = "";

        // Cambiar las iniciales según la categoría
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

    public static String generarIdUsuario(String tipoUsuario) {
        int siguienteId = generarSiguienteId(tipoUsuario);
        String inicial = "";

        // Cambiar las iniciales según la categoría
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

        return inicial + siguienteId;
    }

    public static boolean agregarUsuario(String nombre, String apellido, String email, String tipoUsuario, String telefono, String clave, String fechaRegistro) {
        Connection conexion = ConexionBaseDatos.getConexion();
        if (conexion == null) {
            System.err.println("Error: No se pudo establecer una conexión con la base de datos.");
            return false;
        }

        // Validar campos obligatorios
        if (nombre == null || nombre.isEmpty() || apellido == null || apellido.isEmpty() || email == null || email.isEmpty() ||
            tipoUsuario == null || tipoUsuario.isEmpty() || telefono == null || telefono.isEmpty() || clave == null || clave.isEmpty() ||
            fechaRegistro == null || fechaRegistro.isEmpty()) {
            System.err.println("Error: Todos los campos obligatorios deben tener un valor.");
            return false;
        }

        String idUsuario = generarIdUsuario(tipoUsuario);
        String query = "INSERT INTO Usuarios (id_usuario, nombre, apellido, email, tipo_usuario, telefono, clave, fecha_registro) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = conexion.prepareStatement(query)) {
            statement.setString(1, idUsuario);
            statement.setString(2, nombre);
            statement.setString(3, apellido); 
            statement.setString(4, email);
            statement.setString(5, tipoUsuario);
            statement.setString(6, telefono);
            statement.setString(7, clave);
            statement.setString(8, fechaRegistro);

            int filasInsertadas = statement.executeUpdate();
            if (filasInsertadas > 0) {
                System.out.println("Usuario agregado correctamente: " + idUsuario);
                return true;
            } else {
                System.err.println("No se insertó ninguna fila, verifique los datos.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error al agregar el usuario: " + e.getMessage());
            return false;
        }
    }

    public static boolean agregarDocumento(String titulo, String autor, String tipo) {
        Connection conexion = ConexionBaseDatos.getConexion();
        String query = "INSERT INTO Documentos (titulo, autor, tipo_documento) VALUES (?, ?, ?)";
        try (PreparedStatement statement = conexion.prepareStatement(query)) {
            statement.setString(1, titulo);
            statement.setString(2, autor);
            statement.setString(3, tipo);
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al agregar el documento: " + e.getMessage());
            return false;
        }
    }

    public static boolean guardarConfiguracion(String clave, String valor) {
        Connection conexion = ConexionBaseDatos.getConexion();
        String query = "UPDATE Configuracion SET valor = ? WHERE clave = ?";
        try (PreparedStatement statement = conexion.prepareStatement(query)) {
            statement.setString(1, valor);
            statement.setString(2, clave);
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al guardar la configuración: " + e.getMessage());
            return false;
        }
    }

    public static boolean registrarPrestamo(String usuario, String codigoLibro) {
        Connection conexion = ConexionBaseDatos.getConexion();
        String query = "INSERT INTO Prestamos (usuario, codigo_libro, fecha_prestamo) VALUES (?, ?, NOW())";
        try (PreparedStatement statement = conexion.prepareStatement(query)) {
            statement.setString(1, usuario);
            statement.setString(2, codigoLibro);
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al registrar el préstamo: " + e.getMessage());
            return false;
        }
    }

    public static boolean registrarDevolucion(String codigoLibro) {
        Connection conexion = ConexionBaseDatos.getConexion();
        String query = "UPDATE Prestamos SET fecha_devolucion = NOW() WHERE codigo_libro = ? AND fecha_devolucion IS NULL";
        try (PreparedStatement statement = conexion.prepareStatement(query)) {
            statement.setString(1, codigoLibro);
            int filasActualizadas = statement.executeUpdate();
            return filasActualizadas > 0;
        } catch (SQLException e) {
            System.err.println("Error al registrar la devolución: " + e.getMessage());
            return false;
        }
    }
    
       private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$");

    public static boolean esCorreoValido(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    public static String validarDatosUsuario(String nombre, String apellido, String clave, String email, String telefono, int limitePrestamos) {
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
        } else if (!esCorreoValido(email)) {
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