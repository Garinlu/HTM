/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package htm;

import graph.AbstractNetworkNode;
import graph.NodeInterface;

import java.util.List;


/**
 * @author farmetta
 */
public class MyColumn extends AbstractNetworkNode {

    private List<MySynapse> synapseList;
    private int threshhold = 3;

    /**
     * TODO : Au cours de l'apprentissage, chaque colonne doit atteindre un taux d'activation.
     * Une colonnne est activée si elle reçoit suffisament de retours positif de ses synapses
     * (le retour est positif si la synapse est active et que son entrée associée l'est également).
     * <p>
     * Pour l'apprentissage, parcourir les synapses en entrée, et faire évoluer les poids synaptiques adéquatement.
     */

    public void setSynapse(MySynapse synapse) {
        synapseList.add(synapse);
    }

    public void updateSynapses() {
        boolean state = this.isActivated();
        for (MySynapse synapse : synapseList) {
            if (synapse.isActivated() && synapse.getNeuron().isActivated()) {
                if (state) {
                    synapse.currentValueUdpate(0.1);
                } else {
                    synapse.currentValueUdpate(-0.1);
                }
            }

        }
    }

    private int getValue() {
        int value = 0;
        for (MySynapse synapse : synapseList) {
            if (synapse.isActivated() && synapse.getNeuron().isActivated()) {
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


