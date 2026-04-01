package com.example.labconcurrenceandroid;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // Références aux composants de l'interface
    private TextView labelInfo;
    private ProgressBar barreProgression;
    private ImageView vueImage;

    // Handler permettant de poster des actions sur le UI Thread
    private Handler handlerUI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialisation des composants
        labelInfo       = findViewById(R.id.labelInfo);
        barreProgression = findViewById(R.id.barreProgression);
        vueImage        = findViewById(R.id.vueImage);

        Button btnDemarrerThread = findViewById(R.id.btnDemarrerThread);
        Button btnLancerTache   = findViewById(R.id.btnLancerTache);
        Button btnMessage       = findViewById(R.id.btnMessage);

        // Handler attaché au Looper du thread principal
        handlerUI = new Handler(Looper.getMainLooper());

        // Bouton 3 : vérifie que l'UI reste réactive
        btnMessage.setOnClickListener(v ->
                Toast.makeText(this, "L'interface répond !", Toast.LENGTH_SHORT).show()
        );

        // Bouton 1 : charge une image via un Thread de fond
        btnDemarrerThread.setOnClickListener(v -> demarrerChargementImage());

        // Bouton 2 : lance un calcul intensif avec AsyncTask
        btnLancerTache.setOnClickListener(v -> new TacheLourde().execute());
    }

    // =====================================================
    // PARTIE A — Chargement image avec un Thread manuel
    // =====================================================
    private void demarrerChargementImage() {

        // Afficher la barre et mettre à jour le texte (UI Thread)
        barreProgression.setVisibility(View.VISIBLE);
        barreProgression.setProgress(10);
        labelInfo.setText("État : chargement en cours...");

        // Créer et démarrer un thread de fond
        Thread threadFond = new Thread(() -> {

            // Simuler un délai réseau ou I/O
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // Charger le bitmap (opération pouvant être longue)
            Bitmap imageBitmap = BitmapFactory.decodeResource(
                    getResources(), R.mipmap.ic_launcher
            );

            // Retourner sur le UI Thread pour mettre à jour l'interface
            handlerUI.post(() -> {
                vueImage.setImageBitmap(imageBitmap);
                barreProgression.setVisibility(View.INVISIBLE);
                labelInfo.setText("État : image affichée avec succès ✓");
            });
        });

        threadFond.start();
    }

    // =====================================================
    // PARTIE B — Calcul intensif avec AsyncTask
    // =====================================================
    private class TacheLourde extends AsyncTask<Void, Integer, Long> {

        // Exécuté sur le UI Thread avant le début du travail
        @Override
        protected void onPreExecute() {
            barreProgression.setVisibility(View.VISIBLE);
            barreProgression.setProgress(0);
            labelInfo.setText("État : calcul en cours, patientez...");
        }

        // Exécuté sur un Worker Thread (jamais toucher l'UI ici !)
        @Override
        protected Long doInBackground(Void... params) {
            long total = 0;

            for (int iteration = 1; iteration <= 100; iteration++) {
                // Calcul simulé
                for (int j = 0; j < 200_000; j++) {
                    total += (iteration + j) % 13;
                }
                // Envoyer la progression au UI Thread
                publishProgress(iteration);
            }

            return total;
        }

        // Appelé sur le UI Thread à chaque publishProgress()
        @Override
        protected void onProgressUpdate(Integer... progression) {
            barreProgression.setProgress(progression[0]);
        }

        // Appelé sur le UI Thread quand doInBackground() se termine
        @Override
        protected void onPostExecute(Long total) {
            barreProgression.setVisibility(View.INVISIBLE);
            labelInfo.setText("État : terminé — résultat = " + total + " ✓");
        }
    }
}