package net.fullstack7.swc.repository.search;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import lombok.extern.log4j.Log4j2;
import net.fullstack7.swc.domain.*;
import net.fullstack7.swc.util.LogUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Log4j2
public class PostSearchImpl extends QuerydslRepositorySupport implements PostSearch {
    public PostSearchImpl() {
        super(Post.class);
    }

    private final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public Page<Post> searchAndSort(Pageable pageable, String searchField, String searchValue, String sortField,
                                    String sortDirection, String searchDateBegin, String searchDateEnd, String memberId) {
        LogUtil.logLine("PostRepository -> searchAndSort");
        QPost qPost = QPost.post;
        QThumbUp qThumbUp = QThumbUp.thumbUp;
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
        log.info("searchField : {}" +
                        ", searchValue : {}" +
                        ", sortField : {}" +
                        ", sortDirection : {}" +
                        ", searchDateBegin : {}" +
                        ", searchDateEnd : {}"
        ,searchField, searchValue,sortField,sortDirection,searchDateBegin,searchDateEnd);
        query.where(bb);
        //정렬
        if(sortField!=null) {
            switch(sortField){
                case "createdAt" :
                    query.orderBy(sortDirection.equals("desc")?qPost.createdAt.desc():qPost.createdAt.asc());
                    break;
                case "thumbUps" :
                    query.leftJoin(qPost.thumbUps,qThumbUp);
                    query.groupBy(qPost.postId);
                    query.orderBy(sortDirection.equals("desc")?qThumbUp.count().desc():qThumbUp.count().asc());
                    break;
                default : break;
            }
        }
        //페이징
        log.info("fetchCount before pagination : {}",query.fetchCount());
        LogUtil.log("this.getQuerydsl()",Objects.requireNonNull(this.getQuerydsl()));
        Objects.requireNonNull(this.getQuerydsl()).applyPagination(pageable, query);
        List<Post> posts = query.fetch();
        LogUtil.log("pageSearchImpl : posts",posts);
        for(Post post : posts){
            LogUtil.log("thumbUp",post.getThumbUps());
        }
        int total = (int) query.fetchCount();
        log.info("fetchCount after pagination : {}",query.fetchCount());
        return new PageImpl<>(posts, pageable, total);
    }

    @Override
    public Page<Post> searchAndSortMyShare(Pageable pageable, String searchField, String searchValue, String sortField, String sortDirection, String searchDateBegin, String searchDateEnd, String memberId) {
        LogUtil.logLine("PostRepository -> searchAndSortMyShare");
        QPost qPost = QPost.post;
        QShare qShare = QShare.share;
        JPQLQuery<Post> query = from(qPost);
        JPQLQuery<Share> sQuery = from(qShare);
        query.innerJoin(qShare).on(qPost.postId.eq(qShare.post.postId));
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
        log.info("searchField : {}, searchValue : {}, sortField : {}, sortDirection : {}, searchDateBegin : {}, searchDateEnd : {}"
                ,searchField, searchValue,sortField,sortDirection,searchDateBegin,searchDateEnd);
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
        log.info("fetchCount before pagination : {}",query.fetchCount());
        Objects.requireNonNull(this.getQuerydsl()).applyPagination(pageable, query);
        List<Post> posts = query.fetch();
        int total = (int) query.fetchCount();
        log.info("fetchCount after pagination : {}",query.fetchCount());
        return new PageImpl<>(posts, pageable, total);
    }

    @Override
    public Page<Post> searchAndSortOthersShare(Pageable pageable, String searchField, String searchValue, String sortField, String sortDirection, String searchDateBegin, String searchDateEnd, String memberId) {
        LogUtil.logLine("PostRepository -> searchAndSortOthersShare");
        QPost qPost = QPost.post;
        QShare qShare = QShare.share;
        JPQLQuery<Post> query = from(qPost);
        JPQLQuery<Share> sQuery = from(qShare);
        query.innerJoin(qShare).on(qPost.postId.eq(qShare.post.postId));
        BooleanBuilder bb = new BooleanBuilder();
        //검색
        if(searchField!=null) {
            switch(searchField){
                case "title" : bb.and(qPost.title.like("%"+searchValue+"%"));
                    break;
                case "content" : bb.and(qPost.content.like("%"+searchValue+"%"));
                    break;
                case "hashtag" : bb.and(qPost.hashtag.like("%"+searchValue+"%"));
                default : break;
            }
        }
        bb.and(qPost.createdAt.between(
                LocalDate.parse(searchDateBegin, FORMATTER).atStartOfDay(),
                LocalDate.parse(searchDateEnd, FORMATTER).atStartOfDay().plusDays(1))
        );
        bb.and(qShare.member.memberId.eq(memberId));
        log.info("searchField : {}, searchValue : {}, sortField : {}, sortDirection : {}, searchDateBegin : {}, searchDateEnd : {}"
                ,searchField, searchValue,sortField,sortDirection,searchDateBegin,searchDateEnd);
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
        log.info("fetchCount before pagination : {}",query.fetchCount());
        Objects.requireNonNull(this.getQuerydsl()).applyPagination(pageable, query);
        List<Post> posts = query.fetch();
        int total = (int) query.fetchCount();
        log.info("fetchCount after pagination : {}",query.fetchCount());
        return new PageImpl<>(posts, pageable, total);
    }

    @Override
    public Page<Post> searchAndSortShare(Pageable pageable, String searchField, String searchValue, String sortField, String sortDirection, String searchDateBegin, String searchDateEnd, String memberId, String type) {
        return switch (type) {
            case "my" ->
                    searchAndSortMyShare(pageable, searchField, searchValue, sortField, sortDirection, searchDateBegin, searchDateEnd, memberId);
            case "others" ->
                    searchAndSortOthersShare(pageable, searchField, searchValue, sortField, sortDirection, searchDateBegin, searchDateEnd, memberId);
            default -> null;
        };
    }

}
