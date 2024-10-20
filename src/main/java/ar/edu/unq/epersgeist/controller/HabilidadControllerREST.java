package ar.edu.unq.epersgeist.controller;

import ar.edu.unq.epersgeist.controller.dto.condicion.CondicionBody;
import ar.edu.unq.epersgeist.modelo.TipoDeCondicion;

import ar.edu.unq.epersgeist.controller.dto.habilidad.CrearHabilidadDTO;
import ar.edu.unq.epersgeist.controller.dto.habilidad.HabilidadDTO;
import ar.edu.unq.epersgeist.modelo.condicion.Condicion;

import ar.edu.unq.epersgeist.modelo.habilidad.HabilidadNode;
import ar.edu.unq.epersgeist.servicios.interfaces.HabilidadService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("/habilidad")
public class HabilidadControllerREST {

    private final HabilidadService habilidadService;

    public HabilidadControllerREST(HabilidadService habilidadService) {
        this.habilidadService = habilidadService;
    }

    @PostMapping
    public ResponseEntity<HabilidadDTO> crearHabilidad(@Valid @RequestBody CrearHabilidadDTO crearHabilidadDTO) {
        HabilidadNode espirituCreado = habilidadService.crear(crearHabilidadDTO.aModelo());
        return ResponseEntity.ok(HabilidadDTO.desdeModelo(espirituCreado));
    }

    @PutMapping("/{nombreHabilidadOrigen}/descubrirHabilidad/{nombreHabilidadDestino}")
    public void descubrirHabilidad(@PathVariable String nombreHabilidadOrigen, @PathVariable String nombreHabilidadDestino, @RequestBody CondicionBody condicion){

        Condicion condicionModelo = condicion.aModelo();
        habilidadService.descubrirHabilidad(nombreHabilidadOrigen, nombreHabilidadDestino, condicionModelo);
    }

    @PutMapping("/evolucionarEspiritu/{idEspiritu}")
    public void evolucionar(@PathVariable Long idEspiritu){
        habilidadService.evolucionar(idEspiritu);
    }


    @GetMapping("/{nombre}/conectadas")
    public Set<HabilidadDTO> habilidadesConectadas(@PathVariable String nombre) {
        return habilidadService.habilidadesConectadas(nombre)
                .stream()
                .map(HabilidadDTO::desdeModelo).collect(Collectors.toSet());

    }

    @GetMapping("/habilidadesPosiblesDelEspiritu/{idEspiritu}")
    public ResponseEntity<Set<HabilidadDTO>> habilidadesPosiblesDelEspiritu(@PathVariable Long idEspiritu) {
        Set<HabilidadNode> habilidadesPosibles = habilidadService.habilidadesPosibles(idEspiritu);
        Set<HabilidadDTO> habilidadesDTO = habilidadesPosibles.stream()
                .map(HabilidadDTO::desdeModelo)
                .collect(Collectors.toSet());
        return ResponseEntity.ok(habilidadesDTO);
    }

    @GetMapping("/{nombreOrigen}/caminoMasRentable/{nombreDestino}/conCondiciones")
    public ResponseEntity<List<HabilidadDTO>> caminoMasRentable(@PathVariable String nombreOrigen, @PathVariable String nombreDestino, @RequestParam Set<TipoDeCondicion> condiciones) {

        List<HabilidadNode> habilidadesConectadas = habilidadService.caminoMasRentable(nombreOrigen, nombreDestino, condiciones);
        List<HabilidadDTO> habilidadesDTO = habilidadesConectadas.stream()
                .map(HabilidadDTO::desdeModelo)
                .collect(Collectors.toList());
        return ResponseEntity.ok(habilidadesDTO);

    }

    @GetMapping("/caminoMasMutable/{espirituID}/{nombreHabilidad}")
    public ResponseEntity<List<HabilidadDTO>> caminoMasMutable(@PathVariable Long espirituID, @PathVariable String nombreHabilidad) {
        List<HabilidadNode> camino = habilidadService.caminoMasMutable(espirituID, nombreHabilidad);
        List<HabilidadDTO> habilidadesDTO = camino.stream()
                .map(HabilidadDTO::desdeModelo)
                .collect(Collectors.toList());
        return ResponseEntity.ok(habilidadesDTO);
    }

    @GetMapping("/caminoMenosMutable/{espirituID}/{nombreHabilidad}")
    public ResponseEntity<List<HabilidadDTO>> caminoMenosMutable(@PathVariable Long espirituID, @PathVariable String nombreHabilidad) {
        List<HabilidadNode> camino = habilidadService.caminoMenosMutable(nombreHabilidad, espirituID);
        List<HabilidadDTO> habilidadesDTO = camino.stream()
                .map(HabilidadDTO::desdeModelo)
                .collect(Collectors.toList());
        return ResponseEntity.ok(habilidadesDTO);
    }

}