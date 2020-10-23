package org.mehrabi;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Bookshelf Specification")
@ExtendWith(BooksParameterResolver.class)
public class BookShelfSpec {

    private BookShelf shelf;
    private Book effectiveJava;
    private Book codeComplete;
    private Book mythicalManMonth;
    private Book cleanCode;
    private Book refactoring;

//    @BeforeEach
//    void init() {
//        shelf = new BookShelf();
//        effectiveJava = new Book("Effective Java", "Joshua Bloch",
//                LocalDate.of(2008, Month.MAY, 8));
//        codeComplete = new Book("Code Complete", "Steve McConnel",
//                LocalDate.of(2004, Month.JUNE, 9));
//        mythicalManMonth = new Book("The Mythical Man-Month", "Frederick Phillips Brooks", LocalDate.of(1975, Month.JANUARY, 1));
//        cleanCode = new Book("Clean Code", "Robert C. Martin", LocalDate.of(2008, Month.JANUARY, 1));
//    }

    @BeforeEach
    void init(Map<String, Book> books) {
        shelf = new BookShelf();
        this.effectiveJava = books.get("Effective Java");
        this.codeComplete = books.get("Code Complete");
        this.mythicalManMonth = books.get("The Mythical Man-Month");
        this.cleanCode = books.get("Clean Code");
        this.refactoring = books.get("Refactoring: Improving the Design of Existing Code");
    }

    @DisplayName("is empty when no book is added to it")
    @Test
    public void shelfEmptyWhenNoBookAdded() {
        List<Book> books = shelf.getBooks();
        assertTrue(books.isEmpty(), () -> "Bookshelf should be empty");
    }

    @DisplayName("add two books to bookshelf")
    @Test
    void bookshelfContainsTwoBooksWhenTwoBooksAdded() {
        shelf.add(effectiveJava, codeComplete);
        List<Book> books = shelf.getBooks();
        assertEquals(2, books.size(), () -> "BookShelf should have two books.");
    }

    @DisplayName("add no books to bookshelf  and its empty")
    @Test
    public void emptyBookShelfWhenAddIsCalledWithoutBooks() {
        shelf.add();
        List<Book> books = shelf.getBooks();
        assertTrue(books.isEmpty(), () -> "BookShelf should be empty.");
    }

    @DisplayName("books should be immutable")
    @Test
    void booksReturnedFromBookShelfIsImmutableForClient() {
        shelf.add(effectiveJava, codeComplete);
        List<Book> books = shelf.getBooks();
        try {
            books.add(mythicalManMonth);
            fail(() -> "Should not be able to add book to books");
        } catch (UnsupportedOperationException e) {
            assertTrue(true, () -> "Should throw UnsupportedOperationException.");
        } catch (Exception e) {
            fail(() -> "Excpetion should be instance of UnsupportedOperationException");
        }
    }

    //    @Disabled("Needs to implement Comparator")
    @DisplayName("bookshelf is arranged lexicographically by book title")
    @Test
    void bookshelfArrangedByBookTitle() {
        shelf.add(effectiveJava, codeComplete, mythicalManMonth);
        List<Book> books = shelf.arrange();
        assertEquals(Arrays.asList(codeComplete, effectiveJava, mythicalManMonth), books, () -> "Books in a bookshelf should be arranged lexicographically by book title");
    }

    @DisplayName("books should be in insertion order even after sort when we call getBooks method")
    @Test
    void booksInBookShelfAreInInsertionOrderAfterCallingArrange() {
        shelf.add(effectiveJava, codeComplete, mythicalManMonth);
        shelf.arrange();
        List<Book> books = shelf.getBooks();
        assertEquals(Arrays.asList(effectiveJava, codeComplete, mythicalManMonth), books, () -> "Books in bookshelf are in insertion order");
    }

    @DisplayName("books should arrange by input criteria")
    @Test
    void bookshelfArrangedByUserProvidedCriteria() {
        Comparator<Book> reversed = Comparator.<Book>naturalOrder().reversed();
        List<Book> books = shelf.arrange(reversed);
        assertThat(books).isSortedAccordingTo(reversed);
    }

    @Test
    @DisplayName("books inside bookshelf are grouped by publication year")
    void groupBooksInsideBookShelfByPublicationYear() {
        shelf.add(effectiveJava, codeComplete, mythicalManMonth, cleanCode);
        Map<String, List<Book>> booksByAuthor = shelf.groupBy(Book::getAuthor);
        assertThat(booksByAuthor)
                .containsKey("Joshua Bloch")
                .containsValues(singletonList(effectiveJava));
        assertThat(booksByAuthor)
                .containsKey("Steve McConnel")
                .containsValues(singletonList(codeComplete));
        assertThat(booksByAuthor)
                .containsKey("Frederick Phillips Brooks")
                .containsValues(singletonList(mythicalManMonth));
        assertThat(booksByAuthor)
                .containsKey("Robert C. Martin")
                .containsValues(singletonList(cleanCode));
    }

//    @Nested
//    @DisplayName("is empty")
//    class IsEmpty {
//        @Test
//        @DisplayName("when no book is added to it")
//        public void emptyBookShelfWhenNoBookAdded() {
//// Test case removed for brevity
//        }
//        @Test
//        @DisplayName("when add is called without books")
//        void emptyBookShelfWhenAddIsCalledWithoutBooks() {
//// Test case removed for brevity
//        }
//    }
//
//    @Nested
//    @DisplayName("after adding books")
//    class BooksAreAdded {
//        @Test
//        @DisplayName("contains two books")
//        void bookshelfContainsTwoBooksWhenTwoBooksAdded() {
//// Test case removed for brevity
//        }
//        @Test
//        @DisplayName("returns an immutable books collection to client")
//        void bookshelfIsImmutableForClient() {
//// Test case removed for brevity
//        }
//    }
//}

    @Test
    @DisplayName("is 0% completed and 100% to-read when no book is read yet")
    void progress100PercentUnread() {
        shelf.add(effectiveJava, codeComplete, mythicalManMonth, cleanCode);
        Progress progress = shelf.progress();
        assertThat(progress.completed()).isEqualTo(1);
        assertThat(progress.toRead()).isEqualTo(100);
    }

    @Test
    @DisplayName("is 40% completed and 60% to-read when 2 books are finished and 3 books not read yet")
    void progressWithCompletedAndToReadPercentages() {
        effectiveJava.startedReadingOn(LocalDate.of(2016, Month.JULY, 1));
        effectiveJava.finishedReadingOn(LocalDate.of(2016, Month.JULY, 31));
        cleanCode.startedReadingOn(LocalDate.of(2016, Month.AUGUST, 1));
        cleanCode.finishedReadingOn(LocalDate.of(2016, Month.AUGUST, 31));
        shelf.add(effectiveJava, codeComplete, mythicalManMonth, cleanCode, refactoring);
        Progress progress = shelf.progress();
        assertThat(progress.completed()).isEqualTo(40);
        assertThat(progress.toRead()).isEqualTo(60);
    }

//    @Nested
//    @DisplayName("search")
//    class BookShelfSeachSpec {
//        @BeforeEach
//        void setup() {
//            shelf.add(codeComplete, effectiveJava, mythicalManMonth, cleanCode);
//        }
//
//        @Test
//        @DisplayName(" should find books with title containing text")
//        void shouldFindBooksWithTitleContainingText() {
//            List<Book> books = shelf.findBooksByTitle("code");
//            assertThat(books.size()).isEqualTo(2);
//        }
//
//        @Test
//        @DisplayName(" should find books with title containing text and published after specified date.")
//        void shouldFilterSearchedBooksBasedOnPublishedDate() {
//            List<Book> books = shelf.findBooksByTitle("code", b -> b.getPublishedOn().isBefore(LocalDate.of(2014, 12, 31)));
//            assertThat(books.size()).isEqualTo(2);
//        }
//    }

    @Nested
    @DisplayName("search")
    class BookShelfSeachSpec {
        private Book cleanCode;
        private Book codeComplete;

        @BeforeEach
        void init() {
            cleanCode = new Book("Clean Code", "Robert C. Martin", LocalDate.
                    of(2008, Month.AUGUST, 1));
            codeComplete = new Book("Code Complete", "Steve McConnel", LocalDate.
                    of(2004, Month.JUNE, 9));
        }

        @Nested
        @DisplayName("book published date")
        class BookPulishedFilterSpec {
            @Test
            @DisplayName("is after specified year")
            void validateBookPublishedDatePostAskedYear() {
                BookShelf.BookFilter filter = BookPublishedYearFilter.after(2007);
                assertTrue(filter.apply(cleanCode));
                assertFalse(filter.apply(codeComplete));
            }
        }

        @Test
        @DisplayName("Composite criteria is based on multiple filters")
        void shouldFilterOnMultiplesCriteria(){
            CompositeFilter compositeFilter = new CompositeFilter();
            compositeFilter.addFilter( b -> false);
            assertFalse(compositeFilter.apply(cleanCode));
        }
        @Test
        @DisplayName("Composite criteria does not invoke after first failure")
        void shouldNotInvokeAfterFirstFailure(){
            CompositeFilter compositeFilter = new CompositeFilter();
            compositeFilter.addFilter( b -> false);
            compositeFilter.addFilter( b -> true);
            assertFalse(compositeFilter.apply(cleanCode));
        }
        @Test
        @DisplayName("Composite criteria invokes all filters")
        void shouldInvokeAllFilters(){
            CompositeFilter compositeFilter = new CompositeFilter();
            compositeFilter.addFilter( b -> true);
            compositeFilter.addFilter( b -> true);
            assertTrue(compositeFilter.apply(cleanCode));
        }
    }
}
