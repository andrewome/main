package seedu.recruit.logic.parser;

import static seedu.recruit.commons.core.Messages.MESSAGE_UNKNOWN_COMMAND;

import seedu.recruit.commons.core.index.Index;
import seedu.recruit.logic.LogicState;
import seedu.recruit.logic.commands.Command;
import seedu.recruit.logic.commands.SelectCandidateCommand;
import seedu.recruit.logic.commands.SelectCompanyCommand;
import seedu.recruit.logic.commands.SelectJobCommand;
import seedu.recruit.logic.commands.ShortlistCandidateCommand;
import seedu.recruit.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new ShortlistCandidateInitializationCommand object
 */
public class ShortlistParser {

    /**
     * Constructor to parse commands if the logic state is within shortlist process
     * @param commandWord name of the command taken from RecruitBookParser
     * @param arguments arguments of the command taken from RecruitBookParser
     * @param state logic state
     * @return the shortlist command based on the user input
     * @throws ParseException if the user input does not conform the expected format
     */
    public Command parseCommand(String commandWord, String arguments, LogicState state)
            throws ParseException {
        if (state.nextCommand.equals(ShortlistCandidateCommand.COMMAND_LOGIC_STATE)) {
            switch (commandWord) {

            case ShortlistCandidateCommand.COMMAND_WORD:
                return new ShortlistCandidateCommand();

            default:
                throw new ParseException(MESSAGE_UNKNOWN_COMMAND);
            }

        } else if (state.nextCommand.equals(SelectCompanyCommand.COMMAND_LOGIC_STATE)) {
            switch (commandWord) {

            case SelectCompanyCommand.COMMAND_WORD:
                Index index = ParserUtil.parseIndex(arguments);
                return new SelectCompanyCommand(index);

            default:
                throw new ParseException(MESSAGE_UNKNOWN_COMMAND);
            }
        } else if (state.nextCommand.equals(SelectJobCommand.COMMAND_LOGIC_STATE)) {
            switch (commandWord) {

            case SelectJobCommand.COMMAND_WORD:
                Index index = ParserUtil.parseIndex(arguments);
                return new SelectJobCommand(index);

            default:
                throw new ParseException(MESSAGE_UNKNOWN_COMMAND);
            }

        } else if (state.nextCommand.equals(SelectCandidateCommand.COMMAND_LOGIC_STATE)) {
            switch (commandWord) {

            case SelectCandidateCommand.COMMAND_WORD:
                Index index = ParserUtil.parseIndex(arguments);
                return new SelectCandidateCommand(index);

            default:
                throw new ParseException(MESSAGE_UNKNOWN_COMMAND);
            }
        } else {
            throw new ParseException(MESSAGE_UNKNOWN_COMMAND);
        }
    }
}
