package com.techlab.productos;

/**
 * Representa un producto con id, nombre, precio y stock.
 */
public class Producto {
    private int id;
    private String nombre;
    private double precio;
    private int stock;

    // 🔹 Constructor usado cuando el servicio genera el ID
    public Producto(String nombre, double precio, int stock) {
        setNombre(nombre);
        setPrecio(precio);
        setStock(stock);
    }

    // 🔹 Constructor usado solo para cargar desde archivo
    public Producto(int id, String nombre, double precio, int stock) {
        this.id = id;
        setNombre(nombre);
        setPrecio(precio);
        setStock(stock);
    }

    // ==========================================================
    // 🔹 Getters y Setters con validaciones básicas
    // ==========================================================

    public int getId() {
        return id;
    }

    public void setId(int id) {
        // Se deja el setter solo para compatibilidad con persistencia
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("⚠️  El nombre del producto no puede estar vacío.");
        }
        this.nombre = nombre.trim();
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        if (precio < 0) {
            throw new IllegalArgumentException("⚠️  El precio no puede ser negativo.");
        }
        this.precio = precio;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        if (stock < 0) {
            throw new IllegalArgumentException("⚠️  El stock no puede ser negativo.");
        }
        this.stock = stock;
    }

    // ==========================================================
    // 🔹 Métodos de utilidad
    // ==========================================================

    /** Dos productos se consideran iguales si tienen el mismo nombre y precio. */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Producto other)) return false;
        return nombre.equalsIgnoreCase(other.nombre) && Double.compare(precio, other.precio) == 0;
    }

    @Override
    public int hashCode() {
        return nombre.toLowerCase().hashCode() + Double.hashCode(precio);
    }

    /** Representación legible del producto */
    @Override
    public String toString() {
        return String.format("ID: %d | %-15s | Precio: $%7.2f | Stock: %3d",
                id, nombre, precio, stock);
    }

}
