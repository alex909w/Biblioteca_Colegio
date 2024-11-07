package com.biblioteca.documentos;

public class Tesis extends Documento {
    private String universidad;

    public Tesis(String titulo, String autor, String fechaPublicacion, String categoria, int ejemplares, String universidad) {
        super(titulo, autor, fechaPublicacion, categoria, ejemplares);
        this.universidad = universidad;
    }

    // Getters and Setters
    public String getUniversidad() { return universidad; }
    public void setUniversidad(String universidad) { this.universidad = universidad; }
}
