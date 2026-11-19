package com.schohub.model;

import java.util.List;
import java.util.Locale;

/**
 * The 105 official barangays of Batangas City (PSA PSGC 041005000), in PSGC order.
 *
 * <p>SchoHub is only for students who live in Batangas City, so this class is the single
 * source of truth for both halves of the address: the city itself ({@link #CITY}) and the
 * barangay list offered in the profile form and re-checked on the server.</p>
 */
public final class BatangasCityBarangays {

    public static final String CITY = "Batangas City";
    public static final String PROVINCE = "Batangas";

    private static final List<String> NAMES = List.of(
            // ------------------------------------------------------------------
            // PSGC 041005001 - 041005105
            // ------------------------------------------------------------------
            "Alangilan",
            "Balagtas",
            "Balete",
            "Banaba Center",
            "Banaba Ibaba",
            "Banaba Kanluran",
            "Banaba Silangan",
            "Barangay 1",
            "Barangay 2",
            "Barangay 3",
            "Barangay 4",
            "Barangay 5",
            "Barangay 6",
            "Barangay 7",
            "Barangay 8",
            "Barangay 9",
            "Barangay 10",
            "Barangay 11",
            "Barangay 12",
            "Barangay 13",
            "Barangay 14",
            "Barangay 15",
            "Barangay 16",
            "Barangay 17",
            "Barangay 18",
            "Barangay 19",
            "Barangay 20",
            "Barangay 21",
            "Barangay 22",
            "Barangay 23",
            "Barangay 24",
            "Bilogo",
            "Bolbok",
            "Bukal",
            "Calicanto",
            "Catandala",
            "Concepcion",
            "Conde Itaas",
            "Conde Labac",
            "Cuta",
            "Dalig",
            "Dela Paz",
            "Dela Paz Pulot Aplaya",
            "Dela Paz Pulot Itaas",
            "Domoclay",
            "Dumantay",
            "Gulod Itaas",
            "Gulod Labak",
            "Haligue Kanluran",
            "Haligue Silangan",
            "Ilihan",
            "Kumba",
            "Kumintang Ibaba",
            "Kumintang Ilaya",
            "Libjo",
            "Liponpon, Isla Verde",
            "Maapas",
            "Mabacong",
            "Mahabang Dahilig",
            "Mahabang Parang",
            "Mahacot Kanluran",
            "Mahacot Silangan",
            "Malalim",
            "Malibayo",
            "Malitam",
            "Maruclap",
            "Pagkilatan",
            "Paharang Kanluran",
            "Paharang Silangan",
            "Pallocan Kanluran",
            "Pallocan Silangan",
            "Pinamucan",
            "Pinamucan Ibaba",
            "Pinamucan Silangan",
            "Sampaga",
            "San Agapito, Isla Verde",
            "San Agustin Kanluran, Isla Verde",
            "San Agustin Silangan, Isla Verde",
            "San Andres, Isla Verde",
            "San Antonio, Isla Verde",
            "San Isidro",
            "San Jose Sico",
            "San Miguel",
            "San Pedro",
            "Santa Clara",
            "Santa Rita Aplaya",
            "Santa Rita Karsada",
            "Santo Domingo",
            "Santo Niño",
            "Simlong",
            "Sirang Lupa",
            "Sorosoro Ibaba",
            "Sorosoro Ilaya",
            "Sorosoro Karsada",
            "Tabangao Ambulong",
            "Tabangao Aplaya",
            "Tabangao Dao",
            "Talahib Pandayan",
            "Talahib Payapa",
            "Talumpok Kanluran",
            "Talumpok Silangan",
            "Tinga Itaas",
            "Tinga Labak",
            "Tulo",
            "Wawa");

    private BatangasCityBarangays() {
    }

    /** The official barangay names, ready to be rendered as a dropdown. */
    public static List<String> all() {
        return NAMES;
    }

    /** Official spelling of a barangay of Batangas City, or {@code null} when it is not one. */
    public static String canonical(String value) {
        String key = normalize(value);
        if (key.isEmpty()) {
            return null;
        }
        for (String name : NAMES) {
            if (normalize(name).equals(key)) {
                return name;
            }
        }
        return null;
    }

    public static boolean contains(String value) {
        return canonical(value) != null;
    }

    /** True when the string names Batangas City (accepts "City of Batangas" too). */
    public static boolean isCity(String value) {
        String key = normalize(value);
        return key.equals("batangas city") || key.equals("city of batangas");
    }

    /**
     * Lower-cases the text and drops punctuation, extra spaces and the "(Pob.)" suffix, so
     * "barangay 1 (pob.)" and "Barangay  1" resolve to the same barangay.
     */
    private static String normalize(String value) {
        if (value == null) {
            return "";
        }
        String cleaned = value.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9 ]", " ")
                .replaceAll("\\s+", " ")
                .trim();
        StringBuilder result = new StringBuilder();
        for (String token : cleaned.split(" ")) {
            if (token.equals("pob") || token.equals("poblacion")) {
                continue;
            }
            if (result.length() > 0) {
                result.append(' ');
            }
            result.append(token);
        }
        return result.toString();
    }
}
