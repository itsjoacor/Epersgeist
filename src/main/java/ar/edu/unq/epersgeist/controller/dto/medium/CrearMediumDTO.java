package ar.edu.unq.epersgeist.controller.dto.medium;


import ar.edu.unq.epersgeist.modelo.medium.Medium;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CrearMediumDTO {
    @NotBlank(message = "El nombre no puede estar vacio")
    @NotNull(message = "Debe ingresar un nombre")
    private String       nombre;


    @NotNull(message = "Debe ingresar una mana")
    @PositiveOrZero(message = "El mana no puede ser negativo")
    private int          mana;

    @NotNull(message = "Debe ingresar un id de ubicacion")
    private Long ubicacionID;

    public CrearMediumDTO() {
    }

    public CrearMediumDTO(String nombre, int mana, Long ubicacionID) {
        this.nombre = nombre;
        this.mana = mana;
        this.ubicacionID = ubicacionID;
    }

    public static CrearMediumDTO desdeModelo(Medium medium) {
        return new CrearMediumDTO(
                medium.getNombre(),
                medium.getMana(),
                medium.getUbicacion().getId()
        );
    }

    public Medium aModelo(){
       return new Medium(
                this.nombre,
                this.mana
        );

    }
}



