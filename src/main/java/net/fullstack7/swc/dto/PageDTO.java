package net.fullstack7.swc.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * <p>페이징 할 때 사용할 DTO 클래스</p>
 * <ul>
 *  <li>사용법
 *      <ol>
 *          <li>pageNo, pageSize, searchField, searchValue, sortField, sortDirection 을 해당 페이지에서 request parameter 로 받아옴</li>
 *          <li>initialize() 메서드를 실행해서 초기값을 입력함</li>
 *          <li>setTotalCount(int totalCount) 메서드로 Service 에서 받은 totalCount 를 입력함</li>
 *          <li>Service 객체의 List 반환하는 메서드의 해당 객체를 파라미터로 해서 받은 List 를 SetDtoList() 메서드로 입력함</li>
 *          <li>model 에 해당 객체를 담아서 전송함</li>
 *      </ol>
 *  </li>
 *  <li>예시)
 *      <pre>
 *      {@code
 *          public String list(@Valid PageDTO<BbsDTO> pageDTO
 *              , BindingResult bindingResult
 *              , RedirectAttributes redirectAttributes
 *              , Model model) {
 *          if(bindingResult.hasErrors()) {
 *              //에러 발생시 검색, 정렬 없이 1페이지로 초기화
 *              pageDTO = PageDTO.<BbsDTO>builder().build();
 *          }
 *              pageDTO.initialize();
 *              pageDTO.setTotalCount(bbsService.totalCount(pageDTO));
 *              pageDTO.setDtoList(bbsService.list(pageDTO));
 *              model.addAttribute("pageDTO", pageDTO);
 *              return "bbs/list";
 *          }
 *      }
 *      </pre>
 *  </li>
 *</ul>
 * @param <E> dtoList 에 들어갈 객체
 * @author 강감찬
 * @version 1.1
 * @since 2024-12-05
 */
@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PageDTO<E> {
    private static final String EMPTY = "";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final LocalDateTime NOW = LocalDateTime.now();
    @Builder.Default
    @Min(value = 1)
    @Max(value = 100000)
    private int pageNo = 1;
    @Builder.Default
    @Min(value = 1)
    @Max(value = 100)
    private int pageSize = 10;
    private int offset;
    private int totalCount;
    @Builder.Default
    @Min(value = 1)
    @Max(value = 30)
    private int blockSize = 10;
    private int blockStart;
    private int blockEnd;
    private int totalPage;
    private boolean prev;
    private boolean next;
    //@Pattern(regexp = "^(title|content|memberId)$", message = "싫은데요")
    @Size(max = 100)
    private String searchField;
    @Size(max = 100)
    private String searchValue;
    private String searchDateBegin;
    private String searchDateEnd;
    //@Pattern(regexp = "^(idx|title|regDate)$", message = "싫은데요")
    private String sortField;
    //@Pattern(regexp = "^(asc|desc)$", message = "싫은데요")
    private String sortDirection;
    private String queryString;
    private List<E> dtoList;

    /**
     * 필수 Field 값들을 활용해 PageDTO 의 다른 Field 값들을 초기화함
     * 필수 값 : pageNo, pageSize
     */
    public void initialize(String DEFAULT_SORT_FIELD, String DEFAULT_SORT_ORDER) {
        this.offset = (pageNo - 1) * pageSize;
        this.blockStart = ((pageNo - 1) / blockSize) * blockSize + 1;
        this.blockEnd = blockStart + blockSize - 1;
        this.prev = this.blockStart > 1;
        this.next = this.blockEnd < totalPage;
        if (this.sortField == null || this.sortField.isEmpty()) this.sortField = DEFAULT_SORT_FIELD;
        if (this.sortDirection == null || this.sortDirection.isEmpty()) this.sortDirection = DEFAULT_SORT_ORDER;
        if (this.searchField == null || this.searchField.isEmpty()) this.searchField = EMPTY;
        if (this.searchValue == null || this.searchValue.isEmpty()) this.searchValue = EMPTY;
        if (this.searchDateBegin == null || this.searchDateBegin.isEmpty()) this.searchDateBegin = FORMATTER.format(NOW.minusDays(7));
        if (this.searchDateEnd == null || this.searchDateEnd.isEmpty()) this.searchDateEnd = FORMATTER.format(NOW);
        this.queryString = URLEncoder.encode(
                String.format("searchField=%s&searchValue=%s&sortField=%s&sortDirection=%s",
                        this.searchField,
                        this.searchValue,
                        this.sortField,
                        this.sortDirection
                ),
                StandardCharsets.UTF_8
        );
    }

    public void setSearchField(String searchField) {
        if(searchField != null && searchField.length() > 100) {
            this.searchField = searchField.substring(0, 100);
        } else {
            this.searchField = searchField;
        }
    }
    public void setSearchValue(String searchValue) {
        if(searchValue != null && searchValue.length() > 100) {
            this.searchValue = searchValue.substring(0, 100);
        } else {
            this.searchValue = searchValue;
        }
    }

    /**
     * totalCount 를 입력받아 totalPage 를 설정함
     * totalPage 보다 큰 pageNo 값이 입력될 경우 처리함
     * totalPage 보다 큰 blockEnd 값이 생성된 경우 처리함
     * 처리한 blockEnd 값으로 next 값을 최신화함
     *
     * @param totalCount Service 객체에서 받환받은 List 의 총 객체 수
     */
    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
        this.totalPage = (totalCount - 1) / pageSize + 1;
        this.pageNo = pageNo > totalPage ? totalPage : pageNo;
        this.blockStart = ((pageNo - 1) / blockSize) * blockSize + 1;
        this.blockEnd = Math.min(blockEnd, this.totalPage);
        this.prev = this.blockStart > 1;
        this.next = this.blockEnd < totalPage;
    }

    /**
     * searchField, searchValue, sortField, sortDirection 으로 queryString 을 생성함
     *
     * @return 페이징 링크에 사용할 queryString 값
     */
    public String getQueryString() {
        return this.queryString != null ? URLDecoder.decode(this.queryString, StandardCharsets.UTF_8) : EMPTY;
    }

    /**
     * sortField 과 sortDirection 값에 따른 현재 페이지에 해당하는 Pageable 객체를 반환함
     *
     * @return Pageable 객체
     */
    public Pageable getSortPageable() {
        return PageRequest.of(this.pageNo - 1, this.pageSize, this.sortDirection.equals("desc") ? Sort.by(this.sortField).descending() : Sort.by(this.sortField).ascending());
    }

    /**
     * 현재 페이지에 해당하는 Pageable 객체를 반환함
     *
     * @return Pageable 객체
     */
    public Pageable getPageable() {
        return PageRequest.of(this.pageNo - 1, this.pageSize);
    }
}
