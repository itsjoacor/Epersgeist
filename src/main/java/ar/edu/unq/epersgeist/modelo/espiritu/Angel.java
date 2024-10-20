package ar.edu.unq.epersgeist.modelo.espiritu;


import ar.edu.unq.epersgeist.modelo.medium.Medium;
import ar.edu.unq.epersgeist.modelo.random.Random;
import ar.edu.unq.epersgeist.modelo.random.Randomizer;
import ar.edu.unq.epersgeist.modelo.ubicacion.Cementerio;
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
@DiscriminatorValue("ANGEL")
public class Angel extends Espiritu {

    private int exorcismosResueltos = 0;

    public Angel(String nombre, Ubicacion ubicacion, Integer energia) {
        super(nombre, ubicacion, energia);
    }

    public Angel(String nombre, int energia, int nivelDeConexion, Ubicacion ubicacion, Medium medium, Long id) {
        super(nombre,energia,nivelDeConexion,ubicacion,medium,id);
    }

    public Angel (String nombre, int energia) {
        super(nombre,energia);
    }

    public Angel(Long id, String nombre, Ubicacion ubicacion, Integer energia, Integer nivelDeConexion){
        super(id, nombre, ubicacion, energia, nivelDeConexion);
    }

    private boolean tieneEnergiaSuficienteParaAtacar() {
        return this.energia >= 10;
    }
    public void atacarA(Demonio espiritu) {
        if (this.tieneEnergiaSuficienteParaAtacar()){

            Randomizer randomizer = Random.getInstance().getStrategy();

            int porcentajeDeAtaqueExitoso  = randomizer.getNro() + this.nivelDeConexion;

            if (porcentajeDeAtaqueExitoso > 66) {
                espiritu.disminuirEnergiaPorAtaque(this.nivelDeConexion / 2);
            }

            this.disminuirEnergia(10);
        }
    }

    public void invocarseEn(Ubicacion ubicacion){
        ubicacion.validarEspiritu(this);
        this.setUbicacion(ubicacion);
    }

    @Override
    public void mover(Ubicacion ubicacion) {
        if (ubicacion.esCementerio()) {
            disminuirEnergia(5);
        }
        super.mover(ubicacion);
    }

    @Override
    public boolean esAngel() {
        return true;
    }

    public void exorcismoResuelto() {
        this.exorcismosResueltos++;
    }

}
