# LAB 8 - Threads, AsyncTask et Handler

## Aperçu

Ce lab apprend à exécuter des traitements longs (calcul intensif, chargement d'image)
sans bloquer l'interface utilisateur. L'application Android contient plusieurs boutons :
un lance un traitement long, un autre affiche un Toast immédiatement.
Si l'UI reste fluide pendant le traitement, la programmation asynchrone est correcte.

---

## Objectifs

- Comprendre la différence entre UI Thread et Worker Thread
- Créer un Thread avec un Runnable (syntaxe lambda)
- Mettre à jour l'interface depuis un thread de fond avec Handler
- Utiliser AsyncTask avec gestion de la progression
- Éviter les erreurs classiques : UI bloquée, mise à jour UI hors thread principal

---

## Concepts clés

### 1. UI Thread (Main Thread)
C'est le thread qui affiche l'écran, gère les clics et exécute `onCreate()`.
> ⚠️ Si un traitement long tourne dans le UI Thread → l'application se fige (ANR).

### 2. Worker Thread (Thread de fond)
Thread utilisé pour les calculs lourds, accès réseau, ou chargements longs.
Il travaille en arrière-plan pendant que l'UI reste réactive.

### 3. Règle fondamentale
Un Worker Thread **ne peut pas** modifier directement une View.
Sinon : `CalledFromWrongThreadException`.

### 4. Solutions pour revenir au UI Thread

| Méthode | Usage |
|---|---|
| `view.post(runnable)` | Via une View existante |
| `Handler(Looper.getMainLooper()).post(runnable)` | Via un Handler global |
| `runOnUiThread(runnable)` | Depuis une Activity |

### 5. AsyncTask (approche pédagogique)
Exécute `doInBackground()` dans un thread de fond, puis revient automatiquement
sur l'UI Thread via `onPreExecute()`, `onProgressUpdate()` et `onPostExecute()`.

---

## Structure du projet
```
LabConcurrenceAndroid/
├── app/src/main/
│   ├── java/.../MainActivity.java   ← Logique Thread + AsyncTask
│   └── res/layout/activity_main.xml ← Interface XML
└── README.md
```

---

## Interface (activity_main.xml)

L'interface contient :
- Un `TextView` (`labelInfo`) → affiche l'état courant
- Une `ProgressBar` horizontale (`barreProgression`) → progression 0–100
- Un `ImageView` (`vueImage`) → affiche l'image chargée
- 3 boutons :
  1. **Lancer Thread (image)** → charge une image en arrière-plan
  2. **Démarrer tâche lourde** → lance un calcul intensif avec AsyncTask
  3. **Tester réactivité UI** → affiche un Toast immédiatement


---

## Étapes de réalisation

1. **Créer le projet** → Android Studio → New Project → Empty Views Activity → Java
2. **Remplacer** `activity_main.xml` par le code XML fourni
3. **Remplacer** `MainActivity.java` par le code Java fourni
4. **Lancer** l'application sur émulateur ou appareil réel
5. **Tester** les 3 boutons dans l'ordre indiqué ci-dessous

---

## Tests de validation

| Action | Résultat attendu |
|---|---|
| Clic sur **Lancer Thread (image)** | Barre visible, image chargée après ~1,5s |
| Pendant le chargement → **Tester réactivité** | Toast immédiat → UI non bloquée ✓ |
| Clic sur **Démarrer tâche lourde** | Barre progresse de 0 → 100 puis résultat affiché ✓ |

---

## Points importants à retenir

- `new Thread(...).start()` → crée et démarre un thread de fond
- `handlerUI.post(...)` → obligatoire pour modifier l'UI depuis un thread de fond
- `doInBackground()` → jamais d'accès aux Views ici
- `publishProgress()` → seul moyen légal d'envoyer une progression au UI Thread
- `Thread.currentThread().interrupt()` → bonne pratique lors d'une `InterruptedException`

---

## 📸 Captures d'écran

<p align="center"><img width="182" height="400" alt="1" src="https://github.com/user-attachments/assets/a2d1014e-4607-42ed-a964-4367e77f48ec" />

 
