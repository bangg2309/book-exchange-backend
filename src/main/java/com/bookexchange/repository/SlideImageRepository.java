package com.bookexchange.repository;

import com.bookexchange.entity.Slide;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SlideImageRepository extends JpaRepository<Slide, String> {
}
