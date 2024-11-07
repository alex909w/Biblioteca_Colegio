package com.biblioteca.documentos;

public class CD extends Documento {
    private String artista;
    private String genero;
    private int duracion; // Duración en minutos
    private int numeroCanciones;

    public CD(String titulo, String autor, String fechaPublicacion, String categoria, int ejemplares, String artista, String genero, int duracion, int numeroCanciones) {
        super(titulo, autor, fechaPublicacion, categoria, ejemplares);
        this.artista = artista;
        this.genero = genero;
        this.duracion = duracion;
        this.numeroCanciones = numeroCanciones;
    }

    // Getters and Setters
    public String getArtista() { return artista; }
    public void setArtista(String artista) { this.artista = artista; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public int getDuracion() { return duracion; }
    public void setDuracion(int duracion) { this.duracion = duracion; }

    public int getNumeroCanciones() { return numeroCanciones; }
    public void setNumeroCanciones(int numeroCanciones) { this.numeroCanciones = numeroCanciones; }
}
