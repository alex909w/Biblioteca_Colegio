package acciones;

import utilidades.DateLabelFormatter;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;
import validaciones.Validaciones;
import bd.Tabladeusuarios;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Date;
import java.util.Properties;

public class administracionUsuarios extends JFrame {

    private JTextField txtId, txtNombres, txtApellidos, txtEmail, txtTelefono, txtBuscar;
    private JPasswordField txtClave;
    private JComboBox<String> cbTipoUsuario, cbFiltroBusqueda;
    private JDatePickerImpl datePicker;
    private JTable tableUsuarios;
    private DefaultTableModel tableModel;

    public administracionUsuarios() {
        setTitle("Gestión de Usuarios - Biblioteca Colegio Amigos De Don Bosco");
        setSize(1000, 900);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel de Campos
        JPanel panelCampos = new JPanel();
        panelCampos.setLayout(new GridLayout(5, 4, 10, 10));
        panelCampos.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Componentes del panel de gestión de usuarios
        JLabel lblId = new JLabel("ID:");
        txtId = new JTextField();
        txtId.setEnabled(false);

        JLabel lblNombres = new JLabel("Nombres:");
        txtNombres = new JTextField();

        JLabel lblApellidos = new JLabel("Apellidos:");
        txtApellidos = new JTextField();

        JLabel lblClave = new JLabel("Contraseña:");
        txtClave = new JPasswordField();

        JLabel lblEmail = new JLabel("Correo Electrónico:");
        txtEmail = new JTextField();

        JLabel lblTelefono = new JLabel("Teléfono:");
        txtTelefono = new JTextField();

        JLabel lblTipoUsuario = new JLabel("Tipo de Usuario:");
        cbTipoUsuario = new JComboBox<>(new String[]{"Administrador", "Profesor", "Alumno"});

        JLabel lblFechaRegistro = new JLabel("Fecha de Registro:");
        UtilDateModel model = new UtilDateModel();
        Properties properties = new Properties();
        properties.put("text.today", "Hoy");
        properties.put("text.month", "Mes");
        properties.put("text.year", "Año");
        JDatePanelImpl datePanel = new JDatePanelImpl(model, properties);
        datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());

        // Añadir componentes al panel de campos
        panelCampos.add(lblId);
        panelCampos.add(txtId);
        panelCampos.add(lblNombres);
        panelCampos.add(txtNombres);
        panelCampos.add(lblApellidos);
        panelCampos.add(txtApellidos);
        panelCampos.add(lblClave);
        panelCampos.add(txtClave);
        panelCampos.add(lblEmail);
        panelCampos.add(txtEmail);
        panelCampos.add(lblTelefono);
        panelCampos.add(txtTelefono);
        panelCampos.add(lblTipoUsuario);
        panelCampos.add(cbTipoUsuario);
        panelCampos.add(lblFechaRegistro);
        panelCampos.add(datePicker);

        // Panel de búsqueda
        JPanel panelBusqueda = new JPanel();
        panelBusqueda.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel lblBuscar = new JLabel("Buscar:");
        txtBuscar = new JTextField(20);
        cbFiltroBusqueda = new JComboBox<>(new String[]{"Nombre", "Correo", "Tipo de Usuario"});
        JButton btnBuscar = new JButton("Buscar");

        // Añadir componentes al panel de búsqueda
        panelBusqueda.add(lblBuscar);
        panelBusqueda.add(txtBuscar);
        panelBusqueda.add(cbFiltroBusqueda);
        panelBusqueda.add(btnBuscar);

        // Tabla para mostrar los usuarios
        tableModel = new DefaultTableModel(new Object[]{
            "ID", "Nombres", "Apellidos", "Tipo de Usuario", "Correo Electrónico", "Clave", "Teléfono", "Fecha de Registro", "Límite de Préstamos"}, 0);
        tableUsuarios = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tableUsuarios);

        // Panel de botones de acciones
        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new GridLayout(1, 6, 10, 10));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JButton btnNuevo = new JButton("Nuevo");
        JButton btnAgregar = new JButton("Agregar");
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnEditar = new JButton("Editar");
        JButton btnCancelar = new JButton("Cancelar");

        panelBotones.add(btnNuevo);
        panelBotones.add(btnAgregar);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnCancelar);

        // Acciones de los botones
        btnNuevo.addActionListener(e -> prepararNuevoRegistro());
        btnAgregar.addActionListener(e -> agregarUsuario());
        btnActualizar.addActionListener(e -> actualizarUsuario());
        btnEliminar.addActionListener(e -> eliminarUsuario());
        btnEditar.addActionListener(e -> editarUsuario());
        btnCancelar.addActionListener(e -> limpiarCampos());
        btnBuscar.addActionListener(e -> buscarUsuarios());

        cbTipoUsuario.addActionListener(e -> actualizarIdDinamicamente());

        // Añadir componentes al frame principal
        add(panelCampos, BorderLayout.NORTH);
        add(panelBusqueda, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.CENTER); // Usa BorderLayout.CENTER para mostrar correctamente la tabla
        add(panelBotones, BorderLayout.SOUTH);

        // Cargar los usuarios en la tabla al iniciar
        cargarUsuariosEnTabla();

        setVisible(true);
    }

    // Método para cargar usuarios en la tabla
    private void cargarUsuariosEnTabla() {
        Tabladeusuarios.cargarUsuariosEnTabla(tableModel, this);
    }

    private void actualizarIdDinamicamente() {
        String nuevoId = Validaciones.generarIdUsuario((String) cbTipoUsuario.getSelectedItem());
        txtId.setText(nuevoId);
    }

    private void prepararNuevoRegistro() {
        limpiarCampos();
        actualizarIdDinamicamente();
    }

    private void limpiarCampos() {
        txtNombres.setText("");
        txtApellidos.setText("");
        txtClave.setText("");
        txtEmail.setText("");
        txtTelefono.setText("");
        cbTipoUsuario.setSelectedIndex(0);
        datePicker.getModel().setValue(null);
    }

    private void agregarUsuario() {
        try {
            String id = txtId.getText();
            String nombre = txtNombres.getText();
            String apellidos = txtApellidos.getText();
            String clave = new String(txtClave.getPassword());
            String email = txtEmail.getText();
            String telefono = txtTelefono.getText();
            String tipoUsuario = (String) cbTipoUsuario.getSelectedItem();
            Date selectedDate = (Date) datePicker.getModel().getValue();

            if (selectedDate == null) {
                JOptionPane.showMessageDialog(this, "Por favor, seleccione una fecha válida.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String fechaRegistro = new java.text.SimpleDateFormat("yyyy-MM-dd").format(selectedDate);

            if (nombre.isEmpty() || apellidos.isEmpty() || clave.isEmpty() || email.isEmpty() || telefono.isEmpty() || fechaRegistro.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!email.matches("^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$")) {
                JOptionPane.showMessageDialog(this, "El correo electrónico no es válido.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean agregado = Validaciones.agregarUsuario(nombre, apellidos, email, tipoUsuario, telefono, clave, fechaRegistro);
            if (agregado) {
                JOptionPane.showMessageDialog(this, "Usuario agregado correctamente.");
                prepararNuevoRegistro();
                cargarUsuariosEnTabla();
            } else {
                JOptionPane.showMessageDialog(this, "Error al agregar el usuario. Verifique los datos e inténtelo nuevamente.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ocurrió un error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscarUsuarios() {
        String filtro = (String) cbFiltroBusqueda.getSelectedItem();
        String termino = txtBuscar.getText().toLowerCase();

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            boolean visible = false;
            if (filtro.equals("Nombre")) {
                visible = tableModel.getValueAt(i, 1).toString().toLowerCase().contains(termino);
            } else if (filtro.equals("Correo")) {
                visible = tableModel.getValueAt(i, 4).toString().toLowerCase().contains(termino);
            } else if (filtro.equals("Tipo de Usuario")) {
                visible = tableModel.getValueAt(i, 3).toString().toLowerCase().contains(termino);
            }
            tableUsuarios.setRowSelectionInterval(i, i);
        }
    }

    private void actualizarUsuario() {
        // Implementar la lógica para actualizar un usuario existente
    }

    private void eliminarUsuario() {
        // Implementar la lógica para eliminar un usuario
    }

    private void editarUsuario() {
        // Implementar la lógica para editar un usuario
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new administracionUsuarios());
    }
}
