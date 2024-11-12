package com.biblioteca.ui;

import com.biblioteca.acciones.GestionProductos.AgregarFormulario;
import com.biblioteca.acciones.GestionProductos.EditarFormulario;
import com.biblioteca.acciones.Usuarios.AdministracionUsuarios;
import com.biblioteca.acciones.AgregarInventario;
import com.biblioteca.acciones.ActualizarInventario;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class MenuAdministrador extends JFrame {
    private final Color COLOR_PRIMARIO = new Color(51, 102, 153);
    private final Color FONDO_LATERAL = new Color(248, 249, 250);
    private final Color COLOR_HOVER = new Color(233, 236, 239);
    private final Font FUENTE_PRINCIPAL = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font FUENTE_TITULO = new Font("Segoe UI", Font.BOLD, 24);

    private JPanel panelContenido;
    private JPanel subMenuGestionProductos;
    private JPanel subMenuGestionInventario;
    private boolean subMenuProductosVisible = false;
    private boolean subMenuInventarioVisible = false;

    private JTable tablaInventarios; // Tabla de inventarios

    public MenuAdministrador() {
        setTitle("Sistema Bibliotecario - Panel de Administración");
        setSize(1200, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());
        add(crearPanelSuperior(), BorderLayout.NORTH);
        add(crearPanelNavegacion(), BorderLayout.WEST);

        // Panel de contenido original
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

        JButton btnGestionUsuarios = crearBotonMenu("Gestión de Usuarios", "icon_usuarios.png");
        JButton btnGestionProductos = crearBotonMenu("Gestión de Productos", "icon_productos.png");
        JButton btnGestionInventario = crearBotonMenu("Gestión de Inventario", "icon_inventario.png");
        JButton btnSalir = crearBotonMenu("Salir", "icon_salir.png");

        btnGestionProductos.addActionListener(e -> toggleSubMenuProductos());
        btnGestionInventario.addActionListener(e -> toggleSubMenuInventario());

        btnGestionUsuarios.addActionListener(e -> mostrarContenido(new AdministracionUsuarios()));
        btnSalir.addActionListener(e -> System.exit(0));

        panelNavegacion.add(btnGestionUsuarios);
        panelNavegacion.add(Box.createVerticalStrut(10));
        panelNavegacion.add(btnGestionProductos);
        panelNavegacion.add(crearSubMenuProductos());
        panelNavegacion.add(Box.createVerticalStrut(10));
        panelNavegacion.add(btnGestionInventario);
        panelNavegacion.add(crearSubMenuInventario());
        panelNavegacion.add(Box.createVerticalStrut(10));
        panelNavegacion.add(btnSalir);

        return panelNavegacion;
    }

    private JButton crearBotonMenu(String texto, String icono) {
        JButton boton = new JButton(texto);
        boton.setIcon(new ImageIcon(icono));
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

    private JPanel crearSubMenuProductos() {
        subMenuGestionProductos = new JPanel();
        subMenuGestionProductos.setLayout(new BoxLayout(subMenuGestionProductos, BoxLayout.Y_AXIS));
        subMenuGestionProductos.setBackground(FONDO_LATERAL);
        subMenuGestionProductos.setVisible(false);

        JButton agregarFormulario = new JButton("Agregar Formulario");
        agregarFormulario.setFont(FUENTE_PRINCIPAL);
        agregarFormulario.setHorizontalAlignment(SwingConstants.LEFT);
        agregarFormulario.setBorder(BorderFactory.createEmptyBorder(5, 30, 5, 5));
        agregarFormulario.setBackground(FONDO_LATERAL);
        agregarFormulario.addActionListener(e -> mostrarContenido(new AgregarFormulario()));

        JButton editarFormulario = new JButton("Editar Formulario");
        editarFormulario.setFont(FUENTE_PRINCIPAL);
        editarFormulario.setHorizontalAlignment(SwingConstants.LEFT);
        editarFormulario.setBorder(BorderFactory.createEmptyBorder(5, 30, 5, 5));
        editarFormulario.setBackground(FONDO_LATERAL);
        editarFormulario.addActionListener(e -> mostrarContenido(new EditarFormulario()));

        subMenuGestionProductos.add(agregarFormulario);
        subMenuGestionProductos.add(editarFormulario);

        return subMenuGestionProductos;
    }

    private JPanel crearSubMenuInventario() {
        subMenuGestionInventario = new JPanel();
        subMenuGestionInventario.setLayout(new BoxLayout(subMenuGestionInventario, BoxLayout.Y_AXIS));
        subMenuGestionInventario.setBackground(FONDO_LATERAL);
        subMenuGestionInventario.setVisible(false);

        JButton agregarProducto = new JButton("Agregar Producto");
        agregarProducto.setFont(FUENTE_PRINCIPAL);
        agregarProducto.setHorizontalAlignment(SwingConstants.LEFT);
        agregarProducto.setBorder(BorderFactory.createEmptyBorder(5, 30, 5, 5));
        agregarProducto.setBackground(FONDO_LATERAL);
        agregarProducto.addActionListener(e -> mostrarContenido(new AgregarInventario()));

         JButton actualizarEntradaSalida = new JButton("Actualizar / Eliminar");
        actualizarEntradaSalida.setFont(FUENTE_PRINCIPAL);
        actualizarEntradaSalida.setHorizontalAlignment(SwingConstants.LEFT);
        actualizarEntradaSalida.setBorder(BorderFactory.createEmptyBorder(5, 30, 5, 5));
        actualizarEntradaSalida.setBackground(FONDO_LATERAL);
        actualizarEntradaSalida.addActionListener(e -> mostrarContenido(new ActualizarInventario()));

        JButton buscarProducto = new JButton("Buscar Producto");
        buscarProducto.setFont(FUENTE_PRINCIPAL);
        buscarProducto.setHorizontalAlignment(SwingConstants.LEFT);
        buscarProducto.setBorder(BorderFactory.createEmptyBorder(5, 30, 5, 5));
        buscarProducto.setBackground(FONDO_LATERAL);
       

       

        subMenuGestionInventario.add(agregarProducto);
        subMenuGestionInventario.add(buscarProducto);
        subMenuGestionInventario.add(actualizarEntradaSalida);

        return subMenuGestionInventario;
    }


    private void toggleSubMenuProductos() {
        subMenuProductosVisible = !subMenuProductosVisible;
        subMenuGestionProductos.setVisible(subMenuProductosVisible);
        revalidate();
        repaint();
    }

    private void toggleSubMenuInventario() {
        subMenuInventarioVisible = !subMenuInventarioVisible;
        subMenuGestionInventario.setVisible(subMenuInventarioVisible);
        revalidate();
        repaint();
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
