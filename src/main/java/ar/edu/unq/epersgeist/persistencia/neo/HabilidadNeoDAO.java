package ar.edu.unq.epersgeist.persistencia.neo;

import ar.edu.unq.epersgeist.modelo.TipoDeCondicion;
import ar.edu.unq.epersgeist.modelo.habilidad.HabilidadNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface HabilidadNeoDAO extends Neo4jRepository<HabilidadNode, Long> {

    @Query("MATCH (h: HabilidadNode {nombre: $nombre}) OPTIONAL MATCH(h)-[r: Puede_Mutar_A*]->(m: HabilidadNode) RETURN h, collect(r), collect(m)")
    Optional<HabilidadNode> findByName(String nombre);

    @Query("MATCH (h: HabilidadNode) WHERE h.nombre IN $nombres OPTIONAL MATCH (h)-[r: Puede_Mutar_A*]->(m: HabilidadNode) RETURN h, collect(r), collect(m)")
    List<HabilidadNode> findByNames(List<String> nombres);

    @Query("MATCH (h:HabilidadNode {nombre: $nombreHabilidad})-[:Puede_Mutar_A]->(h2:HabilidadNode) RETURN h2")
    public Set<HabilidadNode> getConectedHabilities(String nombreHabilidad);

    @Query("MATCH (h:HabilidadNode {nombre: $nombreHabilidadOrigen}) " +
            "WITH h, $exorcismosResueltos AS resueltos, $exorcismosEvitados AS evitados, $energia AS energia, $nivelDeConexion AS conexion " +
            "MATCH path=(h)-[:Puede_Mutar_A*]->(m:HabilidadNode) " +
            "WHERE ALL(rel IN relationships(path) WHERE " +
            "(CASE " +
            "WHEN rel.tipoDeCondicion = 'EXORCISMOS_RESUELTOS' THEN resueltos >= rel.cantidad " +
            "WHEN rel.tipoDeCondicion = 'EXORCISMOS_EVITADOS' THEN evitados >= rel.cantidad " +
            "WHEN rel.tipoDeCondicion = 'CANTIDAD_DE_ENERGIA' THEN energia >= rel.cantidad " +
            "WHEN rel.tipoDeCondicion = 'NIVEL_DE_CONEXION' THEN conexion >= rel.cantidad " +
            "ELSE false " +
            "END)) " +
            "WITH path " +
            "ORDER BY length(path) DESC " +
            "LIMIT 1 " +
            "RETURN path")
    public List<HabilidadNode> mostMutablePath(String nombreHabilidadOrigen,
                                               int exorcismosResueltos,
                                               int exorcismosEvitados,
                                               int energia,
                                               int nivelDeConexion);

    @Query("MATCH (h:HabilidadNode {nombre: $nombreHabilidadOrigen}) " +
            "WITH h, $exorcismosResueltos AS resueltos, $exorcismosEvitados AS evitados, $energia AS energia, $nivelDeConexion AS conexion " +
            "MATCH path=(h)-[:Puede_Mutar_A*]->(m:HabilidadNode) " +
            "WHERE ALL(rel IN relationships(path) WHERE " +
            "(CASE " +
            "WHEN rel.tipoDeCondicion = 'EXORCISMOS_RESUELTOS' THEN resueltos >= rel.cantidad " +
            "WHEN rel.tipoDeCondicion = 'EXORCISMOS_EVITADOS' THEN evitados >= rel.cantidad " +
            "WHEN rel.tipoDeCondicion = 'CANTIDAD_DE_ENERGIA' THEN energia >= rel.cantidad " +
            "WHEN rel.tipoDeCondicion = 'NIVEL_DE_CONEXION' THEN conexion >= rel.cantidad " +
            "ELSE false " +
            "END)) " +
            "WITH path " +
            "ORDER BY length(path) ASC " +
            "LIMIT 1 " +
            "RETURN path")
    public List<HabilidadNode> leastMutablePath(String nombreHabilidadOrigen,
                                                int exorcismosResueltos,
                                                int exorcismosEvitados,
                                                int energia,
                                                int nivelDeConexion);


    @Query( "MATCH p = shortestPath((h1:HabilidadNode {nombre: $nombreHabilidadOrigen})-[r:Puede_Mutar_A*]-(h2:HabilidadNode {nombre: $nombreHabilidadDestino}))" +
            "WHERE ALL(r IN relationships(p) WHERE r.tipoDeCondicion IN $condiciones)" +
            "RETURN nodes(p)")
    List<HabilidadNode> caminoMasRentable(String nombreHabilidadOrigen, String nombreHabilidadDestino, Set<TipoDeCondicion> condiciones);

    @Query( "MATCH p = shortestPath((h1:HabilidadNode {nombre: $nombreHabilidadOrigen})-[r:Puede_Mutar_A*]-(h2:HabilidadNode {nombre: $nombreHabilidadDestino}))" +
            "RETURN nodes(p)")
    List<HabilidadNode> caminoPosible(String nombreHabilidadOrigen, String nombreHabilidadDestino);

}
