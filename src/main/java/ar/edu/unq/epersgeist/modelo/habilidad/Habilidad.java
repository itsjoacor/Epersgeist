package ar.edu.unq.epersgeist.modelo.habilidad;


import ar.edu.unq.epersgeist.modelo.espiritu.Espiritu;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class Habilidad {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(unique = true, nullable = false)
        private String nombre;

        public Habilidad(@NonNull String nombre) {
                this.nombre = nombre;
        }


}
