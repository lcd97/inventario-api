package com.sginventario.inventarioWS.service.interfaces;

import com.sginventario.inventarioWS.dto.SucursalDTO;
import java.util.List;

public interface ISucursalService {

    List<SucursalDTO> listar();

    SucursalDTO guardar(SucursalDTO dto);

    SucursalDTO actualizar(Integer id, SucursalDTO dto);

    SucursalDTO obtenerPorId(Integer id);

    void eliminar(Integer id);
}