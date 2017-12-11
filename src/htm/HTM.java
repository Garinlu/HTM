/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package htm;


import graph.graphstream.GraphStreamBuilder;
import graph.graphstream.MyGraphStreamEdge;
import graph.graphstream.MyGraphStreamNode;
import org.graphstream.graph.EdgeFactory;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.NodeFactory;
import org.graphstream.graph.implementations.AbstractGraph;
import org.graphstream.graph.implementations.SingleGraph;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author farmetta
 */
public class HTM {


    /** TODO
     *  Terminer la construction du réseau
     *  Dessiner suivant le type (carré et rond)
     *  Alimenter avec des données
     *  Préparer prototypes fonctions
     *  Connecter un graphe
     */


    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {


        String fileName = "valueIn.txt";
        List<List<Boolean>> valuesIn = new ArrayList<>();

        //read file into stream, try-with-resources
        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            Object[] v = stream.toArray();
            String tmpBool;
            List<Boolean> listBool;
            for (int i = 0; i < v.length; i++) {
                listBool = new ArrayList<>();
                tmpBool = (String) v[i];
                char[] charArray = tmpBool.toCharArray();
                for (int j = 0; j < charArray.length; j++) {
                    listBool.add(charArray[j] == '1');
                }
                valuesIn.add(listBool);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


        Graph graph; // création du graphe
        graph = new SingleGraph("graph");
        graph.setNodeFactory(new NodeFactory<MyGraphStreamNode>() {
			public MyGraphStreamNode newInstance(String id, Graph graph) {
				return new MyGraphStreamNode((AbstractGraph) graph, id); // les noeuds seront de type MyGraphStreamNode
			}
		});
        
        graph.setEdgeFactory(new EdgeFactory<MyGraphStreamEdge>() {
            
            @Override
            public MyGraphStreamEdge newInstance(String id, Node src, Node dst, boolean directed) {
                return new MyGraphStreamEdge(id, src, dst, directed); // les arrêtes seront du type MyGraphStreamEdge
            }
            
			
		});
        
        GraphStreamBuilder gb = new GraphStreamBuilder(graph);
        MyNetwork mn = new MyNetwork(valuesIn, gb, gb);
        
        mn.buildNetwork(24, 17); // un réseau de démonstration avec 16 entrées et 5 colonnes
        graph.display(false);

        new Thread(mn).start(); // exécution d'un processus d'apprentissage, à définir, pour mn

    }

}
