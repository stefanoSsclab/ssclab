package org.ssclab.vrp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VRDPCalc {

    public static void main(String[] args) {
        String filename = "c:\\appo\\coordi.txt"; // Sostituisci con il percorso del tuo file
        List<double[]> coordinates = readCoordinates(filename);
        double[][] distanceMatrix = calculateDistanceMatrix(coordinates);
        printDistanceMatrix(distanceMatrix);
    }

    // Funzione per leggere le coordinate dal file
    public static List<double[]> readCoordinates(String filename) {
        List<double[]> coordinates = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            boolean readingCoordinates = false;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("NODE_COORD_SECTION")) {
                    readingCoordinates = true;
                    continue;
                }
                if (readingCoordinates) {
                    if (line.trim().equals("EOF")) {
                        break;
                    }
                    String[] parts = line.trim().split("\\s+");
                    double x = Double.parseDouble(parts[1]);
                    double y = Double.parseDouble(parts[2]);
                    coordinates.add(new double[]{x, y}); // Creazione di un array double con due elementi
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return coordinates;
    }

    // Funzione per calcolare la matrice delle distanze
    public static double[][] calculateDistanceMatrix(List<double[]> coordinates) {
        int n = coordinates.size();
        double[][] distanceMatrix = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    distanceMatrix[i][j] = euclideanDistance(coordinates.get(i), coordinates.get(j));
                }
            }
        }
        return distanceMatrix;
    }

    // Funzione per calcolare la distanza euclidea tra due punti
    public static double euclideanDistance(double[] point1, double[] point2) {
        return Math.sqrt(Math.pow(point2[0] - point1[0], 2) + Math.pow(point2[1] - point1[1], 2));
    }

    // Funzione per stampare la matrice delle distanze
    public static void printDistanceMatrix(double[][] distanceMatrix) {
        for (double[] row : distanceMatrix) {
            for (double dist : row) {
                System.out.printf("%.2f ", dist);
            }
            System.out.println();
        }
    }
}

