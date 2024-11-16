package com.biblioteca.acciones.GestionProductos;

import com.biblioteca.dao.GestionFormularioDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.border.Border;

public class AgregarFormulario extends JPanel {
    
    // Inician los estilos  

    // Componentes de la interfaz y la conexión a la base de datos
    private GestionFormularioDAO tipoDocumentoDAO;
    private JTextField txtNombreFormulario;
    private JTextField txtNumeroColumnas;
    private JTable tableColumnas;
    private DefaultTableModel tableModel;
    private static final Color PRIMARY_COLOR = new Color(51, 102, 204);
    private static final Color SECONDARY_COLOR = new Color(240, 240, 240);
    private final Font FUENTE_PRINCIPAL = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Color ACCENT_COLOR = new Color(70, 130, 180);
    private static final Color TEXT_COLOR = new Color(51, 51, 51);
    // Constructor: inicializa la interfaz y establece la conexión
    public AgregarFormulario() {
        configurarPanel();
        inicializarConexion();
        configurarComponentes();
    }

    private void configurarPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(10, 10, 10, 10));
    }

    private void inicializarConexion() {
        try {
            tipoDocumentoDAO = new GestionFormularioDAO();
        } catch (SQLException e) {
            mostrarError("Error al conectar con la base de datos", e);
        }
    }

    private void configurarComponentes() {
        agregarPanelSuperior();
        configurarTabla();
        agregarPanelBotones();
    }

private void agregarPanelSuperior() {
    JPanel panelSuperior = new JPanel(new GridBagLayout());
    panelSuperior.setBackground(Color.WHITE);
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(10, 10, 10, 10);

    // Etiquetas y campos de texto
    JLabel lblNombreFormulario = new JLabel("Nombre del Formulario:");
    lblNombreFormulario.setForeground(TEXT_COLOR);
    gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
    panelSuperior.add(lblNombreFormulario, gbc);

    txtNombreFormulario = new JTextField(20);
    txtNombreFormulario.setPreferredSize(new Dimension(220, 35)); // Aumentar tamaño
    txtNombreFormulario.setBackground(Color.WHITE); // Coincide con el color del fondo
    txtNombreFormulario.setBorder(createFieldBorder()); // Añadir borde estilizado
    gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
    panelSuperior.add(txtNombreFormulario, gbc);

    JLabel lblNumeroColumnas = new JLabel("Número de Campos:");
    lblNumeroColumnas.setForeground(TEXT_COLOR);
    gbc.gridx = 0; gbc.gridy = 1;
    panelSuperior.add(lblNumeroColumnas, gbc);

    txtNumeroColumnas = new JTextField(20);
    txtNumeroColumnas.setPreferredSize(new Dimension(220, 35)); // Aumentar tamaño
    txtNumeroColumnas.setBackground(Color.WHITE); // Coincide con el color del fondo
    txtNumeroColumnas.setBorder(createFieldBorder()); // Añadir borde estilizado
    gbc.gridx = 1;
    panelSuperior.add(txtNumeroColumnas, gbc);

    JButton btnGenerarCampos = crearBoton("Generar Campos", PRIMARY_COLOR, Color.WHITE);
    btnGenerarCampos.addActionListener(e -> generarCampos());
    gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
    panelSuperior.add(btnGenerarCampos, gbc);

    add(panelSuperior, BorderLayout.NORTH);
    }
    
    private Border createFieldBorder() {
    return BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(SECONDARY_COLOR),
        BorderFactory.createEmptyBorder(5, 8, 5, 8)
    );
    }

    private void configurarTabla() {
        String[] columnNames = {"Número", "Nombre del Campo"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 1;
            }
        };

        // Configuramos la tabla con un diseño más grande y colores personalizados
        tableColumnas = new JTable(tableModel);
        tableColumnas.setFont(FUENTE_PRINCIPAL.deriveFont(16f));  // Fuente más grande para la tabla
        tableColumnas.setRowHeight(35);  // Aumenta la altura de las filas
        tableColumnas.setBackground(SECONDARY_COLOR);  // Color de fondo de las celdas
        tableColumnas.setForeground(TEXT_COLOR);  // Color del texto de las celdas

        // Centro de alineación para la primera columna (Número)
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        centerRenderer.setBackground(SECONDARY_COLOR);  // Fondo de las celdas de la primera columna
        centerRenderer.setForeground(TEXT_COLOR);  // Color del texto de la primera columna
        tableColumnas.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tableColumnas.getColumnModel().getColumn(0).setMaxWidth(100);  // Ancho fijo para la columna "Número"

        // Aplicamos borde a las celdas de la tabla
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                cell.setFont(FUENTE_PRINCIPAL.deriveFont(16f));  // Ajuste de fuente
                cell.setBackground(SECONDARY_COLOR);
                cell.setForeground(TEXT_COLOR);
                ((JComponent) cell).setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, ACCENT_COLOR));  // Borde personalizado
                return cell;
            }
        };

        // Aplicamos el renderizador personalizado para la columna "Nombre del Campo"
        tableColumnas.getColumnModel().getColumn(1).setCellRenderer(cellRenderer);

        // Añadimos la tabla a un JScrollPane con borde
        JScrollPane scrollPane = new JScrollPane(tableColumnas);
        scrollPane.setBorder(BorderFactory.createLineBorder(ACCENT_COLOR, 2));  // Borde de 2px alrededor del JScrollPane
        add(scrollPane, BorderLayout.CENTER);
    }

    // Finaliza los estilos  

    private void agregarPanelBotones() {
        JButton btnGuardar = crearBoton("Guardar Formulario", PRIMARY_COLOR, Color.WHITE);
        btnGuardar.addActionListener(e -> guardarFormulario());

        JButton btnCancelar = crearBoton("Cancelar", ACCENT_COLOR, Color.WHITE);
        btnCancelar.addActionListener(e -> limpiarCampos());

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelBotones.setBackground(Color.WHITE);
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);

        add(panelBotones, BorderLayout.SOUTH);
    }

    // Método para generar los campos según el número ingresado por el usuario
    private void generarCampos() {
        tableModel.setRowCount(0);

        String nombreFormulario = txtNombreFormulario.getText().trim();
        String numColumnasStr = txtNumeroColumnas.getText().trim();

        if (nombreFormulario.isEmpty() || numColumnasStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese el nombre y el número de campos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            if (tipoDocumentoDAO.existeTabla(nombreFormulario.toLowerCase())) {
                int response = JOptionPane.showOptionDialog(this,
                        "La tabla ya existe. ¿Qué desea hacer?",
                        "Tabla Existente",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        new String[]{"Usar Existente", "Eliminar y Crear Nueva"},
                        "Usar Existente");

                if (response == JOptionPane.NO_OPTION) {
                    tipoDocumentoDAO.eliminarTabla(nombreFormulario.toLowerCase());
                    JOptionPane.showMessageDialog(this, "Tabla eliminada y lista para crear nueva.", "Información", JOptionPane.INFORMATION_MESSAGE);
                } else if (response == JOptionPane.YES_OPTION) {
                    JOptionPane.showMessageDialog(this, "Redirigiendo a la sección de edición de formularios...", "Información", JOptionPane.INFORMATION_MESSAGE);
                    limpiarCampos();
                    return;
                } else {
                    return;
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al verificar o manejar la tabla existente: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int numColumnas;
        try {
            numColumnas = Integer.parseInt(numColumnasStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Número de campos inválido.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        for (int i = 0; i < numColumnas; i++) {
            tableModel.addRow(new Object[]{"Columna " + (i + 1), ""});
        }
    }

    // Método para guardar el formulario y sus campos en la base de datos
    private void guardarFormulario() {
        String nombreFormulario = txtNombreFormulario.getText().trim().toUpperCase();
        if (nombreFormulario.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese el nombre del formulario.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Por favor, genere los campos primero.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ArrayList<String> nombresColumnas = new ArrayList<>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String nombreColumna = tableModel.getValueAt(i, 1).toString().trim().toUpperCase();
            if (nombreColumna.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, complete todos los nombres de los campos.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            nombresColumnas.add(nombreColumna);
        }

        try {
            // Generar nombre de la tabla y crearla en la base de datos
            String nombreTabla = nombreFormulario.toLowerCase().replace(" ", "_") + "_tabla";
            tipoDocumentoDAO.crearTablaParaDocumento(nombreTabla, nombresColumnas);

            // Generar el ID para el nuevo tipo de documento
            String generatedId = tipoDocumentoDAO.generarNuevoId("tiposdocumentos");

            // Insertar el tipo de documento en `tiposdocumentos` con el nombre de la tabla
            tipoDocumentoDAO.insertarTipoDocumento(generatedId, nombreFormulario, nombreTabla);

            JOptionPane.showMessageDialog(this, "Formulario guardado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiarCampos();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al guardar el formulario: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarCampos() {
        txtNombreFormulario.setText("");
        txtNumeroColumnas.setText("");
        tableModel.setRowCount(0);
    }

    private void mostrarError(String mensaje, Exception e) {
        JOptionPane.showMessageDialog(this, mensaje + ": " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    private JButton crearBoton(String texto, Color backgroundColor, Color colorTexto) {
        JButton boton = new JButton(texto);
        boton.setBackground(backgroundColor);
        boton.setForeground(colorTexto);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                boton.setBackground(backgroundColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                boton.setBackground(backgroundColor);
            }
        });
        return boton;
    }
}
