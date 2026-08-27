package co.edu.sena.inventario.model;

public class PedidoResumen {

    private long totalPedidos;
    private long pendientes;
    private long confirmados;
    private long despachados;
    private long cancelados;
    private long urgentes;

    public PedidoResumen(
            long totalPedidos,
            long pendientes,
            long confirmados,
            long despachados,
            long cancelados,
            long urgentes) {

        this.totalPedidos = totalPedidos;
        this.pendientes = pendientes;
        this.confirmados = confirmados;
        this.despachados = despachados;
        this.cancelados = cancelados;
        this.urgentes = urgentes;
    }

    public long getTotalPedidos() { return totalPedidos; }
    public long getPendientes() { return pendientes; }
    public long getConfirmados() { return confirmados; }
    public long getDespachados() { return despachados; }
    public long getCancelados() { return cancelados; }
    public long getUrgentes() { return urgentes; }
}
