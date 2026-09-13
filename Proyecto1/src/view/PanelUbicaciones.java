package view;

import controller.UbicacionControlador;
import model.Almacen;

import javax.swing.*;
import java.awt.*;

public class PanelUbicaciones extends JPanel {

    private UbicacionControlador ubicacionControlador;

    private JButton[][] botonesCelda;
    private JPanel panelMatriz;
    private JLabel etiquetaMensaje;

    public PanelUbicaciones(UbicacionControlador ubicacionControlador) {
        this.ubicacionControlador = ubicacionControlador;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(construirEncabezado(), BorderLayout.NORTH);
        add(construirMatriz(), BorderLayout.CENTER);
        add(construirLeyenda(), BorderLayout.SOUTH);

        refrescarMatriz();
    }

    private JPanel construirEncabezado() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel titulo = new JLabel("Ocupación del Refugio — clic en una celda para asignar o liberar",
                SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 14f));

        etiquetaMensaje = new JLabel(" ", SwingConstants.CENTER);
        etiquetaMensaje.setForeground(Color.RED);

        panel.add(titulo, BorderLayout.NORTH);
        panel.add(etiquetaMensaje, BorderLayout.SOUTH);
        return panel;
    }

    private JScrollPane construirMatriz() {
        // +1 columna para la etiqueta de zona a la izquierda
        panelMatriz = new JPanel(new GridLayout(Almacen.FILAS, Almacen.COLUMNAS + 1, 4, 4));
        botonesCelda = new JButton[Almacen.FILAS][Almacen.COLUMNAS];
        return new JScrollPane(panelMatriz);
    }

    private JPanel construirLeyenda() {
        JPanel panel = new JPanel(new FlowLayout());

        JLabel libre = new JLabel("  Libre  ");
        libre.setOpaque(true);
        libre.setBackground(new Color(210, 245, 210));

        JLabel ocupada = new JLabel("  Ocupada  ");
        ocupada.setOpaque(true);
        ocupada.setBackground(new Color(245, 210, 210));

        panel.add(new JLabel("Leyenda:"));
        panel.add(libre);
        panel.add(ocupada);
        return panel;
    }

    // Reconstruye toda la grilla de botones según el estado actual de
    // Almacen.ubicaciones
    private void refrescarMatriz() {
        panelMatriz.removeAll();

        for (int f = 0; f < Almacen.FILAS; f++) {
            panelMatriz.add(new JLabel(nombreZona(f), SwingConstants.CENTER));

            for (int c = 0; c < Almacen.COLUMNAS; c++) {
                String ocupante = Almacen.ubicaciones[f][c];
                JButton boton = new JButton(ocupante == null ? "Libre" : ocupante);
                boton.setBackground(ocupante == null
                        ? new Color(210, 245, 210)
                        : new Color(245, 210, 210));
                boton.setOpaque(true);

                final int fila = f;
                final int columna = c;
                boton.addActionListener(e -> alClickearCelda(fila, columna));

                botonesCelda[f][c] = boton;
                panelMatriz.add(boton);
            }
        }

        panelMatriz.revalidate();
        panelMatriz.repaint();
    }

    private void alClickearCelda(int fila, int columna) {
        String ocupante = Almacen.ubicaciones[fila][columna];

        if (ocupante == null) {
            String codigoAnimal = JOptionPane.showInputDialog(this,
                    "Celda [" + fila + "][" + columna + "] está libre.\nIngrese el código del animal a asignar:");

            if (codigoAnimal == null || codigoAnimal.trim().isEmpty())
                return; // canceló

            String resultado = ubicacionControlador.asignar(fila, columna, codigoAnimal.trim());
            mostrarResultado(resultado);

        } else {
            int confirmacion = JOptionPane.showConfirmDialog(this,
                    "La celda [" + fila + "][" + columna + "] tiene a " + ocupante + ".\n¿Liberar este espacio?",
                    "Confirmar liberación", JOptionPane.YES_NO_OPTION);

            if (confirmacion != JOptionPane.YES_OPTION)
                return;

            String resultado = ubicacionControlador.liberar(fila, columna);
            mostrarResultado(resultado);
        }

        refrescarMatriz();
    }

    private void mostrarResultado(String resultado) {
        if (resultado.startsWith("OK")) {
            etiquetaMensaje.setForeground(new Color(0, 130, 0));
        } else {
            etiquetaMensaje.setForeground(Color.RED);
        }
        etiquetaMensaje.setText(resultado.replaceFirst("^(OK|ERROR): ", ""));
    }

    private String nombreZona(int fila) {
        if (fila == 0)
            return "Zona Perros";
        if (fila == 1)
            return "Zona Gatos";
        return "Zona " + fila;
    }
}