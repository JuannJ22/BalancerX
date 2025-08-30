package balancer.model;

public class Usuario {
    private String id;
    private String nombre;
    private String username;
    private String passwordHash;
    private String rol;
    public Usuario(){}
    public Usuario(String id, String nombre, String username, String passwordHash, String rol) {
        this.id = id; this.nombre = nombre; this.username = username; this.passwordHash = passwordHash; this.rol = rol;
    }
    public static Builder builder(){ return new Builder(); }
    public static class Builder{
        private String id, nombre, username, passwordHash, rol;
        public Builder id(String v){ this.id=v; return this; }
        public Builder nombre(String v){ this.nombre=v; return this; }
        public Builder username(String v){ this.username=v; return this; }
        public Builder passwordHash(String v){ this.passwordHash=v; return this; }
        public Builder rol(String v){ this.rol=v; return this; }
        public Usuario build(){ return new Usuario(id,nombre,username,passwordHash,rol); }
    }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}
