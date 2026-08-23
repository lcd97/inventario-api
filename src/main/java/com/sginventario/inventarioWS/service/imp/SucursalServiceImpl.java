package com.sginventario.inventarioWS.service.imp;

import com.sginventario.inventarioWS.dto.SucursalDTO;
import com.sginventario.inventarioWS.entity.Sucursal;
import com.sginventario.inventarioWS.repository.SucursalRepository;
import com.sginventario.inventarioWS.service.interfaces.ISucursalService;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import com.sginventario.inventarioWS.exception.*;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SucursalServiceImpl implements ISucursalService {
    private final SucursalRepository repository;
    private final ModelMapper modelMapper;

    public SucursalServiceImpl(SucursalRepository repository, ModelMapper modelMapper) {
        this.repository = repository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<SucursalDTO> listar() {
        return repository.findAll()
                .stream()
                .map(s -> modelMapper.map(s, SucursalDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public SucursalDTO guardar(SucursalDTO dto) {
        normalizarDatos(dto);
        validarDuplicados(dto);

        Sucursal sucursal = modelMapper.map(dto, Sucursal.class);
        sucursal.setActivo(true);

        Sucursal saved = repository.save(sucursal);
        return modelMapper.map(saved, SucursalDTO.class);
    }

    @Override
    public SucursalDTO actualizar(Integer id, SucursalDTO dto) {

        Sucursal sucursal = obtenerEntidad(id);

        normalizarDatos(dto);

        validarDuplicadosEnEdicion(dto, sucursal);

        sucursal.setNombre(dto.getNombre());
        sucursal.setCodigo(dto.getCodigo());
        sucursal.setDireccion(dto.getDireccion());
        sucursal.setActivo(dto.getActivo());

        Sucursal updated = repository.save(sucursal);

        return modelMapper.map(updated, SucursalDTO.class);
    }

    @Override
    public void eliminar(Integer id) {
        Sucursal sucursal = obtenerEntidad(id);

        repository.delete(sucursal);
    }

    @Override
    public SucursalDTO obtenerPorId(Integer id) {
        Sucursal sucursal = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada"));

        return modelMapper.map(sucursal, SucursalDTO.class);
    }

    private Sucursal obtenerEntidad(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Sucursal no encontrada"));
    }

    private void validarDuplicados(SucursalDTO dto) {
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

    private void normalizarDatos(SucursalDTO dto) {
        dto.setNombre(dto.getNombre().trim().toUpperCase());
        dto.setCodigo(dto.getCodigo().trim().toUpperCase());

        if (dto.getDireccion() != null)
            dto.setDireccion(dto.getDireccion().trim());
    }

    private void validarDuplicadosEnEdicion(SucursalDTO dto, Sucursal sucursal) {
        validarNombreUnicoEnEdicion(dto.getNombre(), sucursal.getId());
        validarCodigoUnicoEnEdicion(dto.getCodigo(), sucursal.getId());
    }

    private void validarNombreUnicoEnEdicion(String nombre, Integer idActual) {

        boolean existe = repository.existsByNombreIgnoreCaseAndActivoTrue(nombre);

        if (existe) {
            Sucursal existente = repository.findByNombreIgnoreCase(nombre).get();

            if (!existente.getId().equals(idActual)) {
                throw new BadRequestException("El nombre ya se encuentra registrado en el sistema");
            }
        }
    }

    private void validarCodigoUnicoEnEdicion(String codigo, Integer idActual) {

        boolean existe = repository.existsByCodigo(codigo);

        if (existe) {
            Sucursal existente = repository.findByCodigo(codigo).get();

            if (!existente.getId().equals(idActual)) {
                throw new BadRequestException("El código ya se encuentra registrado en el sistema");
            }
        }
    }

    public List<SucursalDTO> listarActivos() {
        return repository.findByActivoTrue()
                .stream()
                .map(s -> modelMapper.map(s, SucursalDTO.class))
                .collect(Collectors.toList());
    }

}