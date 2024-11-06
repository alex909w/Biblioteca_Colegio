package acciones;

import validaciones.GestionUsuarios;
import utilidades.DateLabelFormatter;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;
import validaciones.Validaciones;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import paneles.MenuAdministrador;

public class administracionUsuarios extends JFrame {

    private JTextField txtId, txtNombres, txtApellidos, txtEmail, txtTelefono, txtBuscar;
    private JPasswordField txtClave;
    private JComboBox<String> cbTipoUsuario, cbFiltroBusqueda;
    private JDatePickerImpl datePicker;
    private JTable tableUsuarios;
    private DefaultTableModel tableModel;
    private JButton btnAgregar, btnActualizar, btnEliminar, btnVolver, btnCancelar, btnCancelarActualizacion;

    private JSpinner spinnerLimitePrestamos; // Nuevo componente para Límite de Préstamos
    private JLabel lblLimitePrestamos;

    private boolean editMode = false;
    private boolean nuevoModo = false;

    // GridBagConstraints para las posiciones de los botones
    private GridBagConstraints gbcNuevo = new GridBagConstraints();
    private GridBagConstraints gbcActualizar = new GridBagConstraints();

    // Declaración de panelBotones como variable de instancia
    private JPanel panelBotones;

    public administracionUsuarios() {
        setTitle("Gestión de Usuarios - Biblioteca Colegio Amigos De Don Bosco");
        setSize(1200, 900);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel de Campos con GridBagLayout
        JPanel panelCampos = new JPanel(new GridBagLayout());
        panelCampos.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Crear los labels y campos de texto
        JLabel lblId = new JLabel("ID:");
        txtId = new JTextField(10);
        txtId.setEditable(false);
        txtId.setForeground(Color.BLACK);

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

        lblLimitePrestamos = new JLabel("Límite de Préstamos:");
        spinnerLimitePrestamos = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1)); // Configurado para números enteros positivos

        JLabel lblFechaRegistro = new JLabel("Fecha de Registro:");
        UtilDateModel model = new UtilDateModel();
        Calendar calendar = Calendar.getInstance();
        model.setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        model.setSelected(true);

        Properties properties = new Properties();
        properties.put("text.today", "Hoy");
        properties.put("text.month", "Mes");
        properties.put("text.year", "Año");
        JDatePanelImpl datePanel = new JDatePanelImpl(model, properties);
        datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());

        // Configurar GridBagConstraints
        GridBagConstraints gbcCampos = new GridBagConstraints();
        gbcCampos.insets = new Insets(10, 10, 10, 10); // Espaciado entre componentes
        gbcCampos.fill = GridBagConstraints.NONE; // No expandir componentes
        gbcCampos.anchor = GridBagConstraints.WEST; // Alinear a la izquierda

        // Añadir componentes al panel de campos utilizando GridBagLayout

        // Fila 0: ID
        gbcCampos.gridx = 0;
        gbcCampos.gridy = 0;
        panelCampos.add(lblId, gbcCampos);

        gbcCampos.gridx = 1;
        gbcCampos.gridy = 0;
        gbcCampos.gridwidth = 1;
        gbcCampos.fill = GridBagConstraints.HORIZONTAL; // Expandir solo el campo de texto
        panelCampos.add(txtId, gbcCampos);

        // Restablecer gridwidth y fill para los siguientes componentes
        gbcCampos.gridwidth = 1;
        gbcCampos.fill = GridBagConstraints.NONE;

        // Fila 1: Nombres y Apellidos (lado a lado)
        gbcCampos.gridx = 0;
        gbcCampos.gridy = 1;
        panelCampos.add(lblNombres, gbcCampos);

        gbcCampos.gridx = 1;
        gbcCampos.gridy = 1;
        gbcCampos.fill = GridBagConstraints.HORIZONTAL; // Expandir el campo de texto
        gbcCampos.weightx = 0.5; // Asignar peso para la expansión proporcional
        panelCampos.add(txtNombres, gbcCampos);

        gbcCampos.gridx = 2;
        gbcCampos.gridy = 1;
        gbcCampos.fill = GridBagConstraints.NONE; // No expandir el label
        gbcCampos.weightx = 0; // Sin peso
        panelCampos.add(lblApellidos, gbcCampos);

        gbcCampos.gridx = 3;
        gbcCampos.gridy = 1;
        gbcCampos.fill = GridBagConstraints.HORIZONTAL; // Expandir el campo de texto
        gbcCampos.weightx = 0.5; // Asignar peso para la expansión proporcional
        panelCampos.add(txtApellidos, gbcCampos);

        // Restablecer fill y weightx
        gbcCampos.fill = GridBagConstraints.NONE;
        gbcCampos.weightx = 0;

        // Fila 2: Clave y Email
        gbcCampos.gridx = 0;
        gbcCampos.gridy = 2;
        panelCampos.add(lblClave, gbcCampos);

        gbcCampos.gridx = 1;
        gbcCampos.gridy = 2;
        gbcCampos.fill = GridBagConstraints.HORIZONTAL;
        gbcCampos.weightx = 0.5;
        panelCampos.add(txtClave, gbcCampos);

        gbcCampos.gridx = 2;
        gbcCampos.gridy = 2;
        gbcCampos.fill = GridBagConstraints.NONE;
        gbcCampos.weightx = 0;
        panelCampos.add(lblEmail, gbcCampos);

        gbcCampos.gridx = 3;
        gbcCampos.gridy = 2;
        gbcCampos.fill = GridBagConstraints.HORIZONTAL;
        gbcCampos.weightx = 0.5;
        panelCampos.add(txtEmail, gbcCampos);

        // Restablecer fill y weightx
        gbcCampos.fill = GridBagConstraints.NONE;
        gbcCampos.weightx = 0;

        // Fila 3: Teléfono y Tipo de Usuario
        gbcCampos.gridx = 0;
        gbcCampos.gridy = 3;
        panelCampos.add(lblTelefono, gbcCampos);

        gbcCampos.gridx = 1;
        gbcCampos.gridy = 3;
        gbcCampos.fill = GridBagConstraints.HORIZONTAL;
        gbcCampos.weightx = 0.5;
        panelCampos.add(txtTelefono, gbcCampos);

        gbcCampos.gridx = 2;
        gbcCampos.gridy = 3;
        gbcCampos.fill = GridBagConstraints.NONE;
        gbcCampos.weightx = 0;
        panelCampos.add(lblTipoUsuario, gbcCampos);

        gbcCampos.gridx = 3;
        gbcCampos.gridy = 3;
        gbcCampos.fill = GridBagConstraints.HORIZONTAL;
        gbcCampos.weightx = 0.5;
        panelCampos.add(cbTipoUsuario, gbcCampos);

        // Restablecer fill y weightx
        gbcCampos.fill = GridBagConstraints.NONE;
        gbcCampos.weightx = 0;

        // Fila 4: Límite de Préstamos y Fecha de Registro
        gbcCampos.gridx = 0;
        gbcCampos.gridy = 4;
        panelCampos.add(lblLimitePrestamos, gbcCampos);

        gbcCampos.gridx = 1;
        gbcCampos.gridy = 4;
        gbcCampos.fill = GridBagConstraints.HORIZONTAL;
        gbcCampos.weightx = 0.5;
        panelCampos.add(spinnerLimitePrestamos, gbcCampos);

        gbcCampos.gridx = 2;
        gbcCampos.gridy = 4;
        gbcCampos.fill = GridBagConstraints.NONE;
        gbcCampos.weightx = 0;
        panelCampos.add(lblFechaRegistro, gbcCampos);

        gbcCampos.gridx = 3;
        gbcCampos.gridy = 4;
        gbcCampos.fill = GridBagConstraints.HORIZONTAL;
        gbcCampos.weightx = 0.5;
        panelCampos.add(datePicker, gbcCampos);

        // Restablecer fill y weightx
        gbcCampos.fill = GridBagConstraints.NONE;
        gbcCampos.weightx = 0;

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
        tableModel = new DefaultTableModel(new Object[]{
            "ID", "Nombres", "Apellidos", "Tipo de Usuario", "Correo Electrónico", "Clave", "Teléfono", "Fecha de Registro", "Límite de Préstamos"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tableUsuarios = new JTable(tableModel);

        // Asignar el renderizador personalizado a la columna "Clave"
        tableUsuarios.getColumn("Clave").setCellRenderer(new PasswordRenderer());

        // Opcional: Ajustar el ancho de la columna "Clave"
        tableUsuarios.getColumn("Clave").setPreferredWidth(150);

        JScrollPane scrollPane = new JScrollPane(tableUsuarios);

        // Inicializar el panel de botones
        panelBotones = new JPanel(new GridBagLayout());
        panelBotones.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Configuración de GridBagConstraints para "Nuevo"
        gbcNuevo.insets = new Insets(0, 20, 0, 20); // Espaciado entre botones
        gbcNuevo.gridx = 0;
        gbcNuevo.gridy = 0;
        gbcNuevo.anchor = GridBagConstraints.CENTER;

        // Configuración de GridBagConstraints para "Actualizar"
        gbcActualizar.insets = new Insets(0, 20, 0, 20);
        gbcActualizar.gridx = 1;
        gbcActualizar.gridy = 0;
        gbcActualizar.anchor = GridBagConstraints.CENTER;

        // Crear los botones
        btnAgregar = new JButton("Nuevo"); // Inicialmente en estado "Nuevo"
        btnActualizar = new JButton("Actualizar");
        btnEliminar = new JButton("Eliminar");
        btnVolver = new JButton("Volver");
        btnCancelar = new JButton("Cancelar"); // Botón "Cancelar" para "Nuevo"
        btnCancelarActualizacion = new JButton("Cancelar"); // Botón "Cancelar" para Actualización

        // **Aumento del Tamaño de los Botones**
        Font buttonFont = new Font("Arial", Font.BOLD, 18); // Fuente más grande
        Dimension buttonSize = new Dimension(180, 60); // Tamaño preferido: Ancho x Alto

        // Aplicar fuente y tamaño a los botones
        aplicarEstiloBoton(btnAgregar, buttonFont, buttonSize);
        aplicarEstiloBoton(btnActualizar, buttonFont, buttonSize);
        aplicarEstiloBoton(btnEliminar, buttonFont, buttonSize);
        aplicarEstiloBoton(btnVolver, buttonFont, buttonSize);
        aplicarEstiloBoton(btnCancelar, buttonFont, buttonSize);
        aplicarEstiloBoton(btnCancelarActualizacion, buttonFont, buttonSize);

        // **Agregar botones al panelBotones**
        panelBotones.add(btnAgregar, gbcNuevo);
        panelBotones.add(btnActualizar, gbcActualizar);

        // Agregar btnEliminar y btnVolver con sus propias restricciones
        GridBagConstraints gbcEliminar = new GridBagConstraints();
        gbcEliminar.insets = new Insets(0, 20, 0, 20);
        gbcEliminar.gridx = 2;
        gbcEliminar.gridy = 0;
        gbcEliminar.anchor = GridBagConstraints.CENTER;
        panelBotones.add(btnEliminar, gbcEliminar);

        GridBagConstraints gbcVolver = new GridBagConstraints();
        gbcVolver.insets = new Insets(0, 20, 0, 20);
        gbcVolver.gridx = 3;
        gbcVolver.gridy = 0;
        gbcVolver.anchor = GridBagConstraints.CENTER;
        panelBotones.add(btnVolver, gbcVolver);

        // Agregar btnCancelarActualizacion pero mantenerlo oculto inicialmente
        btnCancelarActualizacion.setVisible(false);
        // Lo agregaremos dinámicamente cuando sea necesario

        // Asignar ActionListeners a cada botón
        btnAgregar.addActionListener(e -> manejarBotonAgregar());
        btnActualizar.addActionListener(e -> manejarBotonActualizar());
        btnCancelar.addActionListener(e -> cancelarOperacion());
        btnCancelarActualizacion.addActionListener(e -> cancelarActualizacion());
        btnEliminar.addActionListener(e -> eliminarUsuario());
        btnVolver.addActionListener(e -> volverAlMenuAnterior());
        btnBuscar.addActionListener(e -> buscarUsuarios());
        btnLimpiarBusqueda.addActionListener(e -> limpiarBusqueda());

        cbTipoUsuario.addActionListener(e -> actualizarIdDinamicamente());

        // Crear un panel superior que contenga panelCampos y panelBusqueda
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.add(panelCampos, BorderLayout.NORTH);
        panelSuperior.add(panelBusqueda, BorderLayout.SOUTH);

        // Agregar los paneles al JFrame
        add(panelSuperior, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);

        // Bloquear todos los campos por defecto
        habilitarCampos(false);

        cargarUsuariosEnTabla();
        actualizarIdDinamicamente();

        setVisible(true);
    }

    // Clase interna para renderizar la columna de Contraseña
    private class PasswordRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {

            // Obtener el componente por defecto
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (c instanceof JLabel && value instanceof String) {
                String actualPassword = (String) value;
                // Enmascarar la contraseña con asteriscos
                String maskedPassword = actualPassword.replaceAll(".", "*");
                ((JLabel) c).setText(maskedPassword);
            }
            return c;
        }
    }

    // Método para aplicar estilo a los botones
    private void aplicarEstiloBoton(JButton boton, Font fuente, Dimension tamaño) {
        boton.setFont(fuente);
        boton.setPreferredSize(tamaño);
        boton.setMinimumSize(tamaño);
        boton.setMaximumSize(tamaño);
    }

    // Método para manejar el botón "Agregar/Nuevo/Guardar"
    private void manejarBotonAgregar() {
        if (!nuevoModo && !editMode) {
            // Agregar Confirmación al Presionar "Nuevo"
            int confirmCrear = JOptionPane.showConfirmDialog(this,
                    "¿Quieres crear un nuevo usuario?",
                    "Confirmar Nuevo",
                    JOptionPane.YES_NO_OPTION);
            if (confirmCrear == JOptionPane.NO_OPTION) {
                return; // No hacer nada si el usuario elige "No"
            }

            // Proceder a limpiar campos y preparar el formulario para un nuevo usuario
            limpiarCampos();
            habilitarCampos(true);
            actualizarIdDinamicamente(); // Genera el próximo ID
            btnAgregar.setText("Guardar");
            nuevoModo = true;
            editMode = false;

            // Deshabilitar el botón "Actualizar"
            btnActualizar.setEnabled(false);
            btnActualizar.setVisible(false);

            // Deshabilitar los botones "Eliminar" y "Volver"
            btnEliminar.setEnabled(false);
            btnVolver.setEnabled(false);

            // Remover botones del panel
            panelBotones.remove(btnAgregar);
            panelBotones.remove(btnActualizar);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(0, 20, 0, 20);
            gbc.gridy = 0;

            // Agregar "Guardar" (btnAgregar) en la posición de "Nuevo" (gridx=0)
            gbc.gridx = 0;
            panelBotones.add(btnAgregar, gbc);

            // Mostrar "Cancelar" en la posición de "Actualizar" (gridx=1)
            gbc.gridx = 1;
            panelBotones.add(btnCancelar, gbc);
            btnCancelar.setVisible(true);

            panelBotones.revalidate();
            panelBotones.repaint();
        } else if (nuevoModo) {
            // Modo "Guardar"
            agregarUsuario();
        }
    }

    // Método para manejar el botón "Actualizar"
    private void manejarBotonActualizar() {
        if (!editMode && !nuevoModo) {
            int selectedRow = tableUsuarios.getSelectedRow();
            if (selectedRow >= 0) {
                // Mostrar cuadro de diálogo de confirmación
                int confirmEditar = JOptionPane.showConfirmDialog(this,
                        "¿Quieres editar la información del usuario seleccionado?",
                        "Confirmar Edición",
                        JOptionPane.YES_NO_OPTION);
                if (confirmEditar == JOptionPane.YES_OPTION) {
                    // Cargar datos para edición
                    cargarDatosParaEdicion(selectedRow);

                    // Cambiar el texto del botón "Actualizar" a "Guardar"
                    btnActualizar.setText("Guardar");
                    editMode = true;
                    nuevoModo = false;

                    // Ocultar el botón "Nuevo"
                    btnAgregar.setVisible(false);

                    // Deshabilitar los botones "Eliminar" y "Volver"
                    btnEliminar.setEnabled(false);
                    btnVolver.setEnabled(false);

                    // Remover botones del panel
                    panelBotones.remove(btnActualizar);
                    panelBotones.remove(btnCancelarActualizacion);

                    GridBagConstraints gbc = new GridBagConstraints();
                    gbc.insets = new Insets(0, 20, 0, 20);
                    gbc.gridy = 0;

                    // Mover "Guardar" a la posición de "Nuevo" (gridx=0)
                    gbc.gridx = 0;
                    panelBotones.add(btnActualizar, gbc);

                    // Mostrar "CancelarActualizacion" en la posición original de "Actualizar" (gridx=1)
                    gbc.gridx = 1;
                    panelBotones.add(btnCancelarActualizacion, gbc);
                    btnCancelarActualizacion.setVisible(true);

                    panelBotones.revalidate();
                    panelBotones.repaint();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Por favor, selecciona un usuario para actualizar.", "Información", JOptionPane.INFORMATION_MESSAGE);
            }
        } else if (editMode) {
            // Modo "Guardar"
            guardarActualizacion();
        }
    }

    // Método para agregar un nuevo usuario
    private void agregarUsuario() {
        try {
            String id = txtId.getText();
            String nombre = txtNombres.getText().trim();
            String apellidos = txtApellidos.getText().trim();
            String clave = new String(txtClave.getPassword()).trim();
            String email = txtEmail.getText().trim();
            String telefono = txtTelefono.getText().trim();
            String tipoUsuario = (String) cbTipoUsuario.getSelectedItem();
            int limitePrestamos = (int) spinnerLimitePrestamos.getValue(); // Obtener el valor del spinner
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

            // Validar que el límite de préstamos sea un número positivo
            if (limitePrestamos <= 0) {
                JOptionPane.showMessageDialog(this, "El límite de préstamos debe ser un número positivo.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean agregado = GestionUsuarios.agregarUsuario(id, nombre, apellidos, clave, email, tipoUsuario, telefono, fechaRegistro, limitePrestamos, this);
            if (agregado) {
                limpiarCampos();
                habilitarCampos(false); // Deshabilita los campos después de agregar
                btnAgregar.setText("Nuevo"); // Cambia el texto del botón a "Nuevo" después de agregar
                nuevoModo = false;

                // Restaurar el botón "Actualizar"
                btnActualizar.setEnabled(true);
                btnActualizar.setVisible(true);

                // Habilitar los botones "Eliminar" y "Volver"
                btnEliminar.setEnabled(true);
                btnVolver.setEnabled(true);

                // Remover botones del panel
                panelBotones.remove(btnAgregar);
                panelBotones.remove(btnCancelar);

                // Restaurar botones a sus posiciones originales
                panelBotones.add(btnAgregar, gbcNuevo);
                panelBotones.add(btnActualizar, gbcActualizar);

                // Mostrar el botón "Nuevo" nuevamente
                btnAgregar.setVisible(true);

                panelBotones.revalidate();
                panelBotones.repaint();

                cargarUsuariosEnTabla();
            } else {
                JOptionPane.showMessageDialog(this, "Error al agregar el usuario. Verifique los datos e inténtelo nuevamente.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ocurrió un error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

// Método para guardar la actualización de un usuario existente
private void guardarActualizacion() {
    // Confirmación para guardar cambios
    int confirm = JOptionPane.showConfirmDialog(this,
            "¿Desea guardar los cambios realizados al usuario?",
            "Confirmar Guardar",
            JOptionPane.YES_NO_OPTION);
    if (confirm == JOptionPane.YES_OPTION) {
        String id = txtId.getText();
        String nombre = txtNombres.getText().trim();
        String apellidos = txtApellidos.getText().trim();
        String clave = new String(txtClave.getPassword()).trim();
        String email = txtEmail.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String tipoUsuario = (String) cbTipoUsuario.getSelectedItem();
        int limitePrestamos = (int) spinnerLimitePrestamos.getValue(); // Obtener el valor del spinner

        // Validaciones
        if (nombre.isEmpty() || apellidos.isEmpty() || clave.isEmpty() || email.isEmpty() || telefono.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!email.matches("^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$")) {
            JOptionPane.showMessageDialog(this, "El correo electrónico no es válido.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validar que el límite de préstamos sea un número positivo
        if (limitePrestamos <= 0) {
            JOptionPane.showMessageDialog(this, "El límite de préstamos debe ser un número positivo.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Llama a la función para actualizar los datos en la base de datos
        boolean actualizado = GestionUsuarios.actualizarUsuario(id, nombre, apellidos, clave, email, tipoUsuario, telefono, limitePrestamos);
        if (actualizado) {
            JOptionPane.showMessageDialog(this, "Usuario actualizado correctamente.");
            cargarUsuariosEnTabla();
            limpiarCampos();
            habilitarCampos(false); // Deshabilita los campos después de actualizar
            btnActualizar.setText("Actualizar");
            editMode = false;
            nuevoModo = false;

            // Mostrar el botón "Nuevo" nuevamente
            btnAgregar.setVisible(true);

            // Habilitar el botón "Actualizar" y mostrarlo
            btnActualizar.setEnabled(true);
            btnActualizar.setVisible(true);

            // Habilitar los botones "Eliminar" y "Volver"
            btnEliminar.setEnabled(true);
            btnVolver.setEnabled(true);

            // Remover botones del panel
            panelBotones.remove(btnActualizar);
            panelBotones.remove(btnCancelarActualizacion);

            // Restaurar botones a sus posiciones originales
            panelBotones.add(btnAgregar, gbcNuevo);
            panelBotones.add(btnActualizar, gbcActualizar);

            // Ocultar "CancelarActualizacion"
            btnCancelarActualizacion.setVisible(false);

            panelBotones.revalidate();
            panelBotones.repaint();
        } else {
            JOptionPane.showMessageDialog(this, "Error al actualizar el usuario. Verifique los datos ingresados e intente nuevamente.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}



    // Método para cancelar la operación actual (Cancelación de Nuevo Usuario)
    private void cancelarOperacion() {
        // Confirmar la cancelación
        int confirmCancelar = JOptionPane.showConfirmDialog(this,
                "¿Estás seguro de que deseas cancelar la operación?",
                "Confirmar Cancelación",
                JOptionPane.YES_NO_OPTION);
        if (confirmCancelar == JOptionPane.YES_OPTION) {
            limpiarCampos();
            habilitarCampos(false); // Deshabilita los campos
            btnAgregar.setText("Nuevo"); // Restablece el botón "Nuevo"
            nuevoModo = false;
            editMode = false;

            // Habilitar el botón "Actualizar" y mostrarlo
            btnActualizar.setEnabled(true);
            btnActualizar.setVisible(true);

            // Habilitar los botones "Eliminar" y "Volver"
            btnEliminar.setEnabled(true);
            btnVolver.setEnabled(true);

            // Remover botones del panel
            panelBotones.remove(btnAgregar);
            panelBotones.remove(btnCancelar);

            // Restaurar botones a sus posiciones originales
            panelBotones.add(btnAgregar, gbcNuevo);
            panelBotones.add(btnActualizar, gbcActualizar);

            // Mostrar el botón "Nuevo" nuevamente
            btnAgregar.setVisible(true);

            panelBotones.revalidate();
            panelBotones.repaint();
        }
    }

    // Método para cancelar la actualización de un usuario existente
    private void cancelarActualizacion() {
        // Confirmar la cancelación
        int confirmCancelar = JOptionPane.showConfirmDialog(this,
                "¿Estás seguro de que deseas cancelar la actualización?",
                "Confirmar Cancelación",
                JOptionPane.YES_NO_OPTION);
        if (confirmCancelar == JOptionPane.YES_OPTION) {
            limpiarCampos();
            habilitarCampos(false); // Deshabilita los campos
            btnActualizar.setText("Actualizar"); // Restablece el botón "Actualizar"
            editMode = false;
            nuevoModo = false;

            // Mostrar el botón "Nuevo" nuevamente
            btnAgregar.setVisible(true);

            // Habilitar el botón "Actualizar" y mostrarlo
            btnActualizar.setEnabled(true);
            btnActualizar.setVisible(true);

            // Habilitar los botones "Eliminar" y "Volver"
            btnEliminar.setEnabled(true);
            btnVolver.setEnabled(true);

            // Remover botones del panel
            panelBotones.remove(btnActualizar);
            panelBotones.remove(btnCancelarActualizacion);

            // Restaurar botones a sus posiciones originales
            panelBotones.add(btnAgregar, gbcNuevo);
            panelBotones.add(btnActualizar, gbcActualizar);

            // Ocultar "CancelarActualizacion"
            btnCancelarActualizacion.setVisible(false);

            panelBotones.revalidate();
            panelBotones.repaint();
        }
    }

    // Método para cargar usuarios en la tabla
    private void cargarUsuariosEnTabla() {
         GestionUsuarios.cargarUsuariosEnTabla(tableModel, this);
    }

    // Método para generar el próximo ID de usuario
    private void actualizarIdDinamicamente() {
        if (!editMode) {
            String nuevoId = Validaciones.generarIdUsuario((String) cbTipoUsuario.getSelectedItem());
            txtId.setText(nuevoId);
        }
    }

    // Método para cargar datos en el formulario para edición
    private void cargarDatosParaEdicion(int rowIndex) {
        editMode = true; // Activamos el modo de edición
        nuevoModo = false;

        String id = tableModel.getValueAt(rowIndex, 0).toString();
        txtId.setText(id); // Muestra el ID actual
        txtId.setEditable(false); // El ID no se puede editar en modo de edición

        txtNombres.setText(tableModel.getValueAt(rowIndex, 1).toString());
        txtApellidos.setText(tableModel.getValueAt(rowIndex, 2).toString());
        cbTipoUsuario.setSelectedItem(tableModel.getValueAt(rowIndex, 3).toString());
        cbTipoUsuario.setEnabled(false); // Desactiva la edición del campo de tipo de usuario
        txtEmail.setText(tableModel.getValueAt(rowIndex, 4).toString());
        txtClave.setText(tableModel.getValueAt(rowIndex, 5).toString());
        txtTelefono.setText(tableModel.getValueAt(rowIndex, 6).toString());

        try {
            String fechaStr = tableModel.getValueAt(rowIndex, 7).toString();
            Date date = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(fechaStr);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            datePicker.getModel().setDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
            datePicker.getModel().setSelected(true);
        } catch (Exception e) {
            datePicker.getModel().setValue(null);
        }

        // Cargar el límite de préstamos
        String limiteStr = tableModel.getValueAt(rowIndex, 8).toString();
        try {
            int limite = Integer.parseInt(limiteStr);
            spinnerLimitePrestamos.setValue(limite);
        } catch (NumberFormatException e) {
            spinnerLimitePrestamos.setValue(1); // Valor por defecto si hay error
        }

        habilitarCampos(true);
    }

    // Método para limpiar los campos del formulario
    private void limpiarCampos() {
        txtNombres.setText("");
        txtApellidos.setText("");
        txtClave.setText("");
        txtEmail.setText("");
        txtTelefono.setText("");
        cbTipoUsuario.setSelectedIndex(0);
        cbTipoUsuario.setEnabled(!editMode); // Habilitar o deshabilitar según el modo
        spinnerLimitePrestamos.setValue(1); // Resetear al valor por defecto
        tableUsuarios.clearSelection();
        cargarFechaActual();

        // Genera un nuevo ID solo si no estamos en modo edición
        if (!editMode) {
            actualizarIdDinamicamente();
        }
    }

    // Método para habilitar o deshabilitar los campos del formulario
    private void habilitarCampos(boolean habilitar) {
        txtNombres.setEnabled(habilitar);
        txtApellidos.setEnabled(habilitar);
        txtClave.setEnabled(habilitar);
        txtEmail.setEnabled(habilitar);
        txtTelefono.setEnabled(habilitar);
        cbTipoUsuario.setEnabled(habilitar && !editMode); // Deshabilitar en modo edición
        spinnerLimitePrestamos.setEnabled(habilitar);
        datePicker.setEnabled(habilitar);
    }

    // Método para eliminar un usuario seleccionado
    private void eliminarUsuario() {
        int selectedRow = tableUsuarios.getSelectedRow();
        if (selectedRow >= 0) {
            String id = tableModel.getValueAt(selectedRow, 0).toString();
            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Desea eliminar el usuario con ID: " + id + "?",
                    "Confirmar Eliminación",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                boolean eliminado = GestionUsuarios.eliminarUsuario(id);
                if (eliminado) {
                    JOptionPane.showMessageDialog(this, "Usuario eliminado correctamente.");
                    cargarUsuariosEnTabla();
                    limpiarCampos();
                    habilitarCampos(false);
                    // Asegura que el botón "Actualizar" esté en su estado normal
                    btnActualizar.setText("Actualizar");
                } else {
                    JOptionPane.showMessageDialog(this, "Error al eliminar el usuario.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona un usuario para eliminar.", "Información", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Método para buscar usuarios según el filtro y término ingresados
    private void buscarUsuarios() {
        String termino = txtBuscar.getText().trim();
        String filtro = (String) cbFiltroBusqueda.getSelectedItem();
        GestionUsuarios.buscarUsuarioEnTabla(tableModel, filtro, termino);
    }

    // Método para limpiar la búsqueda y recargar todos los usuarios
    private void limpiarBusqueda() {
        txtBuscar.setText("");
        cbFiltroBusqueda.setSelectedIndex(0);
        cargarUsuariosEnTabla();
    }

    // Método para volver al menú anterior (cerrar la ventana actual)
  private void volverAlMenuAnterior() {
    dispose(); // Cierra la ventana actual
    new MenuAdministrador().setVisible(true); // Abre el menú de administrador
}


    // Método para cargar la fecha actual automáticamente
    private void cargarFechaActual() {
        Calendar calendar = Calendar.getInstance();
        datePicker.getModel().setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePicker.getModel().setSelected(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new administracionUsuarios());
    }
}
