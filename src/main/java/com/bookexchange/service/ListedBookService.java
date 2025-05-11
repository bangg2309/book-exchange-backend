package com.bookexchange.service;

import com.bookexchange.dto.request.ListedBookCreationRequest;
import com.bookexchange.dto.response.BookDetailResponse;
import com.bookexchange.dto.response.ListedBooksResponse;
import com.bookexchange.entity.*;
import com.bookexchange.exception.AppException;
import com.bookexchange.exception.ErrorCode;
import com.bookexchange.mapper.ListedBookMapper;
import com.bookexchange.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ListedBookService {

    ListedBookRepository listedBookRepository;
    AuthorRepository authorRepository;
    CategoryRepository categoryRepository;
    UserRepository userRepository;
    SchoolRepository schoolRepository;
    ListedBookMapper listedBookMapper;

    public void createListedBook(ListedBookCreationRequest request) {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        log.info("Request to create listed book: {}", request);

        User seller = userRepository.findByUsername(name).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // Xử lý authors và categories như code hiện tại của bạn
        Set<Author> authors = request.getAuthors().stream()
                .map(authorName ->
                        authorRepository.findByName(authorName).orElseGet(() -> {
                            Author newAuthor = new Author();
                            newAuthor.setName(authorName);
                            return authorRepository.save(newAuthor);
                        })
                )
                .collect(Collectors.toSet());

        Set<Category> categories = request.getCategoriesId().stream()
                .map(categoryId ->
                        categoryRepository.findById(categoryId)
                                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND))
                )
                .collect(Collectors.toSet());

        // Tạo ListedBook nhưng chưa thêm images
        ListedBook listedBook = new ListedBook();
        listedBook.setSeller(seller);
        listedBook.setIsbn(request.getIsbn());
        listedBook.setPublisher(request.getPublisher());
        listedBook.setPublishYear(request.getPublishYear());
        listedBook.setTitle(request.getTitle());
        listedBook.setAuthors(authors);
        listedBook.setCategories(categories);
        listedBook.setDescription(request.getDescription());
        listedBook.setPriceNew(request.getPriceNew());
        listedBook.setPrice(request.getPrice());
        listedBook.setConditionNumber(request.getConditionNumber());
        listedBook.setConditionDescription(request.getConditionDescription());
        listedBook.setAddress(request.getAddress());
        listedBook.setStatus(0);
        listedBook.setPageCount(request.getPageCount());
        listedBook.setLanguage(request.getLanguage());
        listedBook.setThumbnail(request.getThumbnail());
        listedBook.setSchool(schoolRepository.findById(
                request.getSchoolId()).orElseThrow(()
                -> new AppException(ErrorCode.SCHOOL_NOT_FOUND)));

        // Lưu ListedBook trước để có ID
        ListedBook savedBook = listedBookRepository.save(listedBook);
        log.info("Saved ListedBook with ID: {}", savedBook.getId());

        // Bây giờ tạo và liên kết images
        Set<Image> images = request.getImagesUrl().stream()
                .map(imageUrl -> {
                    Image image = new Image();
                    image.setImageUrl(imageUrl);
                    image.setListedBook(savedBook); // Liên kết với book đã có ID
                    return image;
                })
                .collect(Collectors.toSet());

        // Gán images cho book đã lưu
        savedBook.setImages(images);

        // Lưu lại book với images đã được liên kết
        listedBookRepository.save(savedBook);
        log.info("Updated ListedBook with images, total images: {}", images.size());
    }

    public List<ListedBooksResponse> getLatestListedBooks() {
        List<ListedBook> listedbooks = listedBookRepository.findTop4ByStatusOrderByCreatedAtDesc(1);

        return listedbooks.stream()
                .map(listedBookMapper::toListedBooksResponse)
                .collect(Collectors.toList());
    }

    public BookDetailResponse getBookDetail(Long id) {
        ListedBook book = listedBookRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_FOUND));
        return listedBookMapper.toBookDetailResponse(book);
    }
}
