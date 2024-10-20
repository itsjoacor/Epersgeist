package ar.edu.unq.epersgeist.servicios.impl;

import ar.edu.unq.epersgeist.controller.dto.medium.ActualizarMediumDTO;
import ar.edu.unq.epersgeist.modelo.espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.medium.Medium;
import ar.edu.unq.epersgeist.modelo.ubicacion.Ubicacion;
import ar.edu.unq.epersgeist.persistencia.jpa.EspirituDAO;
import ar.edu.unq.epersgeist.persistencia.jpa.MediumDAO;
import ar.edu.unq.epersgeist.persistencia.jpa.UbicacionDAO;
import ar.edu.unq.epersgeist.servicios.exceptions.NoExisteLaEntidadException;

import ar.edu.unq.epersgeist.servicios.interfaces.HabilidadService;
import ar.edu.unq.epersgeist.servicios.interfaces.MediumService;
import ar.edu.unq.epersgeist.servicios.interfaces.UbicacionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MediumServiceImpl implements MediumService {

    private final HabilidadService habilidadService;
    private UbicacionDAO ubicacionDAO;
    private MediumDAO mediumDAO;
    private EspirituDAO espirituDAO;
    private UbicacionService ubicacionService;

    public MediumServiceImpl(MediumDAO dao, EspirituDAO espirituDAO, UbicacionDAO ubicacionDAO, UbicacionService ubicacionService, HabilidadService habilidadService) {
        this.mediumDAO = dao;
        this.espirituDAO = espirituDAO;
        this.ubicacionDAO = ubicacionDAO;
        this.ubicacionService = ubicacionService;
        this.habilidadService = habilidadService;
    }

    @Override
    public void crear(Medium medium) {
            mediumDAO.save(medium);
    }

    @Override
    public Medium crear(Medium medium, Long ubicacionId) {
        Ubicacion ubicacion = ubicacionService.recuperar(ubicacionId);
        medium.setUbicacion(ubicacion);
        return mediumDAO.save(medium);
    }

    @Override
    public Medium recuperar(Long id) {
        return mediumDAO.findById(id).orElseThrow(() -> new NoExisteLaEntidadException(Medium.class.getSimpleName(), id));
    }

    @Override
    public List<Medium> recuperarTodos() {
        return mediumDAO.findAll();
    }

    @Override
    public void actualizar(Medium medium) {
        if (!mediumDAO.existsById(medium.getId())) {
            throw new NoExisteLaEntidadException(Medium.class.getSimpleName(), medium.getId());
        }
        mediumDAO.save(medium);
    }

    public void actualizar(ActualizarMediumDTO medium) {
        Medium mediumRecuperado = this.recuperar(medium.getId());
        mediumRecuperado.setNombre(medium.getNombre());

        mediumDAO.save(mediumRecuperado);
    }


    @Override
    public void eliminar(Long id) {
        if (!mediumDAO.existsById(id)) {
            throw new NoExisteLaEntidadException(Medium.class.getSimpleName(), id);
        }
        mediumDAO.deleteById(id);
    }

    @Override
    public void descansar(Long mediumId) {
        Medium medium = mediumDAO.findById(mediumId).orElseThrow(() -> new NoExisteLaEntidadException(Medium.class.getSimpleName(), mediumId));
        medium.descansar();
        mediumDAO.save(medium);
    }

    @Override
    public void exorcizar(Long idMediumExorcista, Long idMediumAExorcizar) {
        Medium mediumExorcista = mediumDAO.findById(idMediumExorcista).orElseThrow(() -> new NoExisteLaEntidadException(Medium.class.getSimpleName(), idMediumExorcista));
        Medium mediumAExorcizar = mediumDAO.findById(idMediumAExorcizar).orElseThrow(() -> new NoExisteLaEntidadException(Medium.class.getSimpleName(), idMediumAExorcizar));

        mediumExorcista.exorcizar(mediumAExorcizar);

        mediumExorcista.getEspiritus().forEach(espiritu -> habilidadService.evolucionar(espiritu.getId()));
        mediumAExorcizar.getEspiritus().forEach(espiritu -> habilidadService.evolucionar(espiritu.getId()));
        // TODO testear
        mediumDAO.save(mediumExorcista);
        mediumDAO.save(mediumAExorcizar);
    }

    @Override
    public List<Espiritu> espiritus(Long id) {
        return mediumDAO.findEspiritusByMediumId(id);
    }

    @Override
    public Espiritu invocar(Long mediumId, Long espirituId) {
        Medium medium = mediumDAO.findById(mediumId).orElseThrow(() -> new NoExisteLaEntidadException("Medium", mediumId));
        Espiritu espiritu = espirituDAO.findActiveById(espirituId).orElseThrow(() -> new NoExisteLaEntidadException("Espiritu", espirituId));
        Espiritu espirituInvocado = medium.invocar(espiritu);
        mediumDAO.save(medium);
        return espirituInvocado;
    }

    @Override
    public void mover(Long mediumId, Long ubicacionId) {
        Medium medium = mediumDAO.findById(mediumId).orElseThrow( () -> new NoExisteLaEntidadException("Medium", mediumId));
        Ubicacion ubicacion = ubicacionDAO.findById(ubicacionId).orElseThrow( () -> new NoExisteLaEntidadException("Ubicacion", ubicacionId));
        medium.mover(ubicacion);
        mediumDAO.save(medium);
    }
}