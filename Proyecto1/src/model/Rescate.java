package model;
 
public class Rescate {

    public static final int MAX = 80;// Define que como máximo pueden existir 80 reportes de rescate guardados en la memoria.
// Opciones permitidas para evitar errores de escritura:
    public static final String PRIORIDAD_ALTA = "ALTA";
    public static final String PRIORIDAD_MEDIA = "MEDIA";
    public static final String PRIORIDAD_BAJA = "BAJA";

    public static final String ESTADO_PENDIENTE = "PENDIENTE";
    public static final String ESTADO_ATENDIDO = "ATENDIDO";
// Guardan el código del rescate, su prioridad, la fecha y el código del animal vinculado.
    private String codigo; // "R-009"
    private String prioridad; // ALTA, MEDIA o BAJA
    private String estado; // PENDIENTE o ATENDIDO
    private String fechaReporte; // "18/08/2026"
    private String codigoAnimalVinculado; // "" mientras no se atiende; luego "A-009" o el existente
// Constructor): Cuando creas un rescate nuevo, el sistema le pone automáticamente el estado inicial de "PENDIENTE" y deja el código del animal vinculado vacío ("") porque todavía no se ha atendido.
    public Rescate(String codigo, String prioridad, String fechaReporte) {
        this.codigo = codigo;
        this.prioridad = prioridad;
        this.fechaReporte = fechaReporte;
        this.estado = ESTADO_PENDIENTE; // nace pendiente
        this.codigoAnimalVinculado = ""; // aún no hay animal vinculado
    }

// Métodos Getter: Permisos de solo lectura para consultar la información del rescate desde fuera de la clase.
    public String getCodigo() {
        return codigo;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public String getEstado() {
        return estado;
    }

    public String getFechaReporte() {
        return fechaReporte;
    }

    public String getCodigoAnimalVinculado() {
        return codigoAnimalVinculado;
    }

//// Permisos de lectura para obtener la fecha del reporte y el código del animal asociado.
    public void setEstado(String estado) {
        this.estado = estado;
    }

    // Métodos de apoyo que verifican si el rescate aún está pendiente y si ya tiene un animal asignado.
    public void setCodigoAnimalVinculado(String codigoAnimalVinculado) {
        this.codigoAnimalVinculado = codigoAnimalVinculado;
    }

    public boolean estaPendiente() {
        return ESTADO_PENDIENTE.equals(estado);
    }

    public boolean tieneAnimalVinculado() {
        return codigoAnimalVinculado != null && !codigoAnimalVinculado.isEmpty();
    }

    // Línea para persistencia:
    // codigo|prioridad|estado|fechaReporte|codigoAnimalVinculado
    public String aLineaArchivo() {
        return codigo + "|" + prioridad + "|" + estado + "|" + fechaReporte + "|" + codigoAnimalVinculado;
    }

    // Fila para JTable, sin DefaultTableModel
    public Object[] aFilaTabla() {
        return new Object[] { codigo, prioridad, estado, fechaReporte, codigoAnimalVinculado };
    }
}
// "El archivo Rescate.java es una clase del modelo que define la estructura y
// el ciclo de vida de un reporte de rescate. Controla sus estados y prioridades
// mediante constantes, valida si el rescate ya fue atendido o vinculado a un
// animal, y prepara los datos para la persistencia en texto plano y la
// visualización en la tabla.