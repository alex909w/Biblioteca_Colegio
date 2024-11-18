package com.biblioteca.utilidades;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validaciones {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$");

      public static boolean esCorreoValido(String email) {
        if (email == null) {
            return false;
        }
        Matcher matcher = EMAIL_PATTERN.matcher(email);
        return matcher.matches();
    }

    public static String validarDatosUsuario(String nombre, String apellido, String clave, String email, String telefono, int limitePrestamos) {
        StringBuilder errorBuilder = new StringBuilder();

        if (nombre == null || nombre.trim().isEmpty()) {
            errorBuilder.append("El nombre es obligatorio.\n");
        }

        if (apellido == null || apellido.trim().isEmpty()) {
            errorBuilder.append("El apellido es obligatorio.\n");
        }

        if (clave == null || clave.trim().isEmpty()) {
            errorBuilder.append("La contraseña es obligatoria.\n");
        }

        if (email == null || email.trim().isEmpty()) {
            errorBuilder.append("El correo electrónico es obligatorio.\n");
        } else if (!esCorreoValido(email)) {
            errorBuilder.append("El correo electrónico no es válido.\n");
        }

        if (telefono == null || telefono.trim().isEmpty()) {
            errorBuilder.append("El teléfono es obligatorio.\n");
        }

        if (limitePrestamos <= 0) {
            errorBuilder.append("El límite de préstamos debe ser un número positivo.\n");
        }

        return errorBuilder.toString();
    }
    
}
