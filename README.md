# Clone de Space Invaders - Implémentation OpenGL

Un clone complet de Space Invaders développé avec Java et JOGL (Java OpenGL), suivant les principes d'architecture propre avec séparation de la logique et du rendu.

## Fonctionnalités

- **Gameplay classique de Space Invaders** : Contrôlez un vaisseau spatial pour détruire des vagues d'aliens en marche
- **Mouvement fluide** : Gestion des entrées clavier sans latence ni problèmes de répétition
- **IA d'essaim** : Les aliens se déplacent en formation, accélérant et descendant lorsqu'ils touchent les bords de l'écran
- **Système de particules** : Explosions avec particules s'estompant utilisant le mélange OpenGL
- **Détection de collision** : Système de collision AABB (Axis-Aligned Bounding Box)
- **Suivi du score** : Indicateur de score visuel
- **États de jeu** : États En cours, Game Over, Niveau supérieur et Victoire

## Architecture
```
game/
├── core/
│   ├── Main.java           - Point d'entrée, configuration de la fenêtre
│   ├── GameEngine.java     - Boucle de jeu principale, détection de collision
│   └── InputHandler.java   - Gestion des entrées clavier
└── entities/
    ├── GraphicalObject.java - Classe de base abstraite
    ├── Player.java          - Vaisseau du joueur
    ├── Alien.java           - Alien ennemi
    ├── Bullet.java          - Projectile du joueur
    ├── Particle.java        - Particule d'explosion
    ├── AlienBullet.java     - Projectile alien
    ├── WallBricks.java      - Les segments individuels qui composent les murs
    └── Wall.java            - Les murs entre le joueur et les aliens
```

## Prérequis

- **Java** : JDK 8 ou supérieur
- **JOGL** : 2.3.2 ou supérieur (bindings Java OpenGL)

## Installation et exécution

### IDE avec bibliothèque manuelle

1. Téléchargez les JARs JOGL depuis [jogamp-all-plateforms](https://nubo.ircam.fr/index.php/s/9kgbJzqansbajPg?opendetails=)

2. Suivez les instructions sur cette page pour l'installer sur votre IDE :
   [Configurer un projet JogAmp dans votre IDE préféré](https://jogamp.org/wiki/index.php/Setting_up_a_JogAmp_project_in_your_favorite_IDE)

3. Entrez ce qui suit dans les options VM d'exécution de votre IDE :
   `--enable-native-access=ALL-UNNAMED --add-opens java.base/java.lang=ALL-UNNAMED`

## Commandes

- **Flèche gauche / A** : Déplacer à gauche
- **Flèche droite / D** : Déplacer à droite
- **Espace** : Tirer un projectile
- **R** : Redémarrer le jeu (après Game Over ou Victoire)

## Gameplay

1. Détruisez tous les aliens pour gagner
2. Ne laissez pas les aliens atteindre votre position
3. Les aliens accélèrent au fur et à mesure de leur déplacement
4. Le score augmente de 10 points par alien détruit
5. Trois niveaux de difficultés

## Détails techniques

### Comportement d'essaim
- Les aliens se déplacent en groupe toutes les 0,5 secondes
- La vitesse augmente de 20 % après chaque changement de direction
- Descente de 20 unités lorsqu'ils touchent les bords de l'écran

### Pipeline de rendu
1. Effacer l'écran (fond bleu foncé)
2. Mettre à jour la logique du jeu (si en cours)
3. Rendre les entités actives :
    - Joueur
    - Aliens
    - Projectiles
    - Particules
    - Projectiles aliens
    - Briques de mur
    - Murs
4. Rendre les éléments de l'interface utilisateur

## Étendre le jeu

### Ajouter du son
Ajoutez des effets sonores en utilisant `javax.sound.sampled` :
```java
// Dans GameEngine
private Clip shootSound;
private Clip explosionSound;

// Charger les sons dans init()
// Jouer dans spawnExplosion() et création de projectile
```

### Ajouter des power-ups
1. Créer une classe `PowerUp` avec différents types
2. Apparition aléatoire lorsque des aliens sont détruits
3. Ajouter la détection de collision joueur vs power-ups
4. Implémenter les effets (tir rapide, boucliers, etc.)

### Ajouter des types d'aliens
1. Le "Calmar" (Rangée supérieure) : Utilisez deux triangles étroits pour les "tentacules" en bas pour le rendre plus difficile à toucher par le côté.
2. Le "Crabe" (Rangées du milieu) : Utilisez un corps large GL_QUADS avec des "pinces" composées de deux petits carrés sur les coins supérieurs.
3. Le "Poulpe" (Rangées inférieures) : Utilisez une base large avec plusieurs petites lignes verticales pour les pattes afin de créer un aspect "lourd".
4. Le "Vaisseau mystère" : Une forme de diamant rouge plat rare qui vole occasionnellement en haut de l'écran pour des points bonus massifs.

### Ajouter des scores bonus
1. Suivi de seuil : Créez une variable nextLifeThreshold = 1000. Chaque fois que le score dépasse cette valeur, déclenchez la récompense.
2. La récompense : Incrémentez la variable 'lives'
3. Difficulté évolutive : Pour maintenir le défi, nous pouvons augmenter l'exigence pour la prochaine vie

## Notes de performance

- FPS verrouillé à 60 via `FPSAnimator`
- Listes d'entités nettoyées avec `removeIf()` pour l'efficacité
- Les particules se désactivent automatiquement après expiration de la vie
- Aucune création d'objet inutile dans la boucle de jeu

## Licence

Ceci est un projet éducatif démontrant le développement de jeux OpenGL en Java.