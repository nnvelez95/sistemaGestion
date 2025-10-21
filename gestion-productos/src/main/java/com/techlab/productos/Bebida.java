package com.techlab.productos;

/**
 * Representa una bebida que es un tipo de producto con volumen en litros.
 */
public class Bebida extends Producto {

    private double litros;

    // 🔹 Constructor sin ID (nuevo producto)
    public Bebida(String nombre, double precio, int stock, double litros) {
        super(nombre, precio, stock);
        setLitros(litros); // valida
    }

    // 🔹 Constructor con ID (para carga desde archivo)
    public Bebida(int id, String nombre, double precio, int stock, double litros) {
        super(id, nombre, precio, stock);
        setLitros(litros); // valida
    }

    // ==========================================================
    // 🔹 Getter / Setter con validación
    // ==========================================================

    public double getLitros() {
        return litros;
    }

    public void setLitros(double litros) {
        if (litros <= 0) {
            throw new IllegalArgumentException("⚠️  Los litros deben ser mayores que cero.");
        }
        this.litros = litros;
    }

    // ==========================================================
    // 🔹 Métodos de utilidad
    // ==========================================================

    @Override
    public String toString() {
        return String.format("[Bebida] %s | Volumen: %.1f L", super.toString(), litros);
    }

}
