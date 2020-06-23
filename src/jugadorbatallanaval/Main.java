package jugadorbatallanaval;

import co.edu.javeriana.algoritmos.proyecto.Casilla;
import co.edu.javeriana.algoritmos.proyecto.RespuestaJugada;
import co.edu.javeriana.algoritmos.proyecto.ResumenTablero;
import co.edu.javeriana.algoritmos.proyecto.Tablero;

/**
 *
 * @author Paula Juliana Rojas, Juan Pablo Ortiz, Santiago Meneses
 */
public class Main
{

    //VARIABLES
    //Inicializar Tablero de jugador 
    private final int tipoGrafo = 1; //ListaAdyacencia = 1, ListaArista = 2, MatrizAdyacencia = 3
    private final int dimension = 4;
    private final int barcos[] =
    {
        2
    };
    String tablero[][];

    public static void main(String[] args)
    {
        Main inciar = new Main();
        long inicio = System.currentTimeMillis();
        inciar.inicializar();
        long fin = System.currentTimeMillis();
        double tiempo = (double) ((fin - inicio) / 1);
        System.out.println(tiempo + " Milisegundos");
    }

    public void inicializar()
    {
        tablero = new String[dimension + 1][dimension + 1];
        iniciarTableroMuestra();

        //Inicializar
        JugadorGuyIncognito p1 = new JugadorGuyIncognito();
        JugadorGuyIncognito p2 = new JugadorGuyIncognito();
        Tablero tableroP1 = p1.iniciarTablero(dimension, barcos);
        Tablero tableroP2 = p2.iniciarTablero(dimension, barcos);

        System.out.println(" Milisegundos");

        //P1 Recibe el resumen de P2
        p1.recibirResumenRival(tableroP2.obtenerResumen());
        //P2 Recibe el resumen de P1
        p2.recibirResumenRival(tableroP1.obtenerResumen());

        //Obtener Resumen del tablero
        TableroEnemigo tableroEnemigoP1 = p1.getTableroContrincante();
        //imprimirResumen(tableroJugadorP2.obtenerResumen());
        imprimirResumen(tableroEnemigoP1.obtenerResumen());

        Casilla tempDisparo;
        RespuestaJugada respuesta;
        for(int i = 0; p2.getTableroJugador().numeroBarcosNoHundidos() > 0 && p1.getTableroJugador().numeroBarcosNoHundidos() > 0; i++)
        {
            System.out.println("Turno #" + i);

            //P1 Dispara
            tempDisparo = p1.realizarDisparo();
            System.out.println(tempDisparo.toString());

            //P2 recibe disparo y retorna respuesta
            respuesta = p2.registrarDisparoAPosicion(tempDisparo);
            System.out.println(respuesta.getLetrero());

            //P1 procesa respuesta
            p1.procesarResultadoDisparo(respuesta);

            //imprimir resumen con disparos
            imprimirResumenMejorado(p2.getTableroJugador().obtenerResumen(), tempDisparo, respuesta);

            //P2 Dispara
            tempDisparo = p2.realizarDisparo();
            //P1 recibe disparo y retorna respuesta
            respuesta = p1.registrarDisparoAPosicion(tempDisparo);
            //P2 procesa respuesta
            p2.procesarResultadoDisparo(respuesta);
        }
        System.out.println( (p2.getTableroJugador().numeroBarcosNoHundidos()<=0? "GANASTE!!!!!!!!!!  (Ganador P1)":"PERDISTE :(  (Ganador P2)") );

    }

    public void iniciarTableroMuestra()
    {
        for(int i = 0; i < dimension + 1; i++)
        {
            for(int j = 0; j < dimension + 1; j++)
            {
                tablero[i][j] = " ";
            }
        }
    }

    public void imprimirResumen(ResumenTablero obtenerResumen)
    {
        System.out.print("  ");
        for(int i = 0; i < dimension; i++)
        {
            System.out.print(obtenerResumen.getCasillasOcupadasEnColumna(i) + "  ");
        }
        System.out.println("");
        for(int i = 0; i < dimension; i++)
        {
            System.out.println(obtenerResumen.getCasillasOcupadasEnFila(i));
        }
        System.out.println("");
    }

    public void imprimirResumenMejorado(ResumenTablero obtenerResumen, Casilla c, RespuestaJugada r)
    {
        for(int i = 0; i < tablero.length - 1; i++)
        {
            tablero[0][i + 1] = String.valueOf(obtenerResumen.getCasillasOcupadasEnColumna(i));
        }
        for(int i = 0; i < dimension; i++)
        {
            tablero[i + 1][0] = String.valueOf(obtenerResumen.getCasillasOcupadasEnFila(i));
        }

        switch(r)
        {
            case AGUA:
                tablero[c.getFila() + 1][c.getColumna() + 1] = "O";
                break;
            case IMPACTO:
                tablero[c.getFila() + 1][c.getColumna() + 1] = "x";
                break;
            case HUNDIDO:
                tablero[c.getFila() + 1][c.getColumna() + 1] = "X";
                break;
            default:
                throw new AssertionError(r.name());
        }

        for(int i = 0; i < tablero.length; i++)
        {
            for(int j = 0; j < tablero.length; j++)
            {
                System.out.print(tablero[i][j] + "  ");
            }
            System.out.println("");
        }
    }
}
