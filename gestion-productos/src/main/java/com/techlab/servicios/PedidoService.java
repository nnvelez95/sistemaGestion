package com.techlab.servicios;

import com.techlab.excepciones.StockInsuficienteException;
import com.techlab.pedidos.LineaPedido;
import com.techlab.pedidos.Pedido;
import com.techlab.productos.Producto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PedidoService {

    private final List<Pedido> pedidos = new ArrayList<>();
    private int nextId = 1;

    private final ProductoService productoService;

    public PedidoService(ProductoService productoService) {
        this.productoService = productoService;
    }

    /**
     * Crea un nuevo pedido a partir de una lista de líneas.
     */
    public Pedido crearPedido(List<LineaPedido> lineas) throws StockInsuficienteException {
        Pedido pedido = new Pedido(nextId++);

        // Validar stock de cada producto
        for (LineaPedido linea : lineas) {
            Producto p = linea.getProducto();
            if (linea.getCantidad() > p.getStock()) {
                throw new StockInsuficienteException(
                        "Stock insuficiente para el producto: " + p.getNombre());
            }
        }

        // Si todo está bien, descontar stock
        for (LineaPedido linea : lineas) {
            Producto p = linea.getProducto();
            p.setStock(p.getStock() - linea.getCantidad());
            pedido.agregarLinea(linea);
        }

        pedidos.add(pedido);
        return pedido;
    }

    public List<Pedido> listarPedidos() {
        return new ArrayList<>(pedidos);
    }

    public boolean hayPedidos() {
        return !pedidos.isEmpty();
    }
}
