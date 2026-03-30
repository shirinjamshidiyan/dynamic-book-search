package com.shirin.jpaspecdynamicsearchengine.book.web.view;

import com.shirin.jpaspecdynamicsearchengine.book.application.search.BookSearchCriteria;
import com.shirin.jpaspecdynamicsearchengine.book.application.search.BookSearchResult;
import com.shirin.jpaspecdynamicsearchengine.book.application.search.BookSearchUseCase;
import com.shirin.jpaspecdynamicsearchengine.publisher.repository.PublisherRepository;
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
  private final PublisherRepository publisherRepository;

  @GetMapping
  public String search(
      @Valid @ModelAttribute(name = "search") BookSearchCriteria search,
      BindingResult bindingResult,
      @PageableDefault(size = 10, sort = "title") Pageable pageable,
      Model model) {
    model.addAttribute("publishers", publisherRepository.findAllNames());
    model.addAttribute("genres", bookSearchUseCase.getAllGenres());

    if (bindingResult.hasErrors()) {
      model.addAttribute("page", Page.empty());
      return "books";
    }

    Page<BookSearchResult> searchResults = bookSearchUseCase.search(search, pageable);
    model.addAttribute("page", searchResults);

    String sortProperty =
        pageable.getSort().stream().findFirst().map(Sort.Order::getProperty).orElse("title");
    String sortDir =
        pageable.getSort().stream()
            .findFirst()
            .map(order -> order.getDirection().name().toLowerCase())
            .orElse("asc");
    model.addAttribute("sortBy", sortProperty);
    model.addAttribute("sortDir", sortDir);

    int totalPages = searchResults.getTotalPages();
    if (totalPages > 0) {
      int currentPage = searchResults.getNumber() + 1;
      int startPage = Math.max(1, currentPage - 2);
      int endPage = Math.min(totalPages, currentPage + 2);

      List<Integer> pageRange = new ArrayList<>();
      for (int i = startPage; i <= endPage; i++) {
        pageRange.add(i);
      }
      model.addAttribute("pageRange", pageRange);
    }
    return "books";
  }
}
