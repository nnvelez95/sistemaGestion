package com.techlab.productos;

public class Bebida extends Producto {

    private double litros;

    public Bebida(String nombre, double precio, int stock, double litros) {
        super(nombre, precio, stock);  // sin id
        this.litros = litros;
    }

    public Bebida(int id, String nombre, double precio, int stock, double litros) {
        super(id, nombre, precio, stock); // con id (solo carga desde archivo)
        this.litros = litros;
    }

    public double getLitros() {
        return litros;
    }

    public void setLitros(double litros) {
        if (litros > 0) this.litros = litros;
    }

    @Override
    public String toString() {
        return String.format("[Bebida] %s | %.1f L", super.toString(), litros);
    }
}
