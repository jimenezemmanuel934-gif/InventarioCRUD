package co.edu.sena.inventario.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

import co.edu.sena.inventario.model.Pedido;
import co.edu.sena.inventario.model.Producto;
import co.edu.sena.inventario.model.PedidoResumen;

@Service
public class PedidoService {

    //SE CREA LA LISTA DE PEDIDOS Y ATOMIC CREA LOS ID DE MODO QUE NO SE REPITAN COMO UN FOR
    private final List<Pedido> pedidos = new ArrayList<>();

    private final ProductoService productoService;

    private final AtomicLong siguienteID = new AtomicLong(1);

    public PedidoService(ProductoService productoService) {
        this.productoService = productoService;
    }


    //CREAR PEDIDO
    public Pedido crearPedido(Pedido pedido) {

        validarPedido(pedido);

        Producto producto = productoService.buscarPorId(pedido.getProductoID());

        if (producto == null) {
            throw new IllegalArgumentException(
                "El producto no se encuentra en stock"
            );
        }

        pedido.setId(siguienteID.getAndIncrement());

        //NUEVO PEDIDO = PENDIENTE
        pedido.setEstado("PENDIENTE");

        pedidos.add(pedido);

        return pedido;
    }



    //CONFIRMAR PEDIDO
    public Pedido confirmarPedido(Long id) {

        Pedido pedido = buscarPedido(id);

        //SOLO SE CONFIRMAR SI ESTA PENDIENTE
        if (!pedido.getEstado().equalsIgnoreCase("PENDIENTE")) {

            throw new IllegalStateException(
                "Solo se pueden confirmar pedidos PENDIENTES"
            );
        }

        Producto producto =
                productoService.buscarPorId(pedido.getProductoID());

        if (producto == null) {

            throw new IllegalArgumentException(
                "El producto no existe"
            );
        }

        //VERIFICA SI HAY STOCK DISPONIBLE
        if (producto.getCantidad() < pedido.getCantidad()) {

            throw new IllegalStateException(
                "No hay suficiente stock. Stock disponible: "
                + producto.getCantidad()
            );
        }

        //DESCONTAR INVENTARIO
        producto.setCantidad(
            producto.getCantidad() - pedido.getCantidad()
        );

        //CAMBIAR ESTADO
        pedido.setEstado("CONFIRMADO");

        return pedido;
    }



    //CANCELAR PEDIDO
    public Pedido cancelarPedido(Long id) {

        Pedido pedido = buscarPedido(id);

        //NO SE CANCELA DOS VECES
        if (pedido.getEstado().equalsIgnoreCase("CANCELADO")) {

            throw new IllegalStateException(
                "El pedido ya esta cancelado"
            );
        }

        //NO SE PUEDE CANCELAR YA DESPACHADO
        if (pedido.getEstado().equalsIgnoreCase("DESPACHADO")) {

            throw new IllegalStateException(
                "Pedido Despachado no se puede cancelar"
            );
        }

        //SI SE CANCELA CUANDO YA ESTABA CONFIRMADO DEVOLVER LAS UNIDADES DE PRODUCTO
        if (pedido.getEstado().equalsIgnoreCase("CONFIRMADO")) {

            Producto producto =
                    productoService.buscarPorId(
                        pedido.getProductoID()
                    );

            producto.setCantidad(
                producto.getCantidad() + pedido.getCantidad()
            );
        }

        pedido.setEstado("CANCELADO");

        return pedido;
    }



    //DESPACHAR PEDIDO
    public Pedido despacharPedido(Long id) {

        Pedido pedido = buscarPedido(id);

        //SOLO DESPACHA CONFIRMADOS
        if (!pedido.getEstado().equalsIgnoreCase("CONFIRMADO")) {

            throw new IllegalStateException(
                "Solo se pueden despachar pedidos CONFIRMADOS"
            );
        }

        pedido.setEstado("DESPACHADO");

        return pedido;
    }



    //LISTAR PEDIDOS
    public List<Pedido> listarPedidos() {

        return pedidos;
    }



    //BUSCAR PEDIDO
    public Pedido buscarPedido(Long id) {

        return pedidos.stream()
            .filter(p -> p.getId().equals(id))
            .findFirst()
            .orElseThrow(() ->
                new IllegalArgumentException(
                    "Pedido no encontrado"
                )
            );
    }




    //PEDIDOS PENDIENTES
    public List<Pedido> pendientes() {

        return pedidos.stream()
            .filter(p ->
                p.getEstado().equalsIgnoreCase("PENDIENTE")
            )
            .toList();
    }



    //PEDIDOS URGENTES
    public List<Pedido> urgentes() {

        return pedidos.stream()
            .filter(p ->
                p.getPrioridad().equalsIgnoreCase("URGENTE")
            )
            .toList();
    }



    //BUSCAR POR ESTADO
    public List<Pedido> porEstado(String estado) {

        return pedidos.stream()
            .filter(p ->
                p.getEstado().equalsIgnoreCase(estado)
            )
            .toList();
    }



    //SIGUIENTE PEDIDO
    public Pedido siguiente() {

        return pedidos.stream()
            .filter(p ->
                p.getEstado().equalsIgnoreCase("PENDIENTE")
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



    //VALOR DE PRIORIDAD
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



    //PEDIDOS EN RIESGO
    public List<Pedido> enRiesgo() {

        List<Pedido> resultado = new ArrayList<>();

        for (Pedido pedido : pedidos) {

            if (!pedido.getEstado()
                    .equalsIgnoreCase("PENDIENTE")) {

                continue;
            }

            Producto producto =
                productoService.buscarPorId(
                    pedido.getProductoID()
                );

            if (producto == null ||
                producto.getCantidad() < pedido.getCantidad()) {

                resultado.add(pedido);
            }
        }

        return resultado;
    }



    //RESUMEN DE PEDIDOS
    public PedidoResumen resumen() {

        long pendientes = pedidos.stream()
            .filter(p ->
                p.getEstado().equalsIgnoreCase("PENDIENTE")
            )
            .count();

        long confirmados = pedidos.stream()
            .filter(p ->
                p.getEstado().equalsIgnoreCase("CONFIRMADO")
            )
            .count();

        long despachados = pedidos.stream()
            .filter(p ->
                p.getEstado().equalsIgnoreCase("DESPACHADO")
            )
            .count();

        long cancelados = pedidos.stream()
            .filter(p ->
                p.getEstado().equalsIgnoreCase("CANCELADO")
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



    //VALIDACIONES
    private void validarPedido(Pedido pedido) {

        if (pedido.getCliente() == null ||
                pedido.getCliente().trim().isEmpty()) {

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


    
    //VALIDAR PRIORIDAD
    private boolean prioridadValida(String prioridad) {

        return prioridad.equalsIgnoreCase("BAJA")
                || prioridad.equalsIgnoreCase("MEDIA")
                || prioridad.equalsIgnoreCase("ALTA")
                || prioridad.equalsIgnoreCase("URGENTE");
    }

}
