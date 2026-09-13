package controller;

import model.Almacen;
import util.Validador;

public class UbicacionControlador {

    private AnimalControlador animalControlador;

    // Constructor por defecto
    public UbicacionControlador() {
    }

    // Permite conectar el controlador de animales después de instanciarlo
    public void setAnimalControlador(AnimalControlador animalControlador) {
        this.animalControlador = animalControlador;
    }

    public String asignar(int fila, int columna, String codigoAnimal) {

        if (!celdaValida(fila, columna)) {
            Bitacora.error("UBICACIONES", "VALIDACION",
                    "Celda [" + fila + "][" + columna + "] fuera de rango");
            return "ERROR: Celda fuera de rango";
        }

        // Validación 1: Verificar el formato del código (debe ser A-...)
        if (!Validador.esCodigo(codigoAnimal, "A")) {
            Bitacora.error("UBICACIONES", "VALIDACION",
                    "Código '" + codigoAnimal + "' con formato inválido");
            return "ERROR: El código debe tener formato A-000";
        }

        // Validación 2: Verificar que el animal exista registrado en el almacén
        if (animalControlador != null && animalControlador.buscarPorCodigo(codigoAnimal) == null) {
            Bitacora.error("UBICACIONES", "VALIDACION",
                    "Animal " + codigoAnimal + " no existe");
            return "ERROR: Ese animal no está registrado";
        }

        if (Almacen.ubicaciones[fila][columna] != null) {
            Bitacora.error("UBICACIONES", "CAPACIDAD",
                    "Celda [" + fila + "][" + columna + "] ya ocupada por "
                            + Almacen.ubicaciones[fila][columna]);
            return "ERROR: Esa celda ya está ocupada";
        }

        if (buscarCeldaDeAnimal(codigoAnimal) != null) {
            Bitacora.error("UBICACIONES", "VALIDACION",
                    "Animal " + codigoAnimal + " ya tiene una ubicación asignada");
            return "ERROR: Ese animal ya está ubicado en otra celda";
        }

        Almacen.ubicaciones[fila][columna] = codigoAnimal;
        Bitacora.accion("UBICACIONES", "ASIGNAR",
                codigoAnimal + " asignado a [" + fila + "][" + columna + "]");
        return "OK: Animal asignado correctamente";
    }

    public String liberar(int fila, int columna) {

        if (!celdaValida(fila, columna)) {
            Bitacora.error("UBICACIONES", "VALIDACION",
                    "Celda [" + fila + "][" + columna + "] fuera de rango");
            return "ERROR: Celda fuera de rango";
        }

        if (Almacen.ubicaciones[fila][columna] == null) {
            Bitacora.error("UBICACIONES", "VALIDACION",
                    "Celda [" + fila + "][" + columna + "] ya estaba vacía");
            return "ERROR: Esa celda ya está libre";
        }

        String codigoLiberado = Almacen.ubicaciones[fila][columna];
        Almacen.ubicaciones[fila][columna] = null;
        Bitacora.accion("UBICACIONES", "LIBERAR",
                codigoLiberado + " liberado de [" + fila + "][" + columna + "]");
        return "OK: Celda liberada correctamente";
    }

    // Usado por AnimalControlador.eliminar(...) para liberar automáticamente
    // la celda de un animal que pasa a ELIMINADO, sin exigir fila/columna manual.
    public void liberarPorAnimal(String codigoAnimal) {
        int[] celda = buscarCeldaDeAnimal(codigoAnimal);
        if (celda == null)
            return; // el animal no ocupaba ninguna celda, no hay nada que hacer

        int fila = celda[0];
        int columna = celda[1];
        Almacen.ubicaciones[fila][columna] = null;

        Bitacora.accion("UBICACIONES", "LIBERAR",
                codigoAnimal + " liberado automáticamente de [" + fila + "][" + columna
                        + "] por baja lógica");
    }

    // Recorre la matriz buscando en qué celda está un animal. null si no está
    // ubicado.
    public int[] buscarCeldaDeAnimal(String codigoAnimal) {
        for (int f = 0; f < Almacen.FILAS; f++) {
            for (int c = 0; c < Almacen.COLUMNAS; c++) {
                if (codigoAnimal.equals(Almacen.ubicaciones[f][c])) {
                    return new int[] { f, c };
                }
            }
        }
        return null;
    }

    // Filas y columnas disponibles (celda == null) para mostrar disponibilidad
    public Object[][] listarDisponibilidad() {
        Object[][] filas = new Object[Almacen.FILAS][Almacen.COLUMNAS + 1];

        for (int f = 0; f < Almacen.FILAS; f++) {
            filas[f][0] = nombreZona(f);
            for (int c = 0; c < Almacen.COLUMNAS; c++) {
                String ocupante = Almacen.ubicaciones[f][c];
                filas[f][c + 1] = (ocupante == null) ? "Libre" : ocupante;
            }
        }
        return filas;
    }

    private String nombreZona(int fila) {
        if (fila == 0)
            return "Zona Perros";
        if (fila == 1)
            return "Zona Gatos";
        return "Zona " + fila;
    }

    private boolean celdaValida(int fila, int columna) {
        return fila >= 0 && fila < Almacen.FILAS && columna >= 0 && columna < Almacen.COLUMNAS;
    }
}