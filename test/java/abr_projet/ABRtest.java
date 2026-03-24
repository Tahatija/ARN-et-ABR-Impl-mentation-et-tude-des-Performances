package abr_projet;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.*;

/**
 * Tests unitaires pour ABR.
 */
class ABRtest {

    @Test
    void testArbreVideInitialement() {
        ABR<Integer> arbre = new ABR<>();
        assertEquals(0, arbre.size(), "L'arbre doit être vide à l'initialisation");
        assertFalse(arbre.contains(5), "Un arbre vide ne contient aucun élément");
    }

    @Test
    void testInsertionSimple() {
        ABR<Integer> arbre = new ABR<>();
        
        assertTrue(arbre.add(50));
        assertEquals(1, arbre.size());
        assertTrue(arbre.contains(50));
        
        assertTrue(arbre.add(30));
        assertTrue(arbre.add(70));
        assertEquals(3, arbre.size());
    }

    @Test
    void testInsertionDoublons() {
        ABR<Integer> arbre = new ABR<>();
        
        assertTrue(arbre.add(25));
        assertFalse(arbre.add(25), "L'insertion d'un doublon doit retourner false");
        assertEquals(1, arbre.size(), "La taille ne doit pas changer avec un doublon");
    }

    @Test
    void testComparateurPersonnalise() {
        Comparator<Integer> descendant = (a, b) -> b.compareTo(a);
        ABR<Integer> arbre = new ABR<>(descendant);
        
        arbre.add(15);
        arbre.add(5);
        arbre.add(25);
        
        Iterator<Integer> iter = arbre.iterator();
        assertEquals(25, iter.next(), "Premier élément doit être le plus grand");
        assertEquals(15, iter.next());
        assertEquals(5, iter.next(), "Dernier élément doit être le plus petit");
    }

    @Test
    void testConstructeurAvecCollection() {
        List<Integer> donnees = Arrays.asList(45, 12, 67, 23, 89);
        ABR<Integer> arbre = new ABR<>(donnees);
        
        assertEquals(5, arbre.size());
        
        for (Integer val : donnees) {
            assertTrue(arbre.contains(val));
        }
    }

    @Test
    void testIterateurOrdre() {
        ABR<Integer> arbre = new ABR<>();
        arbre.add(50);
        arbre.add(25);
        arbre.add(75);
        arbre.add(10);
        arbre.add(90);
        
        List<Integer> resultat = new ArrayList<>();
        arbre.forEach(resultat::add);
        
        assertEquals(Arrays.asList(10, 25, 50, 75, 90), resultat, 
                    "Les éléments doivent être en ordre croissant");
    }

    @Test
    void testSuppressionFeuille() {
        ABR<Integer> arbre = new ABR<>();
        arbre.add(50);
        arbre.add(25);
        arbre.add(75);
        
        assertTrue(arbre.remove(25));
        assertFalse(arbre.contains(25));
        assertEquals(2, arbre.size());
    }

    @Test
    void testSuppressionNoeudUnEnfant() {
        ABR<Integer> arbre = new ABR<>();
        arbre.add(50);
        arbre.add(25);
        arbre.add(75);
        arbre.add(60);
        
        assertTrue(arbre.remove(75));
        assertFalse(arbre.contains(75));
        assertTrue(arbre.contains(60), "L'enfant doit toujours être présent");
    }

    @Test
    void testSuppressionNoeudDeuxEnfants() {
        ABR<Integer> arbre = new ABR<>();
        arbre.add(50);
        arbre.add(25);
        arbre.add(75);
        arbre.add(10);
        arbre.add(30);
        
        assertTrue(arbre.remove(25));
        assertFalse(arbre.contains(25));
        assertTrue(arbre.contains(10));
        assertTrue(arbre.contains(30));
    }

    @Test
    void testSuppressionRacine() {
        ABR<Integer> arbre = new ABR<>();
        arbre.add(50);
        arbre.add(25);
        arbre.add(75);
        
        assertTrue(arbre.remove(50));
        assertFalse(arbre.contains(50));
        assertEquals(2, arbre.size());
    }

    @Test
    void testSuppressionElementInexistant() {
        ABR<Integer> arbre = new ABR<>();
        arbre.add(50);
        
        assertFalse(arbre.remove(100), "Supprimer un élément absent doit retourner false");
        assertEquals(1, arbre.size());
    }

    @Test
    void testIterateurRemove() {
        ABR<Integer> arbre = new ABR<>();
        arbre.add(50);
        arbre.add(25);
        arbre.add(75);
        
        Iterator<Integer> iter = arbre.iterator();
        assertEquals(25, iter.next());
        iter.remove();
        
        assertFalse(arbre.contains(25));
        assertEquals(2, arbre.size());
    }

    @Test
    void testIterateurException() {
        ABR<Integer> arbre = new ABR<>();
        Iterator<Integer> iter = arbre.iterator();
        
        assertThrows(NoSuchElementException.class, iter::next, 
                    "next() sur un arbre vide doit lever une exception");
    }

    @Test
    void testInsertionMassive() {
        ABR<Integer> arbre = new ABR<>();
        int n = 1000;
        
        for (int i = 0; i < n; i++) {
            arbre.add(i);
        }
        
        assertEquals(n, arbre.size());
        
        for (int i = 0; i < n; i++) {
            assertTrue(arbre.contains(i));
        }
    }

    @Test
    void testToStringNonVide() {
        ABR<Integer> arbre = new ABR<>();
        arbre.add(50);
        arbre.add(25);
        arbre.add(75);
        
        String representation = arbre.toString();
        
        assertTrue(representation.contains("50"));
        assertTrue(representation.contains("25"));
        assertTrue(representation.contains("75"));
        assertFalse(representation.isEmpty());
    }

    @Test
    void testAvecStrings() {
        ABR<String> arbre = new ABR<>();
        
        arbre.add("banane");
        arbre.add("pomme");
        arbre.add("cerise");
        
        assertTrue(arbre.contains("pomme"));
        assertTrue(arbre.contains("banane"));
        assertTrue(arbre.contains("cerise"));
        
        Iterator<String> iter = arbre.iterator();
        assertEquals("banane", iter.next());
        assertEquals("cerise", iter.next());
        assertEquals("pomme", iter.next());
    }
}