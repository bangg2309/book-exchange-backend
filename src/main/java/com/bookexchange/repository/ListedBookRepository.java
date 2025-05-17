package com.bookexchange.repository;

import com.bookexchange.dto.response.ListedBooksResponse;
import com.bookexchange.entity.ListedBook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ListedBookRepository extends JpaRepository<ListedBook, Long> {
    List<ListedBook> findTop4ByStatusOrderByCreatedAtDesc(Integer status);


    @Query(value = "SELECT\n" +
                   "  lb.id,\n" +
                   "  lb.title,\n" +
                   "  lb.price_new,\n" +
                   "  lb.price,\n" +
                   "  lb.condition_number,\n" +
                   "  lb.description,\n" +
                   "  lb.thumbnail,\n" +
                   "  lb.publisher,\n" +
                   "  s.name AS schoolName,\n" +
                   "  u.full_name AS sellerName,\n" +
                   "  a.name AS author\n" +
                   "FROM\n" +
                   "  listed_books lb\n" +
                   "  JOIN book_authors ba ON lb.id = ba.book_id\n" +
                   "  JOIN authors a ON ba.author_id = a.id\n" +
                   "  JOIN users u ON u.id = lb.seller_id\n" +
                   "  JOIN schools s ON s.id = lb.school_id;\n",
            countQuery = "SELECT COUNT(*) " +
                         "FROM listed_books lb " +
                         "JOIN book_authors ba ON lb.id = ba.book_id " +
                         "JOIN authors a ON ba.author_id = a.id",
            nativeQuery = true)
    Page<ListedBooksResponse> getListBookWithPageable(Pageable pageable);
}