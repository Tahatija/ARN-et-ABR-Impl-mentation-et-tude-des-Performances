package abr_projet;


import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Implémentation d'un Arbre Binaire de Recherche (ABR).
 * Cette structure permet de stocker des éléments triés de manière hiérarchique.
 * 
 * @param <T> le type des éléments stockés dans l'arbre
 */
public class ABR<T> extends AbstractCollection<T> {
    
    private NoeudArbre racine;
    private int nombreElements;
    private final Comparator<? super T> comparateurUtilise;

    /**
     * Classe interne représentant un noeud dans l'arbre binaire.
     */
    private class NoeudArbre {
        T valeur;
        NoeudArbre filsGauche;
        NoeudArbre filsDroit;
        NoeudArbre noeudParent;

        NoeudArbre(T val) {
            this.valeur = val;
            this.filsGauche = null;
            this.filsDroit = null;
            this.noeudParent = null;
        }

        /**
         * Trouve le noeud ayant la valeur minimale dans le sous-arbre.
         * @return le noeud avec la plus petite valeur
         */
        NoeudArbre obtenirMinimum() {
            NoeudArbre noeudCourant = this;
            while (noeudCourant.filsGauche != null) {
                noeudCourant = noeudCourant.filsGauche;
            }
            return noeudCourant;
        }

        /**
         * Trouve le successeur en ordre infixe du noeud actuel.
         * @return le successeur ou null si inexistant
         */
        NoeudArbre obtenirSuccesseur() {
            if (this.filsDroit != null) {
                return this.filsDroit.obtenirMinimum();
            }
            
            NoeudArbre noeudActuel = this;
            NoeudArbre parent = this.noeudParent;
            
            while (parent != null && noeudActuel == parent.filsDroit) {
                noeudActuel = parent;
                parent = parent.noeudParent;
            }
            
            return parent;
        }
    }

    /**
     * Constructeur par défaut utilisant l'ordre naturel des éléments.
     */
    public ABR() {
        this.racine = null;
        this.nombreElements = 0;
        this.comparateurUtilise = (a, b) -> ((Comparable<T>) a).compareTo(b);
    }

    /**
     * Constructeur avec un comparateur personnalisé.
     * @param comp le comparateur à utiliser pour ordonner les éléments
     */
    public ABR(Comparator<? super T> comp) {
        this.racine = null;
        this.nombreElements = 0;
        this.comparateurUtilise = comp;
    }

    /**
     * Constructeur à partir d'une collection existante.
     * @param collection la collection d'éléments à insérer
     */
    public ABR(Collection<? extends T> collection) {
        this();
        insererTous(collection);
    }

    @Override
    public int size() {
        return nombreElements;
    }

    @Override
    public Iterator<T> iterator() {
        return new IterateurArbre();
    }

    /**
     * Insère tous les éléments d'une collection dans l'arbre.
     * @param collection la collection à insérer
     * @return true si l'opération réussit
     */
    public boolean insererTous(Collection<? extends T> collection) {
        boolean modifie = false;
        for (T element : collection) {
            if (add(element)) {
                modifie = true;
            }
        }
        return modifie;
    }

    @Override
    public boolean add(T element) {
        NoeudArbre nouveauNoeud = new NoeudArbre(element);
        
        if (racine == null) {
            racine = nouveauNoeud;
            nombreElements++;
            return true;
        }

        NoeudArbre noeudActuel = racine;
        NoeudArbre noeudParent = null;

        while (noeudActuel != null) {
            noeudParent = noeudActuel;
            int comparaison = comparateurUtilise.compare(element, noeudActuel.valeur);
            
            if (comparaison < 0) {
                noeudActuel = noeudActuel.filsGauche;
            } else if (comparaison > 0) {
                noeudActuel = noeudActuel.filsDroit;
            } else {
                return false; // Élément déjà présent
            }
        }

        nouveauNoeud.noeudParent = noeudParent;
        
        if (comparateurUtilise.compare(element, noeudParent.valeur) < 0) {
            noeudParent.filsGauche = nouveauNoeud;
        } else {
            noeudParent.filsDroit = nouveauNoeud;
        }

        nombreElements++;
        return true;
    }

    /**
     * Recherche un élément dans l'arbre.
     * @param obj l'élément à rechercher
     * @return le noeud contenant l'élément ou null
     */
    private NoeudArbre trouverNoeud(Object obj) {
        NoeudArbre noeudActuel = racine;
        
        while (noeudActuel != null) {
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
        
        return null;
    }

    @Override
    public boolean contains(Object obj) {
        return trouverNoeud(obj) != null;
    }

    @Override
    public boolean remove(Object obj) {
        NoeudArbre noeudASupprimer = trouverNoeud(obj);
        
        if (noeudASupprimer == null) {
            return false;
        }
        
        supprimerNoeud(noeudASupprimer);
        nombreElements--;
        return true;
    }

    /**
     * Supprime un noeud de l'arbre.
     * @param noeud le noeud à supprimer
     * @return le successeur du noeud supprimé
     */
    private NoeudArbre supprimerNoeud(NoeudArbre noeud) {
        NoeudArbre noeudARemplacer;
        NoeudArbre enfantUnique;

        // Déterminer quel noeud sera physiquement supprimé
        if (noeud.filsGauche == null || noeud.filsDroit == null) {
            noeudARemplacer = noeud;
        } else {
            noeudARemplacer = noeud.obtenirSuccesseur();
        }

        // Identifier l'enfant du noeud à remplacer
        if (noeudARemplacer.filsGauche != null) {
            enfantUnique = noeudARemplacer.filsGauche;
        } else {
            enfantUnique = noeudARemplacer.filsDroit;
        }

        // Mettre à jour les liens parent-enfant
        if (enfantUnique != null) {
            enfantUnique.noeudParent = noeudARemplacer.noeudParent;
        }

        if (noeudARemplacer.noeudParent == null) {
            racine = enfantUnique;
        } else {
            if (noeudARemplacer == noeudARemplacer.noeudParent.filsGauche) {
                noeudARemplacer.noeudParent.filsGauche = enfantUnique;
            } else {
                noeudARemplacer.noeudParent.filsDroit = enfantUnique;
            }
        }

        // Copier la valeur si nécessaire
        if (noeudARemplacer != noeud) {
            noeud.valeur = noeudARemplacer.valeur;
        }

        return noeudARemplacer.obtenirSuccesseur();
    }

    /**
     * Itérateur pour parcourir l'arbre en ordre infixe.
     */
    private class IterateurArbre implements Iterator<T> {
        private NoeudArbre noeudPrecedent;
        private NoeudArbre noeudSuivant;

        IterateurArbre() {
            noeudPrecedent = null;
            noeudSuivant = (racine == null) ? null : racine.obtenirMinimum();
        }

        @Override
        public boolean hasNext() {
            return noeudSuivant != null;
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException("Plus d'éléments à parcourir");
            }
            
            noeudPrecedent = noeudSuivant;
            noeudSuivant = noeudSuivant.obtenirSuccesseur();
            
            return noeudPrecedent.valeur;
        }

        @Override
        public void remove() {
            if (noeudPrecedent == null) {
                throw new IllegalStateException("Aucun élément à supprimer");
            }
            
            noeudSuivant = supprimerNoeud(noeudPrecedent);
            noeudPrecedent = null;
            nombreElements--;
        }
    }

    @Override
    public String toString() {
        StringBuilder constructeur = new StringBuilder();
        afficherArbre(racine, constructeur, "", calculerLongueurMax(racine));
        return constructeur.toString();
    }

    private void afficherArbre(NoeudArbre noeud, StringBuilder sb, String chemin, int longueurMax) {
        if (noeud == null) {
            return;
        }
        
        afficherArbre(noeud.filsDroit, sb, chemin + "D", longueurMax);
        
        for (int i = 0; i < chemin.length(); i++) {
            for (int j = 0; j < longueurMax + 6; j++) {
                sb.append(' ');
            }
            char symbole = (i == chemin.length() - 1) ? '+' : '|';
            sb.append(symbole);
        }
        
        sb.append("-- ").append(noeud.valeur.toString());
        
        if (noeud.filsGauche != null || noeud.filsDroit != null) {
            sb.append(" --");
            for (int j = noeud.valeur.toString().length(); j < longueurMax; j++) {
                sb.append('-');
            }
            sb.append('|');
        }
        
        sb.append("\n");
        afficherArbre(noeud.filsGauche, sb, chemin + "G", longueurMax);
    }

    private int calculerLongueurMax(NoeudArbre noeud) {
        if (noeud == null) {
            return 0;
        }
        return Math.max(
            noeud.valeur.toString().length(),
            Math.max(calculerLongueurMax(noeud.filsGauche), 
                    calculerLongueurMax(noeud.filsDroit))
        );
    }
}
