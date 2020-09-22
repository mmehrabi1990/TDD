package org.mehrabi;

import java.util.ArrayList;
import java.util.List;

class CompositeFilter implements BookShelf.BookFilter {
    private List<BookShelf.BookFilter> filters;

    CompositeFilter() {
        filters = new ArrayList<>();
    }

    @Override
    public boolean apply(final Book b) {
        return filters.stream()
                .map(bookFilter -> bookFilter.apply(b))
                .reduce(true, (b1, b2) -> b1 && b2);
    }

    void addFilter(final BookShelf.BookFilter bookFilter) {
        filters.add(bookFilter);
    }
}