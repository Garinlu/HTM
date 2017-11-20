/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package htm;

import graph.AbstractNetworkNode;
import graph.NodeInterface;


/**
 *
 * @author farmetta
 */
public class MyColumn extends AbstractNetworkNode {

    /**
     * TODO : Au cours de l'apprentissage, chaque colonne doit atteindre un taux d'activation. 
     * Une colonnne est activée si elle reçoit suffisament de retours positif de ses synapses 
     * (le retour est positif si la synapse est active et que son entrée associée l'est également).
     * 
     * Pour l'apprentissage, parcourir les synapses en entrée, et faire évoluer les poids synaptiques adéquatement.
     * 
     */
    
    
    public MyColumn(NodeInterface _node) {
        super(_node);
    }
}


