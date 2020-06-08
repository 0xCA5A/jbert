package ch.jbert.utils;

import org.jaudiotagger.tag.TagField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TagFields {

    private static final Logger LOG = LoggerFactory.getLogger(TagFields.class);

    public static List<String> toString(List<TagField> tagFields) {
        try {
            return tagFields.stream()
                    .map(ThrowingFunction.of(TagField::getRawContent))
                    .map(String::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            LOG.info("Could not read tag field {}", e.getMessage());
            return Collections.emptyList();
        }
    }

}
