package com.biblioteca.ui;

import com.biblioteca.bd.ConexionBaseDatos;
import javax.swing.*;
import java.awt.*;

public class MenuAlumno extends JFrame {

    public MenuAlumno() {
        setTitle("Menú Alumno - Biblioteca Colegio Amigos De Don Bosco");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel de navegación lateral
        JPanel panelNavegacion = new JPanel();
        panelNavegacion.setLayout(new GridLayout(3, 1, 10, 10));
        panelNavegacion.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        // Botones de navegación
        JButton btnBuscarLibros = new JButton("Buscar Libros");
        JButton btnPrestamos = new JButton("Préstamos");
        JButton btnSalir = new JButton("Salir");

        // Añadir botones al panel de navegación
        panelNavegacion.add(btnBuscarLibros);
        panelNavegacion.add(btnPrestamos);
        panelNavegacion.add(btnSalir);

        // Acción del botón "Buscar Libros"
        btnBuscarLibros.addActionListener(e -> {
            JFrame buscarLibrosFrame = new JFrame("Buscar Libros");
            buscarLibrosFrame.setSize(400, 300);
            buscarLibrosFrame.setLocationRelativeTo(null);

            JPanel panel = new JPanel(new BorderLayout());
            JTextField txtCriterio = new JTextField();
            JButton btnBuscar = new JButton("Buscar");
            JTextArea resultadoArea = new JTextArea();
            resultadoArea.setEditable(false);

            btnBuscar.addActionListener(ev -> {
                String criterio = txtCriterio.getText();
                resultadoArea.setText("Resultados de búsqueda para: " + criterio);
            });

            panel.add(txtCriterio, BorderLayout.NORTH);
            panel.add(btnBuscar, BorderLayout.CENTER);
            panel.add(new JScrollPane(resultadoArea), BorderLayout.SOUTH);

            buscarLibrosFrame.add(panel);
            buscarLibrosFrame.setVisible(true);
        });

        // Acción del botón "Préstamos"
        btnPrestamos.addActionListener(e -> {
            JFrame prestamosFrame = new JFrame("Ver Préstamos");
            prestamosFrame.setSize(400, 250);
            prestamosFrame.setLocationRelativeTo(null);

            JTextArea prestamosArea = new JTextArea("Lista de préstamos actuales");
            prestamosArea.setEditable(false);

            prestamosFrame.add(new JScrollPane(prestamosArea));
            prestamosFrame.setVisible(true);
        });

        // Acción del botón "Salir"
        btnSalir.addActionListener(e -> {
            ConexionBaseDatos.cerrarConexion();
            System.exit(0);
        });

        // Panel principal de contenido
        JPanel panelContenido = new JPanel(new BorderLayout());
        JLabel lblBienvenida = new JLabel("Bienvenido Alumno", SwingConstants.CENTER);
        lblBienvenida.setFont(new Font("Arial", Font.BOLD, 18));
        panelContenido.add(lblBienvenida, BorderLayout.CENTER);

        // Añadir paneles al frame
        add(panelNavegacion, BorderLayout.WEST);
        add(panelContenido, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MenuAlumno().setVisible(true));
    }
}
