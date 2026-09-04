package co.edu.sena.inventario.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.sena.inventario.model.Pedido;
import co.edu.sena.inventario.model.PedidoResumen;
import co.edu.sena.inventario.service.PedidoService;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }


    //CREAR PEDIDO
    @PostMapping
    public ResponseEntity<?> crearPedido(@RequestBody Pedido pedido) {

        try {

            Pedido nuevoPedido = pedidoService.crearPedido(pedido);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(nuevoPedido);

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }



    //LISTAR PEDIDOS
    @GetMapping
    public List<Pedido> listarPedidos() {

        return pedidoService.listarPedidos();
    }



    //BUSCAR PEDIDO POR ID
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPedido(@PathVariable Long id) {

        try {

            Pedido pedido = pedidoService.buscarPedido(id);

            return ResponseEntity.ok(pedido);

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }



    //CONFIRMAR PEDIDO
    @PutMapping("/{id}/confirmar")
    public ResponseEntity<?> confirmarPedido(@PathVariable Long id) {

        try {

            Pedido pedido = pedidoService.confirmarPedido(id);

            return ResponseEntity.ok(pedido);

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());

        } catch (IllegalStateException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }


    //CANCELAR PEDIDO
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelarPedido(@PathVariable Long id) {

        try {

            Pedido pedido = pedidoService.cancelarPedido(id);

            return ResponseEntity.ok(pedido);

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());

        } catch (IllegalStateException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }



    //DESPACHAR PEDIDO
    @PutMapping("/{id}/despachar")
    public ResponseEntity<?> despacharPedido(@PathVariable Long id) {

        try {

            Pedido pedido = pedidoService.despacharPedido(id);

            return ResponseEntity.ok(pedido);

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());

        } catch (IllegalStateException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }



    //PEDIDOS PENDIENTES
    @GetMapping("/pendientes")
    public List<Pedido> pendientes() {

        return pedidoService.pendientes();
    }



    //PEDIDOS URGENTES
    @GetMapping("/urgentes")
    public List<Pedido> urgentes() {

        return pedidoService.urgentes();
    }



    //BUSCAR POR ESTADO
    @GetMapping("/estado")
    public ResponseEntity<?> porEstado(
            @RequestParam String estado) {

        List<Pedido> resultado =
                pedidoService.porEstado(estado);

        return ResponseEntity.ok(resultado);
    }



    //RESUMEN DE PEDIDOS
    @GetMapping("/resumen")
    public PedidoResumen resumen() {

        return pedidoService.resumen();
    }



    //SIGUIENTE PEDIDO
    @GetMapping("/siguiente")
    public ResponseEntity<?> siguiente() {

        Pedido pedido = pedidoService.siguiente();

        if (pedido == null) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("No hay pedidos pendientes");

        }

        return ResponseEntity.ok(pedido);
    }



    //PEDIDOS EN RIESGO
    @GetMapping("/en-riesgo")
    public List<Pedido> enRiesgo() {

        return pedidoService.enRiesgo();
    }

}
