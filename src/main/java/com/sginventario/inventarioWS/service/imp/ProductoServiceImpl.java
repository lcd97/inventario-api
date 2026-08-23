package com.sginventario.inventarioWS.service.imp;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.sginventario.inventarioWS.dto.ProductoDTO;
import com.sginventario.inventarioWS.entity.Marca;
import com.sginventario.inventarioWS.entity.Producto;
import com.sginventario.inventarioWS.repository.MarcaRepository;
import com.sginventario.inventarioWS.repository.ProductoRepository;
import com.sginventario.inventarioWS.service.interfaces.IProductoService;
import com.sginventario.inventarioWS.exception.*;

@Service
public class ProductoServiceImpl implements IProductoService {

    private final ProductoRepository repository;
    private final MarcaRepository marcaRepository;
    private final ModelMapper modelMapper;

    public ProductoServiceImpl(ProductoRepository repository, MarcaRepository marcaRepository,
            ModelMapper modelMapper) {
        this.repository = repository;
        this.marcaRepository = marcaRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<ProductoDTO> listar() {
        return repository.findAll()
                .stream()
                .map(this::aDto)
                .collect(Collectors.toList());
    }

    @Override
    public ProductoDTO obtenerPorId(Integer id) {
        Producto producto = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        return aDto(producto);
    }

    @Override
    public ProductoDTO guardar(ProductoDTO dto) {
        normalizarDatos(dto);
        validarDuplicados(dto);

        Producto producto = modelMapper.map(dto, Producto.class);
        producto.setActivo(true);
        producto.setMarca(obtenerMarca(dto.getMarcaId()));

        Producto saved = repository.save(producto);

        return aDto(saved);
    }

    @Override
    public ProductoDTO actualizar(Integer id, ProductoDTO dto) {
        Producto producto = obtenerEntidad(id);

        normalizarDatos(dto);
        validarDuplicadosEnEdicion(dto, producto.getId());

        producto.setSku(dto.getSku());
        producto.setNombre(dto.getNombre());
        producto.setActivo(dto.getActivo());
        producto.setStock(dto.getStock());
        producto.setMarca(obtenerMarca(dto.getMarcaId()));

        Producto updated = repository.save(producto);

        return aDto(updated);
    }

    @Override
    public void eliminar(Integer id) {
        Producto producto = obtenerEntidad(id);

        repository.delete(producto);
    }

    public List<ProductoDTO> listarActivos() {
        return repository.findByActivoTrue()
                .stream()
                .map(this::aDto)
                .collect(Collectors.toList());
    }

    private ProductoDTO aDto(Producto producto) {
        ProductoDTO dto = modelMapper.map(producto, ProductoDTO.class);

        if (producto.getMarca() != null)
            dto.setMarcaId(producto.getMarca().getId());

        return dto;
    }

    private Marca obtenerMarca(Integer marcaId) {
        return marcaRepository.findById(marcaId)
                .orElseThrow(() -> new ResourceNotFoundException("Marca no encontrada"));
    }

    private Producto obtenerEntidad(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Producto no encontrado"));
    }

    private void normalizarDatos(ProductoDTO dto) {
        dto.setNombre(dto.getNombre().trim().toUpperCase());
        dto.setSku(dto.getSku().trim().toUpperCase());
    }

    private void validarDuplicados(ProductoDTO dto) {
        validarNombreUnico(dto.getNombre(), dto.getMarcaId());
        validarSku(dto.getSku());
    }

    private void validarNombreUnico(String nombre, Integer marcaId) {
        if (repository.existsByNombreIgnoreCaseAndMarca_Id(nombre, marcaId)) {
            throw new BadRequestException("Ya existe un producto con ese nombre y marca en el sistema");
        }
    }

    private void validarSku(String sku) {
        if (repository.existsBySku(sku))
            throw new BadRequestException("El SKU ya se encuentra registrado en el sistema");
    }

    private void validarDuplicadosEnEdicion(ProductoDTO dto, Integer idActual) {
        validarNombreUnicoEnEdicion(dto.getNombre(), dto.getMarcaId(), idActual);
        validarSkuUnicoEnEdicion(dto.getSku(), idActual);
    }

    private void validarNombreUnicoEnEdicion(String nombre, Integer marcaId, Integer idActual) {
        boolean existe = repository.existsByNombreIgnoreCaseAndMarca_IdAndIdNot(nombre, marcaId, idActual);

        if (existe) {
            throw new BadRequestException("Ya existe un producto con ese nombre y marca en el sistema");
        }
    }

    private void validarSkuUnicoEnEdicion(String sku, Integer idActual) {
        boolean existe = repository.existsBySkuAndIdNot(sku, idActual);

        if (existe) {
            throw new BadRequestException("El SKU ya se encuentra registrado en el sistema");
        }
    }
}
