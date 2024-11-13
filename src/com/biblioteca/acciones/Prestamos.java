package com.biblioteca.acciones;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;

public class Prestamos {

    public Prestamos() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public static class RegistrarPrestamoPanel extends JPanel {
        private JTextField txtUsuario;
        private JTextField txtLibro;
        private JButton btnRegistrar;

        public RegistrarPrestamoPanel() {
            setLayout(new GridLayout(3, 2, 5, 5));
            add(new JLabel("ID Usuario:"));
            txtUsuario = new JTextField();
            add(txtUsuario);

            add(new JLabel("ID Libro:"));
            txtLibro = new JTextField();
            add(txtLibro);

            btnRegistrar = new JButton("Registrar Préstamo");
            btnRegistrar.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    registrarPrestamo();
                }
            });
            add(btnRegistrar);
        }

        private void registrarPrestamo() {
            String usuario = txtUsuario.getText();
            String libro = txtLibro.getText();
            // Código de registro del préstamo en la base de datos
            JOptionPane.showMessageDialog(this, "Préstamo registrado para el usuario " + usuario + " y el libro " + libro);
        }
    }

    public static class ConfigurarPrestamosPanel extends JPanel {
        private JCheckBox chkMultaAutomatica;
        private JTextField txtTarifaDiaria;
        private JButton btnGuardar;

        public ConfigurarPrestamosPanel() {
            setLayout(new GridLayout(3, 2, 5, 5));
            add(new JLabel("Activar Multa Automática:"));
            chkMultaAutomatica = new JCheckBox();
            add(chkMultaAutomatica);

            add(new JLabel("Tarifa Diaria de Multa:"));
            txtTarifaDiaria = new JTextField();
            add(txtTarifaDiaria);

            btnGuardar = new JButton("Guardar Configuración");
            btnGuardar.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    guardarConfiguracion();
                }
            });
            add(btnGuardar);
        }

        private void guardarConfiguracion() {
            boolean multaAutomatica = chkMultaAutomatica.isSelected();
            String tarifaDiaria = txtTarifaDiaria.getText();
            // Código para guardar configuración en la base de datos
            JOptionPane.showMessageDialog(this, "Configuración guardada:\nMulta Automática: " + multaAutomatica + "\nTarifa Diaria: " + tarifaDiaria);
        }
    }

    public static class ConsultarPrestamosPanel extends JPanel {
        private JTextField txtIdUsuario;
        private JButton btnBuscar;
        private JTextArea areaResultados;

        public ConsultarPrestamosPanel() {
            setLayout(new BorderLayout(10, 10));
            JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
            panelBusqueda.add(new JLabel("ID Usuario:"));
            txtIdUsuario = new JTextField(15);
            panelBusqueda.add(txtIdUsuario);

            btnBuscar = new JButton("Buscar Préstamos");
            btnBuscar.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    consultarPrestamos();
                }
            });
            panelBusqueda.add(btnBuscar);
            add(panelBusqueda, BorderLayout.NORTH);

            areaResultados = new JTextArea(10, 30);
            areaResultados.setEditable(false);
            add(new JScrollPane(areaResultados), BorderLayout.CENTER);
        }

        private void consultarPrestamos() {
            String idUsuario = txtIdUsuario.getText();
            // Código para consultar los préstamos del usuario en la base de datos
            areaResultados.setText("Resultados de préstamos para el usuario: " + idUsuario);
            // Aquí se añadirían los resultados obtenidos
        }
    }

    public static class RegistrarDevolucionPanel extends JPanel {
        private JTextField txtIdPrestamo;
        private JButton btnRegistrarDevolucion;

        public RegistrarDevolucionPanel() {
            setLayout(new GridLayout(2, 2, 5, 5));
            add(new JLabel("ID Préstamo:"));
            txtIdPrestamo = new JTextField();
            add(txtIdPrestamo);

            btnRegistrarDevolucion = new JButton("Registrar Devolución");
            btnRegistrarDevolucion.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    registrarDevolucion();
                }
            });
            add(btnRegistrarDevolucion);
        }

        private void registrarDevolucion() {
            String idPrestamo = txtIdPrestamo.getText();
            // Código para registrar la devolución en la base de datos
            JOptionPane.showMessageDialog(this, "Devolución registrada para el préstamo ID: " + idPrestamo);
        }
    }
}
