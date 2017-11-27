/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package htm;

import graph.AbstractNetworkNode;
import graph.EdgeInterface;
import graph.NodeInterface;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Stack;


/**
 * @author farmetta
 */
public class MyColumn extends AbstractNetworkNode {


    private final double DELTA = 0.1;
    private List<MySynapse> synapseList;
    private int threshhold = 1;
    private File file;
    private int compteurNbEntree = 0;
    private PriorityQueue<Boolean> lastCompet;
    private final int NB_COMPET_OBS = 15;
    private final double RATIO_COMPET_MIN = 1.2;
    private double boostCompet;

    /**
     * TODO : Au cours de l'apprentissage, chaque colonne doit atteindre un taux d'activation.
     * Une colonnne est activée si elle reçoit suffisament de retours positif de ses synapses
     * (le retour est positif si la synapse est active et que son entrée associée l'est également).
     * <p>
     * Pour l'apprentissage, parcourir les synapses en entrée, et faire évoluer les poids synaptiques adéquatement.
     */

    public void updateSynapses() {
        for (EdgeInterface synapse : this.getNode().getEdgeIn()) {
            if (((MyNeuron) synapse.getNodeIn().getAbstractNetworkNode()).isState()) {
                ((MySynapse) synapse.getAbstractNetworkEdge()).currentValueUdpate(DELTA);
            } else {
                ((MySynapse) synapse.getAbstractNetworkEdge()).currentValueUdpate(-DELTA);
            }

        }
    }

    public void winCompet(boolean result) {
        this.lastCompet.add(result);
        int nbTrue = 0;
        for (boolean res : lastCompet) {
            if (res) nbTrue++;
        }
        double ratio = nbTrue / lastCompet.size();
        double diff = RATIO_COMPET_MIN / (ratio + 1);
        if (diff > 1) {
            this.boost(diff);
        }
    }

    private void boost(double value) {
        this.boostCompet = value;
    }

    private double getValue() {
        return this.getNonBoostedValue() * boostCompet;
    }

    private int getNonBoostedValue() {
        int value = 0;
        for (EdgeInterface synapse : this.getNode().getEdgeIn()) {
            if (((MySynapse) synapse.getAbstractNetworkEdge()).isActivated() && ((MyNeuron) synapse.getNodeIn().getAbstractNetworkNode()).isState()) {
                value++;
            }
        }
        return value;
    }

    public boolean isActivated() throws IOException {
        if (this.compteurNbEntree == 10)
            this.compteurNbEntree = 0;
        this.compteurNbEntree++;

        if (getValue() >= this.threshhold) {
            this.writeState("1", true);
            return true;
        }
        this.writeState("0", true);
        return false;
    }

    public MyColumn(NodeInterface _node, String nameFile) throws IOException {
        super(_node);
        this.lastCompet = new PriorityQueue<>(NB_COMPET_OBS);
        this.boostCompet = 1.0;
        this.file = new File("datas/" + nameFile + ".txt");

        this.writeState(nameFile + " :", false);
    }

    public void writeState(String state, boolean removeDate) throws IOException {
        FileWriter writer = new FileWriter(this.file, removeDate);
        writer.write(state + ((!removeDate || compteurNbEntree == 10) ? "\r\n" : ""));
        writer.close();
    }
}


