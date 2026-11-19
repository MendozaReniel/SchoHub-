package com.schohub.web;

import com.schohub.model.ApplicationPath;
import com.schohub.model.BatangasCityBarangays;
import com.schohub.model.ClassRank;
import com.schohub.model.EducationLevel;
import com.schohub.model.GwaScale;
import com.schohub.model.ProviderType;
import com.schohub.model.Sex;
import com.schohub.model.SpecialTrack;
import com.schohub.service.DocumentCatalog;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Everything the frontend needs to render the forms without hard-coding enum
 * values or the shared document checklists.
 */
@RestController
@RequestMapping("/api")
public class MetaController {

    public record Option(String value, String label) {
    }

    public record MetaResponse(
            List<Option> applicationPaths,
            List<Option> educationLevels,
            List<Option> gwaScales,
            List<Option> providerTypes,
            List<Option> specialTracks,
            List<Option> classRanks,
            List<Option> sexes,
            List<String> cities,
            List<String> barangays,
            List<String> coreDocuments,
            List<String> renewalDocuments) {
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "ok", "service", "schohub-backend");
    }

    @GetMapping("/meta")
    public MetaResponse meta() {
        return new MetaResponse(
                options(ApplicationPath.values(), ApplicationPath::getLabel),
                options(EducationLevel.values(), EducationLevel::getLabel),
                options(GwaScale.values(), GwaScale::getLabel),
                options(ProviderType.values(), ProviderType::getLabel),
                options(SpecialTrack.values(), SpecialTrack::getLabel),
                options(ClassRank.values(), ClassRank::getLabel),
                options(Sex.values(), Sex::name),
                List.of(BatangasCityBarangays.CITY),
                BatangasCityBarangays.all(),
                DocumentCatalog.CORE_DOCUMENTS,
                DocumentCatalog.RENEWAL_DOCUMENTS);
    }

    private static <E extends Enum<E>> List<Option> options(E[] values, java.util.function.Function<E, String> label) {
        return Arrays.stream(values)
                .map(value -> new Option(value.name(), label.apply(value)))
                .toList();
    }
}
