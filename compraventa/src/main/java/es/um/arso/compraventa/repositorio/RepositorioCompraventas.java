package es.um.arso.compraventa.repositorio;

import es.um.arso.compraventa.modelo.Compraventa;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RepositorioCompraventas extends MongoRepository<Compraventa, String> {

    Page<Compraventa> findByIdComprador(String idComprador, Pageable pageable);

    Page<Compraventa> findByIdVendedor(String idVendedor, Pageable pageable);

    Page<Compraventa> findByIdCompradorAndIdVendedor(
            String idComprador, String idVendedor, Pageable pageable);

    List<Compraventa> findByIdComprador(String idComprador);

    List<Compraventa> findByIdVendedor(String idVendedor);

    List<Compraventa> findByIdCompradorAndIdVendedor(String idComprador, String idVendedor);
}
