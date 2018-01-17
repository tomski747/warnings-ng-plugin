package io.jenkins.plugins.analysis.warnings;

import java.io.File;
import java.nio.charset.Charset;

import org.jvnet.localizer.LocaleProvider;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import edu.hm.hafner.analysis.Issue;
import edu.hm.hafner.analysis.IssueBuilder;
import edu.hm.hafner.analysis.Issues;
import edu.hm.hafner.analysis.parser.FindBugsParser;
import static edu.hm.hafner.analysis.parser.FindBugsParser.PriorityProperty.*;
import static hudson.plugins.warnings.WarningsDescriptor.*;
import io.jenkins.plugins.analysis.core.model.DefaultLabelProvider;
import io.jenkins.plugins.analysis.core.model.StaticAnalysisTool;

import hudson.Extension;

/**
 * Provides a parser and customized messages for FindBugs.
 *
 * @author Ullrich Hafner
 */
public class FindBugs extends StaticAnalysisTool {
    private static final String PARSER_NAME = Messages.Warnings_FindBugs_ParserName();
    private static final String SMALL_ICON_URL = IMAGE_PREFIX + "findbugs-24x24.png";
    private static final String LARGE_ICON_URL = IMAGE_PREFIX + "findbugs-48x48.png";

    private boolean useRankAsPriority;

    /**
     * Creates a new instance of {@link FindBugs}.
     */
    @DataBoundConstructor
    public FindBugs() {
        // empty constructor required for stapler
    }

    /**
     * If useRankAsPriority is {@code true}, then the FindBugs parser will use the rank when evaluation the priority.
     * Otherwise the priority of the FindBugs warning will be mapped.
     *
     * @param useRankAsPriority
     *         {@code true} to use the rank, {@code false} to use the
     */
    @DataBoundSetter
    public void setUseRankAsPriority(final boolean useRankAsPriority) {
        this.useRankAsPriority = useRankAsPriority;
    }

    public boolean getUseRankAsPriority() {
        return useRankAsPriority;
    }

    @Override
    public Issues<Issue> parse(final File file, final Charset charset, final IssueBuilder builder) {
        return new FindBugsParser(useRankAsPriority ? RANK : CONFIDENCE).parse(file, builder);
    }

    /** Registers this tool as extension point implementation. */
    @Extension
    public static final class Descriptor extends StaticAnalysisToolDescriptor {
        public Descriptor() {
            super(new FindBugsLabelProvider());
        }
    }

    /**
     * Provides the labels for the parser.
     */
    static class FindBugsLabelProvider extends DefaultLabelProvider {
        private final FindBugsMessages messages = new FindBugsMessages();

        private FindBugsLabelProvider() {
            this("findbugs", PARSER_NAME);

            messages.initialize();
        }

        /**
         * Creates a new {@link FindBugsLabelProvider} with the specified ID.
         *
         * @param id
         *         the ID
         * @param name
         *         the name of the static analysis tool
         */
        protected FindBugsLabelProvider(final String id, final String name) {
            super(id, name);
        }

        @Override
        public String getDescription(final Issue issue) {
            return messages.getMessage(issue.getType(), LocaleProvider.getLocale());
        }

        @Override
        public String getSmallIconUrl() {
            return SMALL_ICON_URL;
        }

        @Override
        public String getLargeIconUrl() {
            return LARGE_ICON_URL;
        }
    }
}