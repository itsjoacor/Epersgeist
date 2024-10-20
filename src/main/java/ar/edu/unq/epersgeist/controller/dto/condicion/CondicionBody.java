package ar.edu.unq.epersgeist.controller.dto.condicion;

import ar.edu.unq.epersgeist.modelo.TipoDeCondicion;
import ar.edu.unq.epersgeist.modelo.condicion.Condicion;
import ar.edu.unq.epersgeist.modelo.medium.Medium;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CondicionBody {
    @NotBlank(message = "El nombre no puede estar vacio")
    @NotNull(message = "Debe ingresar un nombre")
    private TipoDeCondicion tipoDeCondicion;



    @NotNull(message = "La cantidad no puede ser nula")
    @Min(value = 0)
    private int cantidad;

    public CondicionBody() {
    }

    public CondicionBody(TipoDeCondicion tipoCondicion, int cantidad) {
        this.tipoDeCondicion = tipoCondicion;
        this.cantidad = cantidad;
    }



    public Condicion aModelo(){
        return new Condicion(
                this.tipoDeCondicion,
                this.cantidad
        );

    }
}
