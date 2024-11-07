package com.biblioteca.documentos;

public class Libro extends Documento {
    private String isbn;
    private int paginas;

    public Libro(String titulo, String autor, String fechaPublicacion, String categoria, int ejemplares, String isbn, int paginas) {
        super(titulo, autor, fechaPublicacion, categoria, ejemplares);
        this.isbn = isbn;
        this.paginas = paginas;
    }

    // Getters and Setters
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public int getPaginas() { return paginas; }
    public void setPaginas(int paginas) { this.paginas = paginas; }
}

