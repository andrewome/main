package seedu.recruit.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static seedu.recruit.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.recruit.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.recruit.testutil.TypicalPersons.ALICE;
import static seedu.recruit.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.recruit.model.candidate.Candidate;
import seedu.recruit.model.candidate.exceptions.DuplicateCandidateException;
import seedu.recruit.testutil.CandidateBuilder;

public class CandidateBookTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private final CandidateBook candidateBook = new CandidateBook();

    @Test
    public void constructor() {
        assertEquals(Collections.emptyList(), candidateBook.getCandidateList());
    }

    @Test
    public void resetData_null_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        candidateBook.resetData(null);
    }

    @Test
    public void resetData_withValidReadOnlyAddressBook_replacesData() {
        CandidateBook newData = getTypicalAddressBook();
        candidateBook.resetData(newData);
        assertEquals(newData, candidateBook);
    }

    @Test
    public void resetData_withDuplicatePersons_throwsDuplicatePersonException() {
        // Two candidates with the same identity fields
        Candidate editedAlice = new CandidateBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).withTags(VALID_TAG_HUSBAND)
                .build();
        List<Candidate> newCandidates = Arrays.asList(ALICE, editedAlice);
        CandidateBookStub newData = new CandidateBookStub(newCandidates);

        thrown.expect(DuplicateCandidateException.class);
        candidateBook.resetData(newData);
    }

    @Test
    public void hasPerson_nullPerson_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        candidateBook.hasCandidate(null);
    }

    @Test
    public void hasPerson_personNotInAddressBook_returnsFalse() {
        assertFalse(candidateBook.hasCandidate(ALICE));
    }

    @Test
    public void hasPerson_personInAddressBook_returnsTrue() {
        candidateBook.addCandidate(ALICE);
        assertTrue(candidateBook.hasCandidate(ALICE));
    }

    @Test
    public void hasPerson_personWithSameIdentityFieldsInAddressBook_returnsTrue() {
        candidateBook.addCandidate(ALICE);
        Candidate editedAlice = new CandidateBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).withTags(VALID_TAG_HUSBAND)
                .build();
        assertTrue(candidateBook.hasCandidate(editedAlice));
    }

    @Test
    public void getPersonList_modifyList_throwsUnsupportedOperationException() {
        thrown.expect(UnsupportedOperationException.class);
        candidateBook.getCandidateList().remove(0);
    }

    /**
     * A stub ReadOnlyCandidateBook whose candidates list can violate interface constraints.
     */
    private static class CandidateBookStub implements ReadOnlyCandidateBook {
        private final ObservableList<Candidate> candidates = FXCollections.observableArrayList();

        CandidateBookStub(Collection<Candidate> candidates) {
            this.candidates.setAll(candidates);
        }

        @Override
        public ObservableList<Candidate> getCandidateList() {
            return candidates;
        }
    }

}
