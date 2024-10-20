package ar.edu.unq.epersgeist.controller;

import ar.edu.unq.epersgeist.controller.dto.medium.MediumDTO;
import ar.edu.unq.epersgeist.helper.DatabaseCleanerService;
import ar.edu.unq.epersgeist.helper.MockMVCMediumController;
import ar.edu.unq.epersgeist.helper.MockMVCUbicacionController;
import ar.edu.unq.epersgeist.modelo.medium.Medium;
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
public class MediumControllerTest {

    @Autowired
    private MockMVCMediumController mockMVCMediumController;

    @Autowired
    private MockMVCUbicacionController mockMVCUbicacionController;

    @Autowired
    private DatabaseCleanerService databaseCleaner;

    private Medium joaco;
    private Medium nacho;

    private Ubicacion elBosque;

    private Long joacoId;
    @BeforeEach
    public void prepare() throws Throwable {
        elBosque = new Santuario("El Bosque", 50);
        mockMVCUbicacionController.guardarUbicacion(elBosque);

        joaco = new Medium("Medium", 100, elBosque);
        nacho = new Medium("Nacho", 100, elBosque);

        joacoId = mockMVCMediumController.guardarMedium(joaco).getId();
        mockMVCMediumController.guardarMedium(nacho);
    }

    @Test void testCrearMedium() throws Throwable {
        Medium elRecu = new Medium("El  Nuevo", 100, elBosque);

        mockMVCMediumController.guardarMedium(elRecu);
        var elRecurecu = mockMVCMediumController.recuperarMedium(elRecu.getId());

        Assertions.assertEquals(elRecu.getNombre(),elRecurecu.getNombre());
        Assertions.assertEquals(elRecu.getMana(),elRecurecu.getMana());
        assertNotNull(elRecurecu.getId());


    }
    @Test
    public void testRecuperarMedium() throws Throwable {
        MediumDTO elRecuJoaco = mockMVCMediumController.recuperarMedium(joacoId);

        Assertions.assertEquals(joaco.getNombre(), elRecuJoaco.getNombre());
        Assertions.assertEquals(joaco.getMana(),elRecuJoaco.getMana());
        assertNotNull(elRecuJoaco.getId());
    }



    @Test
    public void testGetAllEspiritus() throws Throwable {
        var mediums = mockMVCMediumController.recuperarTodos();

        Assertions.assertEquals(2, mediums.size());
    }


    @Test
    public void testEliminarCorrectamente() throws Throwable {
        MediumDTO joacoRecu = mockMVCMediumController.recuperarMedium(joacoId);
        assertNotNull(joacoRecu.getId());

        mockMVCMediumController.eliminarMedium(joacoId);

        assertThrows(NoExisteLaEntidadException.class, () -> mockMVCMediumController.recuperarMedium(joacoId));
    }



    @AfterEach
    public void cleanUp() { databaseCleaner.deleteAll(); }

}