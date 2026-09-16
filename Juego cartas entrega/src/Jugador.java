import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import javax.swing.JPanel;

public class Jugador {

    private final int TOTAL_CARTAS = 10;
    private final int MARGEN = 10;
    private final int DISTANCIA = 40;

    private Carta[] cartas = new Carta[TOTAL_CARTAS];
    private Random r = new Random();

    public void repartir() {
        for (int i = 0; i < TOTAL_CARTAS; i++) {
            cartas[i] = new Carta(r);
        }
    }

    public void mostrar(JPanel pnl) {
        pnl.setLayout(null);
        pnl.removeAll();
        int posicion = MARGEN + TOTAL_CARTAS * DISTANCIA;
        for (Carta carta : cartas) {
            posicion -= DISTANCIA;
            carta.mostrar(pnl, posicion, MARGEN);
        }
        pnl.repaint();
    }

    public String getGrupos() {
        boolean[] enJuego = new boolean[TOTAL_CARTAS];
        StringBuilder resultado = new StringBuilder();

        // 1. IDENTIFICAR GRUPOS DE IGUAL VALOR (Pares, Ternas, etc.)
        int[] contadores = new int[NombreCarta.values().length];
        for (Carta carta : cartas) {
            contadores[carta.getNombre().ordinal()]++;
        }

        boolean hayGrupos = false;
        for (int i = 0; i < contadores.length; i++) {
            if (contadores[i] >= 2) {
                hayGrupos = true;
                resultado.append(Grupo.values()[contadores[i]])
                         .append(" de ")
                         .append(NombreCarta.values()[i])
                         .append("\n");

                // Marcar cartas usadas
                for (int c = 0; c < TOTAL_CARTAS; c++) {
                    if (cartas[c].getNombre().ordinal() == i) {
                        enJuego[c] = true;
                    }
                }
            }
        }

        if (hayGrupos) {
            resultado.append("\n");
        }

        // 2. IDENTIFICAR ESCALERAS DE LA MISMA PINTA
        for (Pinta pinta : Pinta.values()) {
            List<Carta> cartasPinta = new ArrayList<>();
            for (Carta c : cartas) {
                if (c.getPinta() == pinta) {
                    cartasPinta.add(c);
                }
            }

            // Ordenar por valor numérico
            cartasPinta.sort(Comparator.comparingInt(Carta::getValorNumero));

            // Buscar secuencias consecutivas
            int i = 0;
            while (i < cartasPinta.size()) {
                int inicio = i;
                while (i + 1 < cartasPinta.size() && 
                       cartasPinta.get(i + 1).getValorNumero() == cartasPinta.get(i).getValorNumero() + 1) {
                    i++;
                }

                int longitud = i - inicio + 1;
                if (longitud >= 2) { // 2 o más cartas consecutivas forman escalera
                    Carta primera = cartasPinta.get(inicio);
                    Carta ultima = cartasPinta.get(i);

                    resultado.append(Grupo.values()[longitud])
                             .append(" de ")
                             .append(pinta)
                             .append(" de ")
                             .append(primera.getNombre())
                             .append(" a ")
                             .append(ultima.getNombre())
                             .append("\n");

                    // Marcar cartas usadas en la escalera
                    for (int k = inicio; k <= i; k++) {
                        Carta usada = cartasPinta.get(k);
                        for (int c = 0; c < TOTAL_CARTAS; c++) {
                            if (cartas[c] == usada) {
                                enJuego[c] = true;
                            }
                        }
                    }
                }
                i++;
            }
        }

        // 3. CARTAS SOBRANTES Y TOTAL DE PUNTOS
        StringBuilder sobrantes = new StringBuilder();
        int puntosTotales = 0;
        boolean haySobrantes = false;

        for (int c = 0; c < TOTAL_CARTAS; c++) {
            if (!enJuego[c]) {
                haySobrantes = true;
                sobrantes.append(cartas[c].getNombre())
                         .append(" de ")
                         .append(cartas[c].getPinta())
                         .append("\n");
                puntosTotales += cartas[c].getPuntos();
            }
        }

        resultado.append("\nSobran:\n");
        if (haySobrantes) {
            resultado.append(sobrantes);
        } else {
            resultado.append("Ninguna\n");
        }

        resultado.append("\nPuntos:\n").append(puntosTotales);

        return resultado.toString();
    }
}