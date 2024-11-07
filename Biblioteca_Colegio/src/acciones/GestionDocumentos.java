package acciones;

import bd.ConexionBaseDatos;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;  // Especificamos java.util.List explícitamente
import java.util.Map;
import java.util.Vector;

public class GestionDocumentos extends JFrame {

    private JComboBox<String> tipoDocumentoComboBox;
    private JPanel panelFormulario;
    private JTable tablaDocumentos;
    private Map<String, String> tipoTablaMap;
    private Map<String, List<String>> camposTablaMap;
    private List<JTextField> camposEntrada;

    public GestionDocumentos(JFrame ventanaAnterior) {
        setTitle("Gestión de Documentos");
        setSize(800, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        tipoTablaMap = new HashMap<>();
        camposTablaMap = new HashMap<>();
        camposEntrada = new ArrayList<>();

        JPanel panelEncabezado = new JPanel(new BorderLayout());
        JLabel tipoDocumentoLabel = new JLabel("Tipo de Documento:");
        tipoDocumentoLabel.setFont(new Font("Arial", Font.BOLD, 16));
        tipoDocumentoComboBox = new JComboBox<>();
        tipoDocumentoComboBox.setFont(new Font("Arial", Font.PLAIN, 14));

        cargarTiposDocumentos();

        tipoDocumentoComboBox.addActionListener(e -> actualizarFormulario());

        panelEncabezado.add(tipoDocumentoLabel, BorderLayout.WEST);
        panelEncabezado.add(tipoDocumentoComboBox, BorderLayout.CENTER);

        JPanel panelNorte = new JPanel(new BorderLayout());
        panelNorte.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelNorte.add(panelEncabezado, BorderLayout.NORTH);
        add(panelNorte, BorderLayout.NORTH);

        tablaDocumentos = new JTable();
        JScrollPane scrollPane = new JScrollPane(tablaDocumentos);
        add(scrollPane, BorderLayout.CENTER);

        panelFormulario = new JPanel();
        panelFormulario.setLayout(new BoxLayout(panelFormulario, BoxLayout.Y_AXIS));
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Detalles del Documento"));
        add(panelFormulario, BorderLayout.EAST);

        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton guardarButton = new JButton("Guardar");
        JButton editarButton = new JButton("Editar");
        JButton limpiarButton = new JButton("Limpiar");
        JButton eliminarButton = new JButton("Eliminar");

        guardarButton.addActionListener(e -> guardarDocumento());
        editarButton.addActionListener(e -> editarDocumento());
        limpiarButton.addActionListener(e -> limpiarFormulario());
        eliminarButton.addActionListener(e -> eliminarDocumento());

        panelBotones.add(guardarButton);
        panelBotones.add(editarButton);
        panelBotones.add(limpiarButton);
        panelBotones.add(eliminarButton);

        add(panelBotones, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void cargarTiposDocumentos() {
        try (Connection conexion = ConexionBaseDatos.getConexion()) {
            String sql = "SELECT nombre_tipo FROM TiposDocumentos";
            PreparedStatement statement = conexion.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            tipoDocumentoComboBox.removeAllItems();

            while (resultSet.next()) {
                String tipoDocumento = resultSet.getString("nombre_tipo");
                tipoDocumentoComboBox.addItem(tipoDocumento);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar tipos de documentos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void actualizarFormulario() {
        panelFormulario.removeAll();
        camposEntrada.clear();

        String tipoSeleccionado = (String) tipoDocumentoComboBox.getSelectedItem();
        if (tipoSeleccionado == null) {
            panelFormulario.revalidate();
            panelFormulario.repaint();
            return;
        }

        String nombreTabla = obtenerNombreTabla(tipoSeleccionado);
        if (nombreTabla == null) {
            JOptionPane.showMessageDialog(this, "No se pudo determinar la tabla para el tipo seleccionado.");
            return;
        }

        List<String> camposTabla = obtenerCamposTabla(nombreTabla);
        if (camposTabla == null || camposTabla.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No se pudieron obtener los campos de la tabla.");
            return;
        }

        panelFormulario.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        for (String campo : camposTabla) {
            if (campo.equalsIgnoreCase("id_tipo_documento") || campo.startsWith("id_")) {
                continue;
            }

            JLabel label = new JLabel(campo + ":");
            label.setFont(new Font("Arial", Font.PLAIN, 14));
            gbc.gridx = 0;
            gbc.weightx = 0.3;
            panelFormulario.add(label, gbc);

            JTextField textField = new JTextField();
            textField.setFont(new Font("Arial", Font.PLAIN, 14));
            gbc.gridx = 1;
            gbc.weightx = 0.7;
            panelFormulario.add(textField, gbc);

            camposEntrada.add(textField);
            gbc.gridy++;
        }

        panelFormulario.revalidate();
        panelFormulario.repaint();

        cargarDatosTabla(nombreTabla);
    }

    private String obtenerNombreTabla(String tipoDocumento) {
        if (tipoTablaMap.containsKey(tipoDocumento)) {
            return tipoTablaMap.get(tipoDocumento);
        }

        try (Connection conexion = ConexionBaseDatos.getConexion()) {
            String sql = "SELECT id_tipo_documento FROM TiposDocumentos WHERE nombre_tipo = ?";
            PreparedStatement statement = conexion.prepareStatement(sql);
            statement.setString(1, tipoDocumento);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String idTipoDocumento = resultSet.getString("id_tipo_documento");
                String nombreTabla = mapearIdTipoATabla(idTipoDocumento);
                tipoTablaMap.put(tipoDocumento, nombreTabla);
                return nombreTabla;
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al obtener el ID del tipo de documento: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    private String mapearIdTipoATabla(String idTipoDocumento) {
        try (Connection conexion = ConexionBaseDatos.getConexion()) {
            String sql = "SHOW TABLES LIKE ?";
            PreparedStatement statement = conexion.prepareStatement(sql);
            statement.setString(1, "%" + idTipoDocumento + "%");
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String nombreTabla = resultSet.getString(1);
                return nombreTabla;
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al mapear ID de tipo a tabla: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    private List<String> obtenerCamposTabla(String nombreTabla) {
        if (camposTablaMap.containsKey(nombreTabla)) {
            return camposTablaMap.get(nombreTabla);
        }

        List<String> campos = new ArrayList<>();

        try (Connection conexion = ConexionBaseDatos.getConexion()) {
            String sql = "SELECT * FROM " + nombreTabla + " LIMIT 1";
            PreparedStatement statement = conexion.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnas = metaData.getColumnCount();

            for (int i = 1; i <= columnas; i++) {
                campos.add(metaData.getColumnName(i));
            }

            camposTablaMap.put(nombreTabla, campos);
            return campos;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al obtener los campos de la tabla: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    private void cargarDatosTabla(String nombreTabla) {
        try (Connection conexion = ConexionBaseDatos.getConexion()) {
            String sql = "SELECT * FROM " + nombreTabla;
            PreparedStatement statement = conexion.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnas = metaData.getColumnCount();
            Vector<String> nombresColumnas = new Vector<>();
            for (int i = 1; i <= columnas; i++) {
                nombresColumnas.add(metaData.getColumnName(i));
            }

            Vector<Vector<Object>> datos = new Vector<>();
            while (resultSet.next()) {
                Vector<Object> fila = new Vector<>();
                for (int i = 1; i <= columnas; i++) {
                    fila.add(resultSet.getObject(i));
                }
                datos.add(fila);
            }

            DefaultTableModel modelo = new DefaultTableModel(datos, nombresColumnas) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            tablaDocumentos.setModel(modelo);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar datos de la tabla: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void guardarDocumento() {
        String tipoSeleccionado = (String) tipoDocumentoComboBox.getSelectedItem();
        if (tipoSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un tipo de documento primero.");
            return;
        }

        String nombreTabla = obtenerNombreTabla(tipoSeleccionado);
        if (nombreTabla == null) {
            JOptionPane.showMessageDialog(this, "No se pudo determinar la tabla para el tipo seleccionado.");
            return;
        }

        List<String> camposTabla = camposTablaMap.get(nombreTabla);
        if (camposTabla == null || camposTabla.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No se pudieron obtener los campos de la tabla.");
            return;
        }

        Map<String, String> datosDocumento = new HashMap<>();
        int indiceCampo = 0;
        for (String campo : camposTabla) {
            if (campo.equalsIgnoreCase("id_tipo_documento") || campo.startsWith("id_")) {
                continue;
            }
            String valor = camposEntrada.get(indiceCampo).getText().trim();
            if (valor.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El campo \"" + campo + "\" no puede estar vacío.");
                return;
            }
            datosDocumento.put(campo, valor);
            indiceCampo++;
        }

        StringBuilder columnas = new StringBuilder("id_tipo_documento");
        StringBuilder valores = new StringBuilder("?");
        for (String campo : datosDocumento.keySet()) {
            columnas.append(", ").append(campo);
            valores.append(", ?");
        }

        String sql = "INSERT INTO " + nombreTabla + " (" + columnas.toString() + ") VALUES (" + valores.toString() + ")";

        try (Connection conexion = ConexionBaseDatos.getConexion()) {
            PreparedStatement statement = conexion.prepareStatement(sql);
            statement.setString(1, obtenerIdTipoDocumento(tipoSeleccionado));
            int parametroIndex = 2;
            for (String campo : datosDocumento.keySet()) {
                statement.setString(parametroIndex++, datosDocumento.get(campo));
            }

            int filasInsertadas = statement.executeUpdate();
            if (filasInsertadas > 0) {
                JOptionPane.showMessageDialog(this, "Documento guardado exitosamente.");
                limpiarFormulario();
                cargarDatosTabla(nombreTabla);
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo guardar el documento.");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al guardar el documento: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String obtenerIdTipoDocumento(String tipoDocumento) {
        try (Connection conexion = ConexionBaseDatos.getConexion()) {
            String sql = "SELECT id_tipo_documento FROM TiposDocumentos WHERE nombre_tipo = ?";
            PreparedStatement statement = conexion.prepareStatement(sql);
            statement.setString(1, tipoDocumento);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("id_tipo_documento");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al obtener el ID del tipo de documento: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    private void editarDocumento() {
        JOptionPane.showMessageDialog(this, "Funcionalidad de editar no implementada aún.");
    }

    private void limpiarFormulario() {
        for (JTextField campo : camposEntrada) {
            campo.setText("");
        }
    }

    private void eliminarDocumento() {
        JOptionPane.showMessageDialog(this, "Funcionalidad de eliminar no implementada aún.");
    }

    public static void mostrarGestionDocumentos(JFrame ventanaAnterior) {
        SwingUtilities.invokeLater(() -> {
            ventanaAnterior.dispose();
            new GestionDocumentos(ventanaAnterior).setVisible(true);
        });
    }
}
