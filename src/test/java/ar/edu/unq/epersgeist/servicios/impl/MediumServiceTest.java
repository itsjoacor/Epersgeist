package ar.edu.unq.epersgeist.servicios.impl;


import ar.edu.unq.epersgeist.controller.dto.medium.ActualizarMediumDTO;
import ar.edu.unq.epersgeist.helper.DatabaseCleanerService;
import ar.edu.unq.epersgeist.modelo.espiritu.Angel;
import ar.edu.unq.epersgeist.modelo.espiritu.Demonio;
import ar.edu.unq.epersgeist.modelo.espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.exceptions.EspirituNoEstaLibreException;
import ar.edu.unq.epersgeist.modelo.exceptions.ExorcistaSinAngelesException;
import ar.edu.unq.epersgeist.modelo.exceptions.InvocacionDeAngelEnCementerioException;
import ar.edu.unq.epersgeist.modelo.exceptions.InvocacionDeDemonioEnSantuarioException;
import ar.edu.unq.epersgeist.modelo.medium.Medium;
import ar.edu.unq.epersgeist.modelo.ubicacion.Cementerio;
import ar.edu.unq.epersgeist.modelo.ubicacion.Santuario;
import ar.edu.unq.epersgeist.modelo.ubicacion.Ubicacion;
import ar.edu.unq.epersgeist.modelo.random.NroFijoGetter;
import ar.edu.unq.epersgeist.modelo.random.Random;
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
public class MediumServiceTest {
    private Medium medium;
    private Medium medium1;
    private Medium medium2;
    private Medium mediumRecu;
    @Autowired
    private MediumService mediumService;

    private Ubicacion ubicacion;
    private Ubicacion ubicacion1;
    private Ubicacion ubicacion2;
    @Autowired
    private UbicacionService ubicacionService;

    @Autowired
    private EspirituService espirituService;
    private Espiritu espiritu;
    private Espiritu espirituDos;
    private Espiritu espirituDifLoc;
    private Espiritu espirituDEM1;
    private Espiritu espirituANG2;
    private Medium mediumExorcista;
    private Espiritu espirituAngel;

    private Medium mediumAExorcisar;
    private Espiritu espirituDemonioAMorir;
    private Espiritu espirituDemonioAVivir;
    private Random random;
    private Medium mediumCrud;
    private Medium mediumCrud1;
    private Medium mediumCrud2;
    private Ubicacion ubicacionCrud;
    private Ubicacion ubicacionCrud1;
    private Ubicacion ubicacionCrud2;

    @Autowired
    private DatabaseCleanerService databaseCleaner;

    @BeforeEach
    void setUp() {

        ubicacion = new Cementerio("Casa", 20);
        ubicacion1 = new Santuario("Casa1", 20);
        ubicacion2 = new Santuario("Casa2", 20);
        ubicacionService.crear(ubicacion);
        ubicacionService.crear(ubicacion1);
        ubicacionService.crear(ubicacion2);

        medium = new Medium("Guido", 80, ubicacion);
        medium1 = new Medium("Nahue", 100, ubicacion1);
        medium2 = new Medium("Joaco", 100, ubicacion2);

        espirituDEM1 = new Demonio("EspirituDem", ubicacion1, 100);

        espiritu = new Demonio("Espiritu1", ubicacion, 100);
        espirituDos = new Demonio("Espiritu2", ubicacion, 80);
        espirituDifLoc = new Demonio("Espiritu2", ubicacion2, 80);
        espirituService.crear(espiritu);
        espirituANG2 = new Angel("EspirituAng", ubicacion2, 100);


        mediumExorcista = new Medium("GuidoExor", 300, ubicacion2);
        espirituAngel = new Angel("EspirituAng", ubicacion2, 100);

        mediumAExorcisar = new Medium("JoacoAExor", 50, ubicacion2);
        espirituDemonioAMorir = new Demonio("EspirituDem", ubicacion2, 8);
        espirituDemonioAVivir = new Demonio("EspirituDem", ubicacion2, 100);

        ubicacionCrud = new Santuario("CasaCrud", 20);
        ubicacionCrud1 = new Santuario("Casa1Crud", 20);
        ubicacionCrud2 = new Santuario("Casa2Crud", 20);
        ubicacionService.crear(ubicacionCrud);
        ubicacionService.crear(ubicacionCrud1);
        ubicacionService.crear(ubicacionCrud2);
        mediumCrud = new Medium("GuidoCrud", 100, ubicacionCrud);
        mediumCrud1 = new Medium("NahueCrud", 100, ubicacionCrud1);
        mediumCrud2 = new Medium("JoacoCrud", 100, ubicacionCrud2);

        random = Random.getInstance();

    }


    @Test
    void unMediumDescansaEnUnSantuario(){
        /*
         * Se espera que el medium recupere de mana el 150% de la energia del lugar
         * y que los angeles conectados a el recuperen en energia el total que provee el lugar,
         * pero que los demonios no recuperen energia.
         * */
        var santuario = new Santuario("Nose", 20);
        var demonio = new Demonio("Demonio", santuario, 20);
        var angelito = new Angel("Angelito", santuario, 20);
        var medium = new Medium("Medium", 20, santuario);

        ubicacionService.crear(santuario);
        espirituService.crear(demonio);
        espirituService.crear(angelito);
        mediumService.crear(medium);

        medium.crearConexion(demonio);
        medium.crearConexion(angelito);

        mediumService.actualizar(medium);

        var energiaEsperadaMedium = (int) (medium.getMana() + (santuario.getEnergia() * 1.5));
        var energiaEsperadaDemonio = demonio.getEnergia();
        var energiaEsperadaAngel = angelito.getEnergia() + santuario.getEnergia();


        mediumService.descansar(medium.getId());

        var mediumRecu = mediumService.recuperar(medium.getId());
        var demonioRecu = espirituService.recuperar(demonio.getId());
        var angelitoRecu = espirituService.recuperar(angelito.getId());

        assertEquals(energiaEsperadaMedium, mediumRecu.getMana());
        assertEquals(energiaEsperadaAngel, angelitoRecu.getEnergia());
        assertEquals(energiaEsperadaDemonio, demonioRecu.getEnergia());
    }

    @Test
    void unMediumDescansaEnUnCementerio(){
        /*
         * Se espera que el medium recupere de mana el 50% de la energia del lugar
         * y que los demonios conectados a el recuperen en energia el total que provee el lugar,
         * pero que los angeles no recuperen energia.
         * */

        var cementerio = new Cementerio("Nose", 20);
        var demonio = new Demonio("Demonio", cementerio, 20);
        var angelito = new Angel("Angelito", cementerio, 20);
        var medium = new Medium("Medium", 20, cementerio);

        ubicacionService.crear(cementerio);
        espirituService.crear(demonio);
        espirituService.crear(angelito);
        mediumService.crear(medium);

        medium.crearConexion(demonio);
        medium.crearConexion(angelito);

        mediumService.actualizar(medium);

        var energiaEsperadaMedium = medium.getMana() + (cementerio.getEnergia() / 2);
        var energiaEsperadaDemonio = demonio.getEnergia() + cementerio.getEnergia();
        var energiaEsperadaAngel = angelito.getEnergia();


        mediumService.descansar(medium.getId());

        var mediumRecu = mediumService.recuperar(medium.getId());
        var demonioRecu = espirituService.recuperar(demonio.getId());
        var angelitoRecu = espirituService.recuperar(angelito.getId());

        assertEquals(energiaEsperadaMedium, mediumRecu.getMana());
        assertEquals(energiaEsperadaAngel, angelitoRecu.getEnergia());
        assertEquals(energiaEsperadaDemonio, demonioRecu.getEnergia());
    }


    @Test
    void alDescansarUnMediumQueNoExisteTiraUnError() {

        assertThrows(NoExisteLaEntidadException.class, () -> mediumService.descansar(234423454L));

    }


    @Test
    void inicialmenteUnMediumQueNoEstaConectadoNoTieneNningunEspirituEnSusEspiritus() {
        mediumService.crear(medium);
        List<Espiritu> espiritus = mediumService.espiritus(medium.getId());

        assertEquals(0, espiritus.size());
    }


    @Test
    void cuandoObtengoLosEspiritusDeUnMediumSonObtenidosCorrectamente() {
        mediumService.crear(medium);
        medium.crearConexion(espiritu);
        mediumService.actualizar(medium);

        List<Espiritu> espiritus = mediumService.espiritus(medium.getId());

        assertEquals(1, espiritus.size());

    }

    @Test
    void cuandoSeInvocaAUnEspirituSeLoInvocaCorrectamenteSiNoEstaConectadoAOtroMedium() {
        mediumService.crear(medium);
        espirituService.crear(espirituDifLoc);

        assertNotEquals(medium.getUbicacion(), espirituDifLoc.getUbicacion());
        assertTrue(espirituDifLoc.estaLibre());

        mediumService.invocar(medium.getId(), espirituDifLoc.getId());

        String nombreDeLaUbicacionDelEspiritu = espirituDifLoc.getUbicacion().getNombre();
        assertEquals(ubicacion2.getNombre(), nombreDeLaUbicacionDelEspiritu);

        String nombreEspirituDifLocRecu = espirituService.recuperar(espirituDifLoc.getId()).getUbicacion().getNombre();
        String nombreUbicMediumRecu = mediumService.recuperar(medium.getId()).getUbicacion().getNombre();
        assertEquals(nombreUbicMediumRecu, nombreEspirituDifLocRecu);
    }

    @Test
    void cuandoSeInvocaAUnEspirituSiEstaConectadoAOtroMediumNoLoPuedeInvocar() {
        mediumService.crear(medium);
        mediumService.crear(medium1);
        medium.crearConexion(espiritu);
        mediumService.actualizar(medium);
        espirituService.actualizar(espiritu);


        assertThrows(EspirituNoEstaLibreException.class, () -> mediumService.invocar(medium1.getId(), espiritu.getId()));

    }


    @Test
    void cuandoSeInvocaAUnEspirituSiEstanEnLaMismaUbicacionInvocaPeroSigueEnLaMismaUbicacion() {
        mediumService.crear(medium);

        assertTrue(espiritu.estaLibre());

        mediumService.invocar(medium.getId(), espiritu.getId());

        String nombreEspirituDifLocRecu = espirituService.recuperar(espiritu.getId()).getUbicacion().getNombre();
        String nombreUbicMediumRecu = mediumService.recuperar(medium.getId()).getUbicacion().getNombre();
        assertEquals(nombreUbicMediumRecu, nombreEspirituDifLocRecu);
    }

    @Test
    void noSePuedeInvocarAUnDemonioEstandoEnUnSantuario(){

        var santuario = new Santuario("Nose", 100);
        var mediumSanto = new Medium("Santo", 100, santuario);
        var demonio = new Demonio("Demonio", ubicacion, 100);

        ubicacionService.crear(santuario);
        mediumService.crear(mediumSanto);
        espirituService.crear(demonio);


        assertThrows(InvocacionDeDemonioEnSantuarioException.class, () -> mediumService.invocar(mediumSanto.getId(), demonio.getId()));
    }

    @Test
    void noSePuedeInvocarAUnAngelEstandoEnUnCementerio(){
        var cementerio = new Cementerio("Nose", 100);
        var mediumDiablo = new Medium("Diablo", 100, cementerio);
        var angelito = new Angel("Angelito", ubicacion, 100);

        ubicacionService.crear(cementerio);
        mediumService.crear(mediumDiablo);
        espirituService.crear(angelito);


        assertThrows(InvocacionDeAngelEnCementerioException.class, () -> mediumService.invocar(mediumDiablo.getId(), angelito.getId()));
    }


    @Test
    void unMediumNoPuedeExcorcizarSiNoTieneAlMenosUnEspirituAngel() {
        mediumService.crear(medium1);
        mediumService.crear(medium2);
        espirituService.crear(espirituDEM1);
        medium1.crearConexion(espirituDEM1);
        mediumService.actualizar(medium1);
        mediumService.actualizar(medium2);

        assertThrows(ExorcistaSinAngelesException.class, () -> mediumService.exorcizar(medium2.getId(), medium1.getId()));
    }

    @Test
    void unMediumNoPuedeExorcizarSiElOtroMediumNoTieneDemonios() {
        mediumService.crear(medium1);
        mediumService.crear(medium2);
        espirituService.crear(espirituANG2);
        medium2.crearConexion(espirituANG2);
        mediumService.actualizar(medium1);
        mediumService.actualizar(medium2);

        int energiaAngAntesDelAtaque = (espirituService.recuperar(espirituANG2.getId())).getEnergia();

        mediumService.exorcizar(medium2.getId(), medium1.getId());

        int energiaAngPostAtaque = (espirituService.recuperar(espirituANG2.getId())).getEnergia();
        assertEquals(energiaAngAntesDelAtaque, energiaAngPostAtaque);
    }

    @Test
    void unMediumPuedeExcorcizarAOtroMediumTotalmente() {
        mediumService.crear(mediumExorcista);
        mediumService.crear(mediumAExorcisar);
        espirituService.crear(espirituDemonioAMorir);
        espirituService.crear(espirituAngel);


        mediumExorcista.crearConexion(espirituAngel);
        mediumAExorcisar.crearConexion(espirituDemonioAMorir);

        random.setStrategy(new NroFijoGetter(7));

        mediumService.actualizar(mediumExorcista);
        mediumService.actualizar(mediumAExorcisar);
        espirituService.actualizar(espirituAngel);
        espirituService.actualizar(espirituDemonioAMorir);

        var energiaInicialAngel = espirituAngel.getEnergia();
        mediumService.exorcizar(mediumExorcista.getId(), mediumAExorcisar.getId());
 
        List<Espiritu> espiritusDelExorcizado = mediumService.espiritus(mediumAExorcisar.getId());

        assertEquals(0, espiritusDelExorcizado.size());
        assertEquals((energiaInicialAngel - 10), espirituService.recuperar(espirituAngel.getId()).getEnergia());


    }

    @Test
    void unMediumPuedeExorcizarAOtroMediumPeroParcialmente() {

        mediumService.crear(mediumExorcista);
        espirituService.crear(espirituAngel);
        mediumExorcista.crearConexion(espirituAngel);

        mediumService.crear(mediumAExorcisar);
        espirituService.crear(espirituDemonioAVivir);
        mediumAExorcisar.crearConexion(espirituDemonioAVivir);


        random.setStrategy(new NroFijoGetter(9));

        espirituService.actualizar(espirituAngel);
        espirituService.actualizar(espirituDemonioAVivir);
        mediumService.actualizar(mediumExorcista);
        mediumService.actualizar(mediumAExorcisar);

        var energiaAntesDeExorcizar = espirituDemonioAVivir.getEnergia();
        var danio = espirituAngel.getNivelDeConexion() / 2;
        var energiaEsperada = energiaAntesDeExorcizar - danio;
        mediumService.exorcizar(mediumExorcista.getId(), mediumAExorcisar.getId());


        List<Espiritu> espiritusDelExorcizado = mediumService.espiritus(mediumAExorcisar.getId());


        assertEquals(1, espiritusDelExorcizado.size());
        assertEquals(energiaEsperada, espiritusDelExorcizado.getFirst().getEnergia());

    }
    @Test
    void cuandoObtengoLosEspiritusDeUnMediumQueNoExisteDevuelveListaVacia() {
        medium.crearConexion(espiritu);

        List<Espiritu> espiritus = mediumService.espiritus(medium.getId());

        assertEquals(0, espiritus.size());

    }

    @Test
    void enCondicionSuficienteParaInvocarSiEsUnIDEspirituInexistente() {
        mediumService.crear(medium);
        mediumService.crear(medium1);
        medium.crearConexion(espirituDos);
        mediumService.actualizar(medium);

        assertThrows(NoExisteLaEntidadException.class, () -> mediumService.invocar(medium1.getId(), 23423L));
    }

    @Test
    void enCondicionSuficienteParaInvocarSiEsUnIDMediumInexistente() {
        mediumService.crear(medium);
        medium.crearConexion(espiritu);
        mediumService.actualizar(medium);
        espirituService.actualizar(espiritu);

        assertThrows(NoExisteLaEntidadException.class, () -> mediumService.invocar(234234L, espiritu.getId()));

    }


    @Test
    void  unMediumNoPuedeExcorcizarAOtroSiElSegundoNoExiste() {
        mediumService.crear(mediumExorcista);
        espirituService.crear(espirituDemonioAMorir);
        espirituService.crear(espirituAngel);


        mediumExorcista.crearConexion(espirituAngel);
        mediumAExorcisar.crearConexion(espirituDemonioAMorir);

        random.setStrategy(new NroFijoGetter(7));

        mediumService.actualizar(mediumExorcista);
        espirituService.actualizar(espirituAngel);
        espirituService.actualizar(espirituDemonioAMorir);

        assertThrows(NoExisteLaEntidadException.class, () ->
                mediumService.exorcizar(mediumExorcista.getId(), 3453455L));

    }

    @Test
    void unMediumNoPuedeExcorcizarAOtroSiElPrimeroNoExiste() {
        mediumService.crear(mediumAExorcisar);
        espirituService.crear(espirituDemonioAMorir);
        espirituService.crear(espirituAngel);


        mediumExorcista.crearConexion(espirituAngel);
        mediumAExorcisar.crearConexion(espirituDemonioAMorir);

        random.setStrategy(new NroFijoGetter(7));

        mediumService.actualizar(mediumAExorcisar);
        espirituService.actualizar(espirituAngel);
        espirituService.actualizar(espirituDemonioAMorir);

        assertThrows(NoExisteLaEntidadException.class, () ->
                mediumService.exorcizar(56456L, mediumAExorcisar.getId()));

    }

    @Test
    void testAlCrearUnMediumSeCreaCorrectamente() {
        mediumService.crear(mediumCrud);
        Medium mediumCrudRecu = mediumService.recuperar(mediumCrud.getId());


        assertEquals(mediumCrud.getNombre(), mediumCrudRecu.getNombre());
        assertEquals(mediumCrud.getMana(), mediumCrudRecu.getMana());
        assertEquals(mediumCrud.getUbicacion().getNombre(), mediumCrudRecu.getUbicacion().getNombre());
    }


    @Test
    void testAlrecuperarSeRecuperaCorrectamente() {
        mediumService.crear(mediumCrud1);
        mediumService.crear(mediumCrud2);

        Medium mediumCrudRecu2 = mediumService.recuperar(mediumCrud2.getId());
        List<Medium> mediumCruds = mediumService.recuperarTodos();

        assertEquals(2, mediumCruds.size());
        assertEquals(mediumCrud2.getNombre(), mediumCrudRecu2.getNombre());
        assertEquals(mediumCrud2.getMana(), mediumCrudRecu2.getMana());
        assertEquals(mediumCrud2.getUbicacion().getNombre(), mediumCrudRecu2.getUbicacion().getNombre());
    }

    @Test
    void testNoSePuedeRecuperarAlgoQueNoEsta() {
        assertThrows(NoExisteLaEntidadException.class, () -> mediumService.recuperar(2345234L));
    }

    @Test
    void testAlactualizarSeActualizaCorrectamente() {
        mediumService.crear(mediumCrud);
        ubicacionService.crear(ubicacionCrud1);

        mediumCrud.setNombre("NombreNuevo");
        mediumCrud.setMana(50);
        mediumCrud.setUbicacion(ubicacionCrud1);

        mediumService.actualizar(mediumCrud);
        Medium mediumCrudActualizado = mediumService.recuperar(mediumCrud.getId());


        assertEquals(mediumCrud.getNombre(), mediumCrudActualizado.getNombre());
        assertEquals(mediumCrud.getMana(), mediumCrudActualizado.getMana());
        assertEquals(mediumCrud.getUbicacion().getNombre(), mediumCrudActualizado.getUbicacion().getNombre());
    }

    @Test
    void testNoSePuedeActualizarAlgoQueNoEsta() {
        assertThrows(NoExisteLaEntidadException.class, () -> mediumService.recuperar(2345234L));
    }

    @Test
    void testAleliminarSeEliminaCorrectamente() {
        ubicacionService.crear(ubicacionCrud);
        Medium mediumCrud3 = new Medium("Eliminado", 100, ubicacionCrud);
        mediumService.crear(mediumCrud3);

        mediumService.eliminar(mediumCrud3.getId());
        List<Medium> mediumCruds = mediumService.recuperarTodos();

        assertEquals(0, mediumCruds.size());

    }

    @Test
    void testActualizarPorDTO() {
        mediumService.crear(mediumCrud);
        ubicacionService.crear(ubicacionCrud1);

        ActualizarMediumDTO dto = new ActualizarMediumDTO(mediumCrud.getId(), "NombreNuevo");

        mediumService.actualizar(dto);
        Medium mediumRecuperado = mediumService.recuperar(mediumCrud.getId());


        assertEquals(mediumRecuperado.getNombre(), dto.getNombre());
    }

    @Test
    void testActualizarPorDTOConIdInexistente() {
        ActualizarMediumDTO dto = new ActualizarMediumDTO(1040L, "NombreNuevo");
       assertThrows(NoExisteLaEntidadException.class, () -> mediumService.actualizar(dto));
    }


    @Test
    void testNoSePuedeEliminarAlgoQueNoEsta() {
        assertEquals(0, mediumService.recuperarTodos().size());
        assertThrows(NoExisteLaEntidadException.class, () -> mediumService.eliminar(2345234L));
    }

    @Test
    void testRecuperarTodosSiNoHayRecuperaNada() {

        assertEquals(0, mediumService.recuperarTodos().size());
    }

    @Test
    void testRecuperarRecuperaCorrectamente() {
        mediumService.crear(mediumCrud);
        var nom = mediumCrud.getNombre();
        var mana = mediumCrud.getMana();
        var ubic = mediumCrud.getUbicacion().getNombre();
        Medium mediumCrudRecuper = mediumService.recuperar(mediumCrud.getId());
        var nomrec = mediumCrudRecuper.getNombre();
        var manarec = mediumCrudRecuper.getMana();
        var ubicrec = mediumCrudRecuper.getUbicacion().getNombre();
        assertEquals(nom, nomrec);
        assertEquals(mana, manarec);
        assertEquals(ubic, ubicrec);
    }

    @Test
    void testUnMediumSeMueveAUnaUbicacionJuntoConSusEspiritus() {
        mediumService.crear(medium);
        medium.crearConexion(espiritu);
        mediumService.actualizar(medium);
        mediumService.mover(medium.getId(), ubicacion1.getId());

        var mediumRecuperado = mediumService.recuperar(medium.getId());
        var espirituRecuperado = espirituService.recuperar(espiritu.getId());

        assertEquals(ubicacion1.getNombre(), mediumRecuperado.getUbicacion().getNombre());
        assertEquals(ubicacion1.getNombre(), espirituRecuperado.getUbicacion().getNombre());
    }

    @Test
    void testUnMediumSeMueveAUnSantuarioConUnDemonio() {
        mediumService.crear(medium);
        medium.crearConexion(espiritu);

        var energiaEsperadaDemonio = espiritu.getEnergia() - 10;

        mediumService.actualizar(medium);
        mediumService.mover(medium.getId(), ubicacion1.getId());


        var espirituRecuperado = espirituService.recuperar(espiritu.getId());

        assertEquals(energiaEsperadaDemonio, espirituRecuperado.getEnergia());
    }

    @Test
    void testUnMediumSeMueveAUnCementerioConUnAngel() {
        mediumService.crear(medium1);
        espirituService.crear(espirituANG2);
        medium1.invocar(espirituANG2);
        medium1.crearConexion(espirituANG2);

        var energiaEsperadaAngel = espirituANG2.getEnergia() - 5;

        mediumService.actualizar(medium1);
        mediumService.mover(medium1.getId(), ubicacion.getId());


        var espirituRecuperado = espirituService.recuperar(espirituANG2.getId());

        assertEquals(energiaEsperadaAngel, espirituRecuperado.getEnergia());
    }


    @Test
    void testNoSePuedeMoverAUnaQueNoExiste() {
        mediumService.crear(medium);
        medium.crearConexion(espiritu);
        mediumService.actualizar(medium);

        assertThrows(NoExisteLaEntidadException.class, () -> mediumService.mover(medium.getId(), 124L));
    }

    @Test
    void testNoSePuedeMoverAUnMediumQueNoExiste() {
        mediumService.crear(medium);
        medium.crearConexion(espiritu);
        mediumService.actualizar(medium);

        assertThrows(NoExisteLaEntidadException.class, () -> mediumService.mover(124L, ubicacion.getId()));
    }


    @AfterEach
    public void cleanUp() {
        databaseCleaner.deleteAll();
    }

}
