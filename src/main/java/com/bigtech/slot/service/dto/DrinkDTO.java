package com.bigtech.slot.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.bigtech.slot.domain.Drink} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DrinkDTO implements Serializable {

    private Long id;

    @NotNull
    private String nome;

    private String marca;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DrinkDTO)) {
            return false;
        }

        DrinkDTO drinkDTO = (DrinkDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, drinkDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DrinkDTO{" +
            "id=" + getId() +
            ", nome='" + getNome() + "'" +
            ", marca='" + getMarca() + "'" +
            "}";
    }
}
