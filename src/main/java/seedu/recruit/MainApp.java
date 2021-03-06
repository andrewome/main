package seedu.recruit;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import com.google.common.eventbus.Subscribe;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import seedu.recruit.commons.core.Config;
import seedu.recruit.commons.core.EventsCenter;
import seedu.recruit.commons.core.LogsCenter;
import seedu.recruit.commons.core.Version;
import seedu.recruit.commons.events.ui.ExitAppRequestEvent;
import seedu.recruit.commons.exceptions.DataConversionException;
import seedu.recruit.commons.util.ConfigUtil;
import seedu.recruit.commons.util.StringUtil;
import seedu.recruit.logic.Logic;
import seedu.recruit.logic.LogicManager;
import seedu.recruit.model.CandidateBook;
import seedu.recruit.model.CompanyBook;
import seedu.recruit.model.Model;
import seedu.recruit.model.ModelManager;
import seedu.recruit.model.ReadOnlyCandidateBook;
import seedu.recruit.model.ReadOnlyCompanyBook;
import seedu.recruit.model.UserPrefs;
import seedu.recruit.model.util.SampleDataUtil;
import seedu.recruit.storage.CandidateBookStorage;
import seedu.recruit.storage.CompanyBookStorage;
import seedu.recruit.storage.JsonUserPrefsStorage;
import seedu.recruit.storage.Storage;
import seedu.recruit.storage.StorageManager;
import seedu.recruit.storage.UserPrefsStorage;
import seedu.recruit.storage.XmlCandidateBookStorage;
import seedu.recruit.storage.XmlCompanyBookStorage;
import seedu.recruit.ui.Ui;
import seedu.recruit.ui.UiManager;

/**
 * The main entry point to the application.
 */
public class MainApp extends Application {

    public static final Version VERSION = new Version(1, 4, 0, true);

    private static final Logger logger = LogsCenter.getLogger(MainApp.class);

    protected Ui ui;
    protected Logic logic;
    protected Storage storage;
    protected Model model;
    protected Config config;
    protected UserPrefs userPrefs;


    @Override
    public void init() throws Exception {
        logger.info("=============================[ Initializing RecruitBook ]===========================");
        super.init();

        AppParameters appParameters = AppParameters.parse(getParameters());
        config = initConfig(appParameters.getConfigPath());

        UserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(config.getUserPrefsFilePath());
        userPrefs = initPrefs(userPrefsStorage);
        CandidateBookStorage candidateBookStorage = new XmlCandidateBookStorage(userPrefs.getCandidateBookFilePath());
        CompanyBookStorage companyBookStorage = new XmlCompanyBookStorage(userPrefs.getCompanyBookFilePath());
        storage = new StorageManager(candidateBookStorage, companyBookStorage, userPrefsStorage);
        storage.initialiseFilePermissions();

        initLogging(config);

        model = initModelManager(storage, userPrefs);

        logic = new LogicManager(model, userPrefs);

        ui = new UiManager(logic, config, userPrefs);

        initEventsCenter();
    }

    /**
     * Returns a {@code ModelManager} with the data from {@code storage}'s recruit book and {@code userPrefs}. <br>
     * The data from the sample recruit book will be used instead if {@code storage}'s recruit book is not found,
     * or an empty recruit book will be used instead if errors occur when reading {@code storage}'s recruit book.
     */
    private Model initModelManager(Storage storage, UserPrefs userPrefs) {
        Optional<ReadOnlyCandidateBook> candidateBookOptional;
        Optional<ReadOnlyCompanyBook> companyBookOptional;
        ReadOnlyCandidateBook initialCandidateData;
        ReadOnlyCompanyBook initialJobOfferData;

        try {
            candidateBookOptional = storage.readCandidateBook();
            if (!candidateBookOptional.isPresent()) {
                logger.info("Data file not found. Will be starting with a sample CandidateBook");
            }
            initialCandidateData = candidateBookOptional.orElseGet(SampleDataUtil::getSampleCandidateBook);
        } catch (DataConversionException e) {
            logger.warning("Data file not in the correct format. Will be starting with an empty CandidateBook");
            initialCandidateData = new CandidateBook();
        } catch (IOException e) {
            logger.warning("Problem while reading from the file. Will be starting with an empty CandidateBook");
            initialCandidateData = new CandidateBook();
        }
        try {
            companyBookOptional = storage.readCompanyBook();
            if (!companyBookOptional.isPresent()) {
                logger.info("Data file not found. Will be starting with a sample CompanyBook");
            }
            initialJobOfferData = companyBookOptional.orElseGet(SampleDataUtil::getSampleCompanyBook);
        } catch (DataConversionException e) {
            logger.warning("Data file not in the correct format. Will be starting with an empty CompanyBook");
            initialJobOfferData = new CompanyBook();
        } catch (IOException e) {
            logger.warning("Problem while reading from the file. Will be starting with an empty CompanyBook");
            initialJobOfferData = new CompanyBook();
        }

        return new ModelManager(initialCandidateData, initialJobOfferData, userPrefs);
    }

    private void initLogging(Config config) {
        LogsCenter.init(config);
    }

    /**
     * Returns a {@code Config} using the file at {@code configFilePath}. <br>
     * The default file path {@code Config#DEFAULT_CONFIG_FILE} will be used instead
     * if {@code configFilePath} is null.
     */
    protected Config initConfig(Path configFilePath) {
        Config initializedConfig;
        Path configFilePathUsed;

        configFilePathUsed = Config.DEFAULT_CONFIG_FILE;

        if (configFilePath != null) {
            logger.info("Custom Config file specified " + configFilePath);
            configFilePathUsed = configFilePath;
        }

        logger.info("Using config file : " + configFilePathUsed);

        try {
            Optional<Config> configOptional = ConfigUtil.readConfig(configFilePathUsed);
            initializedConfig = configOptional.orElse(new Config());
        } catch (DataConversionException e) {
            logger.warning("Config file at " + configFilePathUsed + " is not in the correct format. "
                    + "Using default config properties");
            initializedConfig = new Config();
        }

        //Update config file in case it was missing to begin with or there are new/unused fields
        try {
            ConfigUtil.saveConfig(initializedConfig, configFilePathUsed);
        } catch (IOException e) {
            logger.warning("Failed to save config file : " + StringUtil.getDetails(e));
        }
        return initializedConfig;
    }

    /**
     * Returns a {@code UserPrefs} using the file at {@code storage}'s user prefs file path,
     * or a new {@code UserPrefs} with default configuration if errors occur when
     * reading from the file.
     */
    protected UserPrefs initPrefs(UserPrefsStorage storage) {
        Path prefsFilePath = storage.getUserPrefsFilePath();
        logger.info("Using prefs file : " + prefsFilePath);

        UserPrefs initializedPrefs;
        try {
            Optional<UserPrefs> prefsOptional = storage.readUserPrefs();
            initializedPrefs = prefsOptional.orElse(new UserPrefs());
        } catch (DataConversionException e) {
            logger.warning("UserPrefs file at " + prefsFilePath + " is not in the correct format. "
                    + "Using default user prefs");
            initializedPrefs = new UserPrefs();
        } catch (IOException e) {
            logger.warning("Problem while reading from the file. Will be starting with an empty CandidateBook");
            initializedPrefs = new UserPrefs();
        }

        //Update prefs file in case it was missing to begin with or there are new/unused fields
        try {
            storage.saveUserPrefs(initializedPrefs);
        } catch (IOException e) {
            logger.warning("Failed to save config file : " + StringUtil.getDetails(e));
        }

        return initializedPrefs;
    }

    private void initEventsCenter() {
        EventsCenter.getInstance().registerHandler(this);
    }

    @Override
    public void start(Stage primaryStage) {
        logger.info("Starting RecruitBook " + MainApp.VERSION);
        ui.start(primaryStage);
    }

    @Override
    public void stop() {
        logger.info("============================ [ Stopping RecruitBook ] =============================");
        ui.stop();
        try {
            storage.saveUserPrefs(userPrefs);
            storage.removeFilePermissions();
        } catch (IOException e) {
            logger.severe("Failed to save preferences " + StringUtil.getDetails(e));
        }
        Platform.exit();
        System.exit(0);
    }

    @Subscribe
    public void handleExitAppRequestEvent(ExitAppRequestEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
