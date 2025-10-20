package com.techlab.productos;

public class Bebida extends Producto {

    private double litros;

    public Bebida(int id, String nombre, double precio, int stock, double litros) {
        super(id, nombre, precio, stock);
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
