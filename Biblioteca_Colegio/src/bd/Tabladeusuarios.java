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
             PreparedStatement statement = connection.prepareStatement("SELECT id_usuario, nombre, apellido, clave, email FROM Usuarios");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String id = resultSet.getString("id_usuario");
                String nombre = resultSet.getString("nombre");
                String apellidos = resultSet.getString("apellido");
                String clave = resultSet.getString("clave");
                String email = resultSet.getString("email");

                // Añade cada fila a la tabla
                tableModel.addRow(new Object[]{id, nombre, apellidos, clave, email});
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error al cargar los usuarios: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Nota: No llames a cerrarConexion() aquí si aún necesitas la conexión para otras operaciones
    }
}
