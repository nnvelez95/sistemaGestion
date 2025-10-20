package com.techlab.productos;

import java.time.LocalDate;

public class Comida extends Producto {

    private LocalDate fechaVencimiento;

    public Comida(int id, String nombre, double precio, int stock, LocalDate fechaVencimiento) {
        super(id, nombre, precio, stock);
        this.fechaVencimiento = fechaVencimiento;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    @Override
    public String toString() {
        return String.format("[Comida] %s | Vence: %s",
                super.toString(), fechaVencimiento);
    }
}
