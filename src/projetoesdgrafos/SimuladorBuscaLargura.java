package projetoesdgrafos;

import br.com.davidbuzatto.jsge.math.Vector2;
import projetoesdgrafos.grafo.Vertice;
import projetoesdgrafos.utils.LinkedQueue;
import projetoesdgrafos.utils.Utils;
import projetoesdgrafos.grafo.Grafo;
import br.com.davidbuzatto.jsge.core.engine.EngineFrame;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.stream.IntStream;

import static java.lang.Math.atan2;

/**
 * Simulador de Busca em Largura.
 * 
 * @author Prof. Dr. David Buzatto
 */
public class SimuladorBuscaLargura extends EngineFrame {
    
    public SimuladorBuscaLargura() { super( 650, 500, "Busca em Largura", 60, true ); }

    private record Seta(int origem, int destino){};
    private Grafo grafo;
    private Vertice sel;
    private ArrayList<Boolean> visitado;
    private ArrayList<Integer> distTo;
    private ArrayList<Seta> setas;

    @Override
    public void create() {
        grafo = Utils.criarGrafoTeste();
        System.out.println( grafo );
        visitado = new ArrayList<Boolean>(13);
        distTo = new ArrayList<Integer>(13);
        for (int i = 0; i < 13; i++) {
            visitado.add(false);
            distTo.add(0);
        }
        setas = new ArrayList<>();
        setDefaultFontSize( 20 );
        setDefaultStrokeLineWidth( 2 );
        setDefaultStrokeEndCap( STROKE_CAP_ROUND );
    }

    @Override
    public void update( double delta ) {
        if (isMouseButtonPressed(1)) {
            var teste = detectarVertice(getMouseX(), getMouseY());
            if (teste != null) {
                if (sel != null) {
                    sel.cor = EngineFrame.WHITE;
                    setas.clear();
                    for (int i = 0; i < 13; i++) {
                        visitado.set(i, false);
                        distTo.set(i, 0);
                    }
                }
                System.out.println(teste.id);
                teste.cor = EngineFrame.BLUE;
                sel = teste;
                bfs(sel.id);
            }
            for (var s : setas){
                System.out.printf("%d -> %d\n", s.origem, s.destino);
            }
        }
    }

    @Override
    public void draw() {
        clearBackground( WHITE );
        drawText( "Clique para escolher a fonte e executar o algoritmo.", 10, 10, BLACK );
        grafo.draw( this );
        for (var seta : setas) {
            Vector2 inicio = grafo.vertices.get(seta.origem).pos;
            Vector2 fim = grafo.vertices.get(seta.destino).pos;
            drawLine(inicio, fim, GREEN);
            desenharPonta(inicio, fim, GREEN);
        }
    }

    public static void main( String[] args ) {
        new SimuladorBuscaProfundidade();
    }


    private void bfs( int s ) {
        LinkedQueue<Integer> queue = new LinkedQueue<Integer>();
        queue.enqueue( s );
        visitado.set(s, true);
        while ( !queue.isEmpty() ) {
            int v = queue.dequeue();
            for ( var aresta : grafo.adjacentes( v ) ) {
                var w = aresta.destino.id == v ? aresta.origem.id : aresta.destino.id;
                if ( !visitado.get(w) ) {
                    queue.enqueue( w );
                    visitado.set(w, true);
                    setas.add(new SimuladorBuscaLargura.Seta(v,w));
                    distTo.set(w, distTo.get(v) + 1);
                }
            }
        }
    }

    private Vertice detectarVertice (int x, int y) {
        for (var vertice : grafo.vertices.values()) {
            if (vertice.pos.distance(new Vector2(x,y)) < 30)
                return vertice;
        }
        return null;
    }

    private void desenharPonta(Vector2 inicio, Vector2 fim, Paint paint) {
        double delta_x = fim.x - inicio.x;
        double delta_y = fim.y - inicio.y;
        double theta_radians = atan2(delta_y, delta_x) - Math.PI/2;
        Vector2 centro = new Vector2();
        centro.x = ((inicio.x + fim.x) / 2);
        centro.y = ((inicio.y + fim.y) / 2);

        Vector2 ponto1 = rotacionar(new Vector2(0, 5), centro, theta_radians);
        Vector2 ponto2 = rotacionar(new Vector2(-7, -5), centro, theta_radians);
        Vector2 ponto3 = rotacionar(new Vector2(7, -5), centro, theta_radians);

        fillTriangle(
                ponto1,
                ponto2,
                ponto3,
                paint
        );
    }

    private Vector2 rotacionar(Vector2 vetor, Vector2 offset, double dA){

        return new Vector2 (
                (vetor.x * Math.cos(dA) - vetor.y * Math.sin(dA)) + offset.x,
                (vetor.y * Math.cos(dA) + vetor.x * Math.sin(dA)) + offset.y
        );

    }
}
