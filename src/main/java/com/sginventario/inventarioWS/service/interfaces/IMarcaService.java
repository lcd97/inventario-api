package com.sginventario.inventarioWS.service.interfaces;

import com.sginventario.inventarioWS.dto.MarcaDTO;
import java.util.List;

public interface IMarcaService {

    List<MarcaDTO> listar();

    MarcaDTO guardar(MarcaDTO dto);

    MarcaDTO actualizar(Integer id, MarcaDTO dto);

    MarcaDTO obtenerPorId(Integer id);

    void eliminar(Integer id);
}
