package seedu.recruit.storage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import com.google.common.eventbus.Subscribe;

import seedu.recruit.commons.core.ComponentManager;
import seedu.recruit.commons.core.LogsCenter;
import seedu.recruit.commons.events.model.CandidateBookChangedEvent;
import seedu.recruit.commons.events.model.CompanyBookChangedEvent;
import seedu.recruit.commons.events.storage.DataSavingExceptionEvent;
import seedu.recruit.commons.events.storage.UserPrefsChangedEvent;
import seedu.recruit.commons.exceptions.DataConversionException;
import seedu.recruit.model.ReadOnlyCandidateBook;
import seedu.recruit.model.ReadOnlyCompanyBook;
import seedu.recruit.model.UserPrefs;

/**
 * Manages storage of CandidateBook data in local storage.
 */
public class StorageManager extends ComponentManager implements Storage {

    private static final Logger logger = LogsCenter.getLogger(StorageManager.class);
    private CandidateBookStorage candidateBookStorage;
    private CompanyBookStorage companyBookStorage;
    private UserPrefsStorage userPrefsStorage;


    public StorageManager(CandidateBookStorage candidateBookStorage, CompanyBookStorage companyBookStorage,
                          UserPrefsStorage userPrefsStorage) {
        super();
        this.companyBookStorage = companyBookStorage;
        this.candidateBookStorage = candidateBookStorage;
        this.userPrefsStorage = userPrefsStorage;
    }

    // ============================= File Permission methods ============================= //

    @Override
    public void initialiseFilePermissions() {
        File userPrefFile = new File(getUserPrefsFilePath().toString());
        File candidateBookFile = new File(getCandidateBookFilePath().toString());
        File companyBookFile = new File(getCompanyBookFilePath().toString());

        userPrefFile.setWritable(true);
        candidateBookFile.setWritable(true);
        companyBookFile.setWritable(true);
    }

    @Override
    public void removeFilePermissions() {
        File userPrefFile = new File(getUserPrefsFilePath().toString());
        File candidateBookFile = new File(getCandidateBookFilePath().toString());
        File companyBookFile = new File(getCompanyBookFilePath().toString());

        userPrefFile.setReadOnly();
        candidateBookFile.setReadOnly();
        companyBookFile.setReadOnly();
    }

    // ============================= UserPrefs methods ============================= //

    @Override
    public Path getUserPrefsFilePath() {
        return userPrefsStorage.getUserPrefsFilePath();
    }

    @Override
    public Optional<UserPrefs> readUserPrefs() throws DataConversionException, IOException {
        return userPrefsStorage.readUserPrefs();
    }

    @Override
    public void saveUserPrefs(UserPrefs userPrefs) throws IOException {
        userPrefsStorage.saveUserPrefs(userPrefs);
    }

    @Subscribe
    public void handleUserPrefsChangedEvent(UserPrefsChangedEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event,
                "User preferences changed, saving to file"));
        try {
            saveUserPrefs(event.data);
        } catch (IOException e) {
            raise(new DataSavingExceptionEvent(e));
        }
    }


    // ================ CandidateBook methods ==============================

    @Override
    public Path getCandidateBookFilePath() {
        return candidateBookStorage.getCandidateBookFilePath();
    }

    @Override
    public Optional<ReadOnlyCandidateBook> readCandidateBook() throws DataConversionException, IOException {
        return readCandidateBook(candidateBookStorage.getCandidateBookFilePath());
    }

    @Override
    public Optional<ReadOnlyCandidateBook> readCandidateBook(Path filePath)
            throws DataConversionException, IOException {
        logger.fine("Attempting to read candidatebook data from file: " + filePath);
        return candidateBookStorage.readCandidateBook(filePath);
    }

    @Override
    public void saveCandidateBook(ReadOnlyCandidateBook addressBook) throws IOException {
        saveCandidateBook(addressBook, candidateBookStorage.getCandidateBookFilePath());
    }

    @Override
    public void saveCandidateBook(ReadOnlyCandidateBook addressBook, Path filePath) throws IOException {
        logger.fine("Attempting to write to candidatebook data file: " + filePath);
        candidateBookStorage.saveCandidateBook(addressBook, filePath);
    }


    @Override
    @Subscribe
    public void handleCandidateBookChangedEvent(CandidateBookChangedEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event,
                "Local candidatebook changed, saving to file"));
        try {
            saveCandidateBook(event.data);
        } catch (IOException e) {
            raise(new DataSavingExceptionEvent(e));
        }
    }

    // ========================== CompanyBook methods ==============================

    @Override
    public Path getCompanyBookFilePath() {
        return companyBookStorage.getCompanyBookFilePath();
    };

    @Override
    public Optional<ReadOnlyCompanyBook> readCompanyBook() throws DataConversionException, IOException {
        return readCompanyBook(companyBookStorage.getCompanyBookFilePath());
    };

    @Override
    public Optional<ReadOnlyCompanyBook> readCompanyBook(Path filePath) throws DataConversionException, IOException {
        logger.fine("Attempting to read companybook data from file: " + filePath);
        return companyBookStorage.readCompanyBook(filePath);
    }

    @Override
    public void saveCompanyBook(ReadOnlyCompanyBook companyBook) throws IOException {
        saveCompanyBook(companyBook, companyBookStorage.getCompanyBookFilePath());
    }

    @Override
    public void saveCompanyBook(ReadOnlyCompanyBook companyBook, Path filePath) throws IOException {
        logger.fine("Attempting to write companybook to data file: " + filePath);
        companyBookStorage.saveCompanyBook(companyBook, filePath);
    }



    /**
     * Saves the current version of the Company Book to the hard disk.
     * Creates the data file if it is missing.
     * Raises {@link DataSavingExceptionEvent} if there was an error during saving.
     */

    @Override
    @Subscribe
    public void handleCompanyBookChangedEvent(CompanyBookChangedEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event, "Local CompanyBook changed, saving to file"));
        try {
            saveCompanyBook(event.data);
        } catch (IOException e) {
            raise(new DataSavingExceptionEvent(e));
        }
    };

}
