package com.biblioteca.acciones;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JLabel;

public class BuscarInventario extends JPanel {

    public BuscarInventario() {
        setLayout(new BorderLayout());
        add(new JLabel("Buscar Inventario"), BorderLayout.CENTER);
    }
}
