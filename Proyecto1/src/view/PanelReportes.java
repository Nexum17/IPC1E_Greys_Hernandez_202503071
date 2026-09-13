package view;

import controller.ReporteHTML;
import controller.ArchivoUtil;

import javax.swing.*;
import java.awt.*;

public class PanelReportes extends JPanel {

    private JLabel etiquetaResultado;

    public PanelReportes() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(construirBotones(), BorderLayout.NORTH);
        add(construirResultado(), BorderLayout.CENTER);
    }

    private JPanel construirBotones() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 8, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Generar Reportes HTML"));

        JButton botonAnimales = new JButton("Reporte de Animales");
        botonAnimales.addActionListener(e -> ejecutar(ReporteHTML::generarReporteAnimales));

        JButton botonAdopciones = new JButton("Reporte de Adopciones");
        botonAdopciones.addActionListener(e -> ejecutar(ReporteHTML::generarReporteAdopciones));

        JButton botonOcupacion = new JButton("Reporte de Ocupación del Refugio");
        botonOcupacion.addActionListener(e -> ejecutar(ReporteHTML::generarReporteOcupacion));

        JButton botonBitacoraAcciones = new JButton("Bitácora de Acciones");
        botonBitacoraAcciones.addActionListener(e -> ejecutar(() -> ReporteHTML.generarReporteBitacora(true, false)));

        JButton botonBitacoraErrores = new JButton("Bitácora de Errores");
        botonBitacoraErrores.addActionListener(e -> ejecutar(() -> ReporteHTML.generarReporteBitacora(false, true)));

        JButton botonBitacoraCombinada = new JButton("Bitácora Combinada (Acciones + Errores)");
        botonBitacoraCombinada.addActionListener(e -> ejecutar(() -> ReporteHTML.generarReporteBitacora(true, true)));

        panel.add(botonAnimales);
        panel.add(botonAdopciones);
        panel.add(botonOcupacion);
        panel.add(botonBitacoraAcciones);
        panel.add(botonBitacoraErrores);
        panel.add(botonBitacoraCombinada);

        panel.add(construirSeparadorPersistencia());

        return panel;
    }

    private JPanel construirSeparadorPersistencia() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 8, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Persistencia"));

        JButton botonGuardarTodo = new JButton("Guardar todo en archivos");
        botonGuardarTodo.addActionListener(e -> guardarTodo());

        JButton botonCargarTodo = new JButton("Cargar todo desde archivos");
        botonCargarTodo.addActionListener(e -> cargarTodo());

        panel.add(botonGuardarTodo);
        panel.add(botonCargarTodo);
        return panel;
    }

    private JScrollPane construirResultado() {
        etiquetaResultado = new JLabel(" ", SwingConstants.CENTER);
        etiquetaResultado.setVerticalAlignment(SwingConstants.TOP);
        etiquetaResultado.setForeground(new Color(0, 90, 0));

        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.add(etiquetaResultado, BorderLayout.NORTH);
        return new JScrollPane(contenedor);
    }

    // Interfaz funcional mínima: "ejecuta esta operación y devuelve un mensaje"
    private interface Operacion {
        String ejecutar();
    }

    private void ejecutar(Operacion operacion) {
        String resultado = operacion.ejecutar();
        mostrarResultado(resultado);
    }

    private void guardarTodo() {
        StringBuilder resumen = new StringBuilder("<html>");
        resumen.append(ArchivoUtil.guardarAnimales()).append("<br>");
        resumen.append(ArchivoUtil.guardarAdoptantes()).append("<br>");
        resumen.append(ArchivoUtil.guardarSolicitudes()).append("<br>");
        resumen.append(ArchivoUtil.guardarRescates()).append("<br>");
        resumen.append(ArchivoUtil.guardarBitacoraAcciones()).append("<br>");
        resumen.append(ArchivoUtil.guardarBitacoraErrores()).append("<br>");
        resumen.append("</html>");
        etiquetaResultado.setText(resumen.toString());
    }

    private void cargarTodo() {
        StringBuilder resumen = new StringBuilder("<html>");
        resumen.append(ArchivoUtil.cargarAnimales()).append("<br>");
        resumen.append(ArchivoUtil.cargarAdoptantes()).append("<br>");
        resumen.append(ArchivoUtil.cargarSolicitudes()).append("<br>");
        resumen.append(ArchivoUtil.cargarRescates()).append("<br>");
        resumen.append("</html>");
        etiquetaResultado.setText(resumen.toString());
    }

    private void mostrarResultado(String resultado) {
        etiquetaResultado.setForeground(resultado.startsWith("OK")
                ? new Color(0, 130, 0)
                : Color.RED);
        etiquetaResultado.setText("<html>" + resultado.replaceFirst("^(OK|ERROR): ", "") + "</html>");
    }
}