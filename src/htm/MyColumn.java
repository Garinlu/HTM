/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package htm;

import graph.AbstractNetworkNode;
import graph.EdgeInterface;
import graph.NodeInterface;

import java.util.List;


/**
 * @author farmetta
 */
public class MyColumn extends AbstractNetworkNode {


    private final double DELTA = 0.05;
    private List<MySynapse> synapseList;
    private int threshhold = 3;

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
                ((MySynapse) synapse).currentValueUdpate(DELTA);
            } else {
                ((MySynapse) synapse).currentValueUdpate(-DELTA);
            }

        }
    }

    private int getValue() {
        int value = 0;
        for (EdgeInterface synapse : this.getNode().getEdgeIn()) {
            if (((MySynapse) synapse).isActivated() && ((MyNeuron) synapse.getNodeIn().getAbstractNetworkNode()).isState()) {
                value++;
            }
        }
        return value;
    }

    public boolean isActivated() {
        return getValue() >= this.threshhold;
    }

    public MyColumn(NodeInterface _node) {
        super(_node);
    }
}


