package controller;

import model.Almacen;
import model.Animal;
import util.FechaUtil;
import util.Validador;

public class AnimalControlador {

    private UbicacionControlador ubicacionControlador;

    public AnimalControlador(UbicacionControlador ubicacionControlador) {
        this.ubicacionControlador = ubicacionControlador;
    }

    public String registrar(String codigo, String nombre, String especie,
            String edadTexto, String estadoClinico) {

        if (codigo == null || codigo.isEmpty() || nombre == null || nombre.isEmpty()) {
            Bitacora.error("ANIMALES", "VALIDACION", "Campos obligatorios vacíos");
            return "ERROR: Complete todos los campos";
        }

        if (!Validador.esCodigo(codigo, "A")) {
            Bitacora.error("ANIMALES", "VALIDACION", "Código '" + codigo + "' con formato inválido");
            return "ERROR: El código debe tener formato A-000";
        }

        if (existeCodigo(codigo)) {
            Bitacora.error("ANIMALES", "DUPLICADO", "Código " + codigo + " ya existe");
            return "ERROR: Ese código ya está registrado";
        }

        if (!especie.equals(Animal.ESPECIE_PERRO) && !especie.equals(Animal.ESPECIE_GATO)) {
            Bitacora.error("ANIMALES", "VALIDACION", "Especie '" + especie + "' no permitida");
            return "ERROR: Especie no permitida (solo Perro o Gato)";
        }

        int edad;
        try {
            edad = Integer.parseInt(edadTexto);
        } catch (NumberFormatException e) {
            Bitacora.error("ANIMALES", "VALIDACION", "Edad '" + edadTexto + "' no es un número");
            return "ERROR: La edad debe ser un número";
        }

        if (!Validador.enRango(edad, Animal.EDAD_MINIMA, Animal.EDAD_MAXIMA)) {
            Bitacora.error("ANIMALES", "VALIDACION", "Edad " + edad + " fuera de rango");
            return "ERROR: La edad debe estar entre " + Animal.EDAD_MINIMA + " y " + Animal.EDAD_MAXIMA;
        }

        if (Almacen.cantidadAnimales >= Animal.MAX) {
            Bitacora.error("ANIMALES", "CAPACIDAD", "Arreglo de animales lleno");
            return "ERROR: Se alcanzó el máximo de animales registrados";
        }

        String fechaHoy = FechaUtil.fechaActual();
        Animal nuevo = new Animal(codigo, nombre, especie, edad, estadoClinico, fechaHoy);
        Almacen.animales[Almacen.cantidadAnimales] = nuevo;
        Almacen.cantidadAnimales++;

        Bitacora.accion("ANIMALES", "ALTA", "Animal " + codigo + " registrado");
        return "OK: Animal registrado correctamente";
    }

    public Animal buscarPorCodigo(String codigo) {
        for (int i = 0; i < Almacen.cantidadAnimales; i++) {
            if (Almacen.animales[i].getCodigo().equalsIgnoreCase(codigo)) {
                return Almacen.animales[i];
            }
        }
        return null;
    }

    public boolean existeCodigo(String codigo) {
        return buscarPorCodigo(codigo) != null;
    }

    public String editarEstadoClinico(String codigo, String nuevoEstado) {
        Animal animal = buscarPorCodigo(codigo);
        if (animal == null) {
            Bitacora.error("ANIMALES", "VALIDACION", "Código " + codigo + " no existe");
            return "ERROR: Animal no encontrado";
        }

        String anterior = animal.getEstadoClinico();
        animal.setEstadoClinico(nuevoEstado);
        Bitacora.accion("ANIMALES", "EDITAR",
                "Animal " + codigo + " cambia de " + anterior + " a " + nuevoEstado);
        return "OK: Estado clínico actualizado";
    }

    // Baja lógica: NO elimina del arreglo, solo cambia estadoAdopcion
    public String eliminar(String codigo) {
        Animal animal = buscarPorCodigo(codigo);
        if (animal == null) {
            Bitacora.error("ANIMALES", "VALIDACION", "Código " + codigo + " no existe");
            return "ERROR: Animal no encontrado";
        }

        if (!animal.estaActivo()) {
            Bitacora.error("ANIMALES", "VALIDACION", "Código " + codigo + " ya estaba eliminado");
            return "ERROR: Ese animal ya fue eliminado";
        }

        animal.setEstadoAdopcion(Animal.ADOPCION_ELIMINADO);
        ubicacionControlador.liberarPorAnimal(codigo);
        Bitacora.accion("ANIMALES", "BAJA", "Animal " + codigo + " marcado como ELIMINADO");
        return "OK: Animal eliminado (baja lógica)";
    }

    // Solo animales activos, para listados y reportes
    public Object[][] listarActivos() {
        int contador = 0;
        for (int i = 0; i < Almacen.cantidadAnimales; i++) {
            if (Almacen.animales[i].estaActivo())
                contador++;
        }

        Object[][] filas = new Object[contador][];
        int fila = 0;
        for (int i = 0; i < Almacen.cantidadAnimales; i++) {
            if (Almacen.animales[i].estaActivo()) {
                filas[fila] = Almacen.animales[i].aFilaTabla();
                fila++;
            }
        }
        return filas;
    }
}