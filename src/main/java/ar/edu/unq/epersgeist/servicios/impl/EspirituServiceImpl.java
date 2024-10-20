package ar.edu.unq.epersgeist.servicios.impl;

import ar.edu.unq.epersgeist.controller.dto.espiritu.ActualizarEspirituDTO;
import ar.edu.unq.epersgeist.modelo.espiritu.Demonio;
import ar.edu.unq.epersgeist.modelo.ubicacion.Ubicacion;
import ar.edu.unq.epersgeist.persistencia.neo.HabilidadNeoDAO;
import ar.edu.unq.epersgeist.servicios.exceptions.NoExisteLaEntidadException;
import ar.edu.unq.epersgeist.servicios.interfaces.UbicacionService;
import ar.edu.unq.epersgeist.utils.Direccion;
import ar.edu.unq.epersgeist.modelo.espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.medium.Medium;
import ar.edu.unq.epersgeist.persistencia.jpa.EspirituDAO;
import ar.edu.unq.epersgeist.persistencia.jpa.MediumDAO;
import ar.edu.unq.epersgeist.servicios.interfaces.EspirituService;
import ar.edu.unq.epersgeist.utils.Paginador;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class EspirituServiceImpl implements EspirituService {

    private EspirituDAO espirituDAO;
    private MediumDAO mediumDAO;
    private UbicacionService ubicacionService;
    private HabilidadNeoDAO habilidadNeoDAO;

    public EspirituServiceImpl(EspirituDAO daoE, MediumDAO daoM, UbicacionService ubicacionService) {
        this.espirituDAO = daoE;
        this.mediumDAO = daoM;
        this.ubicacionService = ubicacionService;
    }

    @Override
    public Espiritu crear(Espiritu espiritu) {
        return espirituDAO.save(espiritu);
    }

    @Override
    public Espiritu crear(Espiritu espiritu, Long ubicacionID) {
        Ubicacion ubicacion = ubicacionService.recuperar(ubicacionID);
        espiritu.setUbicacion(ubicacion);
        return espirituDAO.save(espiritu);
    }

    @Override
    public Espiritu recuperar(Long espirituId) {
        return espirituDAO.findActiveById(espirituId).orElseThrow(() -> new NoExisteLaEntidadException("Espiritu", espirituId));
    }

    @Override
    public List<Espiritu> recuperarTodos() {
        return espirituDAO.findByDeletedAtIsNull();
    }

    @Override
    public void actualizar(Espiritu espiritu) {
        if (!espirituDAO.existsActiveById(espiritu.getId())) {
            throw new NoExisteLaEntidadException("Espiritu", espiritu.getId());
        }
        espirituDAO.save(espiritu);
    }

    @Override
    public void actualizar(ActualizarEspirituDTO espirituActualizado) {
        Espiritu espiritu = this.recuperar(espirituActualizado.getId());
        espiritu.setNombre(espirituActualizado.getNombre());
        espirituDAO.save(espiritu);
    }

    @Override
    public void eliminar(Long espirituId) {
        Espiritu espiritu = this.recuperar(espirituId);
        espiritu.setDeletedAt(LocalDateTime.now());

        if (espiritu.getMedium() != null) {
            Medium medium = espiritu.getMedium();
            medium.getEspiritus().remove(espiritu);
            mediumDAO.save(medium);
        }

        espirituDAO.save(espiritu);
    }


    @Override
    public List<Demonio> espiritusDemoniacos(Direccion direccion, int pagina, int cantidadPorPagina) {

        Pageable pageable = Paginador.paginar(direccion, pagina, cantidadPorPagina);
        Page<Demonio> demonios = espirituDAO.findDemonios(pageable);
        return demonios.getContent();
    }

    @Override
    public Medium conectar(Long espirituId, Long mediumId) {
        Espiritu espirituAConectar = this.recuperar(espirituId);
        Medium mediumAConectar = mediumDAO.findById(mediumId).orElseThrow(() -> new NoExisteLaEntidadException("Medium", mediumId));

        mediumAConectar.crearConexion(espirituAConectar);

        espirituDAO.save(espirituAConectar);
        mediumDAO.save(mediumAConectar);
        return mediumAConectar;
    }
}