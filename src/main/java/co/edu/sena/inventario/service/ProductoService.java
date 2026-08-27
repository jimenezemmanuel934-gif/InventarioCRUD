package co.edu.sena.inventario.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import co.edu.sena.inventario.model.Producto;

@Service
public class ProductoService {

    private final List<Producto> productos = new ArrayList<>(List.of(
        new Producto(1L, "Papa pastusa", 2500.0, 8, "Tuberculo"),
        new Producto(2L, "Tomate", 3200.0, 30, "Vegetal"),
        new Producto(3L, "Fresa", 8500.0, 20, "Fruta"),
        new Producto(4L, "Mango", 4500.0, 24, "Fruta"),
        new Producto(5L, "Zanahoria", 3900.0, 42, "Vegetal")
    ));

    public List<Producto> listarProductos() {
        return productos;
    }

    public Producto buscarPorId(Long id) {

        for (Producto producto : productos) {

            if (producto.getId().equals(id)) {
                return producto;
            }
        }

        return null;
    }

    public void agregarProducto(Producto producto) {
        productos.add(producto);
    }

    public void eliminarProducto(Long id) {

        productos.removeIf(producto ->
            producto.getId().equals(id)
        );
    }
}
