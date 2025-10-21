package com.techlab.servicios;

import com.techlab.excepciones.StockInsuficienteException;
import com.techlab.pedidos.LineaPedido;
import com.techlab.pedidos.Pedido;
import com.techlab.productos.Producto;
import com.techlab.util.ArchivoUtil;

import java.util.*;
import java.util.stream.Collectors;

public class PedidoService {

    private static final String RUTA_PEDIDOS = "data/pedidos.txt";
    private final List<Pedido> pedidos = new ArrayList<>();
    private int nextId = 1;

    private final ProductoService productoService;

    public PedidoService(ProductoService productoService) {
        if (productoService == null) {
            throw new IllegalArgumentException("El servicio de productos no puede ser nulo.");
        }
        this.productoService = productoService;
    }

    /**
     * Crea un nuevo pedido a partir de una lista de líneas, valida el stock y lo descuenta.
     * @param lineas Lista de líneas de pedido a incluir.
     * @return El Pedido creado.
     * @throws StockInsuficienteException Si el stock es menor a la cantidad solicitada.
     */
    public Pedido crearPedido(List<LineaPedido> lineas) throws StockInsuficienteException {
        // Lógica atómica: valida y descuenta stock.
        validarYDescontarStock(lineas);

        Pedido pedido = new Pedido(nextId++);

        for (LineaPedido linea : lineas) {
            pedido.agregarLinea(linea);
        }

        pedidos.add(pedido);
        return pedido;
    }

    private void validarYDescontarStock(List<LineaPedido> lineas) throws StockInsuficienteException {
        // 1. Fase de Validación:
        for (LineaPedido linea : lineas) {
            Producto p = linea.getProducto();
            if (p == null) {
                throw new IllegalArgumentException("Línea de pedido contiene un producto nulo.");
            }
            if (linea.getCantidad() > p.getStock()) {
                throw new StockInsuficienteException(
                        "Stock insuficiente para el producto: " + p.getNombre() +
                                ". Disponible: " + p.getStock() + ", Solicitado: " + linea.getCantidad());
            }
        }

        // 2. Fase de Descuento (Solo si la validación fue exitosa para *todos*):
        for (LineaPedido linea : lineas) {
            Producto p = linea.getProducto();
            p.setStock(p.getStock() - linea.getCantidad());
        }
    }

    public List<Pedido> listarPedidos() {
        return new ArrayList<>(pedidos);
    }

    public boolean hayPedidos() {
        return !pedidos.isEmpty();
    }

    // ==========================================================
    // 🔹 PERSISTENCIA DE DATOS
    // ==========================================================

    public void guardarEnArchivo() {
        // Uso de Stream API para mapeo eficiente.
        List<String> lineas = pedidos.stream()
                .flatMap(pedido -> pedido.getLineas().stream()
                        .map(lp -> String.format("%d;%s;%d;%.2f",
                                pedido.getId(),
                                lp.getProducto().getNombre(),
                                lp.getCantidad(),
                                lp.getSubtotal())))
                .collect(Collectors.toList());

        ArchivoUtil.escribirLineas(RUTA_PEDIDOS, lineas);
    }


    public void cargarDesdeArchivo() {
        List<String> lineas = ArchivoUtil.leerLineas(RUTA_PEDIDOS);
        Map<Integer, Pedido> mapa = new HashMap<>();

        for (String linea : lineas) {
            String[] datos = linea.split(";");

            // Lanza una RuntimeException para errores de consistencia en el archivo.
            if (datos.length < 3) {
                throw new IllegalStateException("Línea de pedido con formato inválido: " + linea);
            }

            try {
                int idPedido = Integer.parseInt(datos[0]);
                String nombreProducto = datos[1];
                int cantidad = Integer.parseInt(datos[2]);

                Optional<Producto> productoOpt = productoService.buscarPorNombre(nombreProducto);

                // Lanza una RuntimeException si el producto no se encuentra.
                if (productoOpt.isEmpty()) {
                    throw new IllegalStateException("Producto no encontrado durante la carga: " + nombreProducto);
                }

                Pedido pedido = mapa.computeIfAbsent(idPedido, Pedido::new);
                pedido.agregarLinea(new LineaPedido(productoOpt.get(), cantidad));

            } catch (NumberFormatException e) {
                // Captura error de parseo y lanzamos una IllegalStateException.
                throw new IllegalStateException("Error al parsear números en línea: " + linea, e);
            }
        }

        pedidos.clear();
        pedidos.addAll(mapa.values());

        // Uso de Stream API para calcular nextId.
        nextId = pedidos.stream()
                .mapToInt(Pedido::getId)
                .max()
                .orElse(0) + 1;
    }
}