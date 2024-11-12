package com.biblioteca.acciones;

import com.biblioteca.controladores.AdministracionUsuariosController;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Calendar;
import java.util.Properties;

public class AdministracionUsuarios extends JPanel {

    private JTextField txtId, txtNombres, txtApellidos, txtEmail, txtTelefono, txtBuscar;
    private JPasswordField txtClave;
    private JComboBox<String> cbTipoUsuario, cbFiltroBusqueda;
    private JDatePickerImpl datePicker;
    private JTable tableUsuarios;
    private DefaultTableModel tableModel;
    private JButton btnAgregar, btnActualizar, btnEliminar, btnVolver, btnCancelar, btnCancelarActualizacion;
    private JSpinner spinnerLimitePrestamos;
    private JPanel panelBotones;

    private boolean editMode = false;
    private boolean nuevoModo = false;

    private GridBagConstraints gbcNuevo = new GridBagConstraints();
    private GridBagConstraints gbcActualizar = new GridBagConstraints();

    private AdministracionUsuariosController controlador;

    public AdministracionUsuarios() {
        controlador = new AdministracionUsuariosController(this);

        setLayout(new BorderLayout());

        // Panel de Campos
        JPanel panelCampos = new JPanel(new GridBagLayout());
        panelCampos.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Crear los labels y campos de texto
        JLabel lblId = new JLabel("ID:");
        txtId = new JTextField(10);
        txtId.setEditable(false);
        JLabel lblNombres = new JLabel("Nombres:");
        txtNombres = new JTextField(15);
        JLabel lblApellidos = new JLabel("Apellidos:");
        txtApellidos = new JTextField(15);
        JLabel lblClave = new JLabel("Contraseña:");
        txtClave = new JPasswordField(15);
        JLabel lblEmail = new JLabel("Correo Electrónico:");
        txtEmail = new JTextField(15);
        JLabel lblTelefono = new JLabel("Teléfono:");
        txtTelefono = new JTextField(15);
        JLabel lblTipoUsuario = new JLabel("Tipo de Usuario:");
        cbTipoUsuario = new JComboBox<>(new String[]{"Administrador", "Profesor", "Alumno"});
        JLabel lblLimitePrestamos = new JLabel("Límite de Préstamos:");
        spinnerLimitePrestamos = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        JLabel lblFechaRegistro = new JLabel("Fecha de Registro:");
        
        // Date Picker
        UtilDateModel model = new UtilDateModel();
        Properties properties = new Properties();
        properties.put("text.today", "Hoy");
        properties.put("text.month", "Mes");
        properties.put("text.year", "Año");
        JDatePanelImpl datePanel = new JDatePanelImpl(model, properties);
        datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());

        // Configurar GridBagConstraints
        GridBagConstraints gbcCampos = new GridBagConstraints();
        gbcCampos.insets = new Insets(10, 10, 10, 10);
        gbcCampos.anchor = GridBagConstraints.WEST;

        // Añadir componentes al panel de campos
        gbcCampos.gridx = 0; gbcCampos.gridy = 0;
        panelCampos.add(lblId, gbcCampos);
        gbcCampos.gridx = 1;
        panelCampos.add(txtId, gbcCampos);
        gbcCampos.gridx = 0; gbcCampos.gridy = 1;
        panelCampos.add(lblNombres, gbcCampos);
        gbcCampos.gridx = 1;
        panelCampos.add(txtNombres, gbcCampos);
        gbcCampos.gridx = 2;
        panelCampos.add(lblApellidos, gbcCampos);
        gbcCampos.gridx = 3;
        panelCampos.add(txtApellidos, gbcCampos);
        gbcCampos.gridx = 0; gbcCampos.gridy = 2;
        panelCampos.add(lblClave, gbcCampos);
        gbcCampos.gridx = 1;
        panelCampos.add(txtClave, gbcCampos);
        gbcCampos.gridx = 2;
        panelCampos.add(lblEmail, gbcCampos);
        gbcCampos.gridx = 3;
        panelCampos.add(txtEmail, gbcCampos);
        gbcCampos.gridx = 0; gbcCampos.gridy = 3;
        panelCampos.add(lblTelefono, gbcCampos);
        gbcCampos.gridx = 1;
        panelCampos.add(txtTelefono, gbcCampos);
        gbcCampos.gridx = 2;
        panelCampos.add(lblTipoUsuario, gbcCampos);
        gbcCampos.gridx = 3;
        panelCampos.add(cbTipoUsuario, gbcCampos);
        gbcCampos.gridx = 0; gbcCampos.gridy = 4;
        panelCampos.add(lblLimitePrestamos, gbcCampos);
        gbcCampos.gridx = 1;
        panelCampos.add(spinnerLimitePrestamos, gbcCampos);
        gbcCampos.gridx = 2;
        panelCampos.add(lblFechaRegistro, gbcCampos);
        gbcCampos.gridx = 3;
        panelCampos.add(datePicker, gbcCampos);

        // Panel de búsqueda
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblBuscar = new JLabel("Buscar:");
        txtBuscar = new JTextField(20);
        cbFiltroBusqueda = new JComboBox<>(new String[]{"Nombre", "Correo", "Tipo de Usuario"});
        JButton btnBuscar = new JButton("Buscar");
        JButton btnLimpiarBusqueda = new JButton("Limpiar");

        panelBusqueda.add(lblBuscar);
        panelBusqueda.add(txtBuscar);
        panelBusqueda.add(cbFiltroBusqueda);
        panelBusqueda.add(btnBuscar);
        panelBusqueda.add(btnLimpiarBusqueda);

        // Tabla para mostrar los usuarios
        tableModel = new DefaultTableModel(new Object[]{"ID", "Nombres", "Apellidos", "Tipo de Usuario", "Correo Electrónico", "Clave", "Teléfono", "Fecha de Registro", "Límite de Préstamos"}, 0);
        tableUsuarios = new JTable(tableModel);
        tableUsuarios.getColumn("Clave").setCellRenderer(new PasswordRenderer());
        JScrollPane scrollPane = new JScrollPane(tableUsuarios);

        // Panel de botones
        panelBotones = new JPanel(new GridBagLayout());
        btnAgregar = new JButton("Nuevo");
        btnActualizar = new JButton("Actualizar");
        btnEliminar = new JButton("Eliminar");
        btnCancelar = new JButton("Cancelar");
        btnCancelarActualizacion = new JButton("Cancelar Actualización");

        Font buttonFont = new Font("Arial", Font.BOLD, 18);
        Dimension buttonSize = new Dimension(180, 60);
        aplicarEstiloBoton(btnAgregar, buttonFont, buttonSize);
        aplicarEstiloBoton(btnActualizar, buttonFont, buttonSize);
        aplicarEstiloBoton(btnEliminar, buttonFont, buttonSize);
        aplicarEstiloBoton(btnCancelar, buttonFont, buttonSize);
        aplicarEstiloBoton(btnCancelarActualizacion, buttonFont, buttonSize);

        // Añadir botones
        gbcNuevo.insets = new Insets(0, 20, 0, 20);
        gbcNuevo.gridx = 0;
        panelBotones.add(btnAgregar, gbcNuevo);
        gbcActualizar.insets = new Insets(0, 20, 0, 20);
        gbcActualizar.gridx = 1;
        panelBotones.add(btnActualizar, gbcActualizar);

        // Listeners
        btnAgregar.addActionListener(e -> controlador.manejarBotonAgregar());
        btnActualizar.addActionListener(e -> controlador.manejarBotonActualizar());
        btnEliminar.addActionListener(e -> controlador.eliminarUsuario());
        btnCancelar.addActionListener(e -> controlador.cancelarOperacion());
        btnCancelarActualizacion.addActionListener(e -> controlador.cancelarActualizacion());
        btnBuscar.addActionListener(e -> controlador.buscarUsuarios());
        btnLimpiarBusqueda.addActionListener(e -> controlador.limpiarBusqueda());
        cbTipoUsuario.addActionListener(e -> controlador.actualizarIdDinamicamente());

        // Panel superior que contiene panelCampos y panelBusqueda
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.add(panelCampos, BorderLayout.NORTH);
        panelSuperior.add(panelBusqueda, BorderLayout.SOUTH);

        // Agregar paneles al JPanel principal
        add(panelSuperior, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);

        habilitarCampos(false);
        controlador.cargarUsuariosEnTabla();
        controlador.actualizarIdDinamicamente();
    }

    public boolean isNuevoModo() { return nuevoModo; }
    public boolean isEditMode() { return editMode; }
    public void setNuevoModo(boolean nuevoModo) { this.nuevoModo = nuevoModo; }
    public void setEditMode(boolean editMode) { this.editMode = editMode; }

    // Métodos para acceder a los componentes desde el controlador
    public JButton getBtnAgregar() { return btnAgregar; }
    public JButton getBtnActualizar() { return btnActualizar; }
    public JButton getBtnEliminar() { return btnEliminar; }
    public JButton getBtnVolver() { return btnVolver; }
    public JButton getBtnCancelar() { return btnCancelar; }
    public JButton getBtnCancelarActualizacion() { return btnCancelarActualizacion; }
    public JPanel getPanelBotones() { return panelBotones; }
    public JTextField getTxtId() { return txtId; }
    public JTextField getTxtNombres() { return txtNombres; }
    public JTextField getTxtApellidos() { return txtApellidos; }
    public JPasswordField getTxtClave() { return txtClave; }
    public JTextField getTxtEmail() { return txtEmail; }
    public JTextField getTxtTelefono() { return txtTelefono; }
    public JComboBox<String> getCbTipoUsuario() { return cbTipoUsuario; }
    public JSpinner getSpinnerLimitePrestamos() { return spinnerLimitePrestamos; }
    public JDatePickerImpl getDatePicker() { return datePicker; }
    public JTable getTableUsuarios() { return tableUsuarios; }
    public DefaultTableModel getTableModel() { return tableModel; }
    public JTextField getTxtBuscar() { return txtBuscar; }
    public JComboBox<String> getCbFiltroBusqueda() { return cbFiltroBusqueda; }

    // Clase interna para renderizar la columna de Contraseña
    private class PasswordRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (c instanceof JLabel && value instanceof String) {
                ((JLabel) c).setText(((String) value).replaceAll(".", "*"));
            }
            return c;
        }
    }

    // Método para aplicar estilo a los botones
    private void aplicarEstiloBoton(JButton boton, Font fuente, Dimension tamaño) {
        boton.setFont(fuente);
        boton.setPreferredSize(tamaño);
    }

    public void habilitarCampos(boolean habilitar) {
        txtNombres.setEnabled(habilitar);
        txtApellidos.setEnabled(habilitar);
        txtClave.setEnabled(habilitar);
        txtEmail.setEnabled(habilitar);
        txtTelefono.setEnabled(habilitar);
        cbTipoUsuario.setEnabled(habilitar && !editMode);
        spinnerLimitePrestamos.setEnabled(habilitar);
        datePicker.setEnabled(habilitar);
    }

    public void limpiarCampos() {
        txtNombres.setText("");
        txtApellidos.setText("");
        txtClave.setText("");
        txtEmail.setText("");
        txtTelefono.setText("");
        cbTipoUsuario.setSelectedIndex(0);
        cbTipoUsuario.setEnabled(!editMode);
        spinnerLimitePrestamos.setValue(1);
        tableUsuarios.clearSelection();
        cargarFechaActual();
        if (!editMode) {
            controlador.actualizarIdDinamicamente();
        }
    }

    private void cargarFechaActual() {
        Calendar calendar = Calendar.getInstance();
        datePicker.getModel().setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePicker.getModel().setSelected(true);
    }
    
    public GridBagConstraints getGbcNuevo() {
    GridBagConstraints gbcNuevo = new GridBagConstraints();
    gbcNuevo.insets = new Insets(10, 10, 10, 10); // Espaciado entre componentes
    gbcNuevo.gridx = 0;
    gbcNuevo.gridy = 0;
    gbcNuevo.anchor = GridBagConstraints.CENTER;
    return gbcNuevo;
}

public GridBagConstraints getGbcActualizar() {
    GridBagConstraints gbcActualizar = new GridBagConstraints();
    gbcActualizar.insets = new Insets(10, 10, 10, 10); // Espaciado entre componentes
    gbcActualizar.gridx = 1;
    gbcActualizar.gridy = 0;
    gbcActualizar.anchor = GridBagConstraints.CENTER;
    return gbcActualizar;
}

}
