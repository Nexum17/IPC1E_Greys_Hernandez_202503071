package model;

public class Animal {

    public static final int MAX = 100;

    // Dominios permitidos
    public static final String ESPECIE_PERRO = "Perro";
    public static final String ESPECIE_GATO = "Gato";

    public static final String CLINICO_OBSERVACION = "EN_OBSERVACION";
    public static final String CLINICO_TRATAMIENTO = "EN_TRATAMIENTO";
    public static final String CLINICO_APTO = "APTO";

    public static final String ADOPCION_DISPONIBLE = "DISPONIBLE";
    public static final String ADOPCION_ADOPTADO = "ADOPTADO";
    public static final String ADOPCION_ELIMINADO = "ELIMINADO";

    public static final int EDAD_MINIMA = 0;
    public static final int EDAD_MAXIMA = 25;

    private String codigo; // "A-014", único e irrepetible
    private String nombre;
    private String especie;
    private int edadEstimada;
    private String estadoClinico;
    private String estadoAdopcion;
    private String fechaIngreso; // "18/08/2026"

    public Animal(String codigo, String nombre, String especie,
            int edadEstimada, String estadoClinico, String fechaIngreso) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.especie = especie;
        this.edadEstimada = edadEstimada;
        this.estadoClinico = estadoClinico;
        this.fechaIngreso = fechaIngreso;
        this.estadoAdopcion = ADOPCION_DISPONIBLE; // estado inicial por defecto
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEspecie() {
        return especie;
    }

    public int getEdadEstimada() {
        return edadEstimada;
    }

    public String getEstadoClinico() {
        return estadoClinico;
    }

    public String getEstadoAdopcion() {
        return estadoAdopcion;
    }

    public String getFechaIngreso() {
        return fechaIngreso;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setEspecie(String especie) {
        this.especie = especie;
    }

    public void setEdadEstimada(int edadEstimada) {
        this.edadEstimada = edadEstimada;
    }

    public void setEstadoClinico(String estadoClinico) {
        this.estadoClinico = estadoClinico;
    }

    public void setEstadoAdopcion(String estadoAdopcion) {
        this.estadoAdopcion = estadoAdopcion;
    }

    public boolean estaActivo() {
        return !ADOPCION_ELIMINADO.equals(estadoAdopcion);
    }

    // Línea para persistencia: codigo|nombre|especie|edad|clinico|adopcion|fecha
    public String aLineaArchivo() {
        return codigo + "|" + nombre + "|" + especie + "|" + edadEstimada + "|"
                + estadoClinico + "|" + estadoAdopcion + "|" + fechaIngreso;
    }

    // Fila para JTable, sin DefaultTableModel
    public Object[] aFilaTabla() {
        return new Object[] { codigo, nombre, especie, edadEstimada,
                estadoClinico, estadoAdopcion, fechaIngreso };
    }
}