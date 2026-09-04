package co.edu.sena.inventario.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import co.edu.sena.inventario.model.Pedido;
import co.edu.sena.inventario.model.PedidoResumen;
import co.edu.sena.inventario.model.Producto;
import co.edu.sena.inventario.repository.PedidoRepository;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProductoService productoService;

    public PedidoService(
            PedidoRepository pedidoRepository,
            ProductoService productoService) {

        this.pedidoRepository = pedidoRepository;
        this.productoService = productoService;
    }


    // CREAR PEDIDO
    public Pedido crearPedido(Pedido pedido) {

        validarPedido(pedido);

        Producto producto =
                productoService.buscarPorId(
                    pedido.getProductoID()
                );

        if (producto == null) {
            throw new IllegalArgumentException(
                "El producto no se encuentra en stock"
            );
        }

        // El ID lo genera automáticamente la base de datos
        pedido.setEstado("PENDIENTE");

        return pedidoRepository.save(pedido);
    }




    // ACTUALIZAR PEDIDO
public Pedido actualizarPedido(Long id, Pedido pedidoActualizado) {

    Pedido pedidoExistente = buscarPedido(id);

    // No permitir modificar pedidos despachados o cancelados
    if (pedidoExistente.getEstado().equalsIgnoreCase("DESPACHADO")) {
        throw new IllegalStateException(
            "No se puede actualizar un pedido DESPACHADO"
        );
    }

    if (pedidoExistente.getEstado().equalsIgnoreCase("CANCELADO")) {
        throw new IllegalStateException(
            "No se puede actualizar un pedido CANCELADO"
        );
    }

    // Validar los nuevos datos
    validarPedido(pedidoActualizado);

    Producto producto = productoService.buscarPorId(
        pedidoActualizado.getProductoID()
    );

    if (producto == null) {
        throw new IllegalArgumentException(
            "El producto no se encuentra en stock"
        );
    }

    
    //Si el pedido ya estaba CONFIRMADO,
    //primero devolvemos al inventario
    //la cantidad anterior.
    
    if (pedidoExistente.getEstado().equalsIgnoreCase("CONFIRMADO")) {

        Producto productoAnterior = productoService.buscarPorId(
            pedidoExistente.getProductoID()
        );

        if (productoAnterior != null) {
            productoAnterior.setCantidad(
                productoAnterior.getCantidad()
                + pedidoExistente.getCantidad()
            );

            productoService.guardarProducto(productoAnterior);
        }

        
         // El pedido vuelve a PENDIENTE porque
          //estamos modificando sus datos.
         
        pedidoExistente.setEstado("PENDIENTE");
    }

    
    //Si el pedido estaba PENDIENTE,
    //simplemente actualizamos sus datos.
    
    pedidoExistente.setCliente(
        pedidoActualizado.getCliente()
    );

    pedidoExistente.setProductoID(
        pedidoActualizado.getProductoID()
    );

    pedidoExistente.setCantidad(
        pedidoActualizado.getCantidad()
    );

    pedidoExistente.setPrioridad(
        pedidoActualizado.getPrioridad()
    );

    return pedidoRepository.save(pedidoExistente);
}





    // CONFIRMAR PEDIDO
    public Pedido confirmarPedido(Long id) {

        Pedido pedido = buscarPedido(id);

        if (!pedido.getEstado()
                .equalsIgnoreCase("PENDIENTE")) {

            throw new IllegalStateException(
                "Solo se pueden confirmar pedidos PENDIENTES"
            );
        }

        Producto producto =
                productoService.buscarPorId(
                    pedido.getProductoID()
                );

        if (producto == null) {

            throw new IllegalArgumentException(
                "El producto no existe"
            );
        }

        if (producto.getCantidad() < pedido.getCantidad()) {

            throw new IllegalStateException(
                "No hay suficiente stock. Stock disponible: "
                + producto.getCantidad()
            );
        }


        // Descontar inventario
        producto.setCantidad(
            producto.getCantidad() - pedido.getCantidad()
        );

        // Guardar cambio del producto
        productoService.guardarProducto(producto);

        // Cambiar estado
        pedido.setEstado("CONFIRMADO");

        return pedidoRepository.save(pedido);
    }



    // CANCELAR PEDIDO
    public Pedido cancelarPedido(Long id) {

        Pedido pedido = buscarPedido(id);

        if (pedido.getEstado()
                .equalsIgnoreCase("CANCELADO")) {

            throw new IllegalStateException(
                "El pedido ya esta cancelado"
            );
        }

        if (pedido.getEstado()
                .equalsIgnoreCase("DESPACHADO")) {

            throw new IllegalStateException(
                "Pedido Despachado no se puede cancelar"
            );
        }

        // Si estaba confirmado,
        // devolver las unidades al inventario
        if (pedido.getEstado()
                .equalsIgnoreCase("CONFIRMADO")) {

            Producto producto =
                    productoService.buscarPorId(
                        pedido.getProductoID()
                    );

            if (producto != null) {

                producto.setCantidad(
                    producto.getCantidad()
                    + pedido.getCantidad()
                );

                productoService.guardarProducto(producto);
            }
        }

        pedido.setEstado("CANCELADO");

        return pedidoRepository.save(pedido);
    }



    // DESPACHAR PEDIDO
    public Pedido despacharPedido(Long id) {

        Pedido pedido = buscarPedido(id);

        if (!pedido.getEstado()
                .equalsIgnoreCase("CONFIRMADO")) {

            throw new IllegalStateException(
                "Solo se pueden despachar pedidos CONFIRMADOS"
            );
        }

        pedido.setEstado("DESPACHADO");

        return pedidoRepository.save(pedido);
    }



    // LISTAR PEDIDOS
    public List<Pedido> listarPedidos() {

        return pedidoRepository.findAll();
    }


    // BUSCAR PEDIDO
    public Pedido buscarPedido(Long id) {

        return pedidoRepository.findById(id)
            .orElseThrow(() ->
                new IllegalArgumentException(
                    "Pedido no encontrado"
                )
            );
    }


    
    // PEDIDOS PENDIENTES
    
    public List<Pedido> pendientes() {

        return pedidoRepository.findAll()
            .stream()
            .filter(p ->
                p.getEstado()
                    .equalsIgnoreCase("PENDIENTE")
            )
            .toList();
    }


    // PEDIDOS URGENTES
    
    public List<Pedido> urgentes() {

        return pedidoRepository.findAll()
            .stream()
            .filter(p ->
                p.getPrioridad()
                    .equalsIgnoreCase("URGENTE")
            )
            .toList();
    }


    
    // BUSCAR POR ESTADO
    public List<Pedido> porEstado(String estado) {

        return pedidoRepository.findAll()
            .stream()
            .filter(p ->
                p.getEstado()
                    .equalsIgnoreCase(estado)
            )
            .toList();
    }



    // SIGUIENTE PEDIDO
    public Pedido siguiente() {

        return pedidoRepository.findAll()
            .stream()
            .filter(p ->
                p.getEstado()
                    .equalsIgnoreCase("PENDIENTE")
            )
            .sorted(
                Comparator
                    .comparingInt(
                        this::valorPrioridad
                    )
                    .reversed()
                    .thenComparing(Pedido::getId)
            )
            .findFirst()
            .orElse(null);
    }



    // VALOR DE PRIORIDAD
    private int valorPrioridad(Pedido pedido) {

        return switch (
            pedido.getPrioridad().toUpperCase()) {

            case "URGENTE" -> 4;
            case "ALTA" -> 3;
            case "MEDIA" -> 2;
            case "BAJA" -> 1;

            default -> 0;
        };
    }


    // PEDIDOS EN RIESGO
    public List<Pedido> enRiesgo() {

        List<Pedido> resultado = new ArrayList<>();

        for (Pedido pedido :
                pedidoRepository.findAll()) {

            if (!pedido.getEstado()
                    .equalsIgnoreCase("PENDIENTE")) {

                continue;
            }

            Producto producto =
                productoService.buscarPorId(
                    pedido.getProductoID()
                );

            if (producto == null ||
                producto.getCantidad()
                    < pedido.getCantidad()) {

                resultado.add(pedido);
            }
        }

        return resultado;
    }



    // RESUMEN DE PEDIDOS
    public PedidoResumen resumen() {

        List<Pedido> pedidos =
                pedidoRepository.findAll();

        long pendientes = pedidos.stream()
            .filter(p ->
                p.getEstado()
                    .equalsIgnoreCase("PENDIENTE")
            )
            .count();

        long confirmados = pedidos.stream()
            .filter(p ->
                p.getEstado()
                    .equalsIgnoreCase("CONFIRMADO")
            )
            .count();

        long despachados = pedidos.stream()
            .filter(p ->
                p.getEstado()
                    .equalsIgnoreCase("DESPACHADO")
            )
            .count();

        long cancelados = pedidos.stream()
            .filter(p ->
                p.getEstado()
                    .equalsIgnoreCase("CANCELADO")
            )
            .count();

        long urgentes = pedidos.stream()
            .filter(p ->
                p.getPrioridad()
                    .equalsIgnoreCase("URGENTE")
            )
            .count();

        return new PedidoResumen(
            pedidos.size(),
            pendientes,
            confirmados,
            despachados,
            cancelados,
            urgentes
        );
    }



    // VALIDACIONES
    private void validarPedido(Pedido pedido) {

        if (pedido.getCliente() == null ||
                pedido.getCliente()
                    .trim().isEmpty()) {

            throw new IllegalArgumentException(
                "El cliente es obligatorio"
            );
        }

        if (pedido.getProductoID() == null) {

            throw new IllegalArgumentException(
                "El productoID es obligatorio"
            );
        }

        if (pedido.getCantidad() == null ||
                pedido.getCantidad() <= 0) {

            throw new IllegalArgumentException(
                "La cantidad debe ser mayor que cero"
            );
        }

        if (pedido.getPrioridad() == null ||
                !prioridadValida(
                    pedido.getPrioridad())) {

            throw new IllegalArgumentException(
                "Prioridad inválida. Use BAJA, MEDIA, ALTA o URGENTE"
            );
        }
    }



    // VALIDAR PRIORIDAD
    private boolean prioridadValida(String prioridad) {

        return prioridad.equalsIgnoreCase("BAJA")
                || prioridad.equalsIgnoreCase("MEDIA")
                || prioridad.equalsIgnoreCase("ALTA")
                || prioridad.equalsIgnoreCase("URGENTE");
    }

}