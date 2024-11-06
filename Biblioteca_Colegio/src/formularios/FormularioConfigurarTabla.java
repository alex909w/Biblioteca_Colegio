package formularios;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class FormularioConfigurarTabla extends JDialog {

    private JTextField txtNombreDocumento;
    private JTextField txtCantidadColumnas;
    private JButton btnCrearTabla;
    private JPanel panelColumnas;
    private ArrayList<JTextField> columnas = new ArrayList<>();

    public FormularioConfigurarTabla(JFrame parent) {
        super(parent, "Configurar Nuevo Documento", true);
        setSize(400, 400);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // Panel para ingresar el nombre del documento
        JPanel panelNombre = new JPanel(new GridLayout(2, 2, 10, 10));
        JLabel lblNombre = new JLabel("Nombre del documento:");
        txtNombreDocumento = new JTextField();
        panelNombre.add(lblNombre);
        panelNombre.add(txtNombreDocumento);

        // Panel para definir la cantidad de columnas
        JLabel lblCantidadColumnas = new JLabel("Cantidad de columnas:");
        txtCantidadColumnas = new JTextField();
        panelNombre.add(lblCantidadColumnas);
        panelNombre.add(txtCantidadColumnas);
        add(panelNombre, BorderLayout.NORTH);

        // Panel de columnas para definir cada columna
        panelColumnas = new JPanel();
        panelColumnas.setLayout(new BoxLayout(panelColumnas, BoxLayout.Y_AXIS));
        add(panelColumnas, BorderLayout.CENTER);

        // Botón para generar campos de nombre de columna
        JButton btnGenerar = new JButton("Generar Columnas");
        btnGenerar.addActionListener(e -> generarCamposColumnas());
        panelNombre.add(btnGenerar);

        // Botón para crear la tabla
        btnCrearTabla = new JButton("Crear Tabla");
        btnCrearTabla.addActionListener(e -> crearTabla());
        JPanel panelBotonCrear = new JPanel();
        panelBotonCrear.add(btnCrearTabla);
        add(panelBotonCrear, BorderLayout.SOUTH);
    }

    private void generarCamposColumnas() {
        panelColumnas.removeAll();
        columnas.clear();
        try {
            int cantidad = Integer.parseInt(txtCantidadColumnas.getText());
            for (int i = 0; i < cantidad; i++) {
                JPanel panelFila = new JPanel(new FlowLayout(FlowLayout.LEFT));
                JLabel lblColumna = new JLabel("Nombre Columna " + (i + 1) + ": ");
                JTextField txtColumna = new JTextField(15);
                columnas.add(txtColumna);
                panelFila.add(lblColumna);
                panelFila.add(txtColumna);
                panelColumnas.add(panelFila);
            }
            panelColumnas.revalidate();
            panelColumnas.repaint();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese un número válido de columnas.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void crearTabla() {
        String nombreDocumento = txtNombreDocumento.getText();
        if (nombreDocumento.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese un nombre para el documento.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ArrayList<String> nombresColumnas = new ArrayList<>();
        for (JTextField txtColumna : columnas) {
            String nombreColumna = txtColumna.getText();
            if (!nombreColumna.isEmpty()) {
                nombresColumnas.add(nombreColumna);
            } else {
                JOptionPane.showMessageDialog(this, "Por favor, ingrese un nombre para cada columna.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // Aquí podrías incluir lógica para guardar la tabla en la base de datos
        JOptionPane.showMessageDialog(this, "Tabla '" + nombreDocumento + "' creada con columnas: " + nombresColumnas);
        dispose();
    }
}
