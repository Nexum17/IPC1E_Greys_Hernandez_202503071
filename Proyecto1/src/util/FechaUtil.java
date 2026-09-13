package util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FechaUtil {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Fecha del día en formato dd/MM/yyyy, para registrar ingresos y reportes
    public static String fechaActual() {
        return LocalDateTime.now().format(FORMATO_FECHA);
    }

    // Valida formato dd/mm/aaaa Y que la fecha exista realmente (rechaza
    // 31/02/2026)
    public static boolean esFechaValida(String texto) {
        if (texto == null || !texto.matches("\\d{2}/\\d{2}/\\d{4}")) {
            return false;
        }

        String[] partes = texto.split("/");
        int dia = Integer.parseInt(partes[0]);
        int mes = Integer.parseInt(partes[1]);
        int anio = Integer.parseInt(partes[2]);

        if (mes < 1 || mes > 12)
            return false;
        if (dia < 1 || dia > diasDelMes(mes, anio))
            return false;

        return true;
    }

    private static int diasDelMes(int mes, int anio) {
        switch (mes) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            case 2:
                return esBisiesto(anio) ? 29 : 28;
            default:
                return 0; // mes inválido, no debería llegar aquí
        }
    }

    private static boolean esBisiesto(int anio) {
        return (anio % 4 == 0 && anio % 100 != 0) || (anio % 400 == 0);
    }
    
    // Fecha y hora para nombres de archivo: sin ":" ni "/", válido en Windows y
    // Linux
    private static final DateTimeFormatter FORMATO_ARCHIVO = DateTimeFormatter.ofPattern("dd-MM-yyyy_HHmm");

    public static String fechaHoraParaArchivo() {
        return LocalDateTime.now().format(FORMATO_ARCHIVO);
    }
}