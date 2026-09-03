package co.edu.sena.inventario.service;

import java.util.List;

import org.springframework.stereotype.Service;

import co.edu.sena.inventario.model.Producto;
import co.edu.sena.inventario.repository.ProductoRepository;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    //Consultar Todos
    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }

    //Buscar por ID
    public Producto buscarPorId(Long id) {
        return productoRepository.findById(id).orElse(null);
    }

    //Crear Producto
    public void agregarProducto(Producto producto) {
        if (producto.getId() !=null && productoRepository.existsById(producto.getId())) {

            throw new IllegalArgumentException(
                "Ya existe un producto con el ID: " + producto.getId()
            );
        }
        productoRepository.save(producto);
    }

    //Eliminar producto
    public void eliminarProducto(Long id) {
        if (productoRepository.existsById(id)) {
            productoRepository.deleteById(id);
        }
    }

}
