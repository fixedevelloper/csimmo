import 'dart:io';
import 'dart:typed_data';

import 'package:image_picker/image_picker.dart';
import 'package:open_filex/open_filex.dart';
import 'package:path_provider/path_provider.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';

part 'plateforme.g.dart';

/// Écriture et ouverture de fichiers (PDF téléchargés). Abstrait pour être remplacé dans les tests.
abstract class FichiersLocaux {
  /// Écrit [octets] dans le dossier temporaire sous un nom assaini et renvoie le chemin.
  Future<String> ecrire(String nom, Uint8List octets);

  Future<void> ouvrir(String chemin);
}

class FichiersLocauxReels implements FichiersLocaux {
  @override
  Future<String> ecrire(String nom, Uint8List octets) async {
    final dossier = Directory('${(await getTemporaryDirectory()).path}/immogest');
    await dossier.create(recursive: true);
    final fichier = File('${dossier.path}/${nomSur(nom)}');
    await fichier.writeAsBytes(octets, flush: true);
    return fichier.path;
  }

  @override
  Future<void> ouvrir(String chemin) async {
    await OpenFilex.open(chemin);
  }
}

/// Nom de fichier sûr : lettres, chiffres, point, tiret et souligné seulement (jamais de chemin ni de « .. »).
String nomSur(String nom) {
  final propre = nom.replaceAll(RegExp(r'[^A-Za-z0-9._-]'), '_').replaceAll(RegExp(r'\.{2,}'), '.');
  return propre.isEmpty || propre == '.' ? 'document.pdf' : propre;
}

@Riverpod(keepAlive: true)
FichiersLocaux fichiersLocaux(Ref ref) => FichiersLocauxReels();

/// Photo choisie par l'utilisateur (chemin local).
class PhotoChoisie {
  const PhotoChoisie(this.chemin, this.nom);

  final String chemin;
  final String nom;
}

enum SourcePhoto { camera, galerie }

/// Sélection d'une photo (appareil photo ou galerie), réduite à 1600 px pour ménager le réseau.
abstract class SelecteurPhoto {
  Future<PhotoChoisie?> choisir(SourcePhoto source);
}

class SelecteurPhotoReel implements SelecteurPhoto {
  final _picker = ImagePicker();

  @override
  Future<PhotoChoisie?> choisir(SourcePhoto source) async {
    final f = await _picker.pickImage(
      source: source == SourcePhoto.camera ? ImageSource.camera : ImageSource.gallery,
      maxWidth: 1600,
      maxHeight: 1600,
      imageQuality: 85,
    );
    return f == null ? null : PhotoChoisie(f.path, f.name);
  }
}

@Riverpod(keepAlive: true)
SelecteurPhoto selecteurPhoto(Ref ref) => SelecteurPhotoReel();
