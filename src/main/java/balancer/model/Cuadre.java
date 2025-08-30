package balancer.model;

import java.time.LocalDate;

// Clase Cuadre
public class Cuadre {
    private String id;
    private String puntoVentaId;
    private LocalDate fecha;
    private double monto;
    private String observacion;
    public Cuadre(){}
    public Cuadre(String id, String puntoVentaId, LocalDate fecha, double monto){
        this.id=id; this.puntoVentaId=puntoVentaId; this.fecha=fecha; this.monto=monto;
    }
    public static Builder builder(){ return new Builder(); }
    public static class Builder{
        private String id, puntoVentaId, observacion;
        private java.time.LocalDate fecha;
        private double monto;
        public Builder id(String v){ this.id=v; return this; }
        public Builder puntoVentaId(String v){ this.puntoVentaId=v; return this; }
        public Builder fecha(java.time.LocalDate v){ this.fecha=v; return this; }
        public Builder monto(double v){ this.monto=v; return this; }
        public Builder observacion(String v){ this.observacion=v; return this; }
        public Cuadre build(){ Cuadre c = new Cuadre(id, puntoVentaId, fecha, monto); c.setObservacion(observacion); return c; }
    }
    public String getId(){ return id; }
    public void setId(String id){ this.id=id; }
    public String getPuntoVentaId(){ return puntoVentaId; }
    public void setPuntoVentaId(String puntoVentaId){ this.puntoVentaId=puntoVentaId; }
    public java.time.LocalDate getFecha(){ return fecha; }
    public void setFecha(java.time.LocalDate fecha){ this.fecha=fecha; }
    public double getMonto(){ return monto; }
    public void setMonto(double monto){ this.monto=monto; }
    public String getObservacion(){ return observacion; }
    public void setObservacion(String observacion){ this.observacion=observacion; }
}
