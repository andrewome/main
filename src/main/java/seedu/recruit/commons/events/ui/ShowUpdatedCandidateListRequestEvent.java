package seedu.recruit.commons.events.ui;

import javafx.collections.ObservableList;
import seedu.recruit.commons.events.BaseEvent;
import seedu.recruit.model.Model;
import seedu.recruit.model.candidate.Candidate;

/**
 * An event requesting to fetch the latest update of job list.
 */
public class ShowUpdatedCandidateListRequestEvent extends BaseEvent {

    private ObservableList<Candidate> updatedCandidateList;

    public ShowUpdatedCandidateListRequestEvent(Model model) {
        this.updatedCandidateList = model.getFilteredCandidateList();
    }

    public ObservableList<Candidate> getUpdatedCandidateList() {
        return updatedCandidateList;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
