package net.fullstack7.swc.repository.search;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import net.fullstack7.swc.domain.Post;
import net.fullstack7.swc.domain.QPost;
import net.fullstack7.swc.dto.PageDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public class PostSearchImpl extends QuerydslRepositorySupport implements PostSearch {
    public PostSearchImpl() {
        super(Post.class);
    }

    private final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public Page<Post> searchAndSort(Pageable pageable, String searchField, String searchValue, String sortField,
                                    String sortDirection, String searchDateBegin, String searchDateEnd, String memberId) {
        QPost qPost = QPost.post;
        JPQLQuery<Post> query = from(qPost);
        BooleanBuilder bb = new BooleanBuilder();
        //검색
        if(searchField!=null) {
            switch(searchField){
                case "title" : bb.and(qPost.title.like("%"+searchValue+"%"));
                break;
                case "content" : bb.and(qPost.content.like("%"+searchValue+"%"));
                break;
                default : break;
            }
        }
        bb.and(qPost.createdAt.between(
                LocalDate.parse(searchDateBegin, FORMATTER).atStartOfDay(),
                LocalDate.parse(searchDateEnd, FORMATTER).atStartOfDay().plusDays(1))
        );
        bb.and(qPost.member.memberId.eq(memberId));
        query.where(bb);
        //정렬
        if(sortField!=null) {
            switch(sortField){
                case "createdAt" :
                    query.orderBy(sortDirection.equals("desc")?qPost.createdAt.desc():qPost.createdAt.asc());
                    break;
                case "thumbUps" :
                    query.orderBy(sortDirection.equals("desc")?qPost.thumbUps.size().desc():qPost.thumbUps.size().asc());
                    break;
                default : break;
            }
        }
        //페이징
        Objects.requireNonNull(this.getQuerydsl()).applyPagination(pageable, query);
        List<Post> posts = query.fetch();
        int total = (int) query.fetchCount();
        return new PageImpl<>(posts, pageable, total);
    }
}