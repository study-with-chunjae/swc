package net.fullstack7.swc.constant;

import java.util.List;

public class PostPageConstants {
    public static final List<String> ALLOWED_SEARCH_FIELDS = List.of("isCreated","title","content");
    public static final List<String> ALLOWED_SORT_FIELDS = List.of("isCreated","thumbUp");
    public static final List<String> ALLOWED_SORT_ORDER = List.of("desc","asc");
    public static final String DEFAULT_SEARCH_FIELD = "isCreated";
    public static final String DEFAULT_SORT_FIELD = "isCreated";
    public static final String DEFAULT_SORT_ORDER = "desc";
}
