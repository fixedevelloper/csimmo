package com.immogest.licensing;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * JSON canonique : clés triées (ordre des points de code), aucun espace, chaînes échappées de façon unique.
 * C'est la forme qui est signée : NE JAMAIS la modifier (toutes les licences émises deviendraient invalides).
 * Le test {@code CanonicalJsonTest} fige ce format.
 */
final class CanonicalJson {

    private CanonicalJson() {
    }

    static String ecrire(Object valeur) {
        StringBuilder sb = new StringBuilder();
        ecrire(valeur, sb);
        return sb.toString();
    }

    private static void ecrire(Object valeur, StringBuilder sb) {
        switch (valeur) {
            case null -> throw new IllegalArgumentException("null non autorisé dans le JSON canonique");
            case String s -> chaine(s, sb);
            case Integer i -> sb.append(i.intValue());
            case Long l -> sb.append(l.longValue());
            case Boolean b -> sb.append(b.booleanValue());
            case Map<?, ?> m -> {
                Map<String, Object> triee = new TreeMap<>();
                m.forEach((k, v) -> triee.put((String) k, v));
                sb.append('{');
                boolean premier = true;
                for (Map.Entry<String, Object> e : triee.entrySet()) {
                    if (!premier) {
                        sb.append(',');
                    }
                    premier = false;
                    chaine(e.getKey(), sb);
                    sb.append(':');
                    ecrire(e.getValue(), sb);
                }
                sb.append('}');
            }
            case List<?> liste -> {
                sb.append('[');
                for (int i = 0; i < liste.size(); i++) {
                    if (i > 0) {
                        sb.append(',');
                    }
                    ecrire(liste.get(i), sb);
                }
                sb.append(']');
            }
            default -> throw new IllegalArgumentException("type non supporté : " + valeur.getClass());
        }
    }

    private static void chaine(String s, StringBuilder sb) {
        sb.append('"');
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                default -> {
                    if (c < 0x20) {
                        sb.append("\\u").append(String.format("%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
                }
            }
        }
        sb.append('"');
    }
}
