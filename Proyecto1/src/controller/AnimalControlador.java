package controller;

import model.Almacen;
import model.Animal;
import util.FechaUtil;
import util.Validador;

public class AnimalControlador {

    private UbicacionControlador ubicacionControlador;

    public AnimalControlador(UbicacionControlador ubicacionControlador) {
        this.ubicacionControlador = ubicacionControlador;
    }//recibe ref. de ubicacioncontrol en el constructor→perimite comunicar con em model para asignar/verificar espacios

    public String registrar(String codigo, String nombre, String especie,
            String edadTexto, String estadoClinico) {
// Si cualquiera de las dos cosas ocurre con codigo O con nombre, se activa la alerta de campo obligatorio↓.
        if (codigo == null || codigo.isEmpty() || nombre == null || nombre.isEmpty()) {
            Bitacora.error("ANIMALES", "VALIDACION", "Campos obligatorios vacíos");
            return "ERROR: Complete todos los campos";
        }
//exige formato de codigo
        if (!Validador.esCodigo(codigo, "A")) {
            Bitacora.error("ANIMALES", "VALIDACION", "Código '" + codigo + "' con formato inválido");
            return "ERROR: El código debe tener formato A-000";
        }
//busca que no este repetido
        if (existeCodigo(codigo)) {
            Bitacora.error("ANIMALES", "DUPLICADO", "Código " + codigo + " ya existe");
            return "ERROR: Ese código ya está registrado";
        }

        if (!especie.equalsIgnoreCase(Animal.ESPECIE_PERRO) && !especie.equalsIgnoreCase(Animal.ESPECIE_GATO)) {
            Bitacora.error("ANIMALES", "VALIDACION", "Especie '" + especie + "' no permitida");
            return "ERROR: Especie no permitida (solo Perro o Gato)";
        }
//normaliza a "Perro"/"Gato" sin importar cómo lo haya escrito el usuario, para
//que la tabla y los reportes siempre muestren el mismo formato
        especie = especie.equalsIgnoreCase(Animal.ESPECIE_PERRO) ? Animal.ESPECIE_PERRO : Animal.ESPECIE_GATO;

//exige que el estado clínico sea uno de los 3 valores del dominio permitido
        if (!estadoClinico.equals(Animal.CLINICO_OBSERVACION)
                && !estadoClinico.equals(Animal.CLINICO_TRATAMIENTO)
                && !estadoClinico.equals(Animal.CLINICO_APTO)) {
            Bitacora.error("ANIMALES", "VALIDACION", "Estado clínico '" + estadoClinico + "' no permitido");
            return "ERROR: Estado clínico no permitido (EN_OBSERVACION, EN_TRATAMIENTO o APTO)";
        }
     //verifica edad→#.
        int edad;
        try {
            edad = Integer.parseInt(edadTexto);
        } catch (NumberFormatException e) {
            Bitacora.error("ANIMALES", "VALIDACION", "Edad '" + edadTexto + "' no es un número");
            return "ERROR: La edad debe ser un número";
        }
   //rando edad 0-25
        if (!Validador.enRango(edad, Animal.EDAD_MINIMA, Animal.EDAD_MAXIMA)) {
            Bitacora.error("ANIMALES", "VALIDACION", "Edad " + edad + " fuera de rango");
            return "ERROR: La edad debe estar entre " + Animal.EDAD_MINIMA + " y " + Animal.EDAD_MAXIMA;
        }
//almacena y verifica limite/capacidad de animales
        if (Almacen.cantidadAnimales >= Animal.MAX) {
            Bitacora.error("ANIMALES", "CAPACIDAD", "Arreglo de animales lleno");
            return "ERROR: Se alcanzó el máximo de animales registrados";
        }

        String fechaHoy = FechaUtil.fechaActual();//obtieve fecha
        Animal nuevo = new Animal(codigo, nombre, especie, edad, estadoClinico, fechaHoy);//emparenta nwe animal
        Almacen.animales[Almacen.cantidadAnimales] = nuevo;
        Almacen.cantidadAnimales++;//guarda entidad→incrementa contador de animales

        Bitacora.accion("ANIMALES", "ALTA", "Animal " + codigo + " registrado");
        return "OK: Animal registrado correctamente";
    }
   //recorre posiciones 0-almacen.cantanimales→si encuentraretorna a objeto/si no→retorna null
    public Animal buscarPorCodigo(String codigo) {
        for (int i = 0; i < Almacen.cantidadAnimales; i++) {
            if (Almacen.animales[i].getCodigo().equalsIgnoreCase(codigo)) {
                return Almacen.animales[i];
            }
        }
        return null;
    }
    //validaciones de metod. registrar tal que si metodo de arriba encuentra algo→el codigo ya existe(true)
    public boolean existeCodigo(String codigo) {
        return buscarPorCodigo(codigo) != null;
    }
// Esto permite registrar en la Bitacora un mensaje detallado del cambio de estado (ej. "cambia de EN_OBSERVACION a APTO").
    public String editarEstadoClinico(String codigo, String nuevoEstado) {
        Animal animal = buscarPorCodigo(codigo);
        if (animal == null) {//busca, si no existe la variable queda vacia
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
        Animal animal = buscarPorCodigo(codigo);// Busca el código. Si devuelve null, guarda el error en bitácora y responde "ERROR: Animal no encontrado.
        if (animal == null) {
            Bitacora.error("ANIMALES", "VALIDACION", "Código " + codigo + " no existe");
            return "ERROR: Animal no encontrado";
        }
// !animal.estaActivo() revisa si ya fue dado de baja antes. Si es así, no hace nada repetido y avisa "ERROR: Ese animal ya fue eliminado
        if (!animal.estaActivo()) {
            Bitacora.error("ANIMALES", "VALIDACION", "Código " + codigo + " ya estaba eliminado");
            return "ERROR: Ese animal ya fue eliminado";
        }
// Aquí se aplica la baja lógica. No lo borra del arreglo, solo le cambia el estado a "ELIMINADO"
        animal.setEstadoAdopcion(Animal.ADOPCION_ELIMINADO);
        ubicacionControlador.liberarPorAnimal(codigo);// Le avisa al módulo de ubicaciones que el animal fue eliminado para que libere la jaula o espacio que estaba usando.
        Bitacora.accion("ANIMALES", "BAJA", "Animal " + codigo + " marcado como ELIMINADO");
        return "OK: Animal eliminado (baja lógica)";
        // Guarda el registro de la "BAJA" en la bitácora y responde "OK: Animal eliminado (baja lógica)
    }

    // Solo animales activos, para listados y reportes
    public Object[][] listarActivos() {
        int contador = 0;//contar cuantos activos
        for (int i = 0; i < Almacen.cantidadAnimales; i++) {
            if (Almacen.animales[i].estaActivo())
                contador++;
        }

        Object[][] filas = new Object[contador][];//rear matriz exacta activos que acaba de encontrar
        int fila = 0;
        for (int i = 0; i < Almacen.cantidadAnimales; i++) {
            if (Almacen.animales[i].estaActivo()) {
                filas[fila] = Almacen.animales[i].aFilaTabla();//sacalos datos en formato fila, los guarda en matriz y retorna al final
                fila++;
            }
        }//for1:cuenta activos, for 2: llena la matriz
        return filas;
    }
}