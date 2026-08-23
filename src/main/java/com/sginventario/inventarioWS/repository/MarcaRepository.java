package com.sginventario.inventarioWS.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.sginventario.inventarioWS.entity.Marca;

public interface MarcaRepository extends JpaRepository<Marca, Integer> {

    boolean existsByCodigo(String codigo);

    boolean existsByCodigoAndIdNot(String codigo, Integer id);

    boolean existsByNombreIgnoreCaseAndActivoTrue(String nombre);

    boolean existsByNombreIgnoreCaseAndActivoTrueAndIdNot(String nombre, Integer id);

    List<Marca> findByActivoTrue();
}
