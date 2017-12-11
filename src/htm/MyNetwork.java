/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package htm;

import graph.EdgeBuilder;
import graph.EdgeInterface;
import graph.NodeBuilder;
import graph.NodeInterface;

import java.io.IOException;
import java.util.*;


/**
 * @author farmetta
 */
public class MyNetwork implements Runnable {

    private final int THRESHOLD = 3;


    private NodeBuilder nb;
    private EdgeBuilder eb;
    private List<List<Boolean>> valueIn;

    ArrayList<MyNeuron> lstMN;
    ArrayList<MyColumn> lstMC;


    public MyNetwork(List<List<Boolean>> valueIn, NodeBuilder _nb, EdgeBuilder _eb) {
        this.valueIn = valueIn;
        nb = _nb;
        eb = _eb;
    }


    private static final int DENSITE_INPUT_COLUMNS = 24;

    public void buildNetwork(int nbInputs, int nbColumns) throws IOException {


        // création des entrées
        lstMN = new ArrayList<MyNeuron>();
        for (int i = 0; i < nbInputs; i++) {
            NodeInterface ni = nb.getNewNode();
            MyNeuron n = new MyNeuron(ni);
            n.getNode().setPosition(i, 0);
            ni.setAbstractNetworkNode(n);
            lstMN.add(n);
        }
        // création des colonnes
        lstMC = new ArrayList<MyColumn>();
        for (int i = 0; i < nbColumns; i++) {
            NodeInterface ni = nb.getNewNode();
            MyColumn c = new MyColumn(ni, "column_" + i);
            c.getNode().setPosition(i * 2, 10);
            ni.setAbstractNetworkNode(c);

            lstMC.add(c);
        }

        Random rnd = new Random();
        // Connection entre entrées et colonnes
        for (int i = 0; i < DENSITE_INPUT_COLUMNS * lstMC.size(); i++) {

            MyNeuron n = lstMN.get(rnd.nextInt(lstMN.size()));
            MyColumn c = lstMC.get(rnd.nextInt(lstMC.size()));

            if (!n.getNode().isConnectedTo(c.getNode())) {
                EdgeInterface e = eb.getNewEdge(n.getNode(), c.getNode());
                MySynapse s = new MySynapse(e, n);
                e.setAbstractNetworkEdge(s);
            } else {
                i--;
            }
        }


    }

    @Override
    public void run() {
        int i = 0;
        while (true) {
            if (i >= valueIn.size()) i = 0;
            List<Boolean> values = this.valueIn.get(i);

            int j = 0;
            for (MyNeuron neuron : lstMN) {
                neuron.setState(values.get(j));
                j++;
            }

            Map<MyColumn, Double> lstColVal = new HashMap<>();

            /*
                Parcours des colonnes suite à la mise à jour des neurones.
                Appel de la fonction de boost de permanence pour lui dire si sa colonne entre en compétition ou non.
                Ecriture de la sortir pour toute les colonnes n'étant pas illegible.
             */
            for (MyColumn c : lstMC) {
                c.getNode().setState(NodeInterface.State.DESACTIVATED);
                try {
                    if (c.isActivated()) {
                        c.elligible(true);
                        lstColVal.put(c, c.getValue());
                    } else
                        c.writeState("0", true);
                        c.elligible(false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            /*
            On active les colonnes les plus fortes (colmax) tant qu'on dépasse pas le palier THRESHOLD.
            Ecriture de la sortie pour les dernière colonnes.
             */
            int colsAct = 0;
            boolean compet = (lstColVal.size() > THRESHOLD);
            while (THRESHOLD >= colsAct) {
                MyColumn colMax = null;
                for (MyColumn c : lstColVal.keySet()) {
                    if (null == colMax || lstColVal.get(c) > lstColVal.get(colMax)) {
                        colMax = c;
                    }
                }
                if (0 == lstColVal.size())
                    break;
                colMax.getNode().setState(NodeInterface.State.ACTIVATED);
                colMax.updateSynapses();
                try {
                    colMax.writeState("1", true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (compet) colMax.winCompet(true);
                lstColVal.remove(colMax);
                colsAct++;
            }

            /*
            Perdant de la competition
             */
            for (MyColumn c : lstColVal.keySet()) {
                try {
                    c.writeState("0", true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                c.winCompet(false);
            }

            i++;

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
