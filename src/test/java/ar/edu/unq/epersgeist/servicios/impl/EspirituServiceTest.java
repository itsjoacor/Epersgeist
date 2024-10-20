package ar.edu.unq.epersgeist.servicios.impl;

import ar.edu.unq.epersgeist.controller.dto.espiritu.ActualizarEspirituDTO;
import ar.edu.unq.epersgeist.helper.DatabaseCleanerService;
import ar.edu.unq.epersgeist.modelo.espiritu.Angel;
import ar.edu.unq.epersgeist.modelo.espiritu.Demonio;
import ar.edu.unq.epersgeist.modelo.espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.medium.Medium;
import ar.edu.unq.epersgeist.modelo.ubicacion.Santuario;
import ar.edu.unq.epersgeist.modelo.ubicacion.Ubicacion;
import ar.edu.unq.epersgeist.servicios.interfaces.HabilidadService;
import ar.edu.unq.epersgeist.utils.Direccion;
import ar.edu.unq.epersgeist.modelo.exceptions.EspirituNoEstaEnLaMismaUbicacionException;
import ar.edu.unq.epersgeist.modelo.exceptions.EspirituYaConectadoException;
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
public class EspirituServiceTest {

    @Autowired
    private EspirituService espirituService;

    @Autowired
    private MediumService mediumService;

    @Autowired
    private UbicacionService ubicacionService;

    @Autowired
    private DatabaseCleanerService databaseCleaner;


    private Medium joacor;
    private Espiritu melli;
    private Espiritu naguet;
    private Espiritu augusto;
    private Ubicacion bernal;
    private Ubicacion quilmes;

    @BeforeEach
    public void setUp() {


        bernal = new Santuario("Bernal", 20);
        quilmes = new Santuario("Quilmes", 20);
        ubicacionService.crear(bernal);
        ubicacionService.crear(quilmes);

        joacor = new Medium("Joacor", 100, bernal);
        melli = new Demonio("Melli", bernal, 100);
        naguet = new Angel("Naguet", quilmes, 100);
        augusto = new Demonio("August", bernal, 100);
    }


    @Test
    public void testCrearUnEspiritu() {
        espirituService.crear(melli);
        Espiritu espirituRecuperado = espirituService.recuperar(melli.getId());

        assertEquals(melli.getNombre(), espirituRecuperado.getNombre());
        assertInstanceOf(Demonio.class, melli);
        assertEquals(melli.getUbicacion().getNombre(), espirituRecuperado.getUbicacion().getNombre());
        assertEquals(melli.getEnergia(), espirituRecuperado.getEnergia());
    }

    @Test
    public void testCrearUnEspirituConUbicacionId(){
        espirituService.crear(melli, bernal.getId());
        Espiritu espirituRecuperado = espirituService.recuperar(melli.getId());

        assertEquals(melli.getNombre(), espirituRecuperado.getNombre());
        assertInstanceOf(Demonio.class, melli);
        assertEquals(melli.getUbicacion().getNombre(), espirituRecuperado.getUbicacion().getNombre());
        assertEquals(melli.getEnergia(), espirituRecuperado.getEnergia());
        assertEquals(0, melli.getExorcismosResueltos());
        assertEquals(0, melli.getExorcismosEvitados());
    }

    @Test
    public void testCrearUnEspirituConUbicacionIdNoExistente(){

        assertThrows(NoExisteLaEntidadException.class, () -> {espirituService.crear(melli, 1234L);});

    }



    @Test
    public void testRecuperarUnEspiritu() {
        espirituService.crear(melli);
        Espiritu espirituRecuperado = espirituService.recuperar(melli.getId());

        assertEquals(melli.getNombre(), espirituRecuperado.getNombre());
        assertInstanceOf(Demonio.class, melli);
        assertEquals(melli.getUbicacion().getNombre(), espirituRecuperado.getUbicacion().getNombre());
        assertEquals(melli.getEnergia(), espirituRecuperado.getEnergia());
    }

    @Test
    public void testNoSePuedeRecuperarUnEspirituQueNoEstaEnLaBaseDeDatos() {
        assertThrows(NoExisteLaEntidadException.class, () -> {
            espirituService.recuperar(1234L);
        });
    }

    @Test
    public void testRecuperarTodosLosEspiritus() {
        espirituService.crear(melli);
        espirituService.crear(naguet);

        List<Espiritu> espiritusRecuperados = espirituService.recuperarTodos();

        assertEquals(2, espiritusRecuperados.size());
    }

    @Test
    public void recuperarTodosLosEspiritusAunqueNoHayaNinguno() {
        List<Espiritu> espiritusRecuperados = espirituService.recuperarTodos();
        assertEquals(0, espiritusRecuperados.size());
    }

    @Test
    public void testActualizarUnEspiritu() {
        espirituService.crear(melli);
        melli.setNombre("Cocodrilo");
        espirituService.actualizar(melli);

        Espiritu espirituRecuperado = espirituService.recuperar(melli.getId());
        assertEquals("Cocodrilo", espirituRecuperado.getNombre());
    }


    @Test
    public void testNoSePuedeActualizarUnEspirituQueNoEstaEnLaBaseDeDatos() {
        Long idNoExistente = 1234L;
        melli.setId(idNoExistente);

        assertThrows(NoExisteLaEntidadException.class, () -> {
            espirituService.actualizar(melli);
        });
    }

    @Test
    void testActualizarPorDTO() {
        espirituService.crear(augusto);

        ActualizarEspirituDTO dto = new ActualizarEspirituDTO(augusto.getId(), "NombreNuevo");

        espirituService.actualizar(dto);
        Espiritu espirituRecuperado = espirituService.recuperar(dto.getId());


        assertEquals(espirituRecuperado.getNombre(), dto.getNombre());
    }

    @Test
    void testActualizarPorDTOConIdInexistente(){
        ActualizarEspirituDTO dto = new ActualizarEspirituDTO(1030L, "NombreNuevo");
        assertThrows(NoExisteLaEntidadException.class,()-> espirituService.actualizar(dto));
    }

    @Test
    public void testEliminarUnEspiritu() {
        espirituService.crear(naguet);
        espirituService.eliminar(naguet.getId());

        List<Espiritu> espiritusRecuperados = espirituService.recuperarTodos();
        assertEquals(0, espiritusRecuperados.size());
    }


    @Test
    public void testNoSePuedeEliminarUnEspirituQueNoEstaEnLaBaseDeDatos() {
        assertThrows(NoExisteLaEntidadException.class, () -> {
            espirituService.eliminar(1234L);
        });
    }

    @Test
    public void testConseguirEspiritusDemoniacosConListaDescendente() {
        espirituService.crear(melli);
        espirituService.crear(augusto);

        List<Demonio> espiritusDemoniacos = espirituService.espiritusDemoniacos(Direccion.DESCENDENTE, 1, 10);

        assertEquals(2, espiritusDemoniacos.size());
        assertEquals(augusto.getNombre(), espiritusDemoniacos.getFirst().getNombre());
    }

    @Test
    public void testConseguirEspiritusDemoniacosConListaAscendente() {
        espirituService.crear(melli);
        espirituService.crear(augusto);

        List<Demonio> espiritusDemoniacos = espirituService.espiritusDemoniacos(Direccion.ASCENDENTE, 1, 10);

        assertEquals(2, espiritusDemoniacos.size());
        assertEquals(melli.getNombre(), espiritusDemoniacos.getFirst().getNombre());
    }

    @Test
    public void testSiCantidadDePaginaInvalidaSeSeteaDefault1() {
        espirituService.crear(melli);
        espirituService.crear(naguet);

        assertEquals(1,
                (espirituService.espiritusDemoniacos(Direccion.DESCENDENTE, 1, (-3))).size());
    }

    @Test
    public void testSiPaginaInvalidaSeSeteaDefault1() {
        espirituService.crear(melli);
        espirituService.crear(naguet);

        assertEquals(1,
                (espirituService.espiritusDemoniacos(Direccion.DESCENDENTE, 0, 2)).size());
    }

    @Test
    public void testSiLePidoUnaPaginaMayorALaCantidadQuePoseeDevuelveListaVcia() {
        espirituService.crear(melli);
        espirituService.crear(naguet);

        assertEquals(0,
                (espirituService.espiritusDemoniacos(Direccion.DESCENDENTE, 10, 2)).size());
    }

    @Test
    public void testAlPedirUnaPaginaQueNoExisteDevuelveUnaListaVacia() {
        espirituService.crear(melli);
        espirituService.crear(naguet);

        List<Demonio> espiritusDemoniacos = espirituService.espiritusDemoniacos(Direccion.DESCENDENTE, 2, 10);

        assertEquals(0, espiritusDemoniacos.size());
    }

    @Test
    public void testConectarEspirituYMedium() {
        espirituService.crear(melli);
        mediumService.crear(joacor);

        espirituService.conectar(melli.getId(), joacor.getId());

        Espiritu melliRecuperado = espirituService.recuperar(melli.getId());
        Medium joacorRecuperado = mediumService.recuperar(joacor.getId());

        assertEquals(melliRecuperado.getMedium().getNombre(), joacorRecuperado.getNombre());
    }

    @Test
    public void testUnEspirituYaConectadoNoSePuedeConectar() {
        espirituService.crear(melli);
        mediumService.crear(joacor);
        espirituService.conectar(melli.getId(), joacor.getId());

        assertThrows(EspirituYaConectadoException.class, () -> {
            espirituService.conectar(melli.getId(), joacor.getId());
        });
    }

    @Test
    public void testUnEspirituYUnMediumDeDiferentesUbicacionesNoSePuedenConectar() {
        espirituService.crear(naguet);
        mediumService.crear(joacor);


        assertThrows(EspirituNoEstaEnLaMismaUbicacionException.class, () -> {
            espirituService.conectar(naguet.getId(), joacor.getId());
        });
    }


    @AfterEach
    public void cleanUp() {
        databaseCleaner.deleteAll();
    }

}