package com.techlab.servicios;

import com.techlab.excepciones.StockInsuficienteException;
import com.techlab.pedidos.LineaPedido;
import com.techlab.pedidos.Pedido;
import com.techlab.productos.Producto;
import com.techlab.util.ArchivoUtil;

import java.util.*;

public class PedidoService {

    private final List<Pedido> pedidos = new ArrayList<>();
    private int nextId = 1;
    private final String RUTA_PEDIDOS = "data/pedidos.txt";

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
    // 🔹 Guardar pedidos
    public void guardarEnArchivo() {
        List<String> lineas = new ArrayList<>();
        for (Pedido pedido : pedidos) {
            for (LineaPedido lp : pedido.getLineas()) {
                lineas.add(String.format("%d;%s;%d;%.2f",
                        pedido.getId(),
                        lp.getProducto().getNombre(),
                        lp.getCantidad(),
                        lp.getSubtotal()));
            }
        }
        ArchivoUtil.escribirLineas(RUTA_PEDIDOS, lineas);
    }

    // 🔹 Cargar pedidos (básico)
    public void cargarDesdeArchivo() {
        List<String> lineas = ArchivoUtil.leerLineas(RUTA_PEDIDOS);
        Map<Integer, Pedido> mapa = new HashMap<>();

        for (String linea : lineas) {
            String[] datos = linea.split(";");
            int idPedido = Integer.parseInt(datos[0]);
            String nombreProducto = datos[1];
            int cantidad = Integer.parseInt(datos[2]);

            Optional<Producto> producto = productoService.buscarPorNombre(nombreProducto);
            if (producto.isPresent()) {
                Pedido pedido = mapa.computeIfAbsent(idPedido, Pedido::new);
                pedido.agregarLinea(new LineaPedido(producto.get(), cantidad));
            }
        }

        pedidos.clear();
        pedidos.addAll(mapa.values());
        nextId = pedidos.stream().mapToInt(Pedido::getId).max().orElse(0) + 1;
    }
}

