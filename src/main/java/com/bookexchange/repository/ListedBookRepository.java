package com.bookexchange.repository;

import com.bookexchange.entity.ListedBook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ListedBookRepository extends JpaRepository<ListedBook, Long> {
    List<ListedBook> findTop4ByStatusOrderByCreatedAtDesc(Integer status);
}