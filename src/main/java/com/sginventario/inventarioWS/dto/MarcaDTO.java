package com.sginventario.inventarioWS.dto;

import lombok.Data;
import jakarta.validation.constraints.*;

@Data
public class MarcaDTO {

    private Integer id;

    @NotBlank(message = "El código es requerido")
    @Pattern(regexp = "^[A-Z0-9]+$", message = "El código solo puede contener letras mayúsculas y números")
    @Size(max = 10, message = "El máximo de caracteres del campo código es 10")
    private String codigo;

    @NotBlank(message = "El nombre es requerido")
    @Size(max = 150, message = "El máximo de caracteres del campo nombre es 150")
    private String nombre;

    private Boolean activo;
}
