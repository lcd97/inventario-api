package com.sginventario.inventarioWS.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Marca {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "El código es requerido")
    @Size(max = 10, message = "El máximo de caracteres del campo código es 10")
    private String codigo;

    @NotBlank(message = "El nombre es requerido")
    @Size(max = 150, message = "El máximo de caracteres del campo nombre es 150")
    private String nombre;

    @Column(nullable = false)
    private Boolean activo;

    @JsonIgnore
    @OneToMany(mappedBy = "marca", fetch = FetchType.LAZY)
    private List<Producto> productos;
}
