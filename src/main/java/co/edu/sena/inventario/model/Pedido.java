package co.edu.sena.inventario.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Pedido {
     @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String cliente;
    private Long productoID;
    private Integer cantidad;
    private String prioridad;
    private String estado;
   
    public Pedido() {
}


    public Pedido(String cliente, Long productoID, Integer cantidad, String prioridad, String estado) {
        this.cliente = cliente;
        this.productoID = productoID;
        this.cantidad = cantidad;
        this.prioridad = prioridad;
        this.estado = estado;
    }

    public Long getId() { return id; }
    public String getCliente() { return cliente;}
    public Long getProductoID() { return productoID;}
    public Integer getCantidad() { return cantidad;}
    public String getPrioridad() { return prioridad;}
    public String getEstado() { return estado;}

    public void setId(Long id) { this.id = id;}
    public void setCliente( String cliente) { this.cliente = cliente;}
    public void setProductoID(Long productoID) { this.productoID = productoID;}
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad;}
    public void setPrioridad(String prioridad) { this.prioridad = prioridad;}
    public void setEstado(String estado) { this.estado = estado;}
    
}
