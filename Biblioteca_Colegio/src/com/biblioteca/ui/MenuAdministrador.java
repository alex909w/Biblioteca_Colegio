package com.biblioteca.ui;

import com.biblioteca.acciones.AdministracionUsuarios;
import com.biblioteca.acciones.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class MenuAdministrador extends JFrame {
    // Constantes de diseño
    private final Color COLOR_PRIMARIO = new Color(51, 102, 153);
    private final Color FONDO_LATERAL = new Color(248, 249, 250);
    private final Color COLOR_HOVER = new Color(233, 236, 239);
    private final Font FUENTE_PRINCIPAL = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font FUENTE_TITULO = new Font("Segoe UI", Font.BOLD, 24);

    private JPanel panelContenido; // Panel principal para cambiar el contenido dinámicamente

    public MenuAdministrador() {
        setTitle("Sistema Bibliotecario - Panel de Administración");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Configuración del diseño general con el panel superior y el panel de navegación lateral
        setLayout(new BorderLayout());
        add(crearPanelSuperior(), BorderLayout.NORTH);
        add(crearPanelNavegacion(), BorderLayout.WEST);

        // Panel de contenido para cambiar dinámicamente según la selección del usuario
        panelContenido = new JPanel(new BorderLayout());
        JLabel lblBienvenida = new JLabel("Bienvenido, Administrador", SwingConstants.CENTER);
        lblBienvenida.setFont(FUENTE_TITULO);
        panelContenido.add(lblBienvenida, BorderLayout.CENTER);

        add(panelContenido, BorderLayout.CENTER);
    }

    private JPanel crearPanelSuperior() {
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBackground(COLOR_PRIMARIO);
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
        panelNavegacion.setBackground(FONDO_LATERAL);
        panelNavegacion.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(0, 0, 0, 1, new Color(218, 220, 224)),
            BorderFactory.createEmptyBorder(20, 15, 20, 15)
        ));
        panelNavegacion.setPreferredSize(new Dimension(250, 0));

        // Crear botones de menú
        JButton btnGestionUsuarios = crearBotonMenu("Gestión de Usuarios", "icon_usuarios.png");
        JButton btnGestionDocumentos = crearBotonMenu("Gestión de Documentos", "icon_documentos.png");
        JButton btnInventario = crearBotonMenu("Gestión de Inventario", "icon_inventario.png");
        JButton btnConfiguracion = crearBotonMenu("Configuración", "icon_configuracion.png");
        JButton btnBuscarLibros = crearBotonMenu("Buscar Libros", "icon_buscar.png");
        JButton btnPrestamos = crearBotonMenu("Préstamos", "icon_prestamos.png");
        JButton btnSalir = crearBotonMenu("Salir", "icon_salir.png");

        // Acciones para mostrar paneles de contenido en el área central (Panel #1)
        btnGestionUsuarios.addActionListener(e -> mostrarContenido(new AdministracionUsuarios()));
        btnGestionDocumentos.addActionListener(e -> mostrarContenido(new GestionDocumentosPanel()));
        btnInventario.addActionListener(e -> mostrarContenido(new GestionInventarioPanel()));
        btnConfiguracion.addActionListener(e -> mostrarContenido(new ConfiguracionPanel()));
        btnBuscarLibros.addActionListener(e -> mostrarContenido(new BuscarLibrosPanel()));
        btnPrestamos.addActionListener(e -> mostrarContenido(new PrestamosPanel()));

        btnSalir.addActionListener(e -> System.exit(0)); // Cierra la aplicación

        // Añadir botones al panel de navegación
        panelNavegacion.add(btnGestionUsuarios);
        panelNavegacion.add(Box.createVerticalStrut(10));
        panelNavegacion.add(btnGestionDocumentos);
        panelNavegacion.add(Box.createVerticalStrut(10));
        panelNavegacion.add(btnInventario);
        panelNavegacion.add(Box.createVerticalStrut(10));
        panelNavegacion.add(btnConfiguracion);
        panelNavegacion.add(Box.createVerticalStrut(10));
        panelNavegacion.add(btnBuscarLibros);
        panelNavegacion.add(Box.createVerticalStrut(10));
        panelNavegacion.add(btnPrestamos);
        panelNavegacion.add(Box.createVerticalStrut(10));
        panelNavegacion.add(btnSalir);

        return panelNavegacion;
    }

    private JButton crearBotonMenu(String texto, String icono) {
        JButton boton = new JButton(texto);
        boton.setIcon(new ImageIcon(icono)); // Coloca los iconos en la carpeta adecuada
        boton.setHorizontalAlignment(SwingConstants.LEFT);
        boton.setMaximumSize(new Dimension(220, 40));
        boton.setBackground(FONDO_LATERAL);
        boton.setFont(FUENTE_PRINCIPAL);
        boton.setForeground(new Color(33, 37, 41));
        boton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 5));
        boton.setFocusPainted(false);

        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                boton.setBackground(COLOR_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                boton.setBackground(FONDO_LATERAL);
            }
        });
        
        return boton;
    }

    private void mostrarContenido(JPanel panel) {
        panelContenido.removeAll();
        panelContenido.add(panel, BorderLayout.CENTER);
        panelContenido.revalidate();
        panelContenido.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MenuAdministrador().setVisible(true));
    }
}

// Ejemplos de paneles para el contenido dinámico (puedes personalizarlos)
class GestionDocumentosPanel extends JPanel {
    public GestionDocumentosPanel() {
        setLayout(new BorderLayout());
        add(new JLabel("Panel de Gestión de Documentos", SwingConstants.CENTER), BorderLayout.CENTER);
    }
}

class GestionInventarioPanel extends JPanel {
    public GestionInventarioPanel() {
        setLayout(new BorderLayout());
        add(new JLabel("Panel de Gestión de Inventario", SwingConstants.CENTER), BorderLayout.CENTER);
    }
}

class ConfiguracionPanel extends JPanel {
    public ConfiguracionPanel() {
        setLayout(new BorderLayout());
        add(new JLabel("Panel de Configuración", SwingConstants.CENTER), BorderLayout.CENTER);
    }
}

class BuscarLibrosPanel extends JPanel {
    public BuscarLibrosPanel() {
        setLayout(new BorderLayout());
        add(new JLabel("Panel de Búsqueda de Libros", SwingConstants.CENTER), BorderLayout.CENTER);
    }
}

class PrestamosPanel extends JPanel {
    public PrestamosPanel() {
        setLayout(new BorderLayout());
        add(new JLabel("Panel de Préstamos", SwingConstants.CENTER), BorderLayout.CENTER);
    }
}
