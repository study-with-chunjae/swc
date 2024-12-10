package net.fullstack7.swc.constant;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class PostPageConstants {
    public static final List<String> ALLOWED_SEARCH_FIELDS = List.of("createdAt","title","content","topics","hashtag","");
    public static final List<String> ALLOWED_SORT_FIELDS = List.of("createdAt","thumbUps");
    public static final List<String> ALLOWED_SORT_ORDER = List.of("desc","asc");
    public static final String DEFAULT_SEARCH_FIELD = "";
    public static final String DEFAULT_SORT_FIELD = "createdAt";
    public static final String DEFAULT_SORT_ORDER = "desc";
    public static final LocalDateTime DEFAULT_SEARCH_DATE_END = LocalDate.now().atStartOfDay();
    public static final LocalDateTime DEFAULT_SEARCH_DATE_BEGIN = DEFAULT_SEARCH_DATE_END.plusDays(-7);
}
