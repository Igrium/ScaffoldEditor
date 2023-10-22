package com.igrium.scaffold.compile;

import org.slf4j.Logger;

public interface CompileStep {

    /**
     * The description that will be printed when this step executes. For instance, "Writing world..."
     */
    public String getDescription();

    /**
     * If true, the compile will fail if this step fails.
     */
    public boolean isRequired();

    /**
     * Execute this compile step.
     * @param compiler The compiler to use.
     * @param context The compile context.
     * @param logger The logger to output to.
     * @throws Exception If this compile step fails for any reason.
     */
    public void execute(ScaffoldCompiler compiler, CompileContext context, Logger logger) throws Exception;
}
