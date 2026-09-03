package co.edu.sena.inventario.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private Double precio;
    private Integer cantidad;
    private String categoria;

    //Constructor vacio requerido por JPA
    public Producto(){}

    //Constructo para crear productos
public Producto(String nombre, Double precio, Integer cantidad, String categoria) {
    this.nombre = nombre;
    this.precio = precio;
    this.cantidad = cantidad;
    this.categoria = categoria;
}

public Long getId() { return id; }
public String getNombre() { return nombre; }
public Double getPrecio() { return precio; }
public Integer getCantidad() { return cantidad; }
public String getCategoria() { return categoria; }

public void setNombre(String nombre) { this.nombre = nombre; }

public void setPrecio(Double precio) { this.precio = precio; }

public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

public void setCategoria(String categoria) { this.categoria = categoria; }

}