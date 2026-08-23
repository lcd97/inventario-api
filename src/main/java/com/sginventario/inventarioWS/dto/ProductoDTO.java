package com.sginventario.inventarioWS.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProductoDTO {
    private Integer id;

    @NotBlank(message = "El SKU es requerido")
    @Size(max = 15, message = "El máximo de caracteres del campo es 15")
    private String sku;

    @NotBlank(message = "El nombre es requerido")
    @Size(max = 50, message = "El máximo de caracteres del campo es 50")
    private String nombre;

    @NotNull(message = "La marca es requerida")
    private Integer marcaId;

    private Boolean activo;

    @NotNull(message = "El stock es requerido")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;
}
