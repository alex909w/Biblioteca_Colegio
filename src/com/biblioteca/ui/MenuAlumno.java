package com.biblioteca.ui;

import com.biblioteca.bd.ConexionBaseDatos;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

public class MenuAlumno extends JFrame {
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private final Color primaryColor = new Color(51, 102, 153); // Azul principal
    private final Color accentColor = new Color(144, 202, 249); // Azul claro
    private final Color backgroundColor = new Color(250, 250, 250); // Fondo gris claro
    private final Color textColor = new Color(33, 33, 33); // Texto negro
    private final Color FONDO_LATERAL = new Color(248, 249, 250);
    private final Font FUENTE_PRINCIPAL = new Font("Segoe UI", Font.PLAIN, 14);

    public MenuAlumno() {
        setTitle("Biblioteca Colegio Amigos De Don Bosco");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(backgroundColor);

        // Panel superior con logo y título
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Panel de navegación lateral
        JPanel sidePanel = createSidePanel();
        add(sidePanel, BorderLayout.WEST);

        // Panel principal con CardLayout
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(backgroundColor);

        // Paneles del contenido
        cardPanel.add(createHomePanel(), "home");
        cardPanel.add(createSearchPanel(), "search");
        cardPanel.add(createLoansPanel(), "loans");

        add(cardPanel, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(primaryColor);
        headerPanel.setPreferredSize(new Dimension(getWidth(), 60));

        JLabel titleLabel = new JLabel("Sistema de Biblioteca", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        headerPanel.add(titleLabel, BorderLayout.CENTER);
        return headerPanel;
    }

    private JPanel createSidePanel() {
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setBackground(Color.WHITE);
        sidePanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, accentColor));
        sidePanel.setPreferredSize(new Dimension(200, getHeight()));

        addNavigationButton(sidePanel, "Inicio", "home", "icons/home.png");
        addNavigationButton(sidePanel, "Buscar Libros", "search", "icons/search.png");
        addNavigationButton(sidePanel, "Mis Préstamos", "loans", "icons/book.png");

        sidePanel.add(Box.createVerticalGlue());

        JButton btnSalir = createStyledButton("Cerrar Sesión", "icons/logout.png");
        btnSalir.addActionListener(e -> {
            ConexionBaseDatos.cerrarConexion();
            System.exit(0);
        });
        sidePanel.add(btnSalir);
        sidePanel.add(Box.createVerticalStrut(20));

        return sidePanel;
    }

    private void addNavigationButton(JPanel panel, String text, String cardName, String icono) {
        JButton button = createStyledButton(text, icono);
        button.addActionListener(e -> cardLayout.show(cardPanel, cardName));
        panel.add(button);
        panel.add(Box.createVerticalStrut(10));
    }

    private JButton createStyledButton(String texto, String icono) {
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
            public void mouseEntered(MouseEvent e) {
                boton.setBackground(accentColor);
                boton.setForeground(Color.WHITE);
            }

            public void mouseExited(MouseEvent e) {
                boton.setBackground(Color.WHITE);
                boton.setForeground(textColor);
            }
        });

        return boton;
    }

    private JPanel createHomePanel() {
        JPanel homePanel = new JPanel(new GridBagLayout());
        homePanel.setBackground(backgroundColor);
        JLabel welcomeLabel = new JLabel("¡Bienvenido al Sistema de Biblioteca!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeLabel.setForeground(primaryColor);
        homePanel.add(welcomeLabel);
        return homePanel;
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new BorderLayout(20, 20));
        searchPanel.setBackground(backgroundColor);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField searchField = new JTextField();
        JButton searchButton = new JButton("Buscar");
        searchButton.setBackground(primaryColor);
        searchButton.setForeground(Color.WHITE);

        JPanel searchControls = new JPanel(new BorderLayout(10, 0));
        searchControls.add(searchField, BorderLayout.CENTER);
        searchControls.add(searchButton, BorderLayout.EAST);
        searchPanel.add(searchControls, BorderLayout.NORTH);

        String[] columnas = {"ID", "Título", "Autor", "Estado"};
        JTable tabla = new JTable(new DefaultTableModel(new Object[][]{}, columnas));
        tabla.setFillsViewportHeight(true);
        tabla.setRowHeight(25);
        tabla.getTableHeader().setBackground(primaryColor);
        tabla.getTableHeader().setForeground(Color.WHITE);
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setBorder(BorderFactory.createLineBorder(accentColor, 1));
        searchPanel.add(scrollPane, BorderLayout.CENTER);

        return searchPanel;
    }

    private JPanel createLoansPanel() {
        JPanel loansPanel = new JPanel(new BorderLayout(20, 20));
        loansPanel.setBackground(backgroundColor);
        loansPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String[] columnas = {"ID Préstamo", "Libro", "Fecha Préstamo", "Fecha Devolución", "Estado"};
        JTable tabla = new JTable(new DefaultTableModel(new Object[][]{}, columnas));
        tabla.setFillsViewportHeight(true);
        tabla.setRowHeight(25);
        tabla.getTableHeader().setBackground(primaryColor);
        tabla.getTableHeader().setForeground(Color.WHITE);
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(tabla);
        loansPanel.add(scrollPane, BorderLayout.CENTER);

        return loansPanel;
    }

    private static class RoundedBorder extends AbstractBorder {
        private int radius;

        public RoundedBorder(int radius) {
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            g.setColor(Color.LIGHT_GRAY);
            g.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MenuAlumno().setVisible(true));
    }
}
