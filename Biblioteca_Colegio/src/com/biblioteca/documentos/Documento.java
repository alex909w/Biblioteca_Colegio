package com.biblioteca.documentos;

public abstract class Documento {
    private String titulo;
    private String autor;
    private String fechaPublicacion;
    private String categoria;
    private int ejemplares;

    // Constructor
    public Documento(String titulo, String autor, String fechaPublicacion, String categoria, int ejemplares) {
        this.titulo = titulo;
        this.autor = autor;
        this.fechaPublicacion = fechaPublicacion;
        this.categoria = categoria;
        this.ejemplares = ejemplares;
    }

    // Getters and Setters
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }

    public String getFechaPublicacion() { return fechaPublicacion; }
    public void setFechaPublicacion(String fechaPublicacion) { this.fechaPublicacion = fechaPublicacion; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public int getEjemplares() { return ejemplares; }
    public void setEjemplares(int ejemplares) { this.ejemplares = ejemplares; }
}
