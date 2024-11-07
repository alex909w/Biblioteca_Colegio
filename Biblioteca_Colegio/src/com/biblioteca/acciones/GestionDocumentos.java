package com.biblioteca.acciones;

import com.biblioteca.controladores.DocumentoController;
import com.biblioteca.validaciones.TipoDocumentoDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.sql.SQLException;

class DateLabelFormatter extends JFormattedTextField.AbstractFormatter {
    private String datePattern = "yyyy-MM-dd";
    private SimpleDateFormat dateFormatter = new SimpleDateFormat(datePattern);

    @Override
    public Object stringToValue(String text) throws ParseException {
        return text == null || text.isEmpty() ? null : dateFormatter.parseObject(text);
    }

    @Override
    public String valueToString(Object value) throws ParseException {
        if (value instanceof Date) return dateFormatter.format((Date) value);
        if (value instanceof Calendar) return dateFormatter.format(((Calendar) value).getTime());
        return "";
    }
}

public class GestionDocumentos extends JFrame {
    private JComboBox<String> tipoDocumentoComboBox;
    private JPanel panelFormulario;
    private DocumentoController controlador;
    private TipoDocumentoDAO tipoDocumentoDAO;

    private JPanel panelDinamico;
    private JButton btnGuardar, btnLimpiar, btnVolver, btnCrearNuevoTipo;
    private JTextField idGeneradoField; // Campo de texto para el ID autogenerado

    public GestionDocumentos() {
        setTitle("Gestión de Documentos");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        controlador = new DocumentoController();
        tipoDocumentoDAO = new TipoDocumentoDAO();

        setLayout(new BorderLayout(10, 10));
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

        // ComboBox para seleccionar el tipo de documento
        String[] tiposDocumentos = cargarTiposDocumentos();
        tipoDocumentoComboBox = new JComboBox<>(tiposDocumentos);
        tipoDocumentoComboBox.setPreferredSize(new Dimension(200, 30));
        tipoDocumentoComboBox.addActionListener(e -> actualizarFormularioYID());

        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelSuperior.add(new JLabel("Tipo de Documento:"));
        panelSuperior.add(tipoDocumentoComboBox);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnGuardar = crearBotonBasico("Guardar", new Color(76, 175, 80), "Guardar los datos ingresados.");
        btnLimpiar = crearBotonBasico("Limpiar", new Color(255, 152, 0), "Limpiar todos los campos del formulario.");
        btnCrearNuevoTipo = crearBotonBasico("Crear Nuevo Tipo", new Color(33, 150, 243), "Crear un nuevo tipo de documento.");
        btnCrearNuevoTipo.addActionListener(e -> crearNuevoTipoDeDocumento());
        btnVolver = crearBotonBasico("Volver al Menú Anterior", new Color(244, 67, 54), "Volver al menú principal.");

        panelBotones.add(btnGuardar);
        panelBotones.add(btnLimpiar);
        panelBotones.add(btnCrearNuevoTipo);
        panelBotones.add(btnVolver);

        JPanel panelTop = new JPanel();
        panelTop.setLayout(new BoxLayout(panelTop, BoxLayout.Y_AXIS));
        panelTop.add(panelSuperior);
        panelTop.add(panelBotones);
        add(panelTop, BorderLayout.NORTH);

        panelFormulario = new JPanel(new CardLayout());
        panelDinamico = new JPanel(new GridBagLayout()); // Panel para los campos dinámicos
        panelFormulario.add(panelDinamico, "Dinamico");
        add(panelFormulario, BorderLayout.CENTER);

        JPanel panelEstado = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelEstado.add(new JLabel("Estado: Listo para ingresar datos."));
        panelEstado.setBorder(BorderFactory.createEtchedBorder());
        add(panelEstado, BorderLayout.SOUTH);

        actualizarFormularioYID();
    }

    private String[] cargarTiposDocumentos() {
        try {
            ArrayList<String> tipos = tipoDocumentoDAO.obtenerTiposDocumentos();
            return tipos.isEmpty() ? new String[0] : tipos.toArray(new String[0]);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar los tipos de documentos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return new String[0];
        }
    }

    private void actualizarFormularioYID() {
        String tipoDocumento = (String) tipoDocumentoComboBox.getSelectedItem();
        if (tipoDocumento != null && !tipoDocumento.isEmpty()) {
            cargarFormularioParaDocumento(tipoDocumento); // Cargar campos adicionales
            String nuevoID = generarIDParaDocumento(tipoDocumento);
            idGeneradoField.setText(nuevoID); // Muestra el ID generado
        } else {
            JOptionPane.showMessageDialog(this, "No hay formularios disponibles. Por favor, cree uno nuevo.");
        }
    }

   private String generarIDParaDocumento(String tipoDocumento) {
    String prefix = tipoDocumento.substring(0, 3).toUpperCase();
    int numero = 0;

    try {
        // Verifica si la tabla existe; si no, la crea y devuelve el primer ID
        if (!tipoDocumentoDAO.existeTabla(tipoDocumento)) {
            ArrayList<String> columnas = new ArrayList<>();
            columnas.add("Nombre");
            columnas.add("Descripción");

            tipoDocumentoDAO.crearTablaParaDocumento(tipoDocumento, columnas);
            return prefix + "001";
        }

        // Intenta obtener el último número en la secuencia de IDs
        numero = tipoDocumentoDAO.obtenerNumeroActual(prefix);

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error al obtener el ID: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        return prefix + "001"; // Retorna el primer ID en caso de error
    }
    // Si no se encuentra ningún ID previo, comienza la numeración con el primero
    return prefix + String.format("%03d", numero + 1);
}


   private void cargarFormularioParaDocumento(String nombreDocumento) {
    panelDinamico.removeAll(); // Limpiar el panel antes de agregar nuevos campos
    ArrayList<String> columnas;
    try {
        columnas = tipoDocumentoDAO.obtenerColumnasDeTabla(nombreDocumento); // Obtiene las columnas de la tabla registrada
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Agregar solo el campo de ID generado automáticamente
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelDinamico.add(new JLabel("ID Documento:"), gbc);

        gbc.gridx = 1;
        idGeneradoField = new JTextField(15);
        idGeneradoField.setEditable(false);
        panelDinamico.add(idGeneradoField, gbc);

        // Agregar los demás campos, excluyendo cualquier columna que se llame "id"
        int row = 1;
        for (String columna : columnas) {
            if (columna.equalsIgnoreCase("id")) {
                continue; // Omite el campo "id"
            }
            gbc.gridx = 0;
            gbc.gridy = row;
            panelDinamico.add(new JLabel(columna + ":"), gbc);

            gbc.gridx = 1;
            JTextField campoTexto;
            if (columna.toLowerCase().contains("fecha")) {
                campoTexto = new JFormattedTextField(new DateLabelFormatter());
            } else {
                campoTexto = new JTextField(15);
            }
            panelDinamico.add(campoTexto, gbc);
            row++;
        }
        panelDinamico.revalidate();
        panelDinamico.repaint();
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error al cargar el formulario para el documento: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}


    private JButton crearBotonBasico(String texto, Color colorFondo, String tooltip) {
        JButton boton = new JButton(texto);
        boton.setPreferredSize(new Dimension(180, 40));
        boton.setBackground(colorFondo);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setFont(new Font("Arial", Font.BOLD, 14));
        boton.setToolTipText(tooltip);
        boton.setHorizontalAlignment(SwingConstants.CENTER);
        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                boton.setBackground(colorFondo.darker());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                boton.setBackground(colorFondo);
            }
        });
        boton.addActionListener(e -> {
            if ("Guardar".equals(texto)) guardarDocumento();
            else if ("Limpiar".equals(texto)) limpiarFormulario();
            else if ("Volver al Menú Anterior".equals(texto)) volverAlMenu();
        });
        return boton;
    }

    private void guardarDocumento() {
        String tipoDocumento = (String) tipoDocumentoComboBox.getSelectedItem();
        ArrayList<String> datos = new ArrayList<>();
        datos.add(idGeneradoField.getText());

        if (tipoDocumento != null && !tipoDocumento.isEmpty()) {
            for (Component comp : panelDinamico.getComponents()) {
                if (comp instanceof JTextField && comp != idGeneradoField) {
                    datos.add(((JTextField) comp).getText().trim());
                } else if (comp instanceof JFormattedTextField) {
                    datos.add(((JFormattedTextField) comp).getText().trim());
                }
            }
        }

        boolean exito = controlador.guardarDocumento(tipoDocumento, datos.toArray(new String[0]));
        JOptionPane.showMessageDialog(this, exito ? "Documento guardado exitosamente." : "Error al guardar el documento.");
        if (exito) limpiarFormulario();
    }

    private void limpiarFormulario() {
        for (Component comp : panelDinamico.getComponents()) {
            if (comp instanceof JTextField) {
                ((JTextField) comp).setText("");
            } else if (comp instanceof JFormattedTextField) {
                ((JFormattedTextField) comp).setText("");
            }
        }
    }

    private void volverAlMenu() {
        if (JOptionPane.showConfirmDialog(this, "¿Está seguro que desea volver al menú anterior?", "Confirmar Salida", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
            dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GestionDocumentos().setVisible(true));
    }

  private void crearNuevoTipoDeDocumento() {
    String nombreDocumento = JOptionPane.showInputDialog(this, "Ingrese el nombre del nuevo tipo de documento:");
    if (nombreDocumento == null || nombreDocumento.trim().isEmpty()) return;

    try {
        if (tipoDocumentoDAO.existeTabla(nombreDocumento)) {
            int opcion = JOptionPane.showOptionDialog(this,
                    "La tabla ya existe. ¿Qué deseas hacer?",
                    "Tabla Existente",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    new String[]{"Usar Existente", "Eliminar y Crear Nueva", "Editar Columnas", "Cancelar"},
                    "Cancelar");

            switch (opcion) {
                case 0:
                    tipoDocumentoComboBox.addItem(nombreDocumento);
                    actualizarFormularioYID();
                    JOptionPane.showMessageDialog(this, "Usando la tabla existente.");
                    return;
                case 1:
                    tipoDocumentoDAO.eliminarTabla(nombreDocumento);
                    JOptionPane.showMessageDialog(this, "Tabla eliminada.");
                    break;
                case 2:
                    editarColumnasTabla(nombreDocumento);
                    actualizarFormularioYID();
                    return;
                case 3:
                    return;
                default:
                    return;
            }
        }

        int columnas = Integer.parseInt(JOptionPane.showInputDialog(this, "Número de columnas (excluyendo ID):"));
        ArrayList<String> nombresColumnas = new ArrayList<>();

        for (int i = 0; i < columnas; i++) {
            String nombreColumna = JOptionPane.showInputDialog(this, "Nombre de la columna " + (i + 1) + ":");
            nombresColumnas.add(nombreColumna);
        }

        // Crear la tabla en la base de datos con las columnas especificadas
        tipoDocumentoDAO.crearTablaParaDocumento(nombreDocumento, nombresColumnas);

        // Agregar el tipo de documento a la lista de documentos
        tipoDocumentoDAO.agregarTipoDocumento(nombreDocumento);
        tipoDocumentoComboBox.addItem(nombreDocumento);

        actualizarFormularioYID();
        JOptionPane.showMessageDialog(this, "Nuevo tipo de documento creado exitosamente.");

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error al crear el nuevo tipo de documento: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}



    private void editarColumnasTabla(String nombreDocumento) {
        try {
            ArrayList<String> columnasActuales = tipoDocumentoDAO.obtenerColumnasDeTabla(nombreDocumento);
            ArrayList<String> nuevasColumnas = new ArrayList<>(columnasActuales);

            for (int i = 0; i < columnasActuales.size(); i++) {
                String columna = columnasActuales.get(i);
                String nuevaColumna = JOptionPane.showInputDialog(this, "Nombre de la columna " + (i + 1) + " (actual: " + columna + "):", columna);
                if (nuevaColumna != null && !nuevaColumna.trim().isEmpty()) {
                    nuevasColumnas.set(i, nuevaColumna.trim());
                }
            }

            tipoDocumentoDAO.actualizarColumnasTabla(nombreDocumento, nuevasColumnas);
            JOptionPane.showMessageDialog(this, "Columnas actualizadas para la tabla " + nombreDocumento);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al editar columnas: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
