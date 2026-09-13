package model;
 
public class Rescate {

    public static final int MAX = 80;

    public static final String PRIORIDAD_ALTA = "ALTA";
    public static final String PRIORIDAD_MEDIA = "MEDIA";
    public static final String PRIORIDAD_BAJA = "BAJA";

    public static final String ESTADO_PENDIENTE = "PENDIENTE";
    public static final String ESTADO_ATENDIDO = "ATENDIDO";

    private String codigo; // "R-009"
    private String prioridad; // ALTA, MEDIA o BAJA
    private String estado; // PENDIENTE o ATENDIDO
    private String fechaReporte; // "18/08/2026"
    private String codigoAnimalVinculado; // "" mientras no se atiende; luego "A-009" o el existente

    public Rescate(String codigo, String prioridad, String fechaReporte) {
        this.codigo = codigo;
        this.prioridad = prioridad;
        this.fechaReporte = fechaReporte;
        this.estado = ESTADO_PENDIENTE; // nace pendiente
        this.codigoAnimalVinculado = ""; // aún no hay animal vinculado
    }

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

    public void setEstado(String estado) {
        this.estado = estado;
    }

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
