package com.shirin.jpaspecdynamicsearchapp.book.web.view;

import com.shirin.jpaspecdynamicsearchapp.book.application.search.BookSearchCriteria;
import com.shirin.jpaspecdynamicsearchapp.book.application.search.BookSearchCriteriaValidator;
import com.shirin.jpaspecdynamicsearchapp.book.application.search.BookSearchResult;
import com.shirin.jpaspecdynamicsearchapp.book.application.search.BookSearchUseCase;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/view/books")
public class BookSearchViewController {

  private final BookSearchUseCase bookSearchUseCase;

  @GetMapping
  public String search(
      @Valid @ModelAttribute(name = "search") BookSearchCriteria search,
      BindingResult bindingResult,
      @PageableDefault(size = 10, sort = "title") Pageable pageable,
      Model model) {

    model.addAttribute("publishers", bookSearchUseCase.getAllPublisherNames());
    model.addAttribute("genres", bookSearchUseCase.getAllGenres());

    // Business validation
    BookSearchCriteriaValidator.validate(search, bindingResult);
    if (bindingResult.hasErrors()) {
      model.addAttribute("page", Page.empty(pageable));
      model.addAttribute("pageRange", List.of());
      return "books";
    }

    Page<BookSearchResult> searchResults = bookSearchUseCase.searchPage(search, pageable);
    model.addAttribute("page", searchResults);

    Sort.Order order = pageable.getSort().stream().findFirst().orElse(Sort.Order.by("title"));
    model.addAttribute("sortBy", order.getProperty());
    model.addAttribute("sortDir", order.getDirection().name().toLowerCase());
    model.addAttribute("pageRange", buildPageRange(searchResults));

    return "books";
  }

  private List<Integer> buildPageRange(Page<BookSearchResult> page) {
    int totalPages = page.getTotalPages();
    if (totalPages == 0) {
      return List.of();
    }

    int currentPage = page.getNumber() + 1;
    int startPage = Math.max(1, currentPage - 2);
    int endPage = Math.min(totalPages, currentPage + 2);

    List<Integer> pageRange = new ArrayList<>();
    for (int i = startPage; i <= endPage; i++) {
      pageRange.add(i);
    }
    return pageRange;
  }
}
