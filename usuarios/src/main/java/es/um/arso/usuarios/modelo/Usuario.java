package es.um.arso.usuarios.modelo;

import es.um.arso.repositorio.Identificable;
import es.um.arso.utils.LocalDateAdapter;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.eclipse.persistence.annotations.UuidGenerator;

@Entity
@XmlRootElement(name = "usuario")
@UuidGenerator(name = "uuid")
public class Usuario implements Identificable {
    @Id
    @GeneratedValue(generator = "uuid")
    private String id;

    @Column(unique = true)
    private String email;

    private String nombre;
    private String apellidos;
    private String clave;
    private LocalDate fechaNacimiento;
    private String telefono;
    private String githubId;

    private boolean administrador = false;

    private int nCompras = 0;
    private int nVentas = 0;

    // valoraciones recibidas, no emitidas
    private int nValoracionesAsComprador = 0;
    private int nValoracionesAsVendedor = 0;

    private double puntuacionAsComprador = 0.0;
    private double puntuacionAsVendedor = 0.0;

    public Usuario() {}

    public Usuario(String email, String nombre) {
        this.email = email;
        this.nombre = nombre;
    }

    public Usuario(String email, String nombre, String apellidos) {
        this.email = email;
        this.nombre = nombre;
        this.apellidos = apellidos;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getGithubId() {
        return githubId;
    }

    public void setGithubId(String githubId) {
        this.githubId = githubId;
    }

    public boolean isAdministrador() {
        return administrador;
    }

    public void setAdministrador(boolean administrador) {
        this.administrador = administrador;
    }

    public int getNumeroCompras() {
        return nCompras;
    }

    public void setNumeroCompras(int numeroCompras) {
        this.nCompras = numeroCompras;
    }

    public void incrementarNumeroCompras() {
        this.nCompras++;
    }

    public int getNumeroVentas() {
        return nVentas;
    }

    public void setNumeroVentas(int numeroVentas) {
        this.nVentas = numeroVentas;
    }

    public void incrementarNumeroVentas() {
        this.nVentas++;
    }

    public int getNumeroValoracionesAsComprador() {
        return nValoracionesAsComprador;
    }

    public void setNumeroValoracionesAsComprador(int nValoracionesAsComprador) {
        this.nValoracionesAsComprador = nValoracionesAsComprador;
    }

    public int getNumeroValoracionesAsVendedor() {
        return nValoracionesAsVendedor;
    }

    public void setNumeroValoracionesAsVendedor(int nValoracionesAsVendedor) {
        this.nValoracionesAsVendedor = nValoracionesAsVendedor;
    }

    public double getPuntuacionAsComprador() {
        return puntuacionAsComprador;
    }

    public void setPuntuacionAsComprador(double puntuacionAsComprador) {
        this.puntuacionAsComprador = puntuacionAsComprador;
    }

    public double getPuntuacionAsVendedor() {
        return puntuacionAsVendedor;
    }

    public void setPuntuacionAsVendedor(double puntuacionAsVendedor) {
        this.puntuacionAsVendedor = puntuacionAsVendedor;
    }

    /* incrementar el número de puntaciones y actualizar puntuación */
    public void valorar(int puntuacion, String as) {
        if (as.equals("comprador"))
            this.puntuacionAsComprador = ((this.puntuacionAsComprador * this.puntuacionAsComprador) + puntuacion)
                    / ++this.nValoracionesAsComprador;
        else if (as.equals("vendedor"))
            this.puntuacionAsVendedor = ((this.puntuacionAsVendedor * this.puntuacionAsVendedor) + puntuacion)
                    / ++this.nValoracionesAsVendedor;
    }

    @Override
    public String toString() {
        return "Usuario [id="
                + id
                + ", email="
                + email
                + ", nombre="
                + nombre
                + ", apellidos="
                + apellidos
                + ", clave="
                + clave
                + ", fechaNacimiento="
                + fechaNacimiento
                + ", telefono="
                + telefono
                + ", githubId="
                + githubId
                + ", administrador="
                + administrador
                + ", numeroCompras="
                + nCompras
                + ", numeroVentas="
                + nVentas
                + "]";
    }
}
