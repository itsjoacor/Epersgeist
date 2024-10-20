package ar.edu.unq.epersgeist.controller;

import ar.edu.unq.epersgeist.controller.dto.ubicacion.UbicacionDTO;
import ar.edu.unq.epersgeist.helper.DatabaseCleanerService;
import ar.edu.unq.epersgeist.helper.MockMVCUbicacionController;
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
public class UbicacionControllerTest {

    @Autowired
    private MockMVCUbicacionController mockMVCUbicacionController;

    @Autowired
    private DatabaseCleanerService databaseCleaner;

    private Ubicacion elBosque;

    private Long elBosqueId;

    @BeforeEach
    public void prepare() throws Throwable {
        elBosque = new Santuario("El Bosque", 50);
        elBosqueId = mockMVCUbicacionController.guardarUbicacion(elBosque).getId();
    }

    @Test void testCrearUbicacion() throws Throwable {
        Ubicacion elRecu = new Santuario("El  Recuperado", 100);

        mockMVCUbicacionController.guardarUbicacion(elRecu);
        var elRecurecu = mockMVCUbicacionController.recuperarUbicacion(elRecu.getId());

        Assertions.assertEquals(elRecu.getNombre(),elRecurecu.getNombre());
        Assertions.assertEquals(elRecu.getEnergia(),elRecurecu.getEnergia());
        assertNotNull(elRecurecu.getId());


    }
    @Test
    public void testRecuperarUbicacion() throws Throwable {
        UbicacionDTO bosqueRecuperado = mockMVCUbicacionController.recuperarUbicacion(elBosqueId);

        Assertions.assertEquals(elBosque.getNombre(), bosqueRecuperado.getNombre());
    }

    @Test
    public void testEliminarCorrectamente() throws Throwable {
        UbicacionDTO bosqueRecu = mockMVCUbicacionController.recuperarUbicacion(elBosqueId);
        assertNotNull(bosqueRecu.getId());

        mockMVCUbicacionController.eliminarUbicacion(elBosqueId);

        assertThrows(NoExisteLaEntidadException.class, () -> mockMVCUbicacionController.recuperarUbicacion(elBosqueId));
    }




    @AfterEach
    public void cleanUp() { databaseCleaner.deleteAll(); }

}