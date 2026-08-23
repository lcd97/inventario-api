package com.sginventario.inventarioWS.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.sginventario.inventarioWS.entity.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    boolean existsByNombreIgnoreCaseAndMarca_Id(String nombre, Integer marcaId);

    boolean existsByNombreIgnoreCaseAndMarca_IdAndIdNot(String nombre, Integer marcaId, Integer id);

    boolean existsBySkuAndIdNot(String sku, Integer id);

    boolean existsByMarca_Id(Integer marcaId);

    boolean existsBySku(String sku);

    Optional<Producto> findByNombreIgnoreCase(String nombre);

    Optional<Producto> findBySku(String sku);

    List<Producto> findByActivoTrue();
}