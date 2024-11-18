package com.biblioteca.acciones.Mora;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import com.biblioteca.dao.GestionPrestamosDAO; // Ejemplo de DAO para manejar préstamos

/**
 * Panel para calcular la mora en función de los días de retraso.
 * Permite ingresar la fecha de vencimiento y la fecha de devolución.
 */
public class CalcularMoraPanel extends JPanel {

    private GestionPrestamosDAO gestionPrestamosDAO; // Suponiendo que existe un DAO para gestión de préstamos
    private JTextField txtFechaVencimiento;
    private JTextField txtFechaDevolucion;
    private JLabel lblMora;
    private JButton btnCalcular;

    // Tarifa de mora por día
    private static final double TARIFA_MORA_DIARIA = 0.5; // Ejemplo: 0.5 unidades monetarias por día de retraso

    public CalcularMoraPanel() {
        setLayout(new BorderLayout(10, 10));
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        JPanel panelDatos = new JPanel(new GridLayout(3, 2, 5, 5));

        panelDatos.add(new JLabel("Fecha de Vencimiento (YYYY-MM-DD):"));
        txtFechaVencimiento = new JTextField();
        panelDatos.add(txtFechaVencimiento);

        panelDatos.add(new JLabel("Fecha de Devolución (YYYY-MM-DD):"));
        txtFechaDevolucion = new JTextField();
        panelDatos.add(txtFechaDevolucion);

        panelDatos.add(new JLabel("Mora Total:"));
        lblMora = new JLabel("0.0");
        panelDatos.add(lblMora);

        add(panelDatos, BorderLayout.CENTER);

        btnCalcular = new JButton("Calcular Mora");
        btnCalcular.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calcularMora();
            }
        });
        add(btnCalcular, BorderLayout.SOUTH);
    }

    private void calcularMora() {
        try {
            // Parsear fechas de entrada
            LocalDate fechaVencimiento = LocalDate.parse(txtFechaVencimiento.getText().trim());
            LocalDate fechaDevolucion = LocalDate.parse(txtFechaDevolucion.getText().trim());

            long diasRetraso = ChronoUnit.DAYS.between(fechaVencimiento, fechaDevolucion);

            if (diasRetraso > 0) {
                double moraTotal = diasRetraso * TARIFA_MORA_DIARIA;
                DecimalFormat formato = new DecimalFormat("#.00");
                lblMora.setText(formato.format(moraTotal));
            } else {
                lblMora.setText("0.0");
                JOptionPane.showMessageDialog(this, "La devolución no tiene mora o está adelantada.",
                        "Información", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error en el formato de las fechas. Use el formato YYYY-MM-DD.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
