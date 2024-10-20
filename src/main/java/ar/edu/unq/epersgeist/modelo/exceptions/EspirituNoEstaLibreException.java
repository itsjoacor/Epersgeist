package ar.edu.unq.epersgeist.modelo.exceptions;

import ar.edu.unq.epersgeist.modelo.espiritu.Espiritu;

public class EspirituNoEstaLibreException extends ModeloException {

    public EspirituNoEstaLibreException(Espiritu espiritu) {
        super("El espiritu " + espiritu.getNombre() + "ya se encuentra conectado a un medium");
    }
}
