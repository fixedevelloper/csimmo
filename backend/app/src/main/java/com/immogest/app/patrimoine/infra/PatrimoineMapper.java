package com.immogest.app.patrimoine.infra;

import com.immogest.app.patrimoine.CompteurDto;
import com.immogest.app.patrimoine.ImmeubleDto;
import com.immogest.app.patrimoine.LotDto;
import com.immogest.app.patrimoine.PhotoDto;
import com.immogest.app.patrimoine.domain.Compteur;
import com.immogest.app.patrimoine.domain.Immeuble;
import com.immogest.app.patrimoine.domain.Lot;
import com.immogest.app.patrimoine.domain.PhotoPatrimoine;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/** Entités JPA → DTO (les controllers n'exposent jamais une entité). */
@Mapper
public interface PatrimoineMapper {

    @Mapping(target = "nombreLots", constant = "0")
    @Mapping(target = "lotsLibres", constant = "0")
    @Mapping(target = "lotsOccupes", constant = "0")
    @Mapping(target = "lotsEnTravaux", constant = "0")
    ImmeubleDto versDto(Immeuble immeuble);

    @Mapping(target = "immeubleId", source = "immeuble.id")
    @Mapping(target = "immeubleNom", source = "immeuble.nom")
    @Mapping(target = "ville", source = "immeuble.ville")
    @Mapping(target = "quartier", source = "immeuble.quartier")
    LotDto versDto(Lot lot);

    CompteurDto versDto(Compteur compteur);

    PhotoDto versDto(PhotoPatrimoine photo);

    default List<String> trier(Set<String> equipements) {
        return equipements.stream().sorted(String.CASE_INSENSITIVE_ORDER).toList();
    }
}
