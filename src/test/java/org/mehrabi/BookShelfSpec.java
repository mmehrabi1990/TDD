package org.mehrabi;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Bookshelf Specification")
public class BookShelfSpec
{
    @DisplayName("is empty when no book is added to it")
    @Test
    public void shelfEmptyWhenNoBookAdded()
    {
        BookShelf bookShelf = new BookShelf();
        List<String> books = bookShelf.books();
        assertTrue(books.isEmpty(),() -> "Bookshelf should be empty");
    }
}
