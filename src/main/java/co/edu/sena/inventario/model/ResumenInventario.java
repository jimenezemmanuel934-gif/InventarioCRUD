package co.edu.sena.inventario.model;

public class ResumenInventario {

    private int totalProductos;
    private int productosStockBajo;
    private String productoMasCostoso;
    private String productoMasEconomico;

    public ResumenInventario(int totalProductos, int productosStockBajo,
  String productoMasCostoso, String productoMasEconomico) {

        this.totalProductos = totalProductos;
        this.productosStockBajo = productosStockBajo;
        this.productoMasCostoso = productoMasCostoso;
        this.productoMasEconomico = productoMasEconomico;
    }

    public int getTotalProductos() {
        return totalProductos;
    }

    public int getProductosStockBajo() {
        return productosStockBajo;
    }

    public String getProductoMasCostoso() {
        return productoMasCostoso;
    }

    public String getProductoMasEconomico() {
        return productoMasEconomico;
    }
}