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
    private final double THRESHOLD_PERMANENCE_MIN = 1.20;
    private double boost_perm = 1.0;

    private final double DELTA = 0.01;
    private List<MySynapse> synapseList;
    private int threshhold = 3;
    private File file;
    private String nameFile;
    private int compteurNbEntree = 0;
    private PriorityQueue<Boolean> lastCompet;
    private final int NB_COMPET_OBS = 25;
    private final double RATIO_COMPET_MIN = 1.2;
    private double boostCompet;

    private PriorityQueue<Boolean> lastEligibilite;



    public MyColumn(NodeInterface _node, String nameFile) throws IOException {
        super(_node);
        this.nameFile = nameFile;
        this.lastCompet = new PriorityQueue<>(NB_COMPET_OBS);
        this.boostCompet = 1.0;
        this.file = new File("datas/" + nameFile + ".txt");

        this.writeState(nameFile + " :", false);
        lastEligibilite = new PriorityQueue<>();
    }

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

    /**
     * Fonction gérant le boost de compétition. On appelle la fonction ci dessous à chaque compétition effectuée par
     * la colonne. A chaque fois on sauvegarde les NB_COMPET_OBS dernières observations. On effectue un ratio dessus
     * et on regarde si ce ratio est inférieur au palier RATIO_COMPET_MIN. Si c'est le cas on augement le boost.
     * Sinon on le remet à 1.
     * @param result
     */
    public void winCompet(boolean result) {
        this.lastCompet.add(result);
        if (this.lastCompet.size() > NB_COMPET_OBS) this.lastCompet.poll();
        int nbTrue = 0;
        for (boolean res : lastCompet) {
            if (res) nbTrue++;
        }
        if (this.lastCompet.size() < NB_COMPET_OBS) return;
        double ratio = nbTrue / lastCompet.size();
        double diff = RATIO_COMPET_MIN / (ratio + 1);
        if (diff > 1) {
            System.out.println("BOOST COMPETITION : ");
            System.out.println("Boost : " + diff * 1.3 + "   " + nameFile);
            this.boost(diff);
        } else  {
            this.boost(1);
        }
    }

    /**
     * Fonction gérant le boost de permanence. On appelle la fonction ci dessous à chaque nouvelle entrée.
     * A chaque fois on sauvegarde les THRESHOLD_PERMANENCE_NB_OBS dernières observations. On effectue un ratio dessus
     * et on regarde si ce ratio est inférieur au palier THRESHOLD_PERMANENCE_MIN. Si c'est le cas on augement le boost.
     * Sinon on le remet à 1.
     */
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
                System.out.println("BOOST PERMANENCE : ");
                System.out.println(this.nameFile + " : " + this.boost_perm);
            } else {
                this.boost_perm = 1.0;
            }
        }
    }

    private void boost(double value) {
        this.boostCompet = value;
    }

    public double getValue() {
        return this.getNonBoostedValue() * this.boost_perm * this.boostCompet;
    }


    /**
     * Fonction permettant de retourner la valeur de la colonne sans les boosts. Utile pour séparer les modifications de la
     * valeur avec les 2 boosts.
     * @return
     */
    private int getNonBoostedValue() {
        int value = 0;
        for (EdgeInterface synapse : this.getNode().getEdgeIn()) {
            if (((MySynapse) synapse.getAbstractNetworkEdge()).isActivated() && ((MyNeuron) synapse.getNodeIn().getAbstractNetworkNode()).isState()) {
                value++;
            }
        }
        return value;
    }

    /**
     * Retourne l'activation de la colonne. Implémente le compteur compteurNbEntree (pour l'écriture dans le fichier =>
     * retour à la ligne)
     *
     * @return boolean
     */
    public boolean isActivated() {
        if (this.compteurNbEntree == 22)
            this.compteurNbEntree = 0;
        this.compteurNbEntree++;

        return getValue() >= this.threshhold;
    }

    public void writeState(String state, boolean keepData) throws IOException {
        FileWriter writer = new FileWriter(this.file, keepData);
        writer.write(state + ((!keepData || compteurNbEntree == 22) ? "\r\n" : ""));
        writer.close();
    }
}


