package controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import model.Almacen;
import model.Evento;

public class Bitacora {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private static String usuarioActivo = "SISTEMA";

    public static void setUsuarioActivo(String usuario) {
        usuarioActivo = (usuario != null) ? usuario : "SISTEMA";
    }

    // Versión normal: usa el usuario de la sesión activa
    public static void accion(String modulo, String tipoEvento, String descripcion) {
        accion(usuarioActivo, modulo, tipoEvento, descripcion);
    }

    public static void error(String modulo, String tipoEvento, String motivo) {
        error(usuarioActivo, modulo, tipoEvento, motivo);
    }

    // Versión explícita: para casos donde aún no hay sesión (ej. login fallido)
    public static void accion(String usuario, String modulo, String tipoEvento, String descripcion) {
        if (Almacen.cantidadAcciones >= Evento.MAX)
            return;

        Evento evento = new Evento(fechaHoraActual(), usuario, modulo, tipoEvento, descripcion);
        Almacen.acciones[Almacen.cantidadAcciones] = evento;
        Almacen.cantidadAcciones++;
    }

    public static void error(String usuario, String modulo, String tipoEvento, String motivo) {
        if (Almacen.cantidadErrores >= Evento.MAX)
            return;

        Evento evento = new Evento(fechaHoraActual(), usuario, modulo, tipoEvento, motivo);
        Almacen.errores[Almacen.cantidadErrores] = evento;
        Almacen.cantidadErrores++;
    }

    private static String fechaHoraActual() {
        return LocalDateTime.now().format(FORMATO);
    }
}