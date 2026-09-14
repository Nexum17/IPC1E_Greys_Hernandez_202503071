package controller;

import model.Adoptante;//permiso al controlador de manipular objetos
import model.Almacen;//periso accedera la clase 
import util.Validador;//invoca funciones de misma clase parachequear campos

public class AdoptanteControlador {

    public String registrar(String codigo, String nombre, String dpi, String telefono) {

        if (!Validador.noVacio(codigo) || !Validador.noVacio(nombre)
                || !Validador.noVacio(dpi) || !Validador.noVacio(telefono)) {
            Bitacora.error("ADOPTANTES", "VALIDACION", "Campos obligatorios vacíos");
            return "ERROR: Complete todos los campos";
        }

        if (!Validador.esCodigo(codigo, "AD")) {
            Bitacora.error("ADOPTANTES", "VALIDACION", "Código '" + codigo + "' con formato inválido");
            return "ERROR: El código debe tener formato AD-000";
        }

        if (buscarPorCodigo(codigo) != null) {
            Bitacora.error("ADOPTANTES", "DUPLICADO", "Código " + codigo + " ya existe");
            return "ERROR: Ese código ya está registrado";
        }

        if (!Validador.esSoloLetras(nombre)) {
            Bitacora.error("ADOPTANTES", "VALIDACION", "Nombre '" + nombre + "' contiene caracteres inválidos");
            return "ERROR: El nombre solo debe contener letras y espacios";
        }

        if (!Validador.esDPI(dpi)) {
            Bitacora.error("ADOPTANTES", "VALIDACION", "DPI '" + dpi + "' con formato inválido");
            return "ERROR: El DPI debe tener 13 dígitos numéricos";
        }

        if (buscarPorDpi(dpi) != null) {
            Bitacora.error("ADOPTANTES", "DUPLICADO", "DPI " + dpi + " ya existe");
            return "ERROR: Ese DPI ya está registrado";
        }

        if (!Validador.esTelefono(telefono)) {
            Bitacora.error("ADOPTANTES", "VALIDACION", "Teléfono '" + telefono + "' con formato inválido");
            return "ERROR: El teléfono debe tener 8 dígitos";
        }

        if (Almacen.cantidadAdoptantes >= Adoptante.MAX) {
            Bitacora.error("ADOPTANTES", "CAPACIDAD", "Arreglo de adoptantes lleno");
            return "ERROR: Se alcanzó el máximo de adoptantes registrados";
        }

        Adoptante nuevo = new Adoptante(codigo, nombre, dpi, telefono);
        Almacen.adoptantes[Almacen.cantidadAdoptantes] = nuevo;
        Almacen.cantidadAdoptantes++;

        Bitacora.accion("ADOPTANTES", "ALTA", "Adoptante " + codigo + " registrado");
        return "OK: Adoptante registrado correctamente";
    }

    public Adoptante buscarPorCodigo(String codigo) {
        for (int i = 0; i < Almacen.cantidadAdoptantes; i++) {
            if (Almacen.adoptantes[i].getCodigo().equalsIgnoreCase(codigo)) {
                return Almacen.adoptantes[i];
            }
        }
        return null;
    }

    public Adoptante buscarPorDpi(String dpi) {
        for (int i = 0; i < Almacen.cantidadAdoptantes; i++) {
            if (Almacen.adoptantes[i].getDpi().equals(dpi)) {
                return Almacen.adoptantes[i];
            }
        }
        return null;
    }

    public String editar(String codigo, String nuevoNombre, String nuevoTelefono) {
        Adoptante adoptante = buscarPorCodigo(codigo);
        if (adoptante == null) {
            Bitacora.error("ADOPTANTES", "VALIDACION", "Código " + codigo + " no existe");
            return "ERROR: Adoptante no encontrado";
        }

        if (!Validador.esSoloLetras(nuevoNombre)) {
            Bitacora.error("ADOPTANTES", "VALIDACION", "Nombre '" + nuevoNombre + "' contiene caracteres inválidos");
            return "ERROR: El nombre solo debe contener letras y espacios";
        }

        if (!Validador.esTelefono(nuevoTelefono)) {
            Bitacora.error("ADOPTANTES", "VALIDACION", "Teléfono '" + nuevoTelefono + "' con formato inválido");
            return "ERROR: El teléfono debe tener 8 dígitos";
        }

        adoptante.setNombre(nuevoNombre);
        adoptante.setTelefono(nuevoTelefono);
        Bitacora.accion("ADOPTANTES", "EDITAR", "Adoptante " + codigo + " actualizado");
        return "OK: Adoptante actualizado correctamente";
    }

    public Object[][] listarTodos() {
        Object[][] filas = new Object[Almacen.cantidadAdoptantes][];
        for (int i = 0; i < Almacen.cantidadAdoptantes; i++) {
            filas[i] = Almacen.adoptantes[i].aFilaTabla();
        }
        return filas;
    }
}