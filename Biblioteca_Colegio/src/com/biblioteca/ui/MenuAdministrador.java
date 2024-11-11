package com.biblioteca.ui;

import com.biblioteca.acciones.GestionDocumentos;
import com.biblioteca.acciones.administracionUsuarios;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class MenuAdministrador extends JFrame {
    // Constantes de diseño
    private final Color PRIMARY_COLOR = new Color(51, 102, 153);
    private final Color SECONDARY_COLOR = new Color(240, 240, 240);
    private final Color HOVER_COLOR = new Color(41, 82, 123);
    private final Color SIDEBAR_BG = new Color(248, 249, 250);
    private final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private final Font MENU_FONT = new Font("Segoe UI", Font.PLAIN, 15);

    public MenuAdministrador() {
        configurarVentana();
        inicializarComponentes();
    }

    private void configurarVentana() {
        setTitle("Sistema Bibliotecario - Panel de Administración");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(800, 500));
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout());
        
        // Panel superior
        add(crearPanelSuperior(), BorderLayout.NORTH);
        
        // Panel de navegación lateral
        add(crearPanelNavegacion(), BorderLayout.WEST);
        
        // Panel principal de contenido
        add(crearPanelContenido(), BorderLayout.CENTER);
    }

    private JPanel crearPanelSuperior() {
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBackground(PRIMARY_COLOR);
        panelSuperior.setPreferredSize(new Dimension(0, 60));
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel lblTitulo = new JLabel("Biblioteca Colegio Amigos De Don Bosco");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(Color.WHITE);
        panelSuperior.add(lblTitulo, BorderLayout.WEST);

        return panelSuperior;
    }

    private JPanel crearPanelNavegacion() {
        JPanel panelNavegacion = new JPanel();
        panelNavegacion.setLayout(new BoxLayout(panelNavegacion, BoxLayout.Y_AXIS));
        panelNavegacion.setBackground(SIDEBAR_BG);
        panelNavegacion.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(0, 0, 0, 1, new Color(218, 220, 224)),
            BorderFactory.createEmptyBorder(20, 15, 20, 15)
        ));
        panelNavegacion.setPreferredSize(new Dimension(250, 0));

        // Crear botones de menú
        String[] menuItems = {
            "Gestión de Usuarios",
            "Gestión de Documentos",
            "Configuración",
            "Buscar Libros",
            "Préstamos",
            "Salir"
        };

        for (String menuItem : menuItems) {
            JButton btn = crearBotonMenu(menuItem);
            panelNavegacion.add(btn);
            panelNavegacion.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        return panelNavegacion;
    }

    private JButton crearBotonMenu(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(MENU_FONT);
        boton.setForeground(new Color(33, 37, 41));
        boton.setBackground(SIDEBAR_BG);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setHorizontalAlignment(SwingConstants.LEFT);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setMaximumSize(new Dimension(220, 40));
        boton.setPreferredSize(new Dimension(220, 40));

        // Efectos hover
        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                boton.setBackground(new Color(233, 236, 239));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                boton.setBackground(SIDEBAR_BG);
            }
        });

        // Agregar ActionListener según el botón
        switch (texto) {
            case "Gestión de Usuarios":
                boton.addActionListener(e -> {
                    new administracionUsuarios().setVisible(true);
                    dispose();
                });
                break;
            case "Gestión de Documentos":
                boton.addActionListener(e -> {
                    SwingUtilities.invokeLater(() -> {
                        new GestionDocumentos().setVisible(true);
                        dispose();
                    });
                });
                break;
            case "Salir":
                boton.addActionListener(e -> {
                    new BibliotecaLogin().setVisible(true);
                    dispose();
                });
                break;
            default:
                boton.addActionListener(e -> 
                    JOptionPane.showMessageDialog(
                        this,
                        "Funcionalidad en desarrollo: " + texto,
                        "Información",
                        JOptionPane.INFORMATION_MESSAGE
                    )
                );
        }

        return boton;
    }

    private JPanel crearPanelContenido() {
        JPanel panelContenido = new JPanel(new BorderLayout(20, 20));
        panelContenido.setBackground(Color.WHITE);
        panelContenido.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Panel de bienvenida
        JPanel panelBienvenida = new JPanel(new BorderLayout());
        panelBienvenida.setBackground(Color.WHITE);

        JLabel lblBienvenida = new JLabel("Bienvenido, Administrador");
        lblBienvenida.setFont(TITLE_FONT);
        lblBienvenida.setForeground(new Color(33, 37, 41));
        panelBienvenida.add(lblBienvenida, BorderLayout.NORTH);

        // Panel de resumen
        JPanel panelResumen = new JPanel(new GridLayout(2, 2, 20, 20));
        panelResumen.setBackground(Color.WHITE);
        panelResumen.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));

        // Agregar tarjetas de resumen
        panelResumen.add(crearTarjetaResumen("Usuarios Totales", "150"));
        panelResumen.add(crearTarjetaResumen("Libros Disponibles", "1,234"));
        panelResumen.add(crearTarjetaResumen("Préstamos Activos", "45"));
        panelResumen.add(crearTarjetaResumen("Nuevos Usuarios", "12"));

        panelContenido.add(panelBienvenida, BorderLayout.NORTH);
        panelContenido.add(panelResumen, BorderLayout.CENTER);

        return panelContenido;
    }

    private JPanel crearTarjetaResumen(String titulo, String valor) {
        JPanel tarjeta = new JPanel(new BorderLayout(10, 10));
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(233, 236, 239)),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblTitulo.setForeground(new Color(108, 117, 125));

        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblValor.setForeground(new Color(33, 37, 41));

        tarjeta.add(lblTitulo, BorderLayout.NORTH);
        tarjeta.add(lblValor, BorderLayout.CENTER);

        return tarjeta;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new MenuAdministrador().setVisible(true);
        });
    }
}