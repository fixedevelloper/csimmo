package com.immogest.app.notification;

import java.util.List;

/** @param configure tous les champs obligatoires sont renseignés (le canal peut être activé) */
public record CanalParametresDto(TypeCanal canal, boolean actif, boolean configure, List<ChampDto> champs) {
}
