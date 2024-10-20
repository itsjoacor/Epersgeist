package ar.edu.unq.epersgeist.controller;

import ar.edu.unq.epersgeist.controller.dto.espiritu.EspirituDTO;
import ar.edu.unq.epersgeist.helper.DatabaseCleanerService;
import ar.edu.unq.epersgeist.helper.MockMVCEspirituController;
import ar.edu.unq.epersgeist.helper.MockMVCUbicacionController;
import ar.edu.unq.epersgeist.modelo.espiritu.Angel;
import ar.edu.unq.epersgeist.modelo.espiritu.Demonio;
import ar.edu.unq.epersgeist.modelo.espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.ubicacion.Santuario;
import ar.edu.unq.epersgeist.modelo.ubicacion.Ubicacion;
import ar.edu.unq.epersgeist.servicios.exceptions.NoExisteLaEntidadException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class EspirituControllerTest {

    @Autowired
    private MockMVCEspirituController mockMVCEspirituController;

    @Autowired
    private MockMVCUbicacionController mockMVCUbicacionController;

    @Autowired
    private DatabaseCleanerService databaseCleaner;

    private Espiritu guido;
    private Espiritu valen;

    private Ubicacion elBosque;

    private Long guidoId;

    @BeforeEach
    public void prepare() throws Throwable {
        elBosque = new Santuario("El Bosque", 50);
        mockMVCUbicacionController.guardarUbicacion(elBosque);

        guido = new Angel("Guido", elBosque, 100);
        valen = new Demonio("ValenEndemoniado", elBosque, 100);

        guidoId = mockMVCEspirituController.guardarEspiritu(guido).getId();
        mockMVCEspirituController.guardarEspiritu(valen);
    }

    @Test void testCrearEspiritu() throws Throwable {
        Espiritu elRecu = new Angel("ElEstipiru", elBosque, 100);

        mockMVCEspirituController.guardarEspiritu(elRecu);
        var elRecurecu = mockMVCEspirituController.recuperarEspiritu(elRecu.getId());

        Assertions.assertEquals(elRecu.getNombre(),elRecurecu.getNombre());
        Assertions.assertEquals(elRecu.getEnergia(),elRecurecu.getEnergia());
        assertNotNull(elRecurecu.getId());

    }

    @Test
    public void testRecuperarEspiritu() throws Throwable {
        EspirituDTO elRecuGuido = mockMVCEspirituController.recuperarEspiritu(guidoId);

        Assertions.assertEquals(guido.getNombre(), elRecuGuido.getNombre());
        Assertions.assertEquals(guido.getEnergia(),elRecuGuido.getEnergia());
        assertNotNull(elRecuGuido.getId());
    }

    @Test
    public void testGetAllEspiritus() throws Throwable {
        var espiritus = mockMVCEspirituController.recuperarTodos();

        Assertions.assertEquals(2, espiritus.size());
    }

    @Test
    public void testEliminarCorrectamente() throws Throwable {
        EspirituDTO guidoRecu = mockMVCEspirituController.recuperarEspiritu(guidoId);
        assertNotNull(guidoRecu.getId());

        mockMVCEspirituController.eliminarEspiritu(guidoId);

        assertThrows(NoExisteLaEntidadException.class, () -> mockMVCEspirituController.recuperarEspiritu(guidoId));
    }

    @AfterEach
    public void cleanUp() { databaseCleaner.deleteAll(); }

}