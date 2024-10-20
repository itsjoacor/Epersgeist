package ar.edu.unq.epersgeist.modelo;

import ar.edu.unq.epersgeist.modelo.habilidad.Habilidad;
import ar.edu.unq.epersgeist.modelo.habilidad.HabilidadNode;
import ar.edu.unq.epersgeist.modelo.espiritu.Espiritu;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;


@NoArgsConstructor
@Getter
@Setter
@RelationshipProperties
public class Mutacion {

    @RelationshipId
    private Long id;

    @Property("tipoDeCondicion")
    private TipoDeCondicion tipoDeCondicion;

    @Property("cantidad")
    @Min(value = 0)
    private int cantidad;

    @TargetNode
    private HabilidadNode habilidadDestino;

    private boolean evolucionada;

    public Mutacion(TipoDeCondicion tipoDeCondicion, int cantidad, HabilidadNode habilidadDestino) {
        this.tipoDeCondicion = tipoDeCondicion;
        this.cantidad = cantidad;
        this.habilidadDestino = habilidadDestino;
        this.evolucionada = false;
    }

    public void comprobarEvolucion(Espiritu espiritu) {
        if (evolucionada) {
            return;
        }

        if (condicionCumplida(espiritu)) {
            Habilidad habilidad = new Habilidad(habilidadDestino.getNombre());
            habilidad.setId(habilidadDestino.getIdSQL());
            espiritu.addHabilidad(habilidad);
            this.evolucionada = true;
        }
    }

    private boolean condicionCumplida(Espiritu espiritu) {
        return switch (this.tipoDeCondicion) {
            case EXORCISMOS_RESUELTOS -> espiritu.getExorcismosResueltos() >= cantidad;
            case EXORCISMOS_EVITADOS -> espiritu.getExorcismosEvitados() >= cantidad;
            case CANTIDAD_DE_ENERGIA -> espiritu.getEnergia() >= cantidad;
            case NIVEL_DE_CONEXION -> espiritu.getNivelDeConexion() >= cantidad;
            default -> false;
        };
    }
}
