package view;

import javax.swing.*;
import java.awt.*;

// Panel informativo requerido por el enunciado (sección 4.2): la interfaz
// principal debe incluir, además de los módulos operativos, los datos del
// estudiante. No tiene lógica de negocio, solo muestra información fija.
public class PanelEstudiante extends JPanel {

    private static final String NOMBRE = "Greys Hernández";
    private static final String CARNE = "202503071";
    private static final String CURSO = "Introducción a la Programación y Computación 1";
    private static final String SECCION = "E"; // ajustar si corresponde
    private static final String SEMESTRE = "Segundo Semestre 2026";

    public PanelEstudiante() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JPanel tarjeta = new JPanel(new GridBagLayout());
        tarjeta.setBorder(BorderFactory.createTitledBorder("Datos del Estudiante"));
        GridBagConstraints r = new GridBagConstraints();
        r.insets = new Insets(8, 12, 8, 12);
        r.anchor = GridBagConstraints.WEST;

        agregarFila(tarjeta, r, 0, "Nombre:", NOMBRE);
        agregarFila(tarjeta, r, 1, "Carné:", CARNE);
        agregarFila(tarjeta, r, 2, "Curso:", CURSO);
        agregarFila(tarjeta, r, 3, "Sección:", SECCION);
        agregarFila(tarjeta, r, 4, "Semestre:", SEMESTRE);
        agregarFila(tarjeta, r, 5, "Proyecto:", "Centro de Rescate Animal: Gestión de Refugio y Adopciones");

        GridBagConstraints centrado = new GridBagConstraints();
        add(tarjeta, centrado);
    }

    private void agregarFila(JPanel panel, GridBagConstraints r, int fila, String etiqueta, String valor) {
        r.gridx = 0;
        r.gridy = fila;
        JLabel labelEtiqueta = new JLabel(etiqueta);
        labelEtiqueta.setFont(labelEtiqueta.getFont().deriveFont(Font.BOLD));
        panel.add(labelEtiqueta, r);

        r.gridx = 1;
        panel.add(new JLabel(valor), r);
    }
}
