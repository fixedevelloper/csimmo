# Obfuscation du jar de production (profil Maven « obfuscation »). Conservatrice par conception : Spring, JPA, Jackson, Thymeleaf,
# Spring Modulith et Spring Data lisent beaucoup de choses par réflexion, et un jar qui ne démarre plus chez un client coûte plus
# cher qu'un code lisible. Les noms de classes et de packages sont conservés (Modulith, entités, beans, Flyway) ; sont renommés
# les membres privés / package-private non annotés, et les informations de débogage (fichiers source, numéros de ligne) sont retirées.
-dontshrink
-dontoptimize
-dontnote
-dontwarn **
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod,Exceptions,Record,PermittedSubclasses,RuntimeVisibleParameterAnnotations,MethodParameters,NestHost,NestMembers
-renamesourcefileattribute ""
-adaptresourcefilecontents **.properties,META-INF/MANIFEST.MF

# Noms de classes inchangés
-keepnames class com.immogest.** { }
# Tout ce qui est public ou protégé garde son nom (DTO, accesseurs de records lus par Jackson / Thymeleaf, API des modules)
-keepclassmembers class com.immogest.** { public *; protected *; }
# Constructeurs (instanciation par réflexion)
-keepclassmembers class com.immogest.** { <init>(...); }
# Enums, records et classes de configuration
-keepclassmembers enum com.immogest.** { *; }
-keepclassmembers class com.immogest.** extends java.lang.Record { *; }
# Entités JPA : accès par champ
-keepclassmembers @jakarta.persistence.Entity class * { *; }
-keepclassmembers @jakarta.persistence.Embeddable class * { *; }
-keepclassmembers @jakarta.persistence.MappedSuperclass class * { *; }
# Membres annotés par un framework : champs @Value/@Autowired, @Bean, listeners, planifications, validations, JSON, MVC
-keepclassmembers class com.immogest.** {
    @org.springframework.beans.factory.annotation.* *;
    @org.springframework.context.annotation.* *;
    @org.springframework.context.event.* *;
    @org.springframework.modulith.events.* *;
    @org.springframework.web.bind.annotation.* *;
    @org.springframework.scheduling.annotation.* *;
    @org.springframework.transaction.annotation.* *;
    @org.springframework.security.access.prepost.* *;
    @jakarta.persistence.* *;
    @jakarta.validation.constraints.* *;
    @com.fasterxml.jackson.annotation.* *;
}
# Dépôts Spring Data (requêtes dérivées du nom des méthodes), interfaces et mappers MapStruct
-keep interface com.immogest.** { *; }
-keep class com.immogest.**Impl { *; }
# Point d'entrée
-keep class com.immogest.app.ImmoGestApplication { public static void main(java.lang.String[]); }
