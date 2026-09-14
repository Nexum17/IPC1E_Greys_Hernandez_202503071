package util;

public class Validador {

    // "A-014", "AD-007", "S-021", "R-009" → prefijo + guion + dígitos
    public static boolean esCodigo(String texto, String prefijo) {
        if (texto == null || prefijo == null)
            return false;
        String patron = "^" + prefijo + "-\\d+$";//empieza sin dejar espacios+prefijo+digito1omasseguidos/fin
        return texto.matches(patron);
    }

    // Solo letras (con tildes/ñ) y espacios — para nombres
    public static boolean esSoloLetras(String texto) {
        if (texto == null || texto.isEmpty())
            return false;
        return texto.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+");
    }

    // 13 dígitos numéricos — DPI
    public static boolean esDPI(String texto) {
        if (texto == null)
            return false;
        return texto.matches("\\d{13}");
    }

    // 8 dígitos numéricos — teléfono
    public static boolean esTelefono(String texto) {
        if (texto == null)
            return false;
        return texto.matches("\\d{8}");
    }

    // Valor entero dentro de un rango inclusivo
    public static boolean enRango(int valor, int minimo, int maximo) {
        return valor >= minimo && valor <= maximo;
    }

    // Campo de texto no vacío y sin ser solo espacios
    public static boolean noVacio(String texto) {
        return texto != null && !texto.trim().isEmpty();
    }
}