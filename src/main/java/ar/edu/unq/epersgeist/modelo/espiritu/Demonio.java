package ar.edu.unq.epersgeist.modelo.espiritu;

import ar.edu.unq.epersgeist.modelo.medium.Medium;
import ar.edu.unq.epersgeist.modelo.ubicacion.Santuario;
import ar.edu.unq.epersgeist.modelo.ubicacion.Ubicacion;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
@DiscriminatorValue("DEMONIO")
public class Demonio  extends Espiritu {


    private int exorcismosEvitados = 0;

    public Demonio(String nombre, Ubicacion ubicacion, Integer energia) {
        super(nombre, ubicacion, energia);
    }

    public Demonio(String nombre, int energia, int nivelDeConexion, Ubicacion ubicacion, Medium medium, Long id) {
        super(nombre,energia,nivelDeConexion,ubicacion,medium,id);
    }

    public Demonio(String nombre, int energia){
        super(nombre,energia);
    }

    public Demonio(Long id, String nombre, Ubicacion ubicacion, Integer energia, Integer nivelDeConexion){
        super(id, nombre, ubicacion, energia, nivelDeConexion);
    }

    public void disminuirEnergiaPorAtaque(int cantidad) {

        super.disminuirEnergia(cantidad);

        if (this.energia <= 0) {
            this.medium.desconectarseDeEspiritu(this);
            this.medium = null;
            this.nivelDeConexion = 0;
        }
    }

    public void invocarseEn(Ubicacion ubicacion){
        ubicacion.validarEspiritu(this);
        this.setUbicacion(ubicacion);
    }

    @Override
    public void mover(Ubicacion ubicacion) {
        if (ubicacion.esSantuario()) {
            disminuirEnergia(10);

        }
        super.mover(ubicacion);
    }

    @Override
    public boolean esDemonio() {
        return true;
    }

    public void exorcismoEvitado() {
        this.exorcismosEvitados++;
    }
}
