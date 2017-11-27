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
import java.util.List;
import java.util.PriorityQueue;


/**
 * @author farmetta
 */
public class MyColumn extends AbstractNetworkNode {

    private final int THRESHOLD_PERMANENCE_NB_OBS = 40;
    private final double THRESHOLD_PERMANENCE_MIN = 1.10;
    private double boost_perm = 1.0;

    private final double DELTA = 0.01;
    private List<MySynapse> synapseList;
    private int threshhold = 2;
    private File file;
    private String nameFile;
    private int compteurNbEntree = 0;

    private PriorityQueue<Boolean> lastEligibilite;

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

    public double getValue() {
        int value = 0;
        for (EdgeInterface synapse : this.getNode().getEdgeIn()) {
            if (((MySynapse) synapse.getAbstractNetworkEdge()).isActivated() && ((MyNeuron) synapse.getNodeIn().getAbstractNetworkNode()).isState()) {
                value++;
            }
        }
        return value * this.boost_perm;
    }

    public boolean isActivated() throws IOException {
        if (this.compteurNbEntree == 22)
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
        this.nameFile = nameFile;
        this.file = new File("datas/" + nameFile + ".txt");

        this.writeState(nameFile + " :", false);
        lastEligibilite = new PriorityQueue<>();
    }

    public void writeState(String state, boolean removeDate) throws IOException {
        FileWriter writer = new FileWriter(this.file, removeDate);
        writer.write(state + ((!removeDate || compteurNbEntree == 22) ? "\r\n" : ""));
        writer.close();
    }

    public void elligible(boolean isEligible) {
        this.lastEligibilite.add(isEligible);
        if (this.lastEligibilite.size() > THRESHOLD_PERMANENCE_NB_OBS)
            this.lastEligibilite.poll();
        if (this.lastEligibilite.size() == THRESHOLD_PERMANENCE_NB_OBS) {
            int nbEli = 0;
            for (boolean isEli : this.lastEligibilite) {
                if (isEli) nbEli++;
            }

            double ratioAllum = ((nbEli) / THRESHOLD_PERMANENCE_NB_OBS);
            double ratio = THRESHOLD_PERMANENCE_MIN / (ratioAllum + 1);
            if (ratio > 1) {
                this.boost_perm *= ratio;
                System.out.println("BOOST PERM : ");
                System.out.println(this.nameFile + " : " + this.boost_perm);
            } else {
                this.boost_perm = 1.0;
            }
        }
    }
}


