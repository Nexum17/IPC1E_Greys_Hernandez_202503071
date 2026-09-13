package controller;

import java.io.FileWriter;
import java.io.IOException;
import model.Almacen;
import model.Animal;
import model.Solicitud;
import util.FechaUtil;

public class ReporteHTML {

    private static final String CARPETA = "reportes";

    public static String generarReporteAnimales() {
        StringBuilder html = new StringBuilder();
        abrirDocumento(html, "Reporte de Animales");

        html.append("<h1>Reporte de Animales</h1>");
        html.append("<table><tr><th>Código</th><th>Nombre</th><th>Especie</th>")
                .append("<th>Edad</th><th>Estado Clínico</th><th>Estado Adopción</th></tr>");

        for (int i = 0; i < Almacen.cantidadAnimales; i++) {
            Animal a = Almacen.animales[i];
            if (!a.estaActivo())
                continue; // no aparecen en reportes activos

            html.append("<tr>")
                    .append("<td>").append(a.getCodigo()).append("</td>")
                    .append("<td>").append(a.getNombre()).append("</td>")
                    .append("<td>").append(a.getEspecie()).append("</td>")
                    .append("<td>").append(a.getEdadEstimada()).append("</td>")
                    .append("<td>").append(a.getEstadoClinico()).append("</td>")
                    .append("<td>").append(a.getEstadoAdopcion()).append("</td>")
                    .append("</tr>");
        }

        html.append("</table>");
        cerrarDocumento(html);

        return escribirArchivo("reporte_animales", html.toString());
    }

    public static String generarReporteAdopciones() {
        StringBuilder html = new StringBuilder();
        abrirDocumento(html, "Reporte de Adopciones");

        html.append("<h1>Reporte de Adopciones Completadas</h1>");
        html.append("<table><tr><th>Solicitud</th><th>Animal</th><th>Adoptante</th>")
                .append("<th>Fecha</th><th>Estado</th></tr>");

        for (int i = 0; i < Almacen.cantidadSolicitudes; i++) {
            Solicitud s = Almacen.solicitudes[i];
            if (!Solicitud.ESTADO_APROBADA.equals(s.getEstado())
                    && !Solicitud.ESTADO_COMPLETADA.equals(s.getEstado()))
                continue;

            html.append("<tr>")
                    .append("<td>").append(s.getCodigo()).append("</td>")
                    .append("<td>").append(s.getCodigoAnimal()).append("</td>")
                    .append("<td>").append(s.getCodigoAdoptante()).append("</td>")
                    .append("<td>").append(s.getFecha()).append("</td>")
                    .append("<td>").append(s.getEstado()).append("</td>")
                    .append("</tr>");
        }

        html.append("</table>");
        cerrarDocumento(html);

        return escribirArchivo("reporte_adopciones", html.toString());
    }

    public static String generarReporteOcupacion() {
        StringBuilder html = new StringBuilder();
        abrirDocumento(html, "Reporte de Ocupación del Refugio");

        html.append("<h1>Ocupación del Refugio</h1>");
        html.append("<table><tr><th>Zona</th>");
        for (int c = 0; c < Almacen.COLUMNAS; c++) {
            html.append("<th>Jaula ").append(c).append("</th>");
        }
        html.append("</tr>");

        for (int f = 0; f < Almacen.FILAS; f++) {
            html.append("<tr><td>").append(f == 0 ? "Zona Perros" : "Zona Gatos").append("</td>");
            for (int c = 0; c < Almacen.COLUMNAS; c++) {
                String ocupante = Almacen.ubicaciones[f][c];
                html.append("<td>").append(ocupante == null ? "Libre" : ocupante).append("</td>");
            }
            html.append("</tr>");
        }

        html.append("</table>");
        cerrarDocumento(html);

        return escribirArchivo("reporte_ocupacion", html.toString());
    }

    public static String generarReporteBitacora(boolean incluirAcciones, boolean incluirErrores) {
        StringBuilder html = new StringBuilder();
        abrirDocumento(html, "Reporte de Bitácora");

        html.append("<h1>Bitácora del Sistema</h1>");
        html.append("<table><tr><th>Fecha/Hora</th><th>Usuario</th><th>Módulo</th>")
                .append("<th>Tipo</th><th>Descripción</th></tr>");

        if (incluirAcciones) {
            for (int i = 0; i < Almacen.cantidadAcciones; i++) {
                agregarFilaEvento(html, Almacen.acciones[i], "accion");
            }
        }
        if (incluirErrores) {
            for (int i = 0; i < Almacen.cantidadErrores; i++) {
                agregarFilaEvento(html, Almacen.errores[i], "error");
            }
        }

        html.append("</table>");
        cerrarDocumento(html);

        return escribirArchivo("reporte_bitacora", html.toString());
    }

    private static void agregarFilaEvento(StringBuilder html, model.Evento e, String claseCss) {
        html.append("<tr class=\"").append(claseCss).append("\">")
                .append("<td>").append(e.getFechaHora()).append("</td>")
                .append("<td>").append(e.getUsuario()).append("</td>")
                .append("<td>").append(e.getModulo()).append("</td>")
                .append("<td>").append(e.getTipoEvento()).append("</td>")
                .append("<td>").append(e.getDescripcion()).append("</td>")
                .append("</tr>");
    }

    // ---------- estructura común del documento ----------

    private static void abrirDocumento(StringBuilder html, String titulo) {
        html.append("<!DOCTYPE html><html><head><meta charset=\"UTF-8\">")
                .append("<title>").append(titulo).append("</title>")
                .append("<style>")
                .append("body{font-family:Arial,sans-serif;margin:30px;}")
                .append("table{border-collapse:collapse;width:100%;}")
                .append("th,td{border:1px solid #ccc;padding:8px;text-align:left;}")
                .append("th{background-color:#2155a8;color:white;}")
                .append(".error{background-color:#fdd;}")
                .append(".accion{background-color:#dfd;}")
                .append("</style></head><body>");
    }

    private static void cerrarDocumento(StringBuilder html) {
        html.append("<p>Generado: ").append(FechaUtil.fechaActual()).append("</p>");
        html.append("</body></html>");
    }

    // ---------- escritura a disco ----------
    private static String escribirArchivo(String nombreBase, String contenidoHtml) {
        crearCarpetaSiNoExiste();

        String marcaTiempo = FechaUtil.fechaHoraParaArchivo();
        String ruta = CARPETA + "/" + nombreBase + "_" + marcaTiempo + ".html";

        try (FileWriter escritor = new FileWriter(ruta)) {
            escritor.write(contenidoHtml);
            return "OK: Reporte generado en " + ruta;
        } catch (IOException e) {
            return "ERROR: No se pudo generar el reporte";
        }
    }
    

    private static void crearCarpetaSiNoExiste() {
        java.io.File carpeta = new java.io.File(CARPETA);
        if (!carpeta.exists())
            carpeta.mkdirs();
    }
}