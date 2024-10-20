package ar.edu.unq.epersgeist.servicios.impl;


import ar.edu.unq.epersgeist.helper.DatabaseCleanerService;
import ar.edu.unq.epersgeist.modelo.medium.Medium;
import ar.edu.unq.epersgeist.modelo.espiritu.Angel;
import ar.edu.unq.epersgeist.modelo.espiritu.Demonio;
import ar.edu.unq.epersgeist.modelo.espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.ubicacion.Cementerio;
import ar.edu.unq.epersgeist.modelo.ubicacion.Santuario;
import ar.edu.unq.epersgeist.modelo.ubicacion.Ubicacion;
import ar.edu.unq.epersgeist.reportes.ReporteSantuarioMasCorrupto;
import ar.edu.unq.epersgeist.servicios.exceptions.NoExisteLaEntidadException;
import ar.edu.unq.epersgeist.servicios.exceptions.NoExisteUnSantuarioCorruptoException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ReportServiceTest {

    @Autowired
    private ReportServiceImpl reportService;

    @Autowired
    private UbicacionServiceImpl ubicacionService;

    @Autowired
    private EspirituServiceImpl espirituService;

    @Autowired
    private MediumServiceImpl mediumService;

    @Autowired
    private DatabaseCleanerService databaseCleanerService;

    @Test
    public void testGetSantuarioMasCorrupto() {
        Ubicacion montania = new Santuario("Montania", 20);
        Ubicacion bosque = new Santuario("Bosque", 20);

        ubicacionService.crear(bosque);
        ubicacionService.crear(montania);

        Espiritu demonio1 = new Demonio("Demonio1", bosque, 60);
        Espiritu demonio2 = new Demonio("Demonio2", bosque, 70);
        Espiritu angel1 = new Angel("Angel1", bosque, 80);
        Espiritu angel2 = new Angel("Angel2", montania, 90);
        Espiritu angel3 = new Angel("Angel3", montania, 100);

        espirituService.crear(demonio1);
        espirituService.crear(demonio2);
        espirituService.crear(angel1);
        espirituService.crear(angel2);
        espirituService.crear(angel3);

        Ubicacion santuarioMasCorrupto = reportService.getSantuarioMasCorrupto();
        assertEquals(bosque.getId(), santuarioMasCorrupto.getId());
    }

    @Test
    public void testGetSantuarioMasCorruptoSinSantuariosExistentes() {
        Ubicacion bosque = new Cementerio("Bosque", 20);

        ubicacionService.crear(bosque);

        Espiritu demonio1 = new Demonio("Demonio1", bosque, 60);
        Espiritu demonio2 = new Demonio("Demonio2", bosque, 70);
        Espiritu angel1 = new Angel("Angel1", bosque, 80);

        espirituService.crear(demonio1);
        espirituService.crear(demonio2);
        espirituService.crear(angel1);

        assertThrows(NoExisteUnSantuarioCorruptoException.class, () -> reportService.getSantuarioMasCorrupto());
    }

    @Test
    public void testGetSantuarioMasCorruptoConUnCementerioYUnSantuarioVacio() {
        Ubicacion montania = new Santuario("Montania", 20);
        Ubicacion bosque = new Cementerio("Bosque", 20);

        ubicacionService.crear(bosque);
        ubicacionService.crear(montania);

        Espiritu demonio1 = new Demonio("Demonio1", bosque, 60);
        Espiritu demonio2 = new Demonio("Demonio2", bosque, 70);
        Espiritu angel1 = new Angel("Angel1", bosque, 80);

        espirituService.crear(demonio1);
        espirituService.crear(demonio2);
        espirituService.crear(angel1);

        assertThrows(NoExisteUnSantuarioCorruptoException.class, () -> reportService.getSantuarioMasCorrupto());
    }

    @Test
    public void testGetSantuarioMasCorruptoEntreSantuariosEmpatados() {
        Ubicacion montania = new Santuario("Montania", 20);
        Ubicacion bosque = new Santuario("Bosque", 20);

        ubicacionService.crear(bosque);
        ubicacionService.crear(montania);

        Espiritu demonio1 = new Demonio("Demonio1", bosque, 60);
        Espiritu demonio2 = new Demonio("Demonio2", bosque, 70);
        Espiritu angel1 = new Angel("Angel1", bosque, 80);

        Espiritu demonio3 = new Demonio("Demonio3", montania, 60);
        Espiritu demonio4 = new Demonio("Demonio4", montania, 70);
        Espiritu angel2 = new Angel("Angel2", montania, 80);

        espirituService.crear(demonio1);
        espirituService.crear(demonio2);
        espirituService.crear(angel1);
        espirituService.crear(angel2);
        espirituService.crear(demonio3);
        espirituService.crear(demonio4);

        Ubicacion santuarioMasCorrupto = reportService.getSantuarioMasCorrupto();
        assertEquals(bosque.getId(), santuarioMasCorrupto.getId());
    }

    @Test
    public void testGetMediumEndemoniadoEnUbicacion() {
        Ubicacion bosque = new Santuario("Bosque", 20);
        ubicacionService.crear(bosque);

        Medium mediumEndemoniado = new Medium("Medium1", 20, bosque);
        Medium guido = new Medium("Guido", 50, bosque);

        Espiritu demonio1 = new Demonio("Demonio1", bosque, 60);
        Espiritu demonio2 = new Demonio("Demonio2", bosque, 70);
        Espiritu demonio3 = new Demonio("Duko", bosque, 80);
        espirituService.crear(demonio1);
        espirituService.crear(demonio2);
        espirituService.crear(demonio3);
        mediumService.crear(mediumEndemoniado);
        mediumService.crear(guido);

        espirituService.conectar(demonio1.getId(), mediumEndemoniado.getId());
        espirituService.conectar(demonio2.getId(), mediumEndemoniado.getId());
        espirituService.conectar(demonio3.getId(), guido.getId());

        Medium mediumsEndemoniadoRecuperado = reportService.getMediumEndemoniadoEn(bosque.getId());
        assertEquals(mediumsEndemoniadoRecuperado.getId(), mediumEndemoniado.getId());
    }

    @Test
    public void testGetMediumEndemoniadoEnUbicacionSinMediumsEndemoniados() {
        Ubicacion bosque = new Santuario("Bosque", 20);
        ubicacionService.crear(bosque);

        Medium guido = new Medium("Guido", 50, bosque);

        Espiritu angel1 = new Angel("Angel1", bosque, 60);
        Espiritu angel2 = new Angel("Angel2", bosque, 70);
        espirituService.crear(angel1);
        espirituService.crear(angel2);
        mediumService.crear(guido);

        espirituService.conectar(angel1.getId(), guido.getId());
        espirituService.conectar(angel2.getId(), guido.getId());

        assertNull(reportService.getMediumEndemoniadoEn(bosque.getId()));
    }

    @Test
    public void testGetMediumEndemoniadoEnCasoDeEmpateEnLaMismaUbicacion() {
        Ubicacion bosque = new Santuario("Bosque", 20);
        ubicacionService.crear(bosque);

        Medium mediumEndemoniado = new Medium("Medium1", 20, bosque);
        Medium guidoModoDiablo = new Medium("Guido", 50, bosque);

        Espiritu demonio1 = new Demonio("Demonio1", bosque, 60);
        Espiritu demonio2 = new Demonio("Demonio2", bosque, 70);
        Espiritu demonio3 = new Demonio("Duko", bosque, 80);
        Espiritu demonio4 = new Demonio("Ysy A", bosque, 90);
        espirituService.crear(demonio1);
        espirituService.crear(demonio2);
        espirituService.crear(demonio3);
        espirituService.crear(demonio4);
        mediumService.crear(guidoModoDiablo);
        mediumService.crear(mediumEndemoniado);

        espirituService.conectar(demonio1.getId(), mediumEndemoniado.getId());
        espirituService.conectar(demonio2.getId(), mediumEndemoniado.getId());
        espirituService.conectar(demonio3.getId(), guidoModoDiablo.getId());
        espirituService.conectar(demonio4.getId(), guidoModoDiablo.getId());
        // En caso de empate devuelve el primero ordenado por nombre alfabeticamente.
        Medium mediumsEndemoniadoRecuperado = reportService.getMediumEndemoniadoEn(bosque.getId());
        assertEquals(mediumsEndemoniadoRecuperado.getId(), guidoModoDiablo.getId());
    }

    @Test
    public void testCantidadDeDemoniosEnUbicacion() {
        Ubicacion bosque = new Santuario("Bosque", 20);
        ubicacionService.crear(bosque);

        Espiritu demonio1 = new Demonio("Demonio1", bosque, 60);
        Espiritu demonio2 = new Demonio("Demonio2", bosque, 70);
        Espiritu demonio3 = new Demonio("Demonio3", bosque, 80);
        Espiritu angel1 = new Angel("Angel1", bosque, 90);
        espirituService.crear(demonio1);
        espirituService.crear(demonio2);
        espirituService.crear(demonio3);
        espirituService.crear(angel1);

        int cantidadDeDemonios = reportService.cantidadDeDemoniosEn(bosque.getId());
        assertEquals(3, cantidadDeDemonios);
    }

    @Test
    public void testCantidadDeDemoniosEnUbicacionSinDemonios() {
        Ubicacion bosque = new Santuario("Bosque", 20);
        ubicacionService.crear(bosque);

        Espiritu angel1 = new Angel("Angel1", bosque, 90);
        Espiritu angel2 = new Angel("Angel2", bosque, 90);
        Espiritu angel3 = new Angel("Angel3", bosque, 90);
        espirituService.crear(angel1);
        espirituService.crear(angel2);
        espirituService.crear(angel3);

        int cantidadDeDemonios = reportService.cantidadDeDemoniosEn(bosque.getId());
        assertEquals(0, cantidadDeDemonios);
    }

    @Test
    public void testCantidadDeDemoniosEnUnaUbicacionInexistente() {
        assertThrows(NoExisteLaEntidadException.class, () -> reportService.cantidadDeDemoniosEn(1L));
    }

    @Test
    public void testCantidadDeDemoniosLibresEnUbicacion() {
        Ubicacion bosque = new Santuario("Bosque", 20);
        ubicacionService.crear(bosque);

        Espiritu demonio1 = new Demonio("Demonio1", bosque, 60);
        Espiritu demonio2 = new Demonio("Demonio2", bosque, 70);
        Espiritu demonio3 = new Demonio("Demonio3", bosque, 80);
        Espiritu angel1 = new Angel("Angel1", bosque, 90);
        espirituService.crear(demonio1);
        espirituService.crear(demonio2);
        espirituService.crear(demonio3);
        espirituService.crear(angel1);

        Medium medium = new Medium("Medium1", 20, bosque);
        mediumService.crear(medium);

        espirituService.conectar(demonio3.getId(), medium.getId());

        int cantidadDeDemoniosLibres = reportService.cantidadDeDemoniosLibresEn(bosque.getId());
        assertEquals(2, cantidadDeDemoniosLibres);
    }

    @Test
    public void testCantidadDeDemoniosLibresEnUbicacionSinDemonios() {
        Ubicacion bosque = new Santuario("Bosque", 20);
        ubicacionService.crear(bosque);


        Espiritu angel1 = new Angel("Angel1", bosque, 90);
        Espiritu angel2 = new Angel("Angel2", bosque, 90);
        espirituService.crear(angel1);
        espirituService.crear(angel2);

        Medium medium = new Medium("Medium1", 20, bosque);
        mediumService.crear(medium);

        espirituService.conectar(angel1.getId(), medium.getId());

        int cantidadDeDemoniosLibres = reportService.cantidadDeDemoniosLibresEn(bosque.getId());
        assertEquals(0, cantidadDeDemoniosLibres);
    }

    @Test
    public void testCantidadDeDemoniosLibresEnUbicacionInexistente() {
        assertThrows(NoExisteLaEntidadException.class, () -> reportService.cantidadDeDemoniosLibresEn(1L));
    }

    @Test
    public void testObtenerElReporteDelSantuarioMasCorrupto() {
        Ubicacion bosque = new Santuario("Bosque", 20);
        Ubicacion ubicacionEsperada = new Santuario("Montania", 20);
        ubicacionService.crear(bosque);
        ubicacionService.crear(ubicacionEsperada);

        Medium mediumEsperado = new Medium("MediumEsperado", 10, ubicacionEsperada);
        Medium medium1 = new Medium("Medium1", 10, ubicacionEsperada);
        mediumService.crear(medium1);
        mediumService.crear(mediumEsperado);

        Espiritu espiritu1 = new Demonio("Espiritu1", ubicacionEsperada, 70);
        Espiritu espiritu2 = new Demonio("Espiritu2", ubicacionEsperada, 80);
        Espiritu espiritu3 = new Demonio("Espiritu3", ubicacionEsperada, 50);
        Espiritu espiritu4 = new Demonio("Espiritu4", ubicacionEsperada, 50);
        Espiritu espiritu5 = new Demonio("Espiritu5", ubicacionEsperada, 50);
        Espiritu espiritu8 = new Angel("Espiritu8", ubicacionEsperada, 50);
        Espiritu espiritu6 = new Demonio("Espiritu6", ubicacionEsperada, 50);
        Espiritu espiritu7 = new Demonio("Espiritu7", bosque, 50);
        Espiritu espiritu9 = new Demonio("Espiritu9", ubicacionEsperada, 50);
        espirituService.crear(espiritu1);
        espirituService.crear(espiritu2);
        espirituService.crear(espiritu3);
        espirituService.crear(espiritu4);
        espirituService.crear(espiritu5);
        espirituService.crear(espiritu6);
        espirituService.crear(espiritu7);
        espirituService.crear(espiritu8);
        espirituService.crear(espiritu9);

        espirituService.conectar(espiritu1.getId(), mediumEsperado.getId());
        espirituService.conectar(espiritu2.getId(), mediumEsperado.getId());
        espirituService.conectar(espiritu3.getId(), mediumEsperado.getId());
        espirituService.conectar(espiritu4.getId(), mediumEsperado.getId());
        espirituService.conectar(espiritu8.getId(), mediumEsperado.getId());

        espirituService.conectar(espiritu5.getId(), medium1.getId());
        espirituService.conectar(espiritu6.getId(), medium1.getId());

        int cantidadDeDemoniosEsperados = 7;
        int cantidadDeDemoniosLibresEsperados = 1;

        ReporteSantuarioMasCorrupto reporteEsperado = new ReporteSantuarioMasCorrupto(ubicacionEsperada.getNombre(), mediumEsperado, cantidadDeDemoniosEsperados, cantidadDeDemoniosLibresEsperados);
        ReporteSantuarioMasCorrupto reporteObtenido = reportService.santuarioCorrupto();

        assertEquals(reporteEsperado.getNombreSantuario(), reporteObtenido.getNombreSantuario());
        assertEquals(reporteEsperado.getMedium().getId(), reporteObtenido.getMedium().getId());
        assertEquals(reporteEsperado.getDemoniosTotales(), reporteObtenido.getDemoniosTotales());
        assertEquals(reporteEsperado.getDemoniosLibres(), reporteObtenido.getDemoniosLibres());
    }

    @Test
    public void testObtenerElReporteDelSantuarioMasCorruptoEnElCasoDeQueNoExistanSantuarios() {
        assertThrows(NoExisteUnSantuarioCorruptoException.class, () -> reportService.santuarioCorrupto());
    }

    @Test
    public void testObtenerElReporteDelSantuarioMasCorruptoEnElCasoDeQueNoExistanDemoniosEnNingunSantuario() {
        Ubicacion montania = new Santuario("Montania", 20);
        ubicacionService.crear(montania);

        Medium medium1 = new Medium("Medium1", 10, montania);
        mediumService.crear(medium1);

        Espiritu espiritu1 = new Angel("Espiritu1", montania, 70);
        espirituService.crear(espiritu1);

        espirituService.conectar(espiritu1.getId(), medium1.getId());

        assertThrows(NoExisteUnSantuarioCorruptoException.class, () -> reportService.santuarioCorrupto());
    }

    @Test
    public void testObtenerElReporteDelSantuarioMasCorruptoEnElCasoQueHayaUnEmpateEntreLosSantuarios(){
        Ubicacion ubicacionEsperada = new Santuario("Bombonera", 20);
        Ubicacion ubicacion1 = new Santuario("Monumental", 20);
        ubicacionService.crear(ubicacion1);
        ubicacionService.crear(ubicacionEsperada);

        Espiritu espiritu1 = new Demonio("Espiritu1", ubicacionEsperada, 70);
        Espiritu espiritu2 = new Demonio("Espiritu2", ubicacionEsperada, 80);
        Espiritu espiritu3 = new Demonio("Espiritu3", ubicacionEsperada, 50);
        Espiritu espiritu4 = new Demonio("Espiritu4", ubicacion1, 50);
        Espiritu espiritu5 = new Demonio("Espiritu5", ubicacion1, 50);
        Espiritu espiritu6 = new Demonio("Espiritu6", ubicacion1, 50);
        espirituService.crear(espiritu1);
        espirituService.crear(espiritu2);
        espirituService.crear(espiritu3);
        espirituService.crear(espiritu4);
        espirituService.crear(espiritu5);
        espirituService.crear(espiritu6);


        int cantidadDeDemoniosEsperados = 3;
        int cantidadDeDemoniosLibresEsperados = 3;
        Medium mediumEsperado = null;
        // En el caso de empate sobre el nivel de corrupcion de los SANTUARIOS, el reporte va a tener el Santuario que tenga el nombre ordenado alfabeticamente de la A-Z.
        ReporteSantuarioMasCorrupto reporteEsperado = new ReporteSantuarioMasCorrupto(ubicacionEsperada.getNombre(), mediumEsperado, cantidadDeDemoniosEsperados, cantidadDeDemoniosLibresEsperados);
        ReporteSantuarioMasCorrupto reporteObtenido = reportService.santuarioCorrupto();

        assertEquals(reporteEsperado.getNombreSantuario(), reporteObtenido.getNombreSantuario());
        assertEquals(reporteEsperado.getMedium(), reporteObtenido.getMedium());
        assertEquals(reporteEsperado.getDemoniosTotales(), reporteObtenido.getDemoniosTotales());
        assertEquals(reporteEsperado.getDemoniosLibres(), reporteObtenido.getDemoniosLibres());
    }

    @Test
    public void testObtenerElReporteDelSantuarioMasCorruptoEnCasoDeEmpateEntreMediums(){
        Ubicacion ubicacionEsperada = new Santuario("Montania", 20);
        ubicacionService.crear(ubicacionEsperada);

        Medium mediumEsperado = new Medium("Guido", 10, ubicacionEsperada);
        Medium medium1 = new Medium("Medium1", 10, ubicacionEsperada);
        mediumService.crear(medium1);
        mediumService.crear(mediumEsperado);

        Espiritu espiritu1 = new Demonio("Espiritu1", ubicacionEsperada, 70);
        Espiritu espiritu2 = new Demonio("Espiritu2", ubicacionEsperada, 80);
        Espiritu espiritu3 = new Demonio("Espiritu3", ubicacionEsperada, 50);
        Espiritu espiritu4 = new Demonio("Espiritu4", ubicacionEsperada, 50);
        Espiritu espiritu5 = new Demonio("Espiritu5", ubicacionEsperada, 50);
        Espiritu espiritu6 = new Demonio("Espiritu6", ubicacionEsperada, 50);
        Espiritu espiritu7 = new Angel("Espiritu7", ubicacionEsperada, 50);
        Espiritu espiritu8 = new Angel("Espiritu8", ubicacionEsperada, 50);
        Espiritu espiritu9 = new Demonio("Espiritu9", ubicacionEsperada, 50);
        espirituService.crear(espiritu1);
        espirituService.crear(espiritu2);
        espirituService.crear(espiritu3);
        espirituService.crear(espiritu4);
        espirituService.crear(espiritu5);
        espirituService.crear(espiritu6);
        espirituService.crear(espiritu7);
        espirituService.crear(espiritu8);
        espirituService.crear(espiritu9);

        espirituService.conectar(espiritu1.getId(), mediumEsperado.getId());
        espirituService.conectar(espiritu2.getId(), mediumEsperado.getId());
        espirituService.conectar(espiritu3.getId(), mediumEsperado.getId());
        espirituService.conectar(espiritu4.getId(), mediumEsperado.getId());
        espirituService.conectar(espiritu7.getId(), mediumEsperado.getId());
        espirituService.conectar(espiritu8.getId(), mediumEsperado.getId());

        espirituService.conectar(espiritu5.getId(), medium1.getId());
        espirituService.conectar(espiritu6.getId(), medium1.getId());

        int cantidadDeDemoniosEsperados = 7;
        int cantidadDeDemoniosLibresEsperados = 1;
        // En el caso de empate sobre el nivel de corrupcion de los MEDIUMS, el reporte va a tener el medium que tenga el nombre ordenado alfabeticamente de la A-Z.
        ReporteSantuarioMasCorrupto reporteEsperado = new ReporteSantuarioMasCorrupto(ubicacionEsperada.getNombre(), mediumEsperado, cantidadDeDemoniosEsperados, cantidadDeDemoniosLibresEsperados);
        ReporteSantuarioMasCorrupto reporteObtenido = reportService.santuarioCorrupto();

        assertEquals(reporteEsperado.getNombreSantuario(), reporteObtenido.getNombreSantuario());
        assertEquals(reporteEsperado.getMedium().getId(), reporteObtenido.getMedium().getId());
        assertEquals(reporteEsperado.getDemoniosTotales(), reporteObtenido.getDemoniosTotales());
        assertEquals(reporteEsperado.getDemoniosLibres(), reporteObtenido.getDemoniosLibres());
    }

    @Test
    public void testObtenerElReporteDelSantuarioMasCorruptoEnElCasoQueNoExistanUnMediumConDemonios() {
        Ubicacion ubicacionEsperada = new Santuario("Montania", 20);
        ubicacionService.crear(ubicacionEsperada);

        Espiritu espiritu1 = new Demonio("Espiritu1", ubicacionEsperada, 70);
        Espiritu espiritu2 = new Demonio("Espiritu2", ubicacionEsperada, 80);
        Espiritu espiritu3 = new Demonio("Espiritu3", ubicacionEsperada, 50);
        Espiritu espiritu4 = new Demonio("Espiritu4", ubicacionEsperada, 50);
        Espiritu espiritu5 = new Demonio("Espiritu5", ubicacionEsperada, 50);

        espirituService.crear(espiritu1);
        espirituService.crear(espiritu2);
        espirituService.crear(espiritu3);
        espirituService.crear(espiritu4);
        espirituService.crear(espiritu5);

        int cantidadDeDemoniosEsperados = 5;
        int cantidadDeDemoniosLibresEsperados = 5;
        Medium mediumEsperado = null;

        ReporteSantuarioMasCorrupto reporteEsperado = new ReporteSantuarioMasCorrupto(ubicacionEsperada.getNombre(), mediumEsperado, cantidadDeDemoniosEsperados, cantidadDeDemoniosLibresEsperados);
        ReporteSantuarioMasCorrupto reporteObtenido = reportService.santuarioCorrupto();

        assertEquals(reporteEsperado.getNombreSantuario(), reporteObtenido.getNombreSantuario());
        assertEquals(reporteEsperado.getMedium(), reporteObtenido.getMedium());
        assertEquals(reporteEsperado.getDemoniosTotales(), reporteObtenido.getDemoniosTotales());
        assertEquals(reporteEsperado.getDemoniosLibres(), reporteObtenido.getDemoniosLibres());
    }

    @AfterEach
    public void cleanUp() {
        databaseCleanerService.deleteAll();
    }
}
