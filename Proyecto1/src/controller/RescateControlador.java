package controller;

import model.Almacen;
import model.Rescate;
import model.Animal;
import util.Validador;
import util.FechaUtil;

public class RescateControlador {

    private AnimalControlador animalControlador;

    public RescateControlador(AnimalControlador animalControlador) {
        this.animalControlador = animalControlador;
    }

    public String registrar(String codigo, String prioridad, String fechaReporte) {

        if (!Validador.esCodigo(codigo, "R")) {
            Bitacora.error("RESCATES", "VALIDACION", "Código '" + codigo + "' con formato inválido");
            return "ERROR: El código debe tener formato R-000";
        }

        if (buscarPorCodigo(codigo) != null) {
            Bitacora.error("RESCATES", "DUPLICADO", "Código " + codigo + " ya existe");
            return "ERROR: Ese código ya está registrado";
        }

        if (!Rescate.PRIORIDAD_ALTA.equals(prioridad) && !Rescate.PRIORIDAD_MEDIA.equals(prioridad)
                && !Rescate.PRIORIDAD_BAJA.equals(prioridad)) {
            Bitacora.error("RESCATES", "VALIDACION", "Prioridad '" + prioridad + "' no permitida");
            return "ERROR: Prioridad no permitida (ALTA, MEDIA o BAJA)";
        }

        if (!FechaUtil.esFechaValida(fechaReporte)) {
            Bitacora.error("RESCATES", "VALIDACION", "Fecha '" + fechaReporte + "' inválida");
            return "ERROR: Fecha inválida (formato dd/mm/aaaa)";
        }

        if (Almacen.cantidadRescates >= Rescate.MAX) {
            Bitacora.error("RESCATES", "CAPACIDAD", "Arreglo de rescates lleno");
            return "ERROR: Se alcanzó el máximo de rescates registrados";
        }

        Rescate nuevo = new Rescate(codigo, prioridad, fechaReporte);
        Almacen.rescates[Almacen.cantidadRescates] = nuevo;
        Almacen.cantidadRescates++;

        Bitacora.accion("RESCATES", "ALTA", "Rescate " + codigo + " registrado");
        return "OK: Rescate registrado correctamente";
    }

    // Atiende un caso: si no se indica animal existente, genera uno nuevo
    // reutilizando el consecutivo del rescate (R-009 -> A-009).
    public String atender(String codigoRescate, String codigoAnimalExistente,
            String especie, String edadTexto) {

        Rescate rescate = buscarPorCodigo(codigoRescate);
        if (rescate == null) {
            Bitacora.error("RESCATES", "VALIDACION", "Código " + codigoRescate + " no existe");
            return "ERROR: Rescate no encontrado";
        }

        if (!rescate.estaPendiente()) {
            Bitacora.error("RESCATES", "VALIDACION",
                    "Rescate " + codigoRescate + " no está PENDIENTE");
            return "ERROR: Ese rescate ya fue atendido";
        }

        String codigoAnimalFinal;

        boolean seVinculaAnimalExistente = Validador.noVacio(codigoAnimalExistente);

        if (seVinculaAnimalExistente) {
            Animal existente = animalControlador.buscarPorCodigo(codigoAnimalExistente);
            if (existente == null) {
                Bitacora.error("RESCATES", "VALIDACION",
                        "Animal " + codigoAnimalExistente + " no existe");
                return "ERROR: El animal indicado no está registrado";
            }
            codigoAnimalFinal = codigoAnimalExistente;

        } else {
            // Genera un animal nuevo reutilizando el consecutivo del rescate
            String consecutivo = codigoRescate.substring(codigoRescate.indexOf("-") + 1);
            codigoAnimalFinal = "A-" + consecutivo;

            String resultadoAlta = animalControlador.registrar(
                    codigoAnimalFinal, "Sin nombre", especie, edadTexto, Animal.CLINICO_TRATAMIENTO);

            if (resultadoAlta.startsWith("ERROR")) {
                Bitacora.error("RESCATES", "VALIDACION",
                        "No se pudo generar animal " + codigoAnimalFinal + ": " + resultadoAlta);
                return resultadoAlta;
            }
        }

        rescate.setCodigoAnimalVinculado(codigoAnimalFinal);
        rescate.setEstado(Rescate.ESTADO_ATENDIDO);

        Bitacora.accion("RESCATES", "ATENDER",
                "Rescate " + codigoRescate + " atendido, vinculado a " + codigoAnimalFinal);
        return "OK: Rescate atendido, animal " + codigoAnimalFinal;
    }

    public Rescate buscarPorCodigo(String codigo) {
        for (int i = 0; i < Almacen.cantidadRescates; i++) {
            if (Almacen.rescates[i].getCodigo().equalsIgnoreCase(codigo)) {
                return Almacen.rescates[i];
            }
        }
        return null;
    }

    // Reporte activo: ALTA primero, según pide el enunciado
    public Object[][] listarActivosPorPrioridad() {
        int contador = Almacen.cantidadRescates;
        Object[][] filas = new Object[contador][];

        int fila = 0;
        fila = agregarPorPrioridad(filas, fila, Rescate.PRIORIDAD_ALTA);
        fila = agregarPorPrioridad(filas, fila, Rescate.PRIORIDAD_MEDIA);
        fila = agregarPorPrioridad(filas, fila, Rescate.PRIORIDAD_BAJA);

        return filas;
    }

    private int agregarPorPrioridad(Object[][] filas, int filaInicial, String prioridad) {
        int fila = filaInicial;
        for (int i = 0; i < Almacen.cantidadRescates; i++) {
            if (Almacen.rescates[i].getPrioridad().equals(prioridad)) {
                filas[fila] = Almacen.rescates[i].aFilaTabla();
                fila++;
            }
        }
        return fila;
    }
}