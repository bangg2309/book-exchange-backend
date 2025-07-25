package com.bookexchange.service;

import com.bookexchange.dto.request.ListedBookCreationRequest;
import com.bookexchange.dto.response.BookManagementResponse;
import com.bookexchange.dto.response.CategoryManagementResponse;
import com.bookexchange.dto.response.ListedBookDetailResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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

    public Page<BookManagementResponse> getAllBooks(Pageable pageable) {
        return listedBookRepository.findAll(pageable).map(listedBookMapper::toBookManagementResponse);
    }

    public void createListedBook(ListedBookCreationRequest request) {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        log.info("Request to create listed book: {}", request);

        User seller = userRepository.findByUsername(name).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // Xử lý authors và categories như code hiện tại của bạn
        Set<Author> authors = request.getAuthors().stream().map(authorName -> authorRepository.findByName(authorName).orElseGet(() -> {
            Author newAuthor = new Author();
            newAuthor.setName(authorName);
            return authorRepository.save(newAuthor);
        })).collect(Collectors.toSet());

        Set<Category> categories = request.getCategoriesId().stream().map(categoryId -> categoryRepository.findById(categoryId).orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND))).collect(Collectors.toSet());

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
        listedBook.setSchool(schoolRepository.findById(request.getSchoolId()).orElseThrow(() -> new AppException(ErrorCode.SCHOOL_NOT_FOUND)));

        // Lưu ListedBook trước để có ID
        ListedBook savedBook = listedBookRepository.save(listedBook);
        log.info("Saved ListedBook with ID: {}", savedBook.getId());

        // Bây giờ tạo và liên kết images
        Set<Image> images = request.getImagesUrl().stream().map(imageUrl -> {
            Image image = new Image();
            image.setImageUrl(imageUrl);
            image.setListedBook(savedBook); // Liên kết với book đã có ID
            return image;
        }).collect(Collectors.toSet());

        // Gán images cho book đã lưu
        savedBook.setImages(images);

        // Lưu lại book với images đã được liên kết
        listedBookRepository.save(savedBook);
        log.info("Updated ListedBook with images, total images: {}", images.size());
    }

    public List<ListedBooksResponse> getLatestListedBooks() {
        List<ListedBook> listedbooks = listedBookRepository.findTop4ByStatusOrderByCreatedAtDesc(1);

        return listedbooks.stream().map(listedBookMapper::toListedBooksResponse).collect(Collectors.toList());
    }

    public ListedBookDetailResponse getListedDetail(Long id) {
        ListedBook listedBook = listedBookRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.LISTED_BOOK_NOT_FOUND));

        return listedBookMapper.toListedBookDetailResponse(listedBook);
    }

    /**
     * Lấy danh sách sách liên quan theo category
     * @param bookId ID của sách hiện tại
     * @return Danh sách các sách liên quan
     */
    @Transactional(readOnly = true)
    public List<ListedBooksResponse> getRelatedBooks(Long bookId) {
        // Lấy thông tin sách hiện tại
        ListedBook currentBook = listedBookRepository.findById(bookId)
                .orElseThrow(() -> new AppException(ErrorCode.LISTED_BOOK_NOT_FOUND));
        
        // Lấy danh sách category ID của sách hiện tại
        List<Long> categoryIds = currentBook.getCategories().stream()
                .map(Category::getId)
                .collect(Collectors.toList());
        
        // Nếu không có category nào, trả về danh sách rỗng
        if (categoryIds.isEmpty()) {
            return List.of();
        }
        
        // Tìm sách liên quan theo category, giới hạn 4 kết quả
        PageRequest pageRequest = PageRequest.of(0, 4);
        List<ListedBook> relatedBooks = listedBookRepository.findRelatedBooksByCategories(bookId, categoryIds, pageRequest);
        
        // Map kết quả sang ListedBooksResponse bằng mapper
        return relatedBooks.stream()
                .map(listedBookMapper::toListedBooksResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<ListedBooksResponse> getBooks(int page, int size, String sortBy, Sort.Direction direction, String title, String author, Long categoryId, Double minPrice, Double maxPrice, Integer condition, Long schoolId) {

        log.info("Getting books with filters: page={}, size={}, sortBy={}, direction={}", page, size, sortBy, direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        try {
            // Sử dụng truy vấn projection trực tiếp để tối ưu hiệu suất
            // Kết quả trả về đã là ListedBooksResponse, không cần chuyển đổi
            return listedBookRepository.findBooksWithFiltersProjection(title, author, categoryId, minPrice, maxPrice, condition, schoolId, pageable);
        } catch (Exception e) {
            log.error("Error using projection query, falling back to standard query", e);

            // Fallback: sử dụng truy vấn entity và chuyển đổi
            Page<ListedBook> books = listedBookRepository.findBooksWithFilters(title, author, categoryId, minPrice, maxPrice, condition, schoolId, pageable);

            return books.map(listedBookMapper::toListedBooksResponse);
        }
    }

    public List<ListedBooksResponse> getBooksBySellerId(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        List<ListedBook> listedBooks = listedBookRepository.findBySeller(user);

        return listedBooks.stream().map(listedBookMapper::toListedBooksResponse).collect(Collectors.toList());
    }

    public List<ListedBooksResponse> getCurrentUserBooks() {
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();

        User user = userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        List<ListedBook> listedBooks = listedBookRepository.findBySeller(user);

        return listedBooks.stream().map(listedBookMapper::toListedBooksResponse).collect(Collectors.toList());
    }

    public List<ListedBooksResponse> searchBook(String query) {
        log.info("Searching books with query: {}", query);
        List<ListedBooksResponse> listedBooks = listedBookRepository.searchBook(query);
        if (listedBooks.isEmpty()) {
            throw new AppException(ErrorCode.LISTED_BOOK_NOT_FOUND);
        }
        return listedBooks;
    }


    public long countTotalBooks() {
        return listedBookRepository.count();
    }
    

    public long countTotalCategories() {
        return categoryRepository.count();
    }

    /**
     * Phê duyệt sách với status = 1 (đã được phê duyệt)
     * 
     * @param id ID của sách cần phê duyệt
     * @return true nếu phê duyệt thành công, false nếu không
     */
    @Transactional
    public boolean approveBook(Long id) {
        ListedBook listedBook = listedBookRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.LISTED_BOOK_NOT_FOUND));
        
        // Chỉ phê duyệt sách có status = 0 (chưa phê duyệt)
        if (listedBook.getStatus() == 0) {
            listedBook.setStatus(1);
            listedBookRepository.save(listedBook);
            log.info("Book with ID {} has been approved", id);
            return true;
        } else {
            log.warn("Cannot approve book with ID {} because its status is not 0", id);
            return false;
        }
    }

    /**
     * Từ chối sách với status = 2 (đã bị từ chối)
     * 
     * @param id ID của sách cần từ chối
     * @param reason Lý do từ chối (tùy chọn)
     * @return true nếu từ chối thành công, false nếu không
     */
    @Transactional
    public boolean rejectBook(Long id, String reason) {
        ListedBook listedBook = listedBookRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.LISTED_BOOK_NOT_FOUND));
        
        // Chỉ từ chối sách có status = 0 (chưa phê duyệt)
        if (listedBook.getStatus() == 0) {
            listedBook.setStatus(2); // Status 2 cho sách bị từ chối
            log.info("Book with ID {} has been rejected. Reason: {}", id, reason);
            listedBookRepository.save(listedBook);
            return true;
        } else {
            log.warn("Cannot reject book with ID {} because its status is not 0", id);
            return false;
        }
    }

    /**
     * Lấy danh sách sách mới nhất đã được phê duyệt với số lượng cụ thể
     * 
     * @param limit Số lượng sách cần lấy
     * @return Danh sách đối tượng ListedBooksResponse
     */
    public List<ListedBooksResponse> getRecentApprovedBooks(int limit) {
        // Lấy sách có status = 1 (đã được phê duyệt), sắp xếp theo thời gian tạo giảm dần
        PageRequest pageRequest = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ListedBook> books = listedBookRepository.findByStatus(1, pageRequest);
        
        return books.getContent().stream()
                .map(listedBookMapper::toListedBooksResponse)
                .collect(Collectors.toList());
    }

    /**
     * Lấy danh sách sách theo trạng thái (chưa duyệt/đã duyệt/từ chối) với phân trang
     * 
     * @param status Trạng thái sách (0: chưa duyệt, 1: đã duyệt, 2: từ chối)
     * @param pageable Thông tin phân trang
     * @return Page<BookManagementResponse> Danh sách sách với phân trang
     */
    public Page<BookManagementResponse> getBooksByStatus(Integer status, Pageable pageable) {
        Page<ListedBook> books = listedBookRepository.findByStatus(status, pageable);
        return books.map(listedBookMapper::toBookManagementResponse);
    }
}
