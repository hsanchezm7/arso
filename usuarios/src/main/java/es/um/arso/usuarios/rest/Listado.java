package es.um.arso.usuarios.rest;

import es.um.arso.usuarios.servicio.UsuarioResumen;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Listado {

    private List<ResumenExtendido> usuario;

    public List<ResumenExtendido> getUsuario() {
        return usuario;
    }

    public void setUsuario(List<ResumenExtendido> usuario) {
        this.usuario = usuario;
    }

    public static class ResumenExtendido {

        private String url;
        private UsuarioResumen resumen;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public UsuarioResumen getResumen() {
            return resumen;
        }

        public void setResumen(UsuarioResumen resumen) {
            this.resumen = resumen;
        }
    }
}
