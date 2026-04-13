package com.shirin.jpaspecdynamicsearchapp.book.web.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shirin.jpaspecdynamicsearchapp.book.application.search.BookSearchCriteria;
import com.shirin.jpaspecdynamicsearchapp.book.application.search.BookSearchResult;
import com.shirin.jpaspecdynamicsearchapp.book.application.search.BookSearchUseCase;
import com.shirin.jpaspecdynamicsearchapp.book.application.search.PageResponse;
import com.shirin.jpaspecdynamicsearchapp.book.application.search.SearchMode;
import com.shirin.jpaspecdynamicsearchapp.book.web.api.error.FieldErrorDTO;
import com.shirin.jpaspecdynamicsearchapp.book.web.api.error.ValidationErrorMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import jakarta.validation.Validator;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(BookSearchRestController.class)
@AutoConfigureMockMvc(addFilters = false) // For not adding Spring Security filters
@Import(ApiExceptionHandler.class)
class BookSearchRestControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @MockitoBean private BookSearchUseCase bookSearchUseCase;
  @MockitoBean private Validator validator;
  @MockitoBean private MessageSource messageSource;
  @MockitoBean private ValidationErrorMapper validationErrorMapper;

  @BeforeEach
  void setUp() {
    when(messageSource.getMessage(any(), any(), any(), any()))
        .thenAnswer(invocation -> invocation.getArgument(2));
  }

  @Test
  void returnsPageResponseOnValidSearchRequest() throws Exception {

    when(validator.validate(any(BookSearchCriteria.class))).thenReturn(Set.of());

    PageResponse<BookSearchResult> response =
        new PageResponse<>(
            List.of(
                new BookSearchResult(
                    1L,
                    "Clean Code",
                    "Programming",
                    new BigDecimal("40"),
                    2008,
                    true,
                    10L,
                    "Prentice Hall",
                    Set.of("Robert C. Martin"))),
            0,
            10,
            1,
            1);

    when(bookSearchUseCase.searchForApi(any(BookSearchCriteria.class), any(Pageable.class)))
        .thenReturn(response);

    mockMvc
        .perform(post("/api/books/search").contentType(MediaType.APPLICATION_JSON).content("{}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].id").value(1))
        .andExpect(jsonPath("$.content[0].title").value("Clean Code"))
        .andExpect(jsonPath("$.content[0].publisherName").value("Prentice Hall"))
        .andExpect(jsonPath("$.page").value(0))
        .andExpect(jsonPath("$.size").value(10))
        .andExpect(jsonPath("$.totalElements").value(1))
        .andExpect(jsonPath("$.totalPages").value(1));

    ArgumentCaptor<BookSearchCriteria> criteriaCaptor =
        ArgumentCaptor.forClass(BookSearchCriteria.class);
    ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

    // verify flow
    verify(bookSearchUseCase).searchForApi(criteriaCaptor.capture(), pageableCaptor.capture());

    BookSearchCriteria capturedCriteria = criteriaCaptor.getValue();
    Pageable capturedPageable = pageableCaptor.getValue();

    assertThat(capturedCriteria.searchMode()).isEqualTo(SearchMode.AND);
    assertThat(capturedPageable.getPageNumber()).isEqualTo(0);
    assertThat(capturedPageable.getPageSize()).isEqualTo(10);
    assertThat(capturedPageable.getSort().getOrderFor("title")).isNotNull();
    assertThat(capturedPageable.getSort().getOrderFor("title").getDirection())
        .isEqualTo(Sort.Direction.ASC);
  }

  @Test
  void returnsBadRequestOnValidatorReturnsViolations() throws Exception {

    Set<ConstraintViolation<BookSearchCriteria>> violations =
        Set.of(
            mockViolation("title", "Title must be at most 100 characters"),
            mockViolation("minPrice", "Minimum price must be zero or positive"));

    when(validator.validate(any(BookSearchCriteria.class))).thenReturn(violations);
    when(validationErrorMapper.toFieldErrors(any()))
            .thenReturn(
                    List.of(
                            new FieldErrorDTO("title", "Title must be at most 100 characters"),
                            new FieldErrorDTO("minPrice", "Minimum price must be zero or positive")));
    mockMvc
        .perform(post("/api/books/search").contentType(MediaType.APPLICATION_JSON).content("{}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.error").value("Bad Request"))
        .andExpect(jsonPath("$.fieldErrors[*].field", containsInAnyOrder("title", "minPrice")));

    verify(bookSearchUseCase, never())
        .searchForApi(any(BookSearchCriteria.class), any(Pageable.class));
  }

  /*

  When minPrice > maxPrice
  When PublishYearFrom > PublishYearTo
   */
  @Test
  void returnsBadRequestOnSearchRangesAreInvalid() throws Exception {

    when(validator.validate(any(BookSearchCriteria.class))).thenReturn(Set.of());
    when(validationErrorMapper.toFieldErrors(any()))
            .thenReturn(
                    List.of(
                            new FieldErrorDTO("minPrice", "Minimum price cannot be greater than maximum price"),
                            new FieldErrorDTO("publishYearFrom", "Publish year from cannot be greater than publish year to")));

    // invalid request payload
    SearchRequest request =
        new SearchRequest(
            null,
            null,
            new BigDecimal("50.00"),
            new BigDecimal("10.00"),
            2020,
            2010,
            null,
            null,
            null,
            null);

    mockMvc
        .perform(
            post("/api/books/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(
            jsonPath("$.fieldErrors[*].field", containsInAnyOrder("minPrice", "publishYearFrom")));

    verify(bookSearchUseCase, never())
        .searchForApi(any(BookSearchCriteria.class), any(Pageable.class));
  }

  @Test
  void returnsBadRequestOnMalformedJson() throws Exception {
    mockMvc
        .perform(
            post("/api/books/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.message").value("Malformed JSON Request"));

    verify(bookSearchUseCase, never()).searchForApi(any(), any());
  }

  @Test
  void appliesPagingAndSortingParameters() throws Exception {

    when(validator.validate(any(BookSearchCriteria.class))).thenReturn(Set.of());
    when(bookSearchUseCase.searchForApi(any(BookSearchCriteria.class), any(Pageable.class)))
        .thenReturn(new PageResponse<>(List.of(), 1, 5, 0, 0));

    mockMvc
        .perform(
            post("/api/books/search?page=1&size=5&sort=price,desc")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        new SearchRequest(
                            "clean",
                            null,
                            null,
                            null,
                            null,
                            null,
                            true,
                            null,
                            null,
                            SearchMode.OR))))
        .andExpect(status().isOk());

    ArgumentCaptor<BookSearchCriteria> criteriaCaptor =
        ArgumentCaptor.forClass(BookSearchCriteria.class);
    ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

    verify(bookSearchUseCase).searchForApi(criteriaCaptor.capture(), pageableCaptor.capture());

    BookSearchCriteria capturedCriteria = criteriaCaptor.getValue();
    Pageable capturedPageable = pageableCaptor.getValue();

    assertThat(capturedCriteria.title()).isEqualTo("clean");
    assertThat(capturedCriteria.availability()).isTrue();
    assertThat(capturedCriteria.searchMode()).isEqualTo(SearchMode.OR);

    assertThat(capturedPageable.getPageNumber()).isEqualTo(1);
    assertThat(capturedPageable.getPageSize()).isEqualTo(5);
    assertThat(capturedPageable.getSort().getOrderFor("price")).isNotNull();
    assertThat(capturedPageable.getSort().getOrderFor("price").getDirection())
        .isEqualTo(Sort.Direction.DESC);
  }

  @SuppressWarnings("unchecked")
  private ConstraintViolation<BookSearchCriteria> mockViolation(String field, String message) {
    ConstraintViolation<BookSearchCriteria> violation = mock(ConstraintViolation.class);
    Path path = mock(Path.class);
    when(violation.getPropertyPath()).thenReturn(path);
    when(path.toString()).thenReturn(field);
    when(violation.getMessage()).thenReturn(message);

    return violation;
  }

  private record SearchRequest(
      String title,
      String genre,
      BigDecimal minPrice,
      BigDecimal maxPrice,
      Integer publishYearFrom,
      Integer publishYearTo,
      Boolean availability,
      String author,
      String publisherName,
      SearchMode searchMode) {}
}
