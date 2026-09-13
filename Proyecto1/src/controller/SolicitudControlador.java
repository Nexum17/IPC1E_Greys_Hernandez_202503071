package controller;

import model.Almacen;
import model.Solicitud;
import model.Animal;
import util.Validador;
import util.FechaUtil;

public class SolicitudControlador {

    private AnimalControlador animalControlador;
    private AdoptanteControlador adoptanteControlador;

    public SolicitudControlador(AnimalControlador animalControlador,
            AdoptanteControlador adoptanteControlador) {
        this.animalControlador = animalControlador;
        this.adoptanteControlador = adoptanteControlador;
    }

    public String registrar(String codigo, String codigoAnimal, String codigoAdoptante, String fecha) {

        if (!Validador.esCodigo(codigo, "S")) {
            Bitacora.error("SOLICITUDES", "VALIDACION", "Código '" + codigo + "' con formato inválido");
            return "ERROR: El código debe tener formato S-000";
        }

        if (buscarPorCodigo(codigo) != null) {
            Bitacora.error("SOLICITUDES", "DUPLICADO", "Código " + codigo + " ya existe");
            return "ERROR: Ese código ya está registrado";
        }

        if (!FechaUtil.esFechaValida(fecha)) {
            Bitacora.error("SOLICITUDES", "VALIDACION", "Fecha '" + fecha + "' inválida");
            return "ERROR: Fecha inválida (formato dd/mm/aaaa)";
        }

        Animal animal = animalControlador.buscarPorCodigo(codigoAnimal);
        if (animal == null || !animal.estaActivo()) {
            Bitacora.error("SOLICITUDES", "VALIDACION", "Animal " + codigoAnimal + " no existe");
            return "ERROR: El animal no está registrado";
        }

        if (adoptanteControlador.buscarPorCodigo(codigoAdoptante) == null) {
            Bitacora.error("SOLICITUDES", "VALIDACION", "Adoptante " + codigoAdoptante + " no existe");
            return "ERROR: El adoptante no está registrado";
        }

        if (!Animal.ADOPCION_DISPONIBLE.equals(animal.getEstadoAdopcion())) {
            Bitacora.error("SOLICITUDES", "VALIDACION",
                    "Animal " + codigoAnimal + " no está DISPONIBLE");
            return "ERROR: El animal no está disponible para adopción";
        }

        if (Almacen.cantidadSolicitudes >= Solicitud.MAX) {
            Bitacora.error("SOLICITUDES", "CAPACIDAD", "Arreglo de solicitudes lleno");
            return "ERROR: Se alcanzó el máximo de solicitudes registradas";
        }

        Solicitud nueva = new Solicitud(codigo, codigoAnimal, codigoAdoptante, fecha);
        Almacen.solicitudes[Almacen.cantidadSolicitudes] = nueva;
        Almacen.cantidadSolicitudes++;

        Bitacora.accion("SOLICITUDES", "ALTA", "Solicitud " + codigo + " registrada");
        return "OK: Solicitud registrada correctamente";
    }

    public String aprobar(String codigo) {
        Solicitud solicitud = buscarPorCodigo(codigo);
        if (solicitud == null) {
            Bitacora.error("SOLICITUDES", "VALIDACION", "Código " + codigo + " no existe");
            return "ERROR: Solicitud no encontrada";
        }

        if (!solicitud.estaPendiente()) {
            Bitacora.error("SOLICITUDES", "VALIDACION",
                    "Solicitud " + codigo + " no está PENDIENTE");
            return "ERROR: Solo se pueden aprobar solicitudes pendientes";
        }

        Animal animal = animalControlador.buscarPorCodigo(solicitud.getCodigoAnimal());
        if (animal == null) {
            Bitacora.error("SOLICITUDES", "VALIDACION",
                    "Animal " + solicitud.getCodigoAnimal() + " ya no existe");
            return "ERROR: El animal vinculado ya no existe";
        }

        // Aprobar esta solicitud
        solicitud.setEstado(Solicitud.ESTADO_APROBADA);
        animal.setEstadoAdopcion(Animal.ADOPCION_ADOPTADO);

        // Regla: cualquier otra solicitud PENDIENTE del mismo animal se rechaza
        // automáticamente
        for (int i = 0; i < Almacen.cantidadSolicitudes; i++) {
            Solicitud otra = Almacen.solicitudes[i];
            boolean esOtraDelMismoAnimal = otra.getCodigoAnimal().equals(solicitud.getCodigoAnimal());
            boolean esDistintaSolicitud = !otra.getCodigo().equals(codigo);

            if (esOtraDelMismoAnimal && esDistintaSolicitud && otra.estaPendiente()) {
                otra.setEstado(Solicitud.ESTADO_RECHAZADA);
                Bitacora.accion("SOLICITUDES", "RECHAZAR_AUTOMATICO",
                        "Solicitud " + otra.getCodigo() + " rechazada automáticamente "
                                + "(animal " + solicitud.getCodigoAnimal() + " ya fue adoptado)");
            }
        }

        Bitacora.accion("SOLICITUDES", "APROBAR",
                "Solicitud " + codigo + " aprobada, " + solicitud.getCodigoAnimal() + " pasa a ADOPTADO");
        return "OK: Solicitud aprobada";
    }

    public String rechazar(String codigo) {
        Solicitud solicitud = buscarPorCodigo(codigo);
        if (solicitud == null) {
            Bitacora.error("SOLICITUDES", "VALIDACION", "Código " + codigo + " no existe");
            return "ERROR: Solicitud no encontrada";
        }

        if (!solicitud.estaPendiente()) {
            Bitacora.error("SOLICITUDES", "VALIDACION",
                    "Solicitud " + codigo + " no está PENDIENTE");
            return "ERROR: Solo se pueden rechazar solicitudes pendientes";
        }

        solicitud.setEstado(Solicitud.ESTADO_RECHAZADA);
        Bitacora.accion("SOLICITUDES", "RECHAZAR", "Solicitud " + codigo + " rechazada");
        return "OK: Solicitud rechazada";
    }

    public Solicitud buscarPorCodigo(String codigo) {
        for (int i = 0; i < Almacen.cantidadSolicitudes; i++) {
            if (Almacen.solicitudes[i].getCodigo().equalsIgnoreCase(codigo)) {
                return Almacen.solicitudes[i];
            }
        }
        return null;
    }

    public Object[][] listarPendientes() {
        return listarPorEstado(Solicitud.ESTADO_PENDIENTE);
    }

    public Object[][] listarHistorial() {
        Object[][] filas = new Object[Almacen.cantidadSolicitudes][];
        for (int i = 0; i < Almacen.cantidadSolicitudes; i++) {
            filas[i] = Almacen.solicitudes[i].aFilaTabla();
        }
        return filas;
    }

    private Object[][] listarPorEstado(String estado) {
        int contador = 0;
        for (int i = 0; i < Almacen.cantidadSolicitudes; i++) {
            if (Almacen.solicitudes[i].getEstado().equals(estado))
                contador++;
        }

        Object[][] filas = new Object[contador][];
        int fila = 0;
        for (int i = 0; i < Almacen.cantidadSolicitudes; i++) {
            if (Almacen.solicitudes[i].getEstado().equals(estado)) {
                filas[fila] = Almacen.solicitudes[i].aFilaTabla();
                fila++;
            }
        }
        return filas;
    }
}