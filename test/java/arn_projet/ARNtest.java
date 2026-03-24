package arn_projet;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.*;

/**
 * Tests unitaires pour ARN.
 */
class ARNtest {

    @Test
    void testInitialisation() {
        ARN<Integer> arbre = new ARN<>();
        assertEquals(0, arbre.size(), "Un nouvel arbre doit être vide");
        assertEquals(0, arbre.obtenirHauteur(), "La hauteur d'un arbre vide est 0");
    }

    @Test
    void testAjoutUnique() {
        ARN<Integer> arbre = new ARN<>();
        
        assertTrue(arbre.add(42));
        assertEquals(1, arbre.size());
        assertTrue(arbre.contains(42));
    }

    @Test
    void testAjoutMultiple() {
        ARN<Integer> arbre = new ARN<>();
        
        assertTrue(arbre.add(30));
        assertTrue(arbre.add(15));
        assertTrue(arbre.add(45));
        assertTrue(arbre.add(10));
        assertTrue(arbre.add(20));
        
        assertEquals(5, arbre.size());
    }

    @Test
    void testAjoutDoublon() {
        ARN<Integer> arbre = new ARN<>();
        
        assertTrue(arbre.add(100));
        assertFalse(arbre.add(100), "L'ajout d'un doublon doit échouer");
        assertEquals(1, arbre.size());
    }

    @Test
    void testAjoutNull() {
        ARN<Integer> arbre = new ARN<>();
        assertFalse(arbre.add(null), "L'ajout de null doit échouer");
    }

    @Test
    void testConstructeurComparateur() {
        Comparator<Integer> compInverse = Comparator.reverseOrder();
        ARN<Integer> arbre = new ARN<>(compInverse);
        
        arbre.add(10);
        arbre.add(20);
        arbre.add(5);
        
        Iterator<Integer> iter = arbre.iterator();
        assertEquals(20, iter.next(), "Ordre décroissant attendu");
        assertEquals(10, iter.next());
        assertEquals(5, iter.next());
    }

    @Test
    void testConstructeurCollection() {
        List<Integer> liste = Arrays.asList(15, 8, 22, 4, 12);
        ARN<Integer> arbre = new ARN<>(liste);
        
        assertEquals(5, arbre.size());
        
        for (Integer val : liste) {
            assertTrue(arbre.contains(val));
        }
    }

    @Test
    void testHauteurApresPlusieursInsertions() {
        ARN<Integer> arbre = new ARN<>();
        
        for (int i = 1; i <= 7; i++) {
            arbre.add(i);
        }
        
        assertTrue(arbre.obtenirHauteur() <= 4, 
                  "La hauteur doit respecter les propriétés des arbres rouge-noir");
    }

    @Test
    void testContains() {
        ARN<Integer> arbre = new ARN<>();
        arbre.add(50);
        arbre.add(25);
        arbre.add(75);
        
        assertTrue(arbre.contains(50));
        assertTrue(arbre.contains(25));
        assertTrue(arbre.contains(75));
        assertFalse(arbre.contains(100));
    }

    @Test
    void testIterateurOrdre() {
        ARN<Integer> arbre = new ARN<>();
        List<Integer> valeurs = Arrays.asList(50, 25, 75, 10, 30, 60, 90);
        
        valeurs.forEach(arbre::add);
        
        List<Integer> resultatParcouru = new ArrayList<>();
        arbre.forEach(resultatParcouru::add);
        
        List<Integer> valeursTries = new ArrayList<>(valeurs);
        Collections.sort(valeursTries);
        
        assertEquals(valeursTries, resultatParcouru, 
                    "L'itérateur doit parcourir en ordre croissant");
    }

    @Test
    void testSuppressionSimple() {
        ARN<Integer> arbre = new ARN<>();
        arbre.add(50);
        arbre.add(25);
        arbre.add(75);
        
        assertTrue(arbre.remove(25));
        assertFalse(arbre.contains(25));
        assertEquals(2, arbre.size());
    }

    @Test
    void testSuppressionElementAbsent() {
        ARN<Integer> arbre = new ARN<>();
        arbre.add(50);
        
        assertFalse(arbre.remove(999));
        assertEquals(1, arbre.size());
    }

    @Test
    void testSuppressionPlusieursElements() {
        ARN<Integer> arbre = new ARN<>();
        
        for (int i = 1; i <= 10; i++) {
            arbre.add(i);
        }
        
        assertTrue(arbre.remove(5));
        assertTrue(arbre.remove(1));
        assertTrue(arbre.remove(10));
        
        assertEquals(7, arbre.size());
        assertFalse(arbre.contains(5));
        assertFalse(arbre.contains(1));
        assertFalse(arbre.contains(10));
    }

    @Test
    void testIterateurRemove() {
        ARN<Integer> arbre = new ARN<>();
        arbre.add(30);
        arbre.add(15);
        arbre.add(45);
        
        Iterator<Integer> iter = arbre.iterator();
        Integer premier = iter.next();
        iter.remove();
        
        assertFalse(arbre.contains(premier));
        assertEquals(2, arbre.size());
    }

    @Test
    void testIterateurExceptionNext() {
        ARN<Integer> arbre = new ARN<>();
        Iterator<Integer> iter = arbre.iterator();
        
        assertThrows(NoSuchElementException.class, iter::next);
    }

    @Test
    void testIterateurExceptionRemove() {
        ARN<Integer> arbre = new ARN<>();
        arbre.add(10);
        Iterator<Integer> iter = arbre.iterator();
        
        assertThrows(IllegalStateException.class, iter::remove, 
                    "Remove avant next doit lever une exception");
    }

    @Test
    void testInsertionOrdonnee() {
        ARN<Integer> arbre = new ARN<>();
        
        // Insertion dans l'ordre croissant
        for (int i = 1; i <= 100; i++) {
            arbre.add(i);
        }
        
        assertEquals(100, arbre.size());
        
        // Vérifier que la hauteur reste logarithmique
        int hauteur = arbre.obtenirHauteur();
        assertTrue(hauteur <= 2 * Math.log(101) / Math.log(2), 
                  "La hauteur doit être O(log n)");
    }

    @Test
    void testInsertionAleatoire() {
        ARN<Integer> arbre = new ARN<>();
        Random random = new Random(42); // Seed fixe pour reproductibilité
        Set<Integer> valeursAjoutees = new HashSet<>();
        
        for (int i = 0; i < 100; i++) {
            int val = random.nextInt(1000);
            if (arbre.add(val)) {
                valeursAjoutees.add(val);
            }
        }
        
        assertEquals(valeursAjoutees.size(), arbre.size());
        
        for (Integer val : valeursAjoutees) {
            assertTrue(arbre.contains(val));
        }
    }

    @Test
    void testSuppressionTousElements() {
        ARN<Integer> arbre = new ARN<>();
        List<Integer> valeurs = Arrays.asList(15, 8, 22, 4, 12, 19, 25);
        
        valeurs.forEach(arbre::add);
        
        for (Integer val : valeurs) {
            assertTrue(arbre.remove(val));
        }
        
        assertEquals(0, arbre.size());
    }

    @Test
    void testToStringNonVide() {
        ARN<Integer> arbre = new ARN<>();
        arbre.add(50);
        arbre.add(25);
        arbre.add(75);
        
        String representation = arbre.toString();
        
        assertNotNull(representation);
        assertTrue(representation.contains("50"));
        assertTrue(representation.contains("25"));
        assertTrue(representation.contains("75"));
    }

    @Test
    void testAvecChaines() {
        ARN<String> arbre = new ARN<>();
        
        arbre.add("delta");
        arbre.add("alpha");
        arbre.add("gamma");
        arbre.add("beta");
        
        assertEquals(4, arbre.size());
        
        List<String> resultat = new ArrayList<>();
        arbre.forEach(resultat::add);
        
        assertEquals(Arrays.asList("alpha", "beta", "delta", "gamma"), resultat);
    }

    @Test
    void testPerformanceEquilibrage() {
        ARN<Integer> arbre = new ARN<>();
        
        // Insertion dans le pire cas pour un ABR classique
        for (int i = 0; i < 1000; i++) {
            arbre.add(i);
        }
        
        int hauteur = arbre.obtenirHauteur();
        
        // La hauteur doit rester logarithmique
        assertTrue(hauteur < 20, "L'arbre doit rester équilibré même avec insertion séquentielle");
    }
}