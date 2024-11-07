// Clase Revista
package com.biblioteca.documentos;

public class Revista extends Documento {
    private String periodicidad;

    public Revista(String titulo, String autor, String fechaPublicacion, String categoria, int ejemplares, String periodicidad) {
        super(titulo, autor, fechaPublicacion, categoria, ejemplares);
        this.periodicidad = periodicidad;
    }

    // Getters and Setters
    public String getPeriodicidad() { return periodicidad; }
    public void setPeriodicidad(String periodicidad) { this.periodicidad = periodicidad; }
}
