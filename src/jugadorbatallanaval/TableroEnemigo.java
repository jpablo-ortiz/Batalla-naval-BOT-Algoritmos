package jugadorbatallanaval;

import Estructuras.Grafo.GrafoAbstracto;
import Estructuras.Grafo.GrafoListaAdyacencia;
import Estructuras.Grafo.GrafoListaAristas;
import Estructuras.Grafo.GrafoMatrizAdyacencia;
import Estructuras.Vertice;
import co.edu.javeriana.algoritmos.proyecto.Casilla;
import co.edu.javeriana.algoritmos.proyecto.RespuestaJugada;
import co.edu.javeriana.algoritmos.proyecto.ResumenTablero;
import co.edu.javeriana.algoritmos.proyecto.Tablero;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TableroEnemigo implements Tablero
{
    //===============================//
    //========== Atributos ==========//
    //===============================//

    //private int[] casillasOcupadasFila, casillasOcupadasColumna;
    private ResumenTablero tableroResumen;
    private GrafoAbstracto tableroGrafo; // nodos valor, nodos de posicion, y nodos puntaje (todo el tablero)

    private ArrayList<BarcoEnemigo> barcosDelEnemigo; //Almacena los Vertice Barco
    private ArrayList<Vertice<Integer>> casillasOcupadasFila, casillasOcupadasColumna; //Almacena los Vertice CantiCasillasOcupadas Filas y Columnas
    private ArrayList<Vertice<Integer>> verticesValorProbabilidad; //Almacena los Vertices Valor Probabilidad

    private Vertice<Casilla> ultimoDisparo;
    private InspeccionDisparo inspeccionActual;

    private int dimension;

    //===============================//
    //======== Constructores ========//
    //===============================//
    public TableroEnemigo(int dimension, int[] barcos, int tipoGrafo)
    {
        //Inicializacion de variables Array
        barcosDelEnemigo = new ArrayList<>();
        casillasOcupadasFila = new ArrayList<>();
        casillasOcupadasColumna = new ArrayList<>();
        verticesValorProbabilidad = new ArrayList<>();
        inspeccionActual = new InspeccionDisparo();
        this.dimension = dimension;

        //3 implementaciones de grafo para probar la mejor
        switch(tipoGrafo)
        {
            case 1:
                tableroGrafo = new GrafoListaAdyacencia();
                break;
            case 2:
                tableroGrafo = new GrafoListaAristas();
                break;
            case 3:
                tableroGrafo = new GrafoMatrizAdyacencia();
                break;
        }

        //Agregar barcos
        for(int i = 0; i < barcos.length; i++)
        {
            barcosDelEnemigo.add(new BarcoEnemigo(barcos[i]));
        }

        //(Crear el resto del grafo) se hace cuando llegue el tablero resumen
    }

    //===============================//
    //=========== Métodos ===========//
    //===============================//
    //Funciones para Generar tablero necesario
    //Para Generar el tablero necesario para iniciar debe llamarse recibirResumenRival
    public void recibirResumenRival(ResumenTablero resumen)
    {
        if(this.tableroResumen == null)
        {
            this.tableroResumen = resumen;
            creacionGrafo(resumen);
        }
        else
        {
            this.tableroResumen = resumen;
        }
    }

    public void creacionGrafo(ResumenTablero resumen)
    {
        //-------TABLERO RESUMEN-------//
        //Agregar Vertices casillasOcupadasFila y casillasOcupadasColumna
        for(int i = 0; i < dimension; i++)
        {
            Vertice filaTemp = new Vertice<>(resumen.getCasillasOcupadasEnFila(i), tableroGrafo.vertices.size(), 0, "Circulo");
            casillasOcupadasFila.add(filaTemp);
            tableroGrafo.agregarVertice(filaTemp);
            Vertice columnaTemp = new Vertice<>(resumen.getCasillasOcupadasEnColumna(i), tableroGrafo.vertices.size(), 0, "Circulo");
            casillasOcupadasColumna.add(columnaTemp);
            tableroGrafo.agregarVertice(columnaTemp);
        }

        //Agregar aristas para casillas ocupadas entre si y filas ocupadas entre si
        for(int i = 0; i < (dimension * 2) - 2; i += 2)
        {
            tableroGrafo.agregarArista(tableroGrafo.vertices.get(i), tableroGrafo.vertices.get(i + 2), 1.0, false);
            tableroGrafo.agregarArista(tableroGrafo.vertices.get(i + 1), tableroGrafo.vertices.get(i + 3), 1.0, false);
        }

        //Crear las casillas con su probabilidad y conectarla al grafo
        inicializarProbabilidades();

        //Graficar
        mostrarGrafo();
    }

    public void inicializarProbabilidades()
    {
        //Llenar el tablero con las probabilidades T(n^2)

        for(int i = 0; i < dimension; i++) //Recorrido en filas
        {
            int ocupadasFila = this.casillasOcupadasFila.get(i).getValor();
            for(int j = 0; j < dimension; j++) //Recorrido en columnas
            {
                int ocupadasColumna = this.casillasOcupadasColumna.get(j).getValor();
                int probabilidadActual;
                if(ocupadasFila == 0 || ocupadasColumna == 0)
                {
                    probabilidadActual = 0;
                }
                else
                {
                    probabilidadActual = ocupadasFila + ocupadasColumna;
                }

                //Crear la casilla i,j del tablero
                Vertice casillaTemporal = new Vertice<>(new Casilla(i, j), tableroGrafo.vertices.size(), 0, "Cuadrado");

                //Se guarda la casilla en el tableroGrafo
                tableroGrafo.agregarVertice(casillaTemporal);

                //Agregar la arista bidireccional entre la casilla y su fila y columna
                tableroGrafo.agregarArista(casillaTemporal, this.casillasOcupadasFila.get(i), 1.0, true);
                tableroGrafo.agregarArista(casillaTemporal, this.casillasOcupadasColumna.get(j), 1.0, true);

                agregarAristaConVerticeProbabilidad(casillaTemporal, probabilidadActual);
            }
        }

        //obtener todas los vertices Casilla
        List<Vertice> verticesCasillasDelTablero = tableroGrafo.vertices.stream()
                .filter((cadaVertice) -> (cadaVertice.getValor() instanceof Casilla))
                .collect(Collectors.toList());
        //Recorrer cada verticeCasilla para agregar sus vecinos
        for(Vertice<Casilla> cadaCasilla : verticesCasillasDelTablero)
        {
            int fila = cadaCasilla.getValor().getFila();
            int columna = cadaCasilla.getValor().getColumna();
            //Recorrer cadaVecino en busqueda de los vecinos de "cadaCasilla"
            for(Vertice<Casilla> cadaVecino : verticesCasillasDelTablero)
            {
                //Si el vecino no es la misma casilla
                if(cadaCasilla != cadaVecino)
                {
                    //Si es vecino en fila
                    if(cadaVecino.getValor().getFila() == fila - 1 || cadaVecino.getValor().getFila() == fila || cadaVecino.getValor().getFila() == fila + 1)
                    {
                        //Y si es vecino en Columna
                        if(cadaVecino.getValor().getColumna() == columna - 1 || cadaVecino.getValor().getColumna() == columna || cadaVecino.getValor().getColumna() == columna + 1)
                        {
                            //agregar Arista
                            tableroGrafo.agregarArista(cadaCasilla, cadaVecino, 1.0, true);
                        }
                    }
                }
            }
        }
    }

    public void agregarAristaConVerticeProbabilidad(Vertice casillaTemporal, int probabilidadActual)
    {
        //Buscar y guardar si el vertice probabilidad de esta casilla ya existe
        Optional<Vertice<Integer>> item = verticesValorProbabilidad
                .stream()
                .filter(a -> a.getValor() == probabilidadActual)
                .findAny();

        if(item.isPresent()) //Si ya existe el vertice Valor Probabilidad
        {
            //Agregar la arista bidireccional entre la casilla y el vertice del valor de la probabilidad
            tableroGrafo.agregarArista(casillaTemporal, item.get(), 1.0, true);
            //System.out.println("Presente: " + item.get().getValor() + " Probabilida era: " + probabilidadActual);
        }
        else // Si no existe el vertice Valor Probabilidad
        {
            //Se crea un vertice probabilidad con la probabilidad de esta casilla
            Vertice<Integer> verticeValorProbaTemp = new Vertice<>(probabilidadActual, tableroGrafo.vertices.size(), 0, "Rectangulo");

            //Se guarda ese vertice probabilidad en el tablero grafo y en los verticesValorProbabilidad
            verticesValorProbabilidad.add(verticeValorProbaTemp);
            tableroGrafo.agregarVertice(verticeValorProbaTemp);

            //Agregar la arista bidireccional entre la casilla y el vertice del valor de la probabilidad
            tableroGrafo.agregarArista(casillaTemporal, verticeValorProbaTemp, 1.0, true);

            //System.out.println(verticeValorProbaTemp.getValor());
        }
    }

    //Función para actualizar los datos con la respuesta entregada después de disparar
    public void actualizarProbabilidadesTablero(RespuestaJugada resultado)
    {
        /*for(Vertice v : tableroGrafo.vertices)
        {
            if(v.getValor() instanceof Casilla)
            {
                if(((Casilla)v.getValor()).getFila() == 2 && ((Casilla)v.getValor()).getColumna() == 2)
                {
                    System.out.println("EL DE (2,2)  " + darValorProbabilidadDeLaCasilla(v).getValor());
                }
            }
        }*/
        Vertice<Integer> verticeValorProbaUltimoDisparo = darValorProbabilidadDeLaCasilla(ultimoDisparo);
        switch(resultado)
        {
            case AGUA: //agua -> nodo del que se disparo la probabilidad = 0;
                //Quitar la arista que relaciona al ultimoDisparo con el nodo valorProbabilidad.
                tableroGrafo.eliminarArista(ultimoDisparo, verticeValorProbaUltimoDisparo, dimension, true);

                //Si el verticeValorProbabilidad queda suelto eliminarlo
                if(getTableroGrafo().vecinos(verticeValorProbaUltimoDisparo).isEmpty())
                {
                    getVerticesValorProbabilidad().remove(verticeValorProbaUltimoDisparo);
                    getTableroGrafo().eliminarVertice(verticeValorProbaUltimoDisparo);
                }

                //Ponerlo en valorProbabilidad 0
                agregarAristaConVerticeProbabilidad(ultimoDisparo, 0);
                break;

            case IMPACTO:
                procesoDeImpacto(verticeValorProbaUltimoDisparo);
                break;

            case HUNDIDO: //hundido -> 1.Actualizar todos los nodos aledaños en 0 de probabilidad (porque no pueden haber barcos pegados)
                //primero hacer el impacto en la última casilla y ahi si hundir
                procesoDeImpacto(verticeValorProbaUltimoDisparo);

                //Poner Barco Destruido en True (el barco que corresponde a ese tamaño)
                int tamBarco = inspeccionActual.getInspeccionActual().size();
                for(BarcoEnemigo barcoEnemigo : barcosDelEnemigo)
                {
                    if(barcoEnemigo.getTam() == tamBarco && !barcoEnemigo.isDestruido())
                    {
                        barcoEnemigo.setDestruido(true);
                        break;
                    }
                }

                //Poner el valor probabilidad de las casillas del barco hundido en -2
                //Recorrer todos los vertices del barco hundido
                for(Vertice<Casilla> casillaInspeccionActual : inspeccionActual.getInspeccionActual())
                {
                    //Recorrer los vecinos de esa casilla
                    for(Vertice vecino : tableroGrafo.vecinos(casillaInspeccionActual))
                    {
                        //Cuando se encuentre el vecino que está en verticesValorProbabilidad
                        if(verticesValorProbabilidad.contains(vecino))
                        {
                            //Quitar la arista que lo relaciona con el nodo valorProbabilidad.
                            tableroGrafo.eliminarArista(casillaInspeccionActual, vecino, dimension, true);

                            //Si el verticeValorProbabilidad queda suelto eliminarlo
                            if(getTableroGrafo().vecinos(vecino).isEmpty())
                            {
                                getVerticesValorProbabilidad().remove(vecino);
                                getTableroGrafo().eliminarVertice(vecino);
                            }

                            //Ponerlo en arista -2
                            agregarAristaConVerticeProbabilidad(casillaInspeccionActual, -2);
                        }
                        else //Cuando se encuentre un vecino Casilla
                        {
                            Vertice<Integer> verticeValorProbabilidadDeLaCasilla = darValorProbabilidadDeLaCasilla(vecino);
                            //si el valor de probabilidad de esta casilla es distinto a -2,-1 y 0 actualizar proba.
                            if(vecino.getValor() instanceof Casilla && verticeValorProbabilidadDeLaCasilla.getValor() > 0)
                            {
                                //System.out.println(vecino.getValor() + "    "+ verticeValorProbabilidadDeLaCasilla.getValor());

                                //Quitar la arista que lo relaciona con el nodo valorProbabilidad casillasEnLaFila.
                                tableroGrafo.eliminarArista(vecino, verticeValorProbabilidadDeLaCasilla, dimension, true);

                                //Si el verticeValorProbabilidad queda suelto eliminarlo
                                if(getTableroGrafo().vecinos(vecino).isEmpty())
                                {
                                    getVerticesValorProbabilidad().remove(vecino);
                                    getTableroGrafo().eliminarVertice(vecino);
                                }

                                //Ponerlo en arista 0
                                agregarAristaConVerticeProbabilidad(vecino, 0);
                            }
                        }
                    }
                }
                inspeccionActual.clear();
                break;

            default:
                break;
        }
        //mostrarGrafo();
        mejorProbabilidadDeDisparo();
    }

    public void procesoDeImpacto(Vertice<Integer> verticeValorProbaUltimoDisparo)
    {
        //CHECK 1.Actualizar la fila y columna con (casillasOcupadas-1) (los que son 0, -1 y -2 no se les resta (todo v<0))

        //Quitar la arista que relaciona al ultimoDisparo con el nodo valorProbabilidad.
        tableroGrafo.eliminarArista(ultimoDisparo, verticeValorProbaUltimoDisparo, dimension, true);

        //Si el verticeValorProbabilidad queda suelto eliminarlo
        if(getTableroGrafo().vecinos(verticeValorProbaUltimoDisparo).isEmpty())
        {
            getVerticesValorProbabilidad().remove(verticeValorProbaUltimoDisparo);
            getTableroGrafo().eliminarVertice(verticeValorProbaUltimoDisparo);
        }
        //Ponerlo en valorProbabilidad -1
        agregarAristaConVerticeProbabilidad(ultimoDisparo, -1);

        //Meter el impacto en inspeccionActual
        inspeccionActual.insertarDisparo(ultimoDisparo);

        //Actualizar valor de las filasOcupadas y columnasOcupadas en -1
        int valorFila = casillasOcupadasFila.get(ultimoDisparo.getValor().getFila()).getValor();
        int valorColumna = casillasOcupadasColumna.get(ultimoDisparo.getValor().getColumna()).getValor();
        if(valorFila > 0)
        {
            casillasOcupadasFila.get(ultimoDisparo.getValor().getFila()).setValor(valorFila - 1);
            // System.out.println(valorFila+"    "+ casillasOcupadasFila.get(ultimoDisparo.getValor().getFila()).getValor());
        }
        if(valorColumna > 0)
        {
            casillasOcupadasColumna.get(ultimoDisparo.getValor().getColumna()).setValor(valorColumna - 1);
            //System.out.println(valorColumna+"    "+ casillasOcupadasColumna.get(ultimoDisparo.getValor().getColumna()).getValor());
        }

        boolean filaEnCero = (casillasOcupadasFila.get(ultimoDisparo.getValor().getFila()).getValor() <= 0);
        boolean columnaEnCero = (casillasOcupadasColumna.get(ultimoDisparo.getValor().getColumna()).getValor() <= 0);

        //System.out.println("Actualizando Filas del Impacto");
        //Recorrer todas las casillas en esa fila
        for(Vertice<Casilla> casillasEnLaFila : tableroGrafo.vecinos(casillasOcupadasFila.get(ultimoDisparo.getValor().getFila())))
        {
            Vertice<Integer> verticeValorProbabilidadDeLaCasilla = darValorProbabilidadDeLaCasilla(casillasEnLaFila);
            //if(casillasEnLaFila.getValor() instanceof Casilla)
            //    System.out.println(casillasEnLaFila.getValor()+"   "+verticeValorProbabilidadDeLaCasilla.getValor());
            //si el valor de probabilidad de esta casilla es distinto a -2,-1 y 0 actualizar proba.
            if(casillasEnLaFila.getValor() instanceof Casilla && verticeValorProbabilidadDeLaCasilla.getValor() > 0)
            {
                //Quitar la arista que lo relaciona con el nodo valorProbabilidad casillasEnLaFila.
                tableroGrafo.eliminarArista(casillasEnLaFila, verticeValorProbabilidadDeLaCasilla, dimension, true);

                //Si el verticeValorProbabilidad queda suelto eliminarlo
                if(getTableroGrafo().vecinos(verticeValorProbabilidadDeLaCasilla).isEmpty())
                {
                    getVerticesValorProbabilidad().remove(verticeValorProbabilidadDeLaCasilla);
                    getTableroGrafo().eliminarVertice(verticeValorProbabilidadDeLaCasilla);
                }

                //Ponerlo en arista en un valor menor o en 0
                if(filaEnCero/* || verticeValorProbabilidadDeLaCasilla.getValor()==0*/)
                {
                    //System.out.println("365 " + casillasEnLaFila.getValor() + "     " + 0);

                    agregarAristaConVerticeProbabilidad(casillasEnLaFila, 0);
                }
                else
                {
                    //System.out.println("371 " + casillasEnLaFila.getValor() + "     " + (verticeValorProbabilidadDeLaCasilla.getValor() - 1));

                    agregarAristaConVerticeProbabilidad(casillasEnLaFila, verticeValorProbabilidadDeLaCasilla.getValor() - 1);
                }

            }
        }

        //System.out.println("Actualizando Columnas del Impacto");
        //Recorrer todas las casillas en esa Columna
        for(Vertice<Casilla> casillasEnLaColumna : tableroGrafo.vecinos(casillasOcupadasColumna.get(ultimoDisparo.getValor().getColumna())))
        {
            Vertice<Integer> verticeValorProbabilidadDeLaCasilla = darValorProbabilidadDeLaCasilla(casillasEnLaColumna);
            //if(casillasEnLaColumna.getValor() instanceof Casilla)
            //    System.out.println(casillasEnLaColumna.getValor()+"   "+verticeValorProbabilidadDeLaCasilla.getValor());
            //si el valor de probabilidad de esta casilla es distinto a -2,-1 y 0 actualizar proba
            if(casillasEnLaColumna.getValor() instanceof Casilla && verticeValorProbabilidadDeLaCasilla.getValor() > 0)
            {
                //Quitar la arista que lo relaciona con el nodo valorProbabilidad casillasEnLaFila.
                tableroGrafo.eliminarArista(casillasEnLaColumna, verticeValorProbabilidadDeLaCasilla, dimension, true);
                //Si el verticeValorProbabilidad queda suelto eliminarlo
                if(getTableroGrafo().vecinos(verticeValorProbabilidadDeLaCasilla).isEmpty())
                {
                    getVerticesValorProbabilidad().remove(verticeValorProbabilidadDeLaCasilla);
                    getTableroGrafo().eliminarVertice(verticeValorProbabilidadDeLaCasilla);
                }

                //Ponerlo en arista en un valor menor o en 0
                if(columnaEnCero /*|| verticeValorProbabilidadDeLaCasilla.getValor()==0*/)
                {
                    //System.out.println("394 " + casillasEnLaColumna.getValor() + "     " + 0);

                    agregarAristaConVerticeProbabilidad(casillasEnLaColumna, 0);
                }
                else
                {
                    //System.out.println("399 " + casillasEnLaColumna.getValor() + "     " + (verticeValorProbabilidadDeLaCasilla.getValor() - 1));

                    agregarAristaConVerticeProbabilidad(casillasEnLaColumna, verticeValorProbabilidadDeLaCasilla.getValor() - 1);
                }

            }
        }
        //System.out.println("Finalizando el procesoDeImpacto");
    }

    //Función para disparar a la casilla con mayor probabilidad (el sistema para ver la mejor probabilidad para disparar)
    public void mejorProbabilidadDeDisparo()
    {
        //Ordenar el Array de verticesValorProbabilidad para que quede en la posicion 0 el vertice de mayor valor de probabilidad
        ordenarVerticesValorProbMayAMen();
        /*for(Vertice v : tableroGrafo.vertices)
        {
            if(v.getValor() instanceof Casilla)
            {
                if(((Casilla)v.getValor()).getFila() == 2 && ((Casilla)v.getValor()).getColumna() == 2)
                {
                    System.out.println("EL DE (2,2)  " + darValorProbabilidadDeLaCasilla(v).getValor());
                }
            }
        }*/

        //Si aun es agua (osea que en inspeccionarActual este vacio, sin vertices impactados continuamente)
        if(inspeccionActual.getInspeccionActual().isEmpty())
        {
            //buscar las casillas con mayor probabilidad en el tablero (se mira el 0 porque anteriormente se organiza de Mayor a menor los verticesValorProbabilidad)
            ArrayList<Vertice> casillasMayorProbabilidad = tableroGrafo.vecinos(verticesValorProbabilidad.get(0));

            for(Vertice vertice : casillasMayorProbabilidad)
            {
                //   System.out.println("HOLAAAAAAA    " +verticesValorProbabilidad.get(0)+ "      "+vertice.getValor());
            }
            //hacer un disparo en una de las casillas de Mayor Probabilidad aleatoriamente            
            int numAleatorio = (int) (Math.random() * (casillasMayorProbabilidad.size() - 1) + 0);
            ultimoDisparo = casillasMayorProbabilidad.get(numAleatorio);
            //System.out.println("406 Dar disparo a mayor casilla (ultimoDisparo null) get: " + numAleatorio + "   tam: " + casillasMayorProbabilidad.size() + "   valorUltimoDisparo: " + ultimoDisparo.getValor());
        }
        else if(inspeccionActual.getInspeccionActual().size() == 1) //Si hay solo un vertice impactado continuamente
        {
            // System.out.println("HOLAAAAAAA122123123    ");

            //Variables temporales para encontrar la casiila con mayor valor
            int valorMax = 0;
            Vertice verticeValorMax = null;

            //Recorrer los vecinos de la casilla a la que ya se disparo y dio impacto
            for(Vertice vecino : tableroGrafo.vecinos(inspeccionActual.getInspeccionActual().get(0)))
            {
                //si es un vertice casilla
                if(vecino.getValor() instanceof Casilla)
                {
                    //Obtener el verticeValorProbabilidad de la casilla actual
                    Vertice<Integer> verticeValorProbabilidadDeLaCasilla = darValorProbabilidadDeLaCasilla(vecino);

                    //Si el verticeValorProbabilidadDeLaCasilla es mayor al maximo valor acumulado
                    if(verticesValorProbabilidad.contains(verticeValorProbabilidadDeLaCasilla) && verticeValorProbabilidadDeLaCasilla.getValor() > valorMax)
                    {
                        //Remplazar por el nuevo máximo y salir del ciclo
                        valorMax = verticeValorProbabilidadDeLaCasilla.getValor();
                        verticeValorMax = vecino;
                    }
                }
            }
            //System.out.println("429 VerticeValorMax " + verticeValorMax.getValor() + " Valor: "+ valorMax);
            ultimoDisparo = verticeValorMax;
        }
        else //Si hay más de un vertice impactado continuamente
        {

            int valorProbaSigCabeza = Integer.MIN_VALUE;
            int valorProbaSigCola = Integer.MIN_VALUE;
            Vertice<Casilla> verticeProbaSigCabeza = null;
            Vertice<Casilla> verticeProbaSigCola = null;

            //revisar los vecinos de la cabeza y ver el que sigue en la direccion y su valor
            //obtener todas los vertices Casilla
            List<Vertice> vecinosCasilla = tableroGrafo.vecinos(inspeccionActual.getCabeza()).stream()
                    .filter((cadaVertice) -> (cadaVertice.getValor() instanceof Casilla))
                    .collect(Collectors.toList());
            //recorrer otra vez los vecinos de la cabeza
            for(Vertice<Casilla> vecinoSegundoNivel : vecinosCasilla)
            {
                //System.out.println("Cabeza: " + inspeccionActual.getCabeza().getValor() + "  Cola:    " + inspeccionActual.getCola().getValor() + "  Vecino: " + vecinoSegundoNivel.getValor()+ "   DIRECCION: "+ inspeccionActual.getDireccion());
                Vertice<Integer> darValorProbabilidadDeLaCasilla = darValorProbabilidadDeLaCasilla(vecinoSegundoNivel);
                switch(inspeccionActual.getDireccion())
                {
                    case 1:
                        //1 horizontal
                        if(vecinoSegundoNivel.getValor().getFila() == inspeccionActual.getCabeza().getValor().getFila() && vecinoSegundoNivel.getValor().getColumna() > inspeccionActual.getCabeza().getValor().getColumna())
                        {
                            valorProbaSigCabeza = darValorProbabilidadDeLaCasilla.getValor();
                            verticeProbaSigCabeza = vecinoSegundoNivel;
                            //System.out.println("CABEZA:  " + vecinoSegundoNivel.getValor() + "   CON PROBA:" + valorProbaSigCabeza);
                        }
                        break;

                    case 2:
                        //2 vertical
                        if(vecinoSegundoNivel.getValor().getFila() < inspeccionActual.getCabeza().getValor().getFila() && vecinoSegundoNivel.getValor().getColumna() == inspeccionActual.getCabeza().getValor().getColumna())
                        {
                            valorProbaSigCabeza = darValorProbabilidadDeLaCasilla.getValor();
                            verticeProbaSigCabeza = vecinoSegundoNivel;
                           // System.out.println("CABEZA:  " + vecinoSegundoNivel.getValor() + "   CON PROBA:" + valorProbaSigCabeza);
                        }
                        break;

                    case 3:
                        //3 = diagonal abajo derecha
                        if(vecinoSegundoNivel.getValor().getFila() < inspeccionActual.getCabeza().getValor().getFila() && vecinoSegundoNivel.getValor().getColumna() < inspeccionActual.getCabeza().getValor().getColumna())
                        {
                            valorProbaSigCabeza = darValorProbabilidadDeLaCasilla.getValor();
                            verticeProbaSigCabeza = vecinoSegundoNivel;
                            //System.out.println("CABEZA:  " + vecinoSegundoNivel.getValor() + "   CON PROBA:" + valorProbaSigCabeza);
                        }
                        break;

                    case 4:
                        //4 = diagonal abajo izquierda
                        if(vecinoSegundoNivel.getValor().getFila() < inspeccionActual.getCabeza().getValor().getFila() && vecinoSegundoNivel.getValor().getColumna() > inspeccionActual.getCabeza().getValor().getColumna())
                        {
                            valorProbaSigCabeza = darValorProbabilidadDeLaCasilla.getValor();
                            verticeProbaSigCabeza = vecinoSegundoNivel;
                            //System.out.println("CABEZA:  " + vecinoSegundoNivel.getValor() + "   CON PROBA:" + valorProbaSigCabeza);
                        }
                        break;
                }
            }

            //revisar los vecinos de la cola y ver el que sigue en la direccion y su valor
            List<Vertice> vecinosCasillaa = tableroGrafo.vecinos(inspeccionActual.getCola()).stream()
                    .filter((cadaVertice) -> (cadaVertice.getValor() instanceof Casilla))
                    .collect(Collectors.toList());
            //recorrer otra vez los vecinos de la cabeza

            /*for(Vertice vertice : vecinosCasillaa)
            {
                System.out.println(inspeccionActual.getCola().getValor() + "         " + vertice.getValor() + "     " + vecinosCasillaa.size());
            }
            System.out.println("");
            for(Vertice v : tableroGrafo.vertices)
            {
                if(v.getValor() instanceof Casilla)
                {
                    if(((Casilla) v.getValor()).getFila() == inspeccionActual.getCola().getValor().getFila() && ((Casilla) v.getValor()).getColumna() == inspeccionActual.getCola().getValor().getColumna())
                    {
                        for(Vertice vertice : tableroGrafo.vecinos(v))
                        {
                            System.out.println(inspeccionActual.getCola().getValor() + "         " + vertice.getValor() + "     " + tableroGrafo.vecinos(v).size());
                        }
                    }
                }
            }*/

            for(Vertice<Casilla> vecinoSegundoNivel : vecinosCasillaa)
            {
                //System.out.println("  Cola:    " + inspeccionActual.getCola().getValor() + "    Cabeza: " + inspeccionActual.getCabeza().getValor() +  "  Vecino: " + vecinoSegundoNivel.getValor()+ "   DIRECCION: "+ inspeccionActual.getDireccion());
                Vertice<Integer> darValorProbabilidadDeLaCasilla = darValorProbabilidadDeLaCasilla(vecinoSegundoNivel);
                switch(inspeccionActual.getDireccion())
                {
                    case 1:
                        //1 horizontal
                        if(vecinoSegundoNivel.getValor().getFila() == inspeccionActual.getCola().getValor().getFila() && vecinoSegundoNivel.getValor().getColumna() < inspeccionActual.getCola().getValor().getColumna())
                        {
                            valorProbaSigCola = darValorProbabilidadDeLaCasilla.getValor();
                            verticeProbaSigCola = vecinoSegundoNivel;
                            //System.out.println("COLA:  " + vecinoSegundoNivel.getValor() + "   CON PROBA:" + valorProbaSigCola);
                        }
                        break;

                    case 2:
                        //2 vertical
                        if(vecinoSegundoNivel.getValor().getFila() > inspeccionActual.getCola().getValor().getFila() && vecinoSegundoNivel.getValor().getColumna() == inspeccionActual.getCola().getValor().getColumna())
                        {
                            valorProbaSigCola = darValorProbabilidadDeLaCasilla.getValor();
                            verticeProbaSigCola = vecinoSegundoNivel;
                            //System.out.println("COLA:  " + vecinoSegundoNivel.getValor() + "   CON PROBA:" + valorProbaSigCola);
                        }
                        break;

                    case 3:
                        //3 = diagonal abajo derecha
                        if(vecinoSegundoNivel.getValor().getFila() > inspeccionActual.getCola().getValor().getFila() && vecinoSegundoNivel.getValor().getColumna() > inspeccionActual.getCola().getValor().getColumna())
                        {
                            valorProbaSigCola = darValorProbabilidadDeLaCasilla.getValor();
                            verticeProbaSigCola = vecinoSegundoNivel;
                            //System.out.println("COLA:  " + vecinoSegundoNivel.getValor() + "   CON PROBA:" + valorProbaSigCola);
                        }
                        break;

                    case 4:
                        //4 = diagonal abajo izquierda
                        if(vecinoSegundoNivel.getValor().getFila() > inspeccionActual.getCola().getValor().getFila() && vecinoSegundoNivel.getValor().getColumna() < inspeccionActual.getCola().getValor().getColumna())
                        {
                            valorProbaSigCola = darValorProbabilidadDeLaCasilla.getValor();
                            verticeProbaSigCola = vecinoSegundoNivel;
                            //System.out.println("COLA:  " + vecinoSegundoNivel.getValor() + "   CON PROBA:" + valorProbaSigCola);
                        }
                        break;
                }
            }

            if(valorProbaSigCabeza > valorProbaSigCola)
            {
                //System.out.println("Cabeza    " + verticeProbaSigCabeza.getValor() + "  Tamanio:  " + inspeccionActual.getInspeccionActual().size() + " valor:  " + valorProbaSigCabeza);
                ultimoDisparo = verticeProbaSigCabeza;
            }
            else
            {
                //System.out.println("Cola;    " + verticeProbaSigCola.getValor() + "  Tamanio:  " + inspeccionActual.getInspeccionActual().size() + " valor:  " + valorProbaSigCola);
                ultimoDisparo = verticeProbaSigCola;
            }
        }
    }

    //Función para enviar el disparo que se va a realizar
    public Casilla darDisparoAEnemigo()
    {
        if(ultimoDisparo == null)
        {
            //Ordenar el Array de verticesValorProbabilidad para que quede en la posicion 0 el vertice de mayor valor de probabilidad
            ordenarVerticesValorProbMayAMen();

            //buscar las casillas con mayor probabilidad en el tablero (se mira el 0 porque anteriormente se organiza de Mayor a menor los verticesValorProbabilidad)
            ArrayList<Vertice> casillasMayorProbabilidad = tableroGrafo.vecinos(verticesValorProbabilidad.get(0));

            //System.out.println("408 Vecinos de las casillas con mayor probabilidad en el tablero (" + verticesValorProbabilidad.get(0).getValor() + ")");
            for(Vertice vecino : casillasMayorProbabilidad)
            {
                //System.out.print(vecino.getValor() + "     ");
            }
            //System.out.println();

            //hacer un disparo en una de las casillas de Mayor Probabilidad aleatoriamente
            int numAleatorio = (int) (Math.random() * (casillasMayorProbabilidad.size() - 1) + 0);
            ultimoDisparo = casillasMayorProbabilidad.get(numAleatorio);
            //System.out.println("518 Dar disparo a mayor casilla (ultimoDisparo null) get: " + numAleatorio + "   tam: " + casillasMayorProbabilidad.size() + "   valorUltimoDisparo: " + ultimoDisparo.getValor());
            //System.out.println();
        }
        return new Casilla(ultimoDisparo.getValor().getFila(), ultimoDisparo.getValor().getColumna());
    }

    //===============================//
    //======= Métodos De Apoyo ======//
    //===============================//
    public void ordenarVerticesValorProbMayAMen()
    {
        verticesValorProbabilidad.sort(new Comparator<Vertice<Integer>>()
        {
            @Override
            public int compare(Vertice<Integer> o1, Vertice<Integer> o2)
            {
                return o2.getValor().compareTo(o1.getValor());
            }
        });
    }

    public Vertice<Integer> darValorProbabilidadDeLaCasilla(Vertice<Casilla> vertice)
    {
        //System.out.println("440 Valor Del Vertice que se estan buscando lo vecinos: " + vertice.getValor());
        //Recorrer los vecinos del vecino para buscar el vecino con el valor de su probabilidad
        for(Vertice vecino : tableroGrafo.vecinos(vertice))
        {
            //Si el vecinoSegundoNivel es valor de probabilidad y es mayor al maximo valor acumulado
            if(verticesValorProbabilidad.contains(vecino))
            {
                //System.out.println("447 ValorProbabilidad: " + vecino.getValor() + "\n");
                return vecino;
            }
            //System.out.println("449 Vecinos y no ValorProbabilidad: " + vecino.getValor());
        }
        //System.out.println("453 ERROR, no tiene ValorProbabilidad: " + vertice.getValor());
        //System.out.println();
        return null;
    }

    //Quitar para el proyecto final (solo grafica para entender)
    public void mostrarGrafo()
    {
        boolean dirigido = false;
        tableroGrafo.graficar(dirigido);
    }

    //===============================//
    //======= Métodos Override ======//
    //===============================//
    @Override
    public ResumenTablero obtenerResumen()
    {
        int fila[] = new int[casillasOcupadasFila.size()];
        int columna[] = new int[casillasOcupadasFila.size()];

        for(int i = 0; i < casillasOcupadasFila.size(); i++)
        {
            fila[i] = casillasOcupadasFila.get(i).getValor();
            columna[i] = casillasOcupadasColumna.get(i).getValor();
        }
        return new ResumenTablero(fila, columna);
    }

    @Override
    public int numeroBarcosNoHundidos()
    {
        int noHundidos = 0;
        for(BarcoEnemigo barco : barcosDelEnemigo)
        {
            if(!barco.isDestruido())
            {
                noHundidos++;
            }
        }
        return noHundidos;
    }

    //===================================//
    //== Métodos Override Innecesarios ==//
    //===================================//
    @Override
    public List<Casilla> obtenerCasillasOcupadasPorBarco(int numeroBarco)
    {
        //No se necesita para este tablero
        return null;
    }

    @Override
    public RespuestaJugada dispararACasilla(Casilla casilla)
    {
        //No se necesita para este tablero
        return null;
    }

    //===============================//
    //====== Getters y Setters ======//
    //===============================//
    public ResumenTablero getTableroResumen()
    {
        return tableroResumen;
    }

    public void setTableroResumen(ResumenTablero tableroResumen)
    {
        this.tableroResumen = tableroResumen;
    }

    public GrafoAbstracto getTableroGrafo()
    {
        return tableroGrafo;
    }

    public void setTableroGrafo(GrafoAbstracto tableroGrafo)
    {
        this.tableroGrafo = tableroGrafo;
    }

    public ArrayList<BarcoEnemigo> getBarcosDelEnemigo()
    {
        return barcosDelEnemigo;
    }

    public void setBarcosDelEnemigo(ArrayList<BarcoEnemigo> barcosDelEnemigo)
    {
        this.barcosDelEnemigo = barcosDelEnemigo;
    }

    public ArrayList<Vertice<Integer>> getCasillasOcupadasFila()
    {
        return casillasOcupadasFila;
    }

    public void setCasillasOcupadasFila(ArrayList<Vertice<Integer>> casillasOcupadasFila)
    {
        this.casillasOcupadasFila = casillasOcupadasFila;
    }

    public ArrayList<Vertice<Integer>> getCasillasOcupadasColumna()
    {
        return casillasOcupadasColumna;
    }

    public void setCasillasOcupadasColumna(ArrayList<Vertice<Integer>> casillasOcupadasColumna)
    {
        this.casillasOcupadasColumna = casillasOcupadasColumna;
    }

    public ArrayList<Vertice<Integer>> getVerticesValorProbabilidad()
    {
        return verticesValorProbabilidad;
    }

    public void setVerticesValorProbabilidad(ArrayList<Vertice<Integer>> verticesValorProbabilidad)
    {
        this.verticesValorProbabilidad = verticesValorProbabilidad;
    }

    public int getDimension()
    {
        return dimension;
    }

    public void setDimension(int dimension)
    {
        this.dimension = dimension;
    }

}
