package ar.edu.unq.epersgeist.servicios.impl;

import ar.edu.unq.epersgeist.controller.dto.ubicacion.ActualizarUbicacionDTO;
import ar.edu.unq.epersgeist.helper.DatabaseCleanerService;
import ar.edu.unq.epersgeist.modelo.espiritu.Angel;
import ar.edu.unq.epersgeist.modelo.espiritu.Demonio;
import ar.edu.unq.epersgeist.modelo.espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.medium.Medium;
import ar.edu.unq.epersgeist.modelo.ubicacion.Santuario;
import ar.edu.unq.epersgeist.modelo.ubicacion.Ubicacion;
import ar.edu.unq.epersgeist.servicios.exceptions.EntidadConNombreYaExistenteException;
import ar.edu.unq.epersgeist.servicios.exceptions.NoExisteLaEntidadException;
import ar.edu.unq.epersgeist.servicios.interfaces.EspirituService;
import ar.edu.unq.epersgeist.servicios.interfaces.MediumService;
import ar.edu.unq.epersgeist.servicios.interfaces.UbicacionService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UbicacionServiceTest {

    @Autowired
    private UbicacionService ubicacionService;

    @Autowired
    private EspirituService espirituService;

    @Autowired
    private MediumService mediumService;

    @Autowired
    private DatabaseCleanerService databaseCleanerService;

    private Ubicacion playa;

    @BeforeEach
    void setUp() {
         playa = new Santuario("Playa", 20);
    }

    @Test
    public void testCrearUbicacion() {

        ubicacionService.crear(playa);

        assertNotNull(playa.getId());
        Ubicacion ubicacionRecuperada = ubicacionService.recuperar(playa.getId());
        assertEquals(ubicacionRecuperada.getNombre(), playa.getNombre());
        assertEquals(ubicacionRecuperada.getEnergia(), playa.getEnergia());
    }

    @Test
    public void noSePuedeCrearUnaUbicacionConUnNombreYaExistente() {
        ubicacionService.crear(playa);
        Ubicacion playa2 = new Santuario("Playa", 20);

        assertThrows(EntidadConNombreYaExistenteException.class, () -> ubicacionService.crear(playa2));
    }

    @Test
    public void testRecuperarUbicacion() {
        ubicacionService.crear(playa);

        Ubicacion ubicacionRecuperada = ubicacionService.recuperar(playa.getId());
        assertEquals(ubicacionRecuperada.getNombre(), playa.getNombre());
        assertEquals(ubicacionRecuperada.getId(), playa.getId());
        assertEquals(ubicacionRecuperada.getEnergia(), playa.getEnergia());
    }

    @Test
    public void testRecuperarUbicacionInexistente() {
        assertThrows(NoExisteLaEntidadException.class, () -> ubicacionService.recuperar(1L));
    }

    @Test
    public void testRecuperarTodasLasUbicaciones() {
        Ubicacion ubicacion2 = new Santuario("Selva", 20);
        ubicacionService.crear(playa);
        ubicacionService.crear(ubicacion2);

        List<Ubicacion> ubicacionesRecuperadas = ubicacionService.recuperarTodos();
        assertEquals(2, ubicacionesRecuperadas.size());
        assertTrue(ubicacionesRecuperadas.stream().anyMatch(u -> u.getNombre().equals("Playa")));
        assertTrue(ubicacionesRecuperadas.stream().anyMatch(u -> u.getId().equals(playa.getId())));

        assertTrue(ubicacionesRecuperadas.stream().anyMatch(u -> u.getNombre().equals("Selva")));
        assertTrue(ubicacionesRecuperadas.stream().anyMatch(u -> u.getId().equals(ubicacion2.getId())));
    }

    @Test
    public void testActualizarUbicacion() {
        Ubicacion ubicacion = new Santuario("Bosque", 20);
        ubicacionService.crear(ubicacion);

        ubicacion.setNombre("Templo");
        ubicacionService.actualizar(ubicacion);

        Ubicacion ubicacionActualizada = ubicacionService.recuperar(ubicacion.getId());
        assertEquals("Templo", ubicacionActualizada.getNombre());
    }

    @Test
    public void testActualizarUbicacionInexistente() {
        Ubicacion ubicacion = new Santuario("Bosque", 20);
        ubicacion.setId(1L);
        assertThrows(NoExisteLaEntidadException.class, () -> ubicacionService.actualizar(ubicacion));
    }

    @Test
    public void testActualizarUbicacionConDTO() {
        Ubicacion ubicacion = new Santuario("Bosque", 20);
        ubicacionService.crear(ubicacion);

        ActualizarUbicacionDTO dto = new ActualizarUbicacionDTO(ubicacion.getId(), "Templo", 30);
        ubicacionService.actualizar(dto);

        Ubicacion ubicacionActualizada = ubicacionService.recuperar(ubicacion.getId());

        assertEquals("Templo", ubicacionActualizada.getNombre());
        assertEquals(30, ubicacionActualizada.getEnergia());
    }

    @Test
    public void testActualizarUbicacionConDTOConIdInexistente() {
        ActualizarUbicacionDTO dto = new ActualizarUbicacionDTO(1240L, "Templo", 30);

        assertThrows(NoExisteLaEntidadException.class, () -> ubicacionService.actualizar(dto));
    }


    @Test
    public void testEliminarUbicacion() {
        Ubicacion ubicacion = new Santuario("Desierto", 20);
        ubicacionService.crear(ubicacion);

        ubicacionService.eliminar(ubicacion.getId());

        assertThrows(NoExisteLaEntidadException.class, () -> ubicacionService.recuperar(ubicacion.getId()));
    }

    @Test
    public void testEliminarUbicacionInexistente() {
        assertThrows(NoExisteLaEntidadException.class, () -> ubicacionService.eliminar(1L));
    }

   @Test
   public void testEspiritusEnUbicacionVacia() {
       Ubicacion ubicacion = new Santuario("Playa", 20);
       ubicacionService.crear(ubicacion);
       assertEquals(0, ubicacionService.espiritusEn(ubicacion.getId()).size());
   }


    @Test
    public void testEspiritusEnUbicacion() {
        Ubicacion ubicacion = new Santuario("Bosque", 20);
        ubicacionService.crear(ubicacion);

        Espiritu espiritu1 = new Angel("Espiritu1", ubicacion, 70);
        Espiritu espiritu2 = new Demonio("Espiritu2", ubicacion, 80);

        Ubicacion desierto = new Santuario("Desierto", 20);
        ubicacionService.crear(desierto);
        Espiritu espirituEnOtraUbicacion = new Demonio("Espiritu3", desierto, 50);

        espirituService.crear(espiritu1);
        espirituService.crear(espiritu2);
        espirituService.crear(espirituEnOtraUbicacion);

        List<Espiritu> espiritusEnUbicacion = ubicacionService.espiritusEn(ubicacion.getId());
        assertEquals(2, espiritusEnUbicacion.size());
        assertTrue(espiritusEnUbicacion.stream().anyMatch(e -> e.getId().equals(espiritu1.getId())));
        assertTrue(espiritusEnUbicacion.stream().anyMatch(e -> e.getId().equals(espiritu2.getId())));
    }

    @Test
    public void testMediumsSinEspiritusEnUbicacion() {
        Ubicacion ubicacion = new Santuario("Templo", 20);
        ubicacionService.crear(ubicacion);

        Medium medium1 = new Medium("Medium1", 10, ubicacion);
        Medium medium2 = new Medium("Medium2", 50, ubicacion);
        Medium mediumSinEspiritu = new Medium("Medium3", 100, ubicacion);

        Espiritu espiritu1 = new Angel("Espiritu1", ubicacion, 70);
        Espiritu espiritu2 = new Demonio("Espiritu2", ubicacion, 80);

        espirituService.crear(espiritu1);
        espirituService.crear(espiritu2);

        medium1.crearConexion(espiritu1);
        medium2.crearConexion(espiritu2);

        mediumService.crear(medium1);
        mediumService.crear(medium2);
        mediumService.crear(mediumSinEspiritu);

        espirituService.actualizar(espiritu1);
        espirituService.actualizar(espiritu2);

        List<Medium> mediumsSinEspiritus = ubicacionService.mediumsSinEspiritusEn(ubicacion.getId());

        assertEquals(1, mediumsSinEspiritus.size());
        assertTrue(mediumsSinEspiritus.stream().anyMatch(m -> m.getId().equals(mediumSinEspiritu.getId())));
    }


    @Test
    public void testActualizarUbicacionConUnNombreYaExistente() {
        Ubicacion bosque = new Santuario("Bosque", 20);
        Ubicacion montania = new Santuario("Montania", 20);

        ubicacionService.crear(bosque);
        ubicacionService.crear(montania);

        montania.setNombre("Bosque");

        assertThrows(EntidadConNombreYaExistenteException.class, () -> ubicacionService.actualizar(montania));
    }

    @AfterEach
    public void cleanUp() {
        databaseCleanerService.deleteAll();
    }
}