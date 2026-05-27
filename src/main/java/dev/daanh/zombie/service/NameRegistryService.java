package dev.daanh.zombie.service;

import net.datafaker.Faker;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Random;

@Service
public class NameRegistryService {

    public String getRandomFirstName(boolean isMale, String countryCode, Random random) {
        Faker faker = new Faker(getLocaleForCountry(countryCode), random);
        return isMale ? faker.name().firstName() : faker.name().firstName(); // datafaker mostly mixes them or we can just use firstName()
    }

    public String getRandomLastName(String countryCode, Random random) {
        Faker faker = new Faker(getLocaleForCountry(countryCode), random);
        return faker.name().lastName();
    }

    private Locale getLocaleForCountry(String countryCode) {
        if (countryCode == null) return Locale.US;
        
        return switch (countryCode.toUpperCase()) {
            case "NL" -> new Locale("nl", "NL");
            case "US" -> Locale.US;
            case "GB", "UK" -> Locale.UK;
            case "FR" -> Locale.FRANCE;
            case "DE" -> Locale.GERMANY;
            case "IT" -> Locale.ITALY;
            case "ES" -> new Locale("es", "ES");
            case "RU" -> new Locale("ru", "RU");
            case "CN" -> Locale.CHINA;
            case "JP" -> Locale.JAPAN;
            case "KR" -> Locale.KOREA;
            case "SA", "EG", "AE" -> new Locale("ar", "SA");
            case "BR" -> new Locale("pt", "BR");
            case "PT" -> new Locale("pt", "PT");
            case "MX", "AR", "CO" -> new Locale("es", "MX");
            case "IN" -> new Locale("en", "IN");
            case "ZA" -> new Locale("en", "ZA");
            case "SE" -> new Locale("sv", "SE");
            case "NO" -> new Locale("no", "NO");
            case "FI" -> new Locale("fi", "FI");
            case "DK" -> new Locale("da", "DK");
            case "GR" -> new Locale("el", "GR");
            case "PL" -> new Locale("pl", "PL");
            case "UA" -> new Locale("uk", "UA");
            case "TR" -> new Locale("tr", "TR");
            case "IR" -> new Locale("fa", "IR");
            case "IL" -> new Locale("he", "IL");
            case "TH" -> new Locale("th", "TH");
            case "VN" -> new Locale("vi", "VN");
            case "ID" -> new Locale("id", "ID");
            default -> new Locale("en", countryCode.toUpperCase()); // Fallback
        };
    }
}
