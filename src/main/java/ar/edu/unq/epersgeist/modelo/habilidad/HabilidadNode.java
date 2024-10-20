package ar.edu.unq.epersgeist.modelo.habilidad;

import ar.edu.unq.epersgeist.modelo.Mutacion;
import ar.edu.unq.epersgeist.modelo.espiritu.Espiritu;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Node("HabilidadNode")
public class HabilidadNode {

    @Id
    @GeneratedValue
    private Long id;

    private String nombre;

    private Long idSQL;
    @Relationship(type = "Puede_Mutar_A", direction = Relationship.Direction.OUTGOING)
    private List<Mutacion> posiblesMutaciones;

    public HabilidadNode(String nombre) {
        this.nombre = nombre;
        this.posiblesMutaciones = new ArrayList<Mutacion>();
    }

    public HabilidadNode(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
        this.posiblesMutaciones = new ArrayList<Mutacion>();
    }

    public void agregarMutacion(Mutacion mutacion) {
        this.posiblesMutaciones.add(mutacion);
    }

    public void comprobarEvolucion(Espiritu espiritu) {
        this.posiblesMutaciones.forEach(mutacion -> mutacion.comprobarEvolucion(espiritu));
    }
}
