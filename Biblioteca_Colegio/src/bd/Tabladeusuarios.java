package bd;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Tabladeusuarios {

    public static void cargarUsuariosEnTabla(DefaultTableModel tableModel, JFrame frame) {
        // Limpia la tabla antes de cargar los datos
        tableModel.setRowCount(0);

        // Intenta obtener la conexión y realizar la operación
        try (Connection connection = ConexionBaseDatos.getConexion();
             PreparedStatement statement = connection.prepareStatement("SELECT id_usuario, nombre, apellido, tipo_usuario, email, clave, telefono, fecha_registro, limite_prestamos FROM Usuarios");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String id = resultSet.getString("id_usuario");
                String nombre = resultSet.getString("nombre");
                String apellidos = resultSet.getString("apellido");
                String tipoUsuario = resultSet.getString("tipo_usuario");
                String email = resultSet.getString("email");
                String clave = resultSet.getString("clave");
                String telefono = resultSet.getString("telefono");
                String fechaRegistro = resultSet.getString("fecha_registro");
                int limitePrestamos = resultSet.getInt("limite_prestamos");

                // Añade cada fila a la tabla
                tableModel.addRow(new Object[]{id, nombre, apellidos, tipoUsuario, email, clave, telefono, fechaRegistro, limitePrestamos});
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error al cargar los usuarios: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
