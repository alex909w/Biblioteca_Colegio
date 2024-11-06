package acciones;

import formularios.SeleccionarTipoDocumento;
import javax.swing.*;
import java.awt.*;

public class GestionDocumentos extends JFrame {

    public GestionDocumentos(JFrame ventanaAnterior) {
        setTitle("Gestión de Documentos");
        setSize(500, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel tituloLabel = new JLabel("Gestión de Documentos", SwingConstants.CENTER);
        tituloLabel.setFont(new Font("Arial", Font.BOLD, 24));
        tituloLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(tituloLabel, BorderLayout.NORTH);

        JPanel panelOpciones = new JPanel();
        panelOpciones.setLayout(new GridLayout(3, 2, 15, 15));
        panelOpciones.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton agregarButton = new JButton("Agregar Nuevo ");
        JButton buscarButton = new JButton("Buscar Documentos");
        JButton editarButton = new JButton("Actualizar o Editar Documento");
        JButton eliminarButton = new JButton("Eliminar Documento");
        JButton inventarioButton = new JButton("Inventario de Documentos");
        JButton prestamosButton = new JButton("Gestión de Préstamos");
        JButton reportesButton = new JButton("Reportes");

        Font buttonFont = new Font("Arial", Font.PLAIN, 16);
        agregarButton.setFont(buttonFont);
        buscarButton.setFont(buttonFont);
        editarButton.setFont(buttonFont);
        eliminarButton.setFont(buttonFont);
        inventarioButton.setFont(buttonFont);
        prestamosButton.setFont(buttonFont);
        reportesButton.setFont(buttonFont);

        agregarButton.addActionListener(e -> SeleccionarTipoDocumento.mostrarSeleccion(this));
        buscarButton.addActionListener(e -> abrirFormularioBuscar());
        editarButton.addActionListener(e -> abrirFormularioEditar());
        eliminarButton.addActionListener(e -> abrirFormularioEliminar());
        inventarioButton.addActionListener(e -> abrirInventario());
        prestamosButton.addActionListener(e -> gestionarPrestamos());
        reportesButton.addActionListener(e -> generarReportes());

        panelOpciones.add(agregarButton);
        panelOpciones.add(buscarButton);
        panelOpciones.add(editarButton);
        panelOpciones.add(eliminarButton);
        panelOpciones.add(inventarioButton);
        panelOpciones.add(prestamosButton);

        JPanel panelVolver = new JPanel();
        panelVolver.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        JButton volverButton = new JButton("Volver");
        volverButton.setFont(buttonFont);
        volverButton.addActionListener(e -> {
            ventanaAnterior.setVisible(true);
            dispose();
        });
        panelVolver.add(volverButton);

        add(panelOpciones, BorderLayout.CENTER);
        add(panelVolver, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void abrirFormularioBuscar() {
        JOptionPane.showMessageDialog(this, "Abrir formulario para buscar documentos");
    }

    private void abrirFormularioEditar() {
        JOptionPane.showMessageDialog(this, "Abrir formulario para editar documento");
    }

    private void abrirFormularioEliminar() {
        JOptionPane.showMessageDialog(this, "Abrir formulario para eliminar documento");
    }

    private void abrirInventario() {
        JOptionPane.showMessageDialog(this, "Mostrar inventario de documentos");
    }

    private void gestionarPrestamos() {
        JOptionPane.showMessageDialog(this, "Abrir gestión de préstamos");
    }

    private void generarReportes() {
        JOptionPane.showMessageDialog(this, "Generar reportes de documentos");
    }
    
    public static void mostrarGestionDocumentos(JFrame ventanaAnterior) {
    SwingUtilities.invokeLater(() -> {
        ventanaAnterior.dispose(); // Cerrar la ventana anterior
        new GestionDocumentos(ventanaAnterior).setVisible(true); // Abrir la nueva ventana y pasar la referencia
    });
}

}
