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


/**
 * @author farmetta
 */
public class MyColumn extends AbstractNetworkNode {


    private final double DELTA = 0.1;
    private List<MySynapse> synapseList;
    private int threshhold = 1;
    private File file;
    private int compteurNbEntree = 0;

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

    private int getValue() {
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

        this.file = new File("datas/" + nameFile + ".txt");

        this.writeState(nameFile + " :", false);
    }

    public void writeState(String state, boolean removeDate) throws IOException {
        FileWriter writer = new FileWriter(this.file, removeDate);
        writer.write(state + ((!removeDate || compteurNbEntree == 10) ? "\r\n" : ""));
        writer.close();
    }
}


