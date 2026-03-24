package arn_projet;

import java.util.*;

/**
 * Implémentation d'un Arbre Rouge-Noir (ARN).
 * Structure auto-équilibrée garantissant des opérations en O(log n).
 * 
 * @param <T> le type des éléments stockés
 */
public class ARN<T> extends AbstractCollection<T> {
    
    private NoeudRN racine;
    private NoeudRN noeudSentinelle;
    private Comparator<? super T> comparateurUtilise;

    /**
     * Énumération pour les couleurs des noeuds.
     */
    private enum Couleur {
        ROUGE, NOIR
    }

    /**
     * Classe représentant un noeud de l'arbre rouge-noir.
     */
    private class NoeudRN {
        T valeur;
        NoeudRN filsGauche;
        NoeudRN filsDroit;
        NoeudRN parent;
        Couleur couleur;

        NoeudRN(T val) {
            this.valeur = val;
            this.couleur = Couleur.ROUGE;
        }

        /**
         * Trouve le noeud minimum dans le sous-arbre.
         * @return le noeud avec la plus petite valeur
         */
        NoeudRN trouverMinimum() {
            NoeudRN noeudActuel = this;
            while (noeudActuel.filsGauche != noeudSentinelle) {
                noeudActuel = noeudActuel.filsGauche;
            }
            return noeudActuel;
        }

        /**
         * Trouve le successeur en ordre infixe.
         * @return le noeud successeur
         */
        NoeudRN trouverSuccesseur() {
            if (this.filsDroit != noeudSentinelle) {
                return this.filsDroit.trouverMinimum();
            }
            
            NoeudRN x = this;
            NoeudRN y = this.parent;
            
            while (y != noeudSentinelle && x == y.filsDroit) {
                x = y;
                y = y.parent;
            }
            
            return y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            @SuppressWarnings("unchecked")
            NoeudRN noeud = (NoeudRN) o;
            return Objects.equals(valeur, noeud.valeur);
        }

        @Override
        public int hashCode() {
            return Objects.hash(valeur);
        }
    }

    /**
     * Constructeur par défaut.
     */
    public ARN() {
        comparateurUtilise = (a, b) -> ((Comparable<T>) a).compareTo(b);
        noeudSentinelle = creerSentinelle();
        racine = noeudSentinelle;
    }

    /**
     * Constructeur avec comparateur personnalisé.
     * @param comp le comparateur à utiliser
     */
    public ARN(Comparator<? super T> comp) {
        this.comparateurUtilise = comp;
        noeudSentinelle = creerSentinelle();
        racine = noeudSentinelle;
    }

    /**
     * Constructeur à partir d'une collection.
     * @param collection la collection à insérer
     */
    public ARN(Collection<? extends T> collection) {
        this();
        for (T element : collection) {
            add(element);
        }
    }

    /**
     * Crée un noeud sentinelle.
     * @return le noeud sentinelle
     */
    private NoeudRN creerSentinelle() {
        NoeudRN sentinelle = new NoeudRN(null);
        sentinelle.couleur = Couleur.NOIR;
        sentinelle.parent = sentinelle;
        sentinelle.filsGauche = sentinelle;
        sentinelle.filsDroit = sentinelle;
        return sentinelle;
    }

    @Override
    public Iterator<T> iterator() {
        return new IterateurARN();
    }

    @Override
    public int size() {
        return calculerTaille(racine);
    }

    private int calculerTaille(NoeudRN noeud) {
        if (noeud == noeudSentinelle) {
            return 0;
        }
        return 1 + calculerTaille(noeud.filsGauche) + calculerTaille(noeud.filsDroit);
    }

    /**
     * Calcule la hauteur de l'arbre.
     * @return la hauteur
     */
    public int obtenirHauteur() {
        return calculerHauteur(racine);
    }

    private int calculerHauteur(NoeudRN noeud) {
        if (noeud == noeudSentinelle) {
            return 0;
        }
        
        int hauteurGauche = calculerHauteur(noeud.filsGauche);
        int hauteurDroite = calculerHauteur(noeud.filsDroit);
        
        return 1 + Math.max(hauteurGauche, hauteurDroite);
    }

    @Override
    public boolean contains(Object obj) {
        return rechercherNoeud(obj) != noeudSentinelle;
    }

    /**
     * Recherche un élément dans l'arbre.
     * @param obj l'objet à rechercher
     * @return le noeud trouvé ou la sentinelle
     */
    private NoeudRN rechercherNoeud(Object obj) {
        NoeudRN noeudActuel = racine;
        
        while (noeudActuel != noeudSentinelle) {
            @SuppressWarnings("unchecked")
            int comparaison = comparateurUtilise.compare(noeudActuel.valeur, (T) obj);
            
            if (comparaison == 0) {
                return noeudActuel;
            } else if (comparaison > 0) {
                noeudActuel = noeudActuel.filsGauche;
            } else {
                noeudActuel = noeudActuel.filsDroit;
            }
        }
        
        return noeudSentinelle;
    }

    @Override
    public boolean add(T element) {
        if (element == null) {
            return false;
        }

        NoeudRN nouveauNoeud = new NoeudRN(element);
        NoeudRN parentPotentiel = noeudSentinelle;
        NoeudRN noeudActuel = racine;

        while (noeudActuel != noeudSentinelle) {
            parentPotentiel = noeudActuel;
            
            int comparaison = comparateurUtilise.compare(element, noeudActuel.valeur);
            
            if (comparaison == 0) {
                return false; // Élément déjà présent
            } else if (comparaison < 0) {
                noeudActuel = noeudActuel.filsGauche;
            } else {
                noeudActuel = noeudActuel.filsDroit;
            }
        }

        nouveauNoeud.parent = parentPotentiel;

        if (parentPotentiel == noeudSentinelle) {
            racine = nouveauNoeud;
            nouveauNoeud.couleur = Couleur.NOIR;
        } else {
            if (comparateurUtilise.compare(element, parentPotentiel.valeur) < 0) {
                parentPotentiel.filsGauche = nouveauNoeud;
            } else {
                parentPotentiel.filsDroit = nouveauNoeud;
            }
        }

        nouveauNoeud.filsGauche = noeudSentinelle;
        nouveauNoeud.filsDroit = noeudSentinelle;
        nouveauNoeud.couleur = Couleur.ROUGE;

        reparerApresInsertion(nouveauNoeud);
        return true;
    }

    /**
     * Répare les propriétés de l'arbre rouge-noir après insertion.
     * @param noeud le noeud inséré
     */
    private void reparerApresInsertion(NoeudRN noeud) {
        while (noeud.parent.couleur == Couleur.ROUGE) {
            if (noeud.parent == noeud.parent.parent.filsGauche) {
                NoeudRN oncle = noeud.parent.parent.filsDroit;
                
                if (oncle.couleur == Couleur.ROUGE) {
                    // Cas 1: l'oncle est rouge
                    noeud.parent.couleur = Couleur.NOIR;
                    oncle.couleur = Couleur.NOIR;
                    noeud.parent.parent.couleur = Couleur.ROUGE;
                    noeud = noeud.parent.parent;
                } else {
                    if (noeud == noeud.parent.filsDroit) {
                        // Cas 2: noeud est un fils droit
                        noeud = noeud.parent;
                        rotationGauche(noeud);
                    }
                    // Cas 3
                    noeud.parent.couleur = Couleur.NOIR;
                    noeud.parent.parent.couleur = Couleur.ROUGE;
                    rotationDroite(noeud.parent.parent);
                }
            } else {
                NoeudRN oncle = noeud.parent.parent.filsGauche;
                
                if (oncle.couleur == Couleur.ROUGE) {
                    noeud.parent.couleur = Couleur.NOIR;
                    oncle.couleur = Couleur.NOIR;
                    noeud.parent.parent.couleur = Couleur.ROUGE;
                    noeud = noeud.parent.parent;
                } else {
                    if (noeud == noeud.parent.filsGauche) {
                        noeud = noeud.parent;
                        rotationDroite(noeud);
                    }
                    noeud.parent.couleur = Couleur.NOIR;
                    noeud.parent.parent.couleur = Couleur.ROUGE;
                    rotationGauche(noeud.parent.parent);
                }
            }
        }
        
        racine.couleur = Couleur.NOIR;
    }

    /**
     * Effectue une rotation à gauche.
     * @param noeud le noeud pivot
     */
    private void rotationGauche(NoeudRN noeud) {
        NoeudRN y = noeud.filsDroit;
        noeud.filsDroit = y.filsGauche;
        
        if (y.filsGauche != noeudSentinelle) {
            y.filsGauche.parent = noeud;
        }
        
        y.parent = noeud.parent;
        
        if (noeud.parent == noeudSentinelle) {
            racine = y;
        } else if (noeud == noeud.parent.filsGauche) {
            noeud.parent.filsGauche = y;
        } else {
            noeud.parent.filsDroit = y;
        }
        
        y.filsGauche = noeud;
        noeud.parent = y;
    }

    /**
     * Effectue une rotation à droite.
     * @param noeud le noeud pivot
     */
    private void rotationDroite(NoeudRN noeud) {
        NoeudRN y = noeud.filsGauche;
        noeud.filsGauche = y.filsDroit;
        
        if (y.filsDroit != noeudSentinelle) {
            y.filsDroit.parent = noeud;
        }
        
        y.parent = noeud.parent;
        
        if (noeud.parent == noeudSentinelle) {
            racine = y;
        } else if (noeud == noeud.parent.filsDroit) {
            noeud.parent.filsDroit = y;
        } else {
            noeud.parent.filsGauche = y;
        }
        
        y.filsDroit = noeud;
        noeud.parent = y;
    }

    @Override
    public boolean remove(Object obj) {
        NoeudRN noeudASupprimer = rechercherNoeud(obj);
        
        if (noeudASupprimer == noeudSentinelle) {
            return false;
        }
        
        supprimerNoeud(noeudASupprimer);
        return true;
    }

    /**
     * Supprime un noeud de l'arbre.
     * @param noeud le noeud à supprimer
     */
    private void supprimerNoeud(NoeudRN noeud) {
        NoeudRN y = (noeud.filsGauche == noeudSentinelle || noeud.filsDroit == noeudSentinelle) 
                    ? noeud : noeud.trouverSuccesseur();
        
        NoeudRN x = (y.filsGauche != noeudSentinelle) ? y.filsGauche : y.filsDroit;
        
        x.parent = y.parent;
        
        if (y.parent == noeudSentinelle) {
            racine = x;
        } else if (y == y.parent.filsGauche) {
            y.parent.filsGauche = x;
        } else {
            y.parent.filsDroit = x;
        }
        
        if (y != noeud) {
            noeud.valeur = y.valeur;
        }
        
        if (y.couleur == Couleur.NOIR) {
            reparerApresSuppression(x);
        }
    }

    /**
     * Répare les propriétés après une suppression.
     * @param noeud le noeud à partir duquel réparer
     */
    private void reparerApresSuppression(NoeudRN noeud) {
        while (noeud != racine && noeud.couleur == Couleur.NOIR) {
            if (noeud == noeud.parent.filsGauche) {
                NoeudRN frere = noeud.parent.filsDroit;
                
                if (frere.couleur == Couleur.ROUGE) {
                    frere.couleur = Couleur.NOIR;
                    noeud.parent.couleur = Couleur.ROUGE;
                    rotationGauche(noeud.parent);
                    frere = noeud.parent.filsDroit;
                }
                
                if (frere.filsGauche.couleur == Couleur.NOIR && 
                    frere.filsDroit.couleur == Couleur.NOIR) {
                    frere.couleur = Couleur.ROUGE;
                    noeud = noeud.parent;
                } else {
                    if (frere.filsDroit.couleur == Couleur.NOIR) {
                        frere.filsGauche.couleur = Couleur.NOIR;
                        frere.couleur = Couleur.ROUGE;
                        rotationDroite(frere);
                        frere = noeud.parent.filsDroit;
                    }
                    
                    frere.couleur = noeud.parent.couleur;
                    noeud.parent.couleur = Couleur.NOIR;
                    frere.filsDroit.couleur = Couleur.NOIR;
                    rotationGauche(noeud.parent);
                    noeud = racine;
                }
            } else {
                NoeudRN frere = noeud.parent.filsGauche;
                
                if (frere.couleur == Couleur.ROUGE) {
                    frere.couleur = Couleur.NOIR;
                    noeud.parent.couleur = Couleur.ROUGE;
                    rotationDroite(noeud.parent);
                    frere = noeud.parent.filsGauche;
                }
                
                if (frere.filsGauche.couleur == Couleur.NOIR && 
                    frere.filsDroit.couleur == Couleur.NOIR) {
                    frere.couleur = Couleur.ROUGE;
                    noeud = noeud.parent;
                } else {
                    if (frere.filsGauche.couleur == Couleur.NOIR) {
                        frere.filsDroit.couleur = Couleur.NOIR;
                        frere.couleur = Couleur.ROUGE;
                        rotationGauche(frere);
                        frere = noeud.parent.filsGauche;
                    }
                    
                    frere.couleur = noeud.parent.couleur;
                    noeud.parent.couleur = Couleur.NOIR;
                    frere.filsGauche.couleur = Couleur.NOIR;
                    rotationDroite(noeud.parent);
                    noeud = racine;
                }
            }
        }
        
        noeud.couleur = Couleur.NOIR;
    }

    /**
     * Itérateur pour parcourir l'arbre.
     */
    private class IterateurARN implements Iterator<T> {
        NoeudRN noeudActuel;
        NoeudRN noeudSuivant;

        IterateurARN() {
            noeudActuel = noeudSentinelle;
            noeudSuivant = racine.trouverMinimum();
        }

        @Override
        public boolean hasNext() {
            return noeudSuivant != noeudSentinelle;
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException("Plus d'éléments");
            }
            
            noeudActuel = noeudSuivant;
            noeudSuivant = noeudSuivant.trouverSuccesseur();
            
            return noeudActuel.valeur;
        }

        @Override
        public void remove() {
            if (noeudActuel == noeudSentinelle) {
                throw new IllegalStateException("Aucun élément à supprimer");
            }
            
            supprimerNoeud(noeudActuel);
            noeudActuel = noeudSentinelle;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        afficherArbre(racine, sb, "", calculerLongueurMax(racine));
        return sb.toString();
    }

    private void afficherArbre(NoeudRN noeud, StringBuilder sb, String chemin, int longueur) {
        if (noeud == noeudSentinelle) {
            return;
        }
        
        afficherArbre(noeud.filsDroit, sb, chemin + "D", longueur);
        
        for (int i = 0; i < chemin.length(); i++) {
            for (int j = 0; j < longueur + 6; j++) {
                sb.append(' ');
            }
            char c = (i == chemin.length() - 1) ? '+' : 
                    (i < chemin.length() - 1 && chemin.charAt(i) != chemin.charAt(i + 1)) ? '|' : ' ';
            sb.append(c);
        }
        
        if (noeud.couleur == Couleur.ROUGE) {
            sb.append("-- ").append("\u001B[31m").append(noeud.valeur).append("\u001B[0m");
        } else {
            sb.append("-- ").append(noeud.valeur);
        }
        
        if (noeud.filsGauche != noeudSentinelle || noeud.filsDroit != noeudSentinelle) {
            sb.append(" --");
            for (int j = noeud.valeur.toString().length(); j < longueur; j++) {
                sb.append('-');
            }
            sb.append('|');
        }
        
        sb.append("\n");
        afficherArbre(noeud.filsGauche, sb, chemin + "G", longueur);
    }

    private int calculerLongueurMax(NoeudRN noeud) {
        if (noeud == noeudSentinelle) {
            return 0;
        }
        return Math.max(
            noeud.valeur.toString().length(),
            Math.max(calculerLongueurMax(noeud.filsGauche), 
                    calculerLongueurMax(noeud.filsDroit))
        );
    }
}