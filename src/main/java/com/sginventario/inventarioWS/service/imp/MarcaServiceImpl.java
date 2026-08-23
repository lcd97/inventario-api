package com.sginventario.inventarioWS.service.imp;

import com.sginventario.inventarioWS.dto.MarcaDTO;
import com.sginventario.inventarioWS.entity.Marca;
import com.sginventario.inventarioWS.repository.MarcaRepository;
import com.sginventario.inventarioWS.repository.ProductoRepository;
import com.sginventario.inventarioWS.service.interfaces.IMarcaService;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import com.sginventario.inventarioWS.exception.*;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MarcaServiceImpl implements IMarcaService {

    private final MarcaRepository repository;
    private final ProductoRepository productoRepository;
    private final ModelMapper modelMapper;

    public MarcaServiceImpl(MarcaRepository repository, ProductoRepository productoRepository,
            ModelMapper modelMapper) {
        this.repository = repository;
        this.productoRepository = productoRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<MarcaDTO> listar() {
        return repository.findAll()
                .stream()
                .map(m -> modelMapper.map(m, MarcaDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public MarcaDTO guardar(MarcaDTO dto) {
        normalizarDatos(dto);
        validarDuplicados(dto);

        Marca marca = modelMapper.map(dto, Marca.class);
        marca.setActivo(true);

        Marca saved = repository.save(marca);

        return modelMapper.map(saved, MarcaDTO.class);
    }

    @Override
    public MarcaDTO actualizar(Integer id, MarcaDTO dto) {

        Marca marca = obtenerEntidad(id);

        normalizarDatos(dto);

        validarDuplicadosEnEdicion(dto, marca.getId());

        marca.setCodigo(dto.getCodigo());
        marca.setNombre(dto.getNombre());
        marca.setActivo(dto.getActivo());

        Marca updated = repository.save(marca);

        return modelMapper.map(updated, MarcaDTO.class);
    }

    @Override
    public void eliminar(Integer id) {
        Marca marca = obtenerEntidad(id);

        if (productoRepository.existsByMarca_Id(id)) {
            throw new BadRequestException("No se puede eliminar la marca porque tiene productos asociados");
        }

        repository.delete(marca);
    }

    @Override
    public MarcaDTO obtenerPorId(Integer id) {
        Marca marca = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Marca no encontrada"));

        return modelMapper.map(marca, MarcaDTO.class);
    }

    public List<MarcaDTO> listarActivos() {
        return repository.findByActivoTrue()
                .stream()
                .map(m -> modelMapper.map(m, MarcaDTO.class))
                .collect(Collectors.toList());
    }

    private Marca obtenerEntidad(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Marca no encontrada"));
    }

    private void validarDuplicados(MarcaDTO dto) {
        validarNombreUnico(dto.getNombre());
        validarCodigoUnico(dto.getCodigo());
    }

    private void validarNombreUnico(String nombre) {
        if (repository.existsByNombreIgnoreCaseAndActivoTrue(nombre)) {
            throw new BadRequestException("El nombre ya se encuentra registrado en el sistema");
        }
    }

    private void validarCodigoUnico(String codigo) {
        if (repository.existsByCodigo(codigo)) {
            throw new BadRequestException("El código ya se encuentra registrado en el sistema");
        }
    }

    private void normalizarDatos(MarcaDTO dto) {
        dto.setNombre(dto.getNombre().trim().toUpperCase());
        dto.setCodigo(dto.getCodigo().trim().toUpperCase());
    }

    private void validarDuplicadosEnEdicion(MarcaDTO dto, Integer idActual) {
        validarNombreUnicoEnEdicion(dto.getNombre(), idActual);
        validarCodigoUnicoEnEdicion(dto.getCodigo(), idActual);
    }

    private void validarNombreUnicoEnEdicion(String nombre, Integer idActual) {
        if (repository.existsByNombreIgnoreCaseAndActivoTrueAndIdNot(nombre, idActual)) {
            throw new BadRequestException("El nombre ya se encuentra registrado en el sistema");
        }
    }

    private void validarCodigoUnicoEnEdicion(String codigo, Integer idActual) {
        if (repository.existsByCodigoAndIdNot(codigo, idActual)) {
            throw new BadRequestException("El código ya se encuentra registrado en el sistema");
        }
    }
}
