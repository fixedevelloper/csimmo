package com.immogest.app.tiers.infra;

import com.immogest.app.tiers.GarantDto;
import com.immogest.app.tiers.LocataireDto;
import com.immogest.app.tiers.MandatDto;
import com.immogest.app.tiers.PieceDto;
import com.immogest.app.tiers.PrestataireDto;
import com.immogest.app.tiers.ProprietaireDto;
import com.immogest.app.tiers.domain.Garant;
import com.immogest.app.tiers.domain.Locataire;
import com.immogest.app.tiers.domain.Mandat;
import com.immogest.app.tiers.domain.PieceIdentite;
import com.immogest.app.tiers.domain.Prestataire;
import com.immogest.app.tiers.domain.ProprietaireTiers;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/** Entités JPA → DTO. Le numéro de CNI n'est jamais copié ici : le service le déchiffre à la demande. */
@Mapper
public interface TiersMapper {

    ProprietaireDto versDto(ProprietaireTiers proprietaire);

    MandatDto versDto(Mandat mandat);

    @Mapping(target = "numeroCni", ignore = true)
    LocataireDto versDto(Locataire locataire);

    @Mapping(target = "numeroCni", ignore = true)
    GarantDto versDto(Garant garant);

    PrestataireDto versDto(Prestataire prestataire);

    PieceDto versDto(PieceIdentite piece);
}
