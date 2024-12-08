# Spoon Logging Injector

Ce projet utilise **Spoon** pour injecter automatiquement des logs dans un projet Spring.

## Fonctionnement
- **Entrée** : Répertoire contenant le code source du projet Spring.  
- **Sortie** : Répertoire où le code source modifié (avec les logs injectés) sera généré.  

## Utilisation
1. Compiler le projet :
   ```bash
   javac -cp spoon-core-X.Y.Z.jar SpoonRunner.java
   ```
2. Exécuter le programme :
   ```bash
   java -cp .:spoon-core-X.Y.Z.jar SpoonRunner <inputPath> <outputPath>
   ```
   Remplacez `<inputPath>` et `<outputPath>` par les chemins vers le répertoire source et celui de sortie.

## Dépendances
- [Spoon](https://spoon.gforge.inria.fr/) : framework pour l'analyse et la transformation de code Java.  
